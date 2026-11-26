@file:Suppress("MaxLineLength") // Casos de prueba en una línea.

package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.auth.DatosSesionGuardada
import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.offline.AccionEnColaException
import com.elcobre.lavanderiaelcobre.data.offline.AccionPendiente
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.AlmacenEnMemoria
import com.elcobre.lavanderiaelcobre.data.offline.CacheLectura
import com.elcobre.lavanderiaelcobre.data.offline.ColaAcciones
import com.elcobre.lavanderiaelcobre.data.offline.EjecutorAcciones
import com.elcobre.lavanderiaelcobre.data.offline.EstadoAccion
import com.elcobre.lavanderiaelcobre.data.offline.FotoPendiente
import com.elcobre.lavanderiaelcobre.data.offline.HoraOriginal
import com.elcobre.lavanderiaelcobre.data.offline.RelojAcciones
import com.elcobre.lavanderiaelcobre.data.offline.RelojDispositivo
import com.elcobre.lavanderiaelcobre.data.offline.ResultadoEnvio
import com.elcobre.lavanderiaelcobre.data.offline.ResultadoRegistro
import com.elcobre.lavanderiaelcobre.data.offline.Sincronizador
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import com.elcobre.lavanderiaelcobre.ui.gestion.ahoraMs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class RelojFalso(var ahora: Long = 1_000_000_000L, var monotonico: Long = 50_000L, var arranque: String = "1") : RelojDispositivo {
    override fun ahoraMs() = ahora
    override fun monotonicoMs() = monotonico
    override fun arranque() = arranque
    override fun zonaHoraria() = "America/Santiago"
}

/** Servidor simulado: guarda lo aplicado por id y responde según el escenario. */
private class ServidorFalso : EjecutorAcciones {
    var conectado = true
    var horaServidor = 1_000_000_000L
    val aplicadas = mutableMapOf<String, HoraOriginal>()
    val rechazar = mutableMapOf<String, String>()
    var sesionValida = true
    var fotosConRed = true
    val fotosSubidas = mutableListOf<String>()

    override suspend fun horaServidorMs(): Long? = if (conectado) horaServidor else null

    override suspend fun aplicar(accion: AccionPendiente, hora: HoraOriginal): ResultadoEnvio = when {
        !conectado -> ResultadoEnvio.SinRed
        !sesionValida -> ResultadoEnvio.Sesion("Sesión vencida")
        accion.id in aplicadas -> ResultadoEnvio.Aplicada // OPERACION_YA_APLICADA
        accion.entidad in rechazar -> ResultadoEnvio.Rechazada(rechazar.getValue(accion.entidad))
        else -> {
            aplicadas[accion.id] = hora
            ResultadoEnvio.Aplicada
        }
    }

    override suspend fun subirFotos(accion: AccionPendiente): ResultadoEnvio =
        if (!conectado || !fotosConRed) ResultadoEnvio.SinRed else ResultadoEnvio.Aplicada.also { fotosSubidas += accion.id }
}

class SinConexionTest {
    private val reloj = RelojFalso()
    private val servidor = ServidorFalso()
    private val cola = ColaAcciones(AlmacenEnMemoria())
    private var uid: String? = "operario-1"
    private val sync = Sincronizador(cola, servidor, reloj) { uid }

    private fun accion(id: String, entidad: String = "c1", tipo: TipoAccion = TipoAccion.COMPLETAR_ETAPA, fotos: List<FotoPendiente> = emptyList()) =
        sync.nueva(id, tipo, uid!!, entidad, mapOf("comandaId" to entidad), "Acción $id", fotos)

    @AfterTest
    fun limpiar() {
        SesionPersistente.limpiar()
    }

    @Test
    fun colaPersistente_sobreviveAlCierreDeLaApp() = runTest {
        val almacen = AlmacenEnMemoria()
        val primera = ColaAcciones(almacen)
        primera.agregar(accion("a1"))
        // "Reabrir la app": otra instancia lee el mismo archivo.
        assertEquals(listOf("a1"), ColaAcciones(almacen).acciones.value.map { it.id })
    }

