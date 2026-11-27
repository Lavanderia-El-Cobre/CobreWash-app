@file:Suppress("MaxLineLength") // Casos de prueba en una línea, como las pruebas de la web.

package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.AdministracionRepository
import com.elcobre.lavanderiaelcobre.data.EdicionUsuarioInput
import com.elcobre.lavanderiaelcobre.data.IdentidadComanda
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.RecepcionRepository
import com.elcobre.lavanderiaelcobre.data.RepartoRepository
import com.elcobre.lavanderiaelcobre.data.RetornoFlotaInput
import com.elcobre.lavanderiaelcobre.data.SalidaFlotaInput
import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaEntrega
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.DisponibilidadVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EntregasReparto
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaComandas
import com.elcobre.lavanderiaelcobre.data.model.PanelReparto
import com.elcobre.lavanderiaelcobre.data.model.ProblemaEntrega
import com.elcobre.lavanderiaelcobre.data.model.RolRef
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.TipoServicioCatalogo
import com.elcobre.lavanderiaelcobre.data.model.UsuarioGestion
import com.elcobre.lavanderiaelcobre.data.model.VehiculoReparto
import com.elcobre.lavanderiaelcobre.ui.components.GUIAS_FOTOS_VEHICULO
import com.elcobre.lavanderiaelcobre.ui.components.siguienteGuiaFoto
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.EntregasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.MovimientoInput
import com.elcobre.lavanderiaelcobre.ui.gestion.RepartoViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.UsuariosViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/** Recepción de comandas y recorridos del repartidor, con repositorios falsos (sin Firebase). */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GestionViewModelsTest {

    private class RecepcionFalsa : RecepcionRepository {
        val creadas = mutableListOf<IdentidadComanda>()
        val clientesCreados = mutableListOf<NuevoClienteInput>()
        val ediciones = mutableListOf<String>()
        var fallosCrear = 0
        var fallarDetalle = false
        var errorEntregar: String? = null
        val comandasGuardadas = mutableListOf(
            ComandaGestion("k1", "ELCOBRE-AB12", EstadoComanda.PENDIENTE, ClienteFicha("c1", "Ana"), 3000.0, "", "10.0"),
            ComandaGestion("k2", "ELCOBRE-CD34", EstadoComanda.FINALIZADA, ClienteFicha("c2", "Hotel"), 9000.0, "", "11.0"),
        )

        override suspend fun comandas(filtro: FiltroComandas, offset: Int, limit: Int): PaginaComandas {
            val lista = comandasGuardadas.filter { filtro.estados.isEmpty() || it.estado in filtro.estados }
            return PaginaComandas(lista.drop(offset).take(limit), lista.size, mapOf(EstadoComanda.PENDIENTE to 1))
        }

        override suspend fun detalle(id: String): ComandaGestion? {
            if (fallarDetalle) throw OperacionComandaException("Timeout leyendo detalle")
            return comandasGuardadas.firstOrNull { it.id == id }
        }

        override suspend fun entregar(id: String) {
            errorEntregar?.let { throw OperacionComandaException(it) }
        }

        override suspend fun catalogos() = CatalogosComanda(servicios = listOf(TipoServicioCatalogo("s1", "Lavado", 1200.0)))

        override suspend fun crearCliente(input: NuevoClienteInput): String {
            clientesCreados += input
            return "nuevo-${clientesCreados.size}"
        }

        override suspend fun crearComanda(identidad: IdentidadComanda, formulario: FormularioComanda): String {
            creadas += identidad
            if (fallosCrear-- > 0) throw OperacionComandaException("Sin conexión")
            comandasGuardadas += ComandaGestion(
                identidad.id,
                identidad.numero,
                EstadoComanda.PENDIENTE,
                ClienteFicha(formulario.clienteId, ""),
                0.0,
                "",
                "1.0",
            )
            return identidad.id
        }

        override suspend fun editarComanda(id: String, version: String, formulario: FormularioComanda) {
            ediciones += "$id@$version"
        }
    }

    private fun scope() = CoroutineScope(UnconfinedTestDispatcher())

    private val formularioNuevoCliente = FormularioComanda(
        nuevoCliente = true,
        cliente = NuevoClienteInput("Hotel Cobre", TipoClienteFicha.HOTEL, "12.345.678-5"),
        lineas = listOf(LineaFormulario(prenda = "TOALLA BAÑO", cantidad = 2, precioUnitario = 800.0)),
        pesoKg = "4,5",
    )

    @Test
    fun crearComanda_conClienteNuevo_reintentaSinDuplicarFichaNiNumero() {
        val repo = RecepcionFalsa().apply { fallosCrear = 1 }
        var ids = 0
        val vm = ComandasViewModel(repo, scope(), idNuevo = { "uuid-${++ids}" })
        vm.abrirNueva()
        vm.actualizarFormulario(formularioNuevoCliente)

        vm.guardar()
        assertEquals("Sin conexión", vm.errorFormulario)
        assertEquals(1, repo.clientesCreados.size)
        // Tras el fallo, el formulario apunta a la ficha creada para no volver a crearla.
        assertEquals("nuevo-1", vm.formulario?.clienteId)

        vm.guardar()
        assertEquals(1, repo.clientesCreados.size)
        assertEquals(2, repo.creadas.size)
        assertEquals(repo.creadas[0], repo.creadas[1])
        assertNull(vm.formulario)
        assertTrue(vm.mensaje!!.contains(repo.creadas[0].numero))
        assertEquals("uuid-1", vm.detalle?.id)
    }

    @Test
    fun guardar_conFormularioInvalido_noLlamaAlServidor() {
        val repo = RecepcionFalsa()
        val vm = ComandasViewModel(repo, scope())
        vm.abrirNueva()
        vm.guardar()
        assertNotNull(vm.errorFormulario)
        assertTrue(repo.creadas.isEmpty())
    }

    @Test
    fun editar_enviaLaVersionLeidaYSoloParaPendientes() {
        val repo = RecepcionFalsa()
        val vm = ComandasViewModel(repo, scope())
        vm.refrescar()
        vm.abrirEdicion(repo.comandasGuardadas[1])
        assertNull(vm.formulario, "Una comanda lista no se edita")

        vm.abrirEdicion(repo.comandasGuardadas[0])
        vm.actualizarFormulario(
            vm.formulario!!.copy(lineas = listOf(LineaFormulario(prenda = "CAMISA", precioUnitario = 1000.0)), pesoKg = "1,2"),
        )
        vm.guardar()
        assertEquals(listOf("k1@10.0"), repo.ediciones)
        assertNull(vm.formulario)
    }

    @Test
    fun filtroPorEstado_alternaYRecarga() {
        val vm = ComandasViewModel(RecepcionFalsa(), scope())
        vm.refrescar()
        assertEquals(2, vm.comandas.size)
        vm.alternarEstado(EstadoComanda.FINALIZADA)
        assertEquals(listOf("k2"), vm.comandas.map { it.id })
        vm.alternarEstado(EstadoComanda.FINALIZADA)
        assertEquals(2, vm.comandas.size)
    }

    @Test
    fun agregarLinea_usaElPrecioBaseDelPrimerServicio() {
        val vm = ComandasViewModel(RecepcionFalsa(), scope())
        vm.abrirNueva()
        vm.agregarLinea()
        val linea = vm.formulario!!.lineas.last()
        assertEquals("Lavado", linea.servicio)
        assertEquals(1200.0, linea.precioUnitario)
        assertEquals(1200.0, vm.precioBase("LAVADO"))
    }

    @Test
    fun vincularSesion_otraCuentaBorraLoCargado() {
        val vm = ComandasViewModel(RecepcionFalsa(), scope())
        val primera = Any()
        vm.vincularSesion(primera)
        vm.refrescar()
        vm.abrirDetalle("k1")
        assertEquals(2, vm.comandas.size)

        vm.vincularSesion(primera)
        assertEquals(2, vm.comandas.size, "La misma sesión conserva los datos")

        vm.vincularSesion(Any())
        assertTrue(vm.comandas.isEmpty())
        assertNull(vm.detalle)
    }

    private class RepartoFalso(
        val salidas: List<SalidaReparto>,
        val vehiculos: List<VehiculoReparto> = emptyList(),
    ) : RepartoRepository {
        val iniciadas = mutableListOf<InspeccionInput>()
        val salidasRegistradas = mutableListOf<SalidaFlotaInput>()
        val retornos = mutableListOf<RetornoFlotaInput>()
        override suspend fun panel() = PanelReparto(vehiculos, salidas)
        override suspend fun iniciarProgramada(salida: SalidaReparto, input: InspeccionInput) {
            iniciadas += input
        }
        override suspend fun registrarSalida(input: SalidaFlotaInput): String {
            salidasRegistradas += input
            return "nueva"
        }
        override suspend fun registrarRetorno(input: RetornoFlotaInput) {
            retornos += input
        }
    }

    private fun inspeccion(km: Int) = InspeccionReparto("i$km", EstadoCondicionVehiculo.APTO, km, null, "", 0)

    private val salidas = listOf(
        SalidaReparto("s1", EstadoSalida.FINALIZADA, "v1", "HLRT24", "Fiat", "", null, null, null, inspeccion(1000), inspeccion(1250)),
        SalidaReparto("s2", EstadoSalida.PROGRAMADA, "v1", "HLRT24", "Fiat", "", null, null, null, null, null),
        SalidaReparto("s3", EstadoSalida.EN_SERVICIO, "v2", "AB1234", "Peugeot", "", null, null, null, inspeccion(500), null),
    )

    @Test
    fun reparto_agrupaYCalculaKilometrajes() {
        val vm = RepartoViewModel(RepartoFalso(salidas), scope())
        vm.refrescar()
        assertEquals("s3", vm.salidaEnCurso?.id)
        assertEquals(listOf("s2"), vm.programadas.map { it.id })
        assertEquals(listOf("s1"), vm.historial.map { it.id })
        assertEquals(250, vm.kilometrosRecorridos)
        assertEquals(1250, vm.kilometrajeMinimo(salidas[1]))
        assertEquals(500, vm.kilometrajeMinimo(salidas[2]))
    }

    @Test
    fun reparto_validaAntesDeEnviar() {
        val repo = RepartoFalso(salidas)
        val vm = RepartoViewModel(repo, scope())
        vm.refrescar()
        var error: String? = null
        vm.iniciarProgramada(salidas[1], InspeccionInput("s2", 1100, EstadoCondicionVehiculo.APTO, null, emptyList())) { error = it }
        assertNotNull(error)
        vm.iniciarProgramada(salidas[1], InspeccionInput("s2", 1300, EstadoCondicionVehiculo.NO_APTO, null, emptyList())) { error = it }
        assertNotNull(error)
        assertTrue(repo.iniciadas.isEmpty())

        vm.iniciarProgramada(salidas[1], InspeccionInput("s2", 1300, EstadoCondicionVehiculo.APTO, null, emptyList())) { error = it }
        assertNull(error)
        assertEquals(1, repo.iniciadas.size)

        // Con observaciones hay que describir lo que se vio, como la web.
        vm.registrarRetorno(salidas[2], MovimientoInput(650, EstadoCondicionVehiculo.CON_OBSERVACIONES, "", emptyList())) { error = it }
        assertEquals("Describe lo que observaste en el vehículo.", error)
        vm.registrarRetorno(salidas[2], MovimientoInput(400, EstadoCondicionVehiculo.APTO, "", emptyList())) { error = it }
        assertNotNull(error, "El regreso no puede tener menos kilómetros que la salida")
        vm.registrarRetorno(salidas[2], MovimientoInput(650, EstadoCondicionVehiculo.CON_OBSERVACIONES, "Rayón", emptyList())) { error = it }
        assertNull(error)
        assertEquals(650, repo.retornos.single().kilometraje)
        assertEquals("s3", repo.retornos.single().salidaId)
    }

    @Test
    fun reparto_salidaPropiaSoloConVehiculoDisponibleYSinOtraEnCurso() {
        val disponible = VehiculoReparto("v9", "KJTR55", "Kia", null, DisponibilidadVehiculo.DISPONIBLE, 800, null)
        val conEnCurso = RepartoFalso(salidas, listOf(disponible))
        val vm = RepartoViewModel(conEnCurso, scope())
        vm.refrescar()
        var error: String? = null
        vm.registrarSalida(disponible, MovimientoInput(900, EstadoCondicionVehiculo.APTO, "", emptyList())) { error = it }
        assertNotNull(error, "Con una salida en curso no se saca otro vehículo")

        val libre = RepartoFalso(salidas.filter { it.estado != EstadoSalida.EN_SERVICIO }, listOf(disponible))
        val vm2 = RepartoViewModel(libre, scope())
        vm2.refrescar()
        vm2.registrarSalida(disponible, MovimientoInput(700, EstadoCondicionVehiculo.APTO, "", emptyList())) { error = it }
        assertNotNull(error, "El odómetro no puede retroceder")
        vm2.registrarSalida(disponible, MovimientoInput(900, EstadoCondicionVehiculo.NO_APTO, "Freno", emptyList())) { error = it }
        assertNotNull(error, "Un vehículo no apto no sale")
        vm2.registrarSalida(disponible, MovimientoInput(900, EstadoCondicionVehiculo.APTO, "", emptyList())) { error = it }
        assertNull(error)
        assertEquals("v9", libre.salidasRegistradas.single().vehiculoId)
        assertEquals(900, libre.salidasRegistradas.single().kilometraje)
    }

    private class EntregasFalsas(val datos: EntregasReparto) : RepartoRepository {
        val entregadas = mutableListOf<Pair<String, String?>>()
        val problemas = mutableListOf<Triple<String, String, String?>>()
        override suspend fun entregas() = datos
        override suspend fun entregar(comandaId: String, detalle: String?) {
            entregadas += comandaId to detalle
        }
        override suspend fun reportarProblema(comandaId: String, motivo: String, detalle: String?) {
            problemas += Triple(comandaId, motivo, detalle)
        }
    }

    private val comandaLista = ComandaEntrega(
        "c1", "ELCOBRE-A1B2", "Hotel Pacífico", TipoClienteFicha.HOTEL, null, "Av. Grecia 123", null, null, null, null,
        "2 prendas · TOALLA ×2",
        listOf(ProblemaEntrega("p1", "Entrega: Cliente ausente", null, EstadoIncidencia.ABIERTA, "", "Juan")),
    )

    @Test
    fun entregas_buscanEntregaYReportanProblemas() {
        val repo = EntregasFalsas(EntregasReparto(listOf(comandaLista)))
        val vm = EntregasViewModel(repo, scope())
        vm.refrescar()
        assertEquals(1, vm.conProblemas)
        vm.buscar("grecia")
        assertEquals(1, vm.comandas.size)
        vm.buscar("ELCOBRE-ZZZZ")
        assertTrue(vm.comandas.isEmpty())

        var error: String? = null
        vm.entregar(comandaLista, "  Recibió   conserjería ") { error = it }
        assertNull(error)
        assertEquals("c1" to "Recibió conserjería", repo.entregadas.single())

        vm.reportarProblema(comandaLista, "Otro", "  ") { error = it }
        assertEquals("Describe el problema de la entrega.", error)
        vm.reportarProblema(comandaLista, "Inventado", "") { error = it }
        assertEquals("Selecciona el motivo del problema.", error)
        vm.reportarProblema(comandaLista, "Cliente ausente", "") { error = it }
        assertNull(error)
        assertEquals(Triple("c1", "Cliente ausente", null), repo.problemas.single())
    }

    @Test
    fun crearComanda_siDetalleFalla_cierraFormularioYNoDuplica() {
        val repo = RecepcionFalsa().apply { fallarDetalle = true }
        val vm = ComandasViewModel(repo, scope(), idNuevo = { "uuid-det" })
        vm.abrirNueva()
        vm.actualizarFormulario(formularioNuevoCliente)

        vm.guardar()
        assertNull(vm.formulario, "Debe cerrar el formulario aunque falle la recarga del comprobante")
        assertNull(vm.detalle)
        assertEquals(1, repo.creadas.size)
        assertTrue(vm.mensaje!!.contains("No se pudo cargar su comprobante"))
    }

    @Test
    fun entregarComanda_siFalla_exponeErrorEnViewModel() {
        val repo = RecepcionFalsa().apply { errorEntregar = "No autorizado para entregar" }
        val vm = ComandasViewModel(repo, scope())
        val finalizada = repo.comandasGuardadas[1]
        var callbackError: String? = null
        vm.entregar(finalizada) { callbackError = it }
        assertEquals("No autorizado para entregar", callbackError)
        assertEquals("No autorizado para entregar", vm.error)
    }

    @Test
    fun usuarios_alternarActivoSiFalla_preservaErrorTrasRefrescar() {
        val rol = RolRef("r1", "operario", "Operario")
        val usuario = UsuarioGestion("u1", "Ana", "Diaz", "ana@cobre.cl", null, null, activo = true, rol = rol)
        val repo = object : AdministracionRepository {
            override suspend fun roles() = listOf(rol)
            override suspend fun usuarios() = listOf(usuario)
            override suspend fun actualizarUsuario(input: EdicionUsuarioInput) {
                throw OperacionComandaException("No puedes desactivar tu propia cuenta")
            }
        }
        val vm = UsuariosViewModel(repo, scope())
        vm.refrescar()
        vm.alternarActivo(usuario)
        assertEquals("No puedes desactivar tu propia cuenta", vm.error)
    }

    @Test
    fun guiaFotosVehiculo_empiezaPorFrenteYReproponeFaltantes() {
        val primera = siguienteGuiaFoto(emptyList())
        assertEquals("Frente", primera.etiqueta)
        assertEquals("Frente del vehículo", primera.tituloCamara)
        assertEquals(1, primera.paso)

        // Si se tomó Frente y Lateral izquierdo (descartando Lateral derecho), propone Lateral derecho.
        val parciales = listOf(
            FotoChequeo("f1", "/tmp/1.jpg", "Frente", 1),
            FotoChequeo("f2", "/tmp/2.jpg", "Lateral izquierdo", 2),
        )
        val faltante = siguienteGuiaFoto(parciales)
        assertEquals("Lateral derecho", faltante.etiqueta)
        assertEquals("Lateral derecho del vehículo", faltante.tituloCamara)
        assertEquals(2, faltante.paso)

        // Tras cubrir las 5 guías, propone evidencia adicional numerada.
        val completas = GUIAS_FOTOS_VEHICULO.mapIndexed { idx, guia ->
            FotoChequeo("f$idx", "/tmp/$idx.jpg", guia.etiqueta, idx + 1)
        }
        val extra = siguienteGuiaFoto(completas)
        assertEquals("Evidencia 6", extra.etiqueta)
        assertEquals("Evidencia adicional #6", extra.tituloCamara)
        assertNull(extra.paso)
    }
}
