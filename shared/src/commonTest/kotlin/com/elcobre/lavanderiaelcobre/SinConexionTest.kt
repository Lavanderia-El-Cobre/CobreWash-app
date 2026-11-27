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
import com.elcobre.lavanderiaelcobre.data.model.DisponibilidadVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.PanelReparto
import com.elcobre.lavanderiaelcobre.data.model.VehiculoReparto
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import com.elcobre.lavanderiaelcobre.ui.gestion.ahoraMs
import com.elcobre.lavanderiaelcobre.ui.gestion.salidaPendienteDesdeCola
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    var comentarioConRed = true
    var comentarioRechazado: String? = null
    var horaDisponible = true
    val fotosSubidas = mutableListOf<String>()
    val comentariosEnviados = mutableListOf<Pair<String, String>>()

    override suspend fun horaServidorMs(): Long? = if (conectado && horaDisponible) horaServidor else null

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

    override suspend fun enviarComentario(accion: AccionPendiente): ResultadoEnvio {
        val texto = accion.datos["comentario"]?.takeIf { it.isNotBlank() } ?: return ResultadoEnvio.Aplicada
        return when {
            !conectado || !comentarioConRed -> ResultadoEnvio.SinRed
            !sesionValida -> ResultadoEnvio.Sesion("Sesión vencida")
            comentarioRechazado != null -> ResultadoEnvio.Rechazada(comentarioRechazado!!)
            else -> {
                comentariosEnviados += accion.id to texto
                ResultadoEnvio.Aplicada
            }
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
    private val sync = Sincronizador(cola, servidor, reloj, uidActual = { uid })

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
        vm.limpiarAviso()
        assertNull(vm.avisoOperacion)
    }

    @Test
    fun avanceConComentario_noSePierdeSinConexionYSeReintentaSinDuplicarEtapa() = runTest {
        servidor.comentarioConRed = false
        val a = sync.nueva(
            "av1", TipoAccion.COMPLETAR_ETAPA, uid!!, "c1",
            mapOf("comandaId" to "c1", "comentario" to "Prenda delicada"), "Completar etapa 2",
        )
        val r = sync.registrar(a)
        assertTrue(r is ResultadoRegistro.EnCola)
        val pendiente = cola.acciones.value.single()
        assertTrue(pendiente.aplicada, "La etapa quedó aplicada")
        assertTrue(!pendiente.comentarioAplicado, "El comentario sigue pendiente")
        servidor.comentarioConRed = true
        sync.sincronizar(forzar = true)
        assertEquals(1, servidor.aplicadas.size, "No se duplica la etapa")
        assertEquals(listOf("av1" to "Prenda delicada"), servidor.comentariosEnviados)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun horaServidorCaidaConRedValidada_continuaConRelojDudoso() = runTest {
        servidor.horaDisponible = false
        val r = sync.registrar(accion("h1"))
        assertEquals(ResultadoRegistro.Enviada, r)
        assertTrue(servidor.aplicadas.getValue("h1").relojDudoso)
    }

    @Test
    fun reintentarAhora_enviaDeInmediatoYEmiteEventoSincronizacion() = runTest {
        AccionesSinConexion.configurar(cola, sync, { uid }) {}
        AccionesSinConexion.informarConexion(true)
        servidor.conectado = false
        sync.registrar(accion("r1"))
        servidor.conectado = true
        servidor.rechazar["c1"] = "Conflicto temporal"
        sync.sincronizar(forzar = true)
        assertEquals(EstadoAccion.REQUIERE_ATENCION, cola.acciones.value.single().estado)

        servidor.rechazar.clear()
        var recibido: Int? = null
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            recibido = AccionesSinConexion.sincronizaciones.first().enviadas
        }
        val resumen = AccionesSinConexion.reintentarAhora("r1")
        job.join()
        assertEquals(1, resumen?.enviadas)
        assertEquals(1, recibido)
        assertTrue(cola.acciones.value.isEmpty())
    }

    @Test
    fun salidaYRegresoSinConexion_seEncadenanYAdminPuedeDespacharDosVehiculos() = runTest {
        AccionesSinConexion.configurar(cola, sync, { uid }) {}
        servidor.conectado = false
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(
                TipoAccion.SALIDA_FLOTA, "sal-1",
                mapOf("vehiculoId" to "v1", "repartidorId" to "r1", "kilometraje" to "12000", "estadoVehiculo" to "APTO"),
                "Salida 1",
            )
        }
        val panel = PanelReparto(
            vehiculos = listOf(VehiculoReparto("v1", "HLRT-24", "Suzuki APV", null, DisponibilidadVehiculo.DISPONIBLE, 11900, null)),
        )
        val proyectada = salidaPendienteDesdeCola(cola.acciones.value, panel)
        assertNotNull(proyectada)
        assertEquals("sal-1", proyectada.id)
        assertEquals(EstadoSalida.EN_SERVICIO, proyectada.estado)
        assertEquals("HLRT-24", proyectada.patente)

        // Permite registrar el regreso sobre la misma salida sin conexión.
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(TipoAccion.RETORNO_FLOTA, "sal-1", mapOf("salidaId" to "sal-1"), "Regreso 1")
        }

        // En modo admin se permiten salidas simultáneas para vehículos y conductores distintos.
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(
                TipoAccion.SALIDA_FLOTA, "sal-2",
                mapOf("vehiculoId" to "v2", "repartidorId" to "r2", "modoAdmin" to "true"), "Salida admin 1",
            )
        }
        assertFailsWith<AccionEnColaException> {
            AccionesSinConexion.ejecutar(
                TipoAccion.SALIDA_FLOTA, "sal-3",
                mapOf("vehiculoId" to "v3", "repartidorId" to "r3", "modoAdmin" to "true"), "Salida admin 2",
            )
        }
        // Pero bloquea duplicar el mismo vehículo.
        val dup = assertFailsWith<OperacionComandaException> {
            AccionesSinConexion.ejecutar(
                TipoAccion.SALIDA_FLOTA, "sal-4",
                mapOf("vehiculoId" to "v2", "repartidorId" to "r4", "modoAdmin" to "true"), "Salida duplicada",
            )
        }
        assertTrue(dup !is AccionEnColaException)
    }

    @Test
    fun rechazoEnComentarioPosterior_muestraMotivoRealYNoMensajeSinRed() = runTest {
        servidor.comentarioRechazado = "Comentario excede el límite permitido"
        val a = sync.nueva(
            "av-rej", TipoAccion.COMPLETAR_ETAPA, uid!!, "c1",
            mapOf("comandaId" to "c1", "comentario" to "Texto largo"), "Completar etapa",
        )
        val r = sync.registrar(a)
        assertTrue(r is ResultadoRegistro.EnCola)
        assertTrue(r.motivo.contains("Comentario excede el límite permitido"), "Debe informar el rechazo real: ${r.motivo}")
        assertEquals(EstadoAccion.REQUIERE_ATENCION, cola.acciones.value.single().estado)
    }

    @Test
    fun cacheDeLectura_noEscribeTrasLimpiarEnVuelo() = runTest {
        val archivos = mutableMapOf<String, AlmacenEnMemoria>()
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        CacheLectura.configurar({ nombre -> archivos.getOrPut(nombre) { AlmacenEnMemoria() } }, { archivos.clear() }, dispatcher)
        AccionesSinConexion.configurar(cola, sync, { uid }) {}
        val serializador = ListSerializer(String.serializer())
        val valor = CacheLectura.leer("lista", serializador) {
            CacheLectura.limpiar()
            listOf("Sensible")
        }
        assertEquals(listOf("Sensible"), valor)
        assertTrue(archivos.values.all { it.leer() == null }, "No debe persistir caché de una sesión cerrada en vuelo")
    }
}