    @Test
    fun sinRed_quedaEnColaYSeEnviaAlVolver_sinDuplicar() = runTest {
        servidor.conectado = false
        assertTrue(sync.registrar(accion("a1")) is ResultadoRegistro.EnCola)
        assertEquals(1, cola.acciones.value.size)
        servidor.conectado = true
        sync.sincronizar(forzar = true)
        assertTrue(cola.acciones.value.isEmpty())
        // Reintento del mismo id (la respuesta se perdió): el servidor lo da por aplicado, no duplica.
        cola.agregar(accion("a1"))
        sync.sincronizar(forzar = true)
        assertEquals(1, servidor.aplicadas.size)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun horaOriginal_usaElRelojMonotonicoAunqueElTelefonoTengaMalLaHora() = runTest {
        servidor.conectado = false
        reloj.ahora = 5_000_000_000L // el reloj del teléfono está muy adelantado
        sync.registrar(accion("a1"))
        // Pasan 10 minutos reales; el servidor está en su hora correcta.
        reloj.monotonico += 10 * 60_000L
        reloj.ahora += 10 * 60_000L
        servidor.conectado = true
        servidor.horaServidor = 2_000_000_000L
        sync.sincronizar(forzar = true)
        val hora = servidor.aplicadas.getValue("a1")
        assertEquals(2_000_000_000L - 10 * 60_000L, hora.ocurridoEnMs)
        assertEquals(false, hora.relojDudoso)
    }

    @Test
    fun horaOriginal_trasReiniciarElTelefono_corrigeConElDesfaseYMarcaDudosa() {
        val a = accion("a1").copy(ocurridoEnMs = 1_000L, arranque = "1")
        val corregida = RelojAcciones.horaOriginal(a, servidorAhoraMs = 10_000_000L, dispositivoAhoraMs = 9_000_000L, monotonicoAhoraMs = 10L, arranqueActual = "2")
        assertEquals(1_000L + 1_000_000L, corregida.ocurridoEnMs) // hora del teléfono + 1.000.000 ms de desfase con el servidor
        assertTrue(corregida.relojDudoso)
        val sinServidor = RelojAcciones.horaOriginal(a, null, 0, 0, "1")
        assertTrue(sinServidor.relojDudoso)
    }

    @Test
    fun ordenYDependencias_unRechazoBloqueaLasSiguientesDeLaMismaComanda() = runTest {
        servidor.conectado = false
        sync.registrar(accion("e1", "c1"))
        sync.registrar(accion("e2", "c1"))
        sync.registrar(accion("e3", "c2"))
        servidor.conectado = true
        servidor.rechazar["c1"] = "La etapa ya fue completada"
        sync.sincronizar(forzar = true)
        val restantes = cola.acciones.value.associateBy { it.id }
        assertEquals(EstadoAccion.REQUIERE_ATENCION, restantes.getValue("e1").estado)
        assertEquals("La etapa ya fue completada", restantes.getValue("e1").ultimoError)
        assertEquals(EstadoAccion.PENDIENTE, restantes.getValue("e2").estado, "Espera: no se envía fuera de orden")
        assertNull(restantes["e3"], "Otra comanda sigue su curso")
    }

    @Test
    fun rechazoEnElPrimerIntento_seInformaEnPantallaYNoQuedaEnCola() = runTest {
        servidor.rechazar["c1"] = "Solo se puede entregar una comanda finalizada"
        val r = sync.registrar(accion("a1"))
        assertEquals(ResultadoRegistro.Rechazada("Solo se puede entregar una comanda finalizada"), r)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun rechazoDespuesDeTrabajarSinConexion_noSePierdeHastaDescartar() = runTest {
        servidor.conectado = false
        sync.registrar(accion("a1"))
        servidor.conectado = true
        servidor.rechazar["c1"] = "HORA_FUERA_DE_RANGO"
        sync.sincronizar(forzar = true)
        assertEquals(EstadoAccion.REQUIERE_ATENCION, cola.acciones.value.single().estado)
        sync.sincronizar(forzar = true)
        assertEquals(1, cola.acciones.value.size, "No se pierde ni se reenvía sola")
        servidor.rechazar.clear()
        sync.reintentar("a1")
        sync.sincronizar(forzar = true)
        assertTrue(cola.acciones.value.isEmpty())

        servidor.conectado = false
        sync.registrar(accion("a2", "c9"))
        sync.descartar("a2")
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun sesionVencidaOtraCuenta_pausaSinBorrar() = runTest {
        servidor.conectado = false
        sync.registrar(accion("a1"))
        servidor.conectado = true
        servidor.sesionValida = false
        val r = sync.sincronizar(forzar = true)
        assertNotNull(r.pausada)
        assertEquals(1, cola.acciones.value.size)
        // Entra otra persona en el teléfono: no se envían acciones ajenas.
        servidor.sesionValida = true
        uid = "otra-cuenta"
        sync.sincronizar(forzar = true)
        assertEquals(1, cola.acciones.value.size)
        uid = "operario-1"
        sync.sincronizar(forzar = true)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun fotos_seSubenDespuesDelMovimientoYSeReintentanSinRepetirlo() = runTest {
        servidor.fotosConRed = false
        val r = sync.registrar(accion("s1", "salida-1", TipoAccion.SALIDA_FLOTA, listOf(FotoPendiente("f1", "file:///x/captura-1.jpg"))))
        assertTrue(r is ResultadoRegistro.EnCola)
        val pendiente = cola.acciones.value.single()
        assertTrue(pendiente.aplicada, "El movimiento ya quedó guardado")
        assertEquals(setOf("file:///x/captura-1.jpg"), cola.rutasFotos(), "La limpieza de capturas no la borra")
        servidor.fotosConRed = true
        sync.sincronizar(forzar = true)
        assertEquals(1, servidor.aplicadas.size, "No se repite la salida")
        assertEquals(listOf("s1"), servidor.fotosSubidas)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun reintentos_conEsperaCreciente() {
        assertEquals(15_000L, RelojAcciones.espera(1))
        assertEquals(30_000L, RelojAcciones.espera(2))
        assertEquals(5 * 60_000L, RelojAcciones.espera(20))
    }

    @Test
    fun noSeAceptaRepetirUnAvanceSinEnviar() = runTest {
        AccionesSinConexion.configurar(cola, sync, { uid }) {}
        servidor.conectado = false
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(TipoAccion.COMPLETAR_ETAPA, "c1", mapOf("comandaId" to "c1"), "Completar etapa 1")
        }
        val error = assertFailsWith<OperacionComandaException> {
            AccionesSinConexion.ejecutar(TipoAccion.COMPLETAR_ETAPA, "c1", mapOf("comandaId" to "c1"), "Completar etapa 1")
        }
        assertTrue(error !is AccionEnColaException)
        assertEquals(1, cola.acciones.value.size)
        // Las incidencias sí se pueden registrar varias.
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(TipoAccion.REGISTRAR_INCIDENCIA, "c1", mapOf("comandaId" to "c1", "descripcion" to null), "Incidencia")
        }
        assertEquals(2, cola.acciones.value.size)
        assertTrue("descripcion" !in cola.acciones.value.last().datos, "Los opcionales vacíos no se guardan")
    }

    @Test
    fun cacheDeLectura_soloSinConexionYPorCuenta() = runTest {
        val archivos = mutableMapOf<String, AlmacenEnMemoria>()
        CacheLectura.configurar({ nombre -> archivos.getOrPut(nombre) { AlmacenEnMemoria() } }, { archivos.clear() })
        AccionesSinConexion.configurar(cola, sync, { uid }) {}
        val serializador = ListSerializer(String.serializer())
        assertEquals(listOf("A"), CacheLectura.leer("lista", serializador) { listOf("A") })
        AccionesSinConexion.informarConexion(false)
        val copia = CacheLectura.leer("lista", serializador) { throw OperacionComandaException("No se pudo conectar con Firebase.") }
        assertEquals(listOf("A"), copia)
        assertTrue(CacheLectura.desdeCache.value)
        // Un rechazo del servidor con conexión no se tapa con la copia.
        AccionesSinConexion.informarConexion(true)
        assertFailsWith<OperacionComandaException> {
            CacheLectura.leer("lista", serializador) { throw OperacionComandaException("No autorizado") }
        }
        // Otra cuenta no ve la copia ajena; al cerrar sesión se borra.
        uid = "otra"
        AccionesSinConexion.informarConexion(false)
        assertFailsWith<OperacionComandaException> { CacheLectura.leer("lista", serializador) { throw OperacionComandaException("sin conexión") } }
        CacheLectura.limpiar()
        assertTrue(archivos.isEmpty())
        AccionesSinConexion.informarConexion(true)
    }

    @Test
    fun aperturaSinConexion_entraConLaSesionGuardada() {
        SesionPersistente.guardar(DatosSesionGuardada("op@cobre.cl", "Ana", "operario"), ahoraMs())
        val vm = CobreViewModel(
            FakeComandasRepository(), CoroutineScope(UnconfinedTestDispatcher()),
            restaurarPerfil = { PerfilResultado.Error("No se pudo validar tu sesión. Conéctate e inicia sesión nuevamente.") },
        )
        assertEquals(Pantalla.Dashboard, vm.pantalla)
        assertNotNull(vm.avisoOperacion)
    }
}
