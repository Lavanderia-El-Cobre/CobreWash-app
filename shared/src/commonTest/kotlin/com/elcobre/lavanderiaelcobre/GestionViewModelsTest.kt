package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.IdentidadComanda
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.RecepcionRepository
import com.elcobre.lavanderiaelcobre.data.RepartoRepository
import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaComandas
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.TipoServicioCatalogo
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.RepartoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Recepción de comandas y recorridos del repartidor, con repositorios falsos (sin Firebase). */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GestionViewModelsTest {

    private class RecepcionFalsa : RecepcionRepository {
        val creadas = mutableListOf<IdentidadComanda>()
        val clientesCreados = mutableListOf<NuevoClienteInput>()
        val ediciones = mutableListOf<String>()
        var fallosCrear = 0
        val comandasGuardadas = mutableListOf(
            ComandaGestion("k1", "ELCOBRE-AB12", EstadoComanda.PENDIENTE, ClienteFicha("c1", "Ana"), 3000.0, "", "10.0"),
            ComandaGestion("k2", "ELCOBRE-CD34", EstadoComanda.FINALIZADA, ClienteFicha("c2", "Hotel"), 9000.0, "", "11.0"),
        )

        override suspend fun comandas(filtro: FiltroComandas, offset: Int, limit: Int): PaginaComandas {
            val lista = comandasGuardadas.filter { filtro.estados.isEmpty() || it.estado in filtro.estados }
            return PaginaComandas(lista.drop(offset).take(limit), lista.size, mapOf(EstadoComanda.PENDIENTE to 1))
        }

        override suspend fun detalle(id: String): ComandaGestion? = comandasGuardadas.firstOrNull { it.id == id }

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
        vm.actualizarFormulario(vm.formulario!!.copy(lineas = listOf(LineaFormulario(prenda = "CAMISA", precioUnitario = 1000.0))))
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

    private class RepartoFalso(val salidas: List<SalidaReparto>) : RepartoRepository {
        val iniciadas = mutableListOf<InspeccionInput>()
        val retornos = mutableListOf<InspeccionInput>()
        override suspend fun misSalidas() = salidas
        override suspend fun vehiculos() = emptyList<com.elcobre.lavanderiaelcobre.data.model.Vehiculo>()
        override suspend fun iniciarSalida(salida: SalidaReparto, input: InspeccionInput) {
            iniciadas += input
        }
        override suspend fun registrarRetorno(input: InspeccionInput) {
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
        assertEquals(listOf("s3"), vm.enRuta.map { it.id })
        assertEquals(listOf("s2"), vm.programadas.map { it.id })
        assertEquals(listOf("s1"), vm.bitacora.map { it.id })
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
        vm.iniciar(salidas[1], InspeccionInput("s2", 1100, EstadoCondicionVehiculo.APTO, null, emptyList())) { error = it }
        assertNotNull(error)
        vm.iniciar(salidas[1], InspeccionInput("s2", 1300, EstadoCondicionVehiculo.NO_APTO, null, emptyList())) { error = it }
        assertNotNull(error)
        assertTrue(repo.iniciadas.isEmpty())

        vm.iniciar(salidas[1], InspeccionInput("s2", 1300, EstadoCondicionVehiculo.APTO, null, emptyList())) { error = it }
        assertNull(error)
        assertEquals(1, repo.iniciadas.size)

        vm.registrarRetorno(
            salidas[2],
            InspeccionInput("s3", 650, EstadoCondicionVehiculo.CON_OBSERVACIONES, "Rayón", emptyList()),
        ) { error = it }
        assertNull(error)
        assertEquals(650, repo.retornos.single().kilometraje)
    }
}
