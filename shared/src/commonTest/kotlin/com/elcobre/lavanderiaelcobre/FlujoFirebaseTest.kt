package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.estadoTrasCompletar
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerCodigoQr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class FlujoFirebaseTest {
    private val sesion = Sesion.DeOperario(Operario("Test", "Operario"))

    @BeforeTest fun setUp() { SesionPersistente.limpiar() }
    @AfterTest fun tearDown() { SesionPersistente.limpiar() }

    @Test fun cerrarSesion_desconectaYBorraDatos() = runTest {
        val repo = FakeComandasRepository()
        val vm = CobreViewModel(repo, backgroundScope)
        vm.iniciarSesion(sesion)
        testScheduler.runCurrent()
        assertTrue(vm.pedidos.isNotEmpty())
        vm.cerrarSesion()
        assertTrue(repo.desconectado)
        assertTrue(vm.pedidos.isEmpty())
    }

    @Test fun asignacionFallida_noAbreDetalle() = runTest {
        val repo = FakeComandasRepository()
        val vm = CobreViewModel(repo, this)
        vm.iniciarSesion(sesion)
        advanceUntilIdle()
        repo.falla = true
        var abierto = false
        var error = false
        vm.procesarQrEscaneado("P001", { abierto = true }, { error = true })
        advanceUntilIdle()
        assertTrue(error)
        assertTrue(!abierto)
        assertNotNull(vm.errorOperacion)
        assertTrue(!vm.cargando)
    }

    @Test fun avanceFallido_conservaLaEtapa() = runTest {
        val repo = FakeComandasRepository()
        val vm = CobreViewModel(repo, this)
        vm.iniciarSesion(sesion)
        advanceUntilIdle()
        val antes = vm.pedido("P001")!!.etapaActual
        repo.falla = true
        vm.avanzarEtapa("P001", "")
        advanceUntilIdle()
        assertEquals(antes, vm.pedido("P001")!!.etapaActual)
        assertNotNull(vm.errorOperacion)
    }

    @Test fun estadosResultantes_correspondenAlContratoFirebase() {
        assertEquals("EN_PROCESO", estadoTrasCompletar(1))
        assertEquals("EN_PROCESO", estadoTrasCompletar(3))
        assertEquals("FINALIZADA", estadoTrasCompletar(4))
        assertEquals("ENTREGADA", estadoTrasCompletar(5))
    }

    @Test fun escaneoExitoso_cargaDatosAntesDeAbrir() = runTest {
        val repo = FakeComandasRepository()
        val vm = CobreViewModel(repo, this)
        vm.iniciarSesion(sesion)
        advanceUntilIdle()
        var destino: String? = null
        vm.procesarQrEscaneado("P001", { destino = it }, { error("No debe fallar") })
        advanceUntilIdle()
        assertEquals("P001", destino)
        assertEquals(1, repo.escaneos)
        assertEquals(repo.detalle("P001"), vm.pedido("P001"))
    }

    @Test fun entregaPendiente_permiteCerrarLaComanda() {
        val pedido = pedidosFixturePrueba().first().copy(
            etapaActual = com.elcobre.lavanderiaelcobre.data.model.EtapaProceso.ENTREGA,
            estadoComandaDb = "FINALIZADA", etapaIdDb = "etapa-entrega", ordenEtapaDb = 5,
        )
        assertTrue(!pedido.esFinal)
        assertTrue(pedido.puedeCompletar)
        assertTrue(pedido.copy(estadoComandaDb = "ENTREGADA").esFinal)
        assertTrue(!pedido.copy(estadoComandaDb = "ENTREGADA").puedeCompletar)
    }

    @Test fun qr_rechazaDominiosYParametrosManipulados() {
        val codigo = "00000000-0000-4000-8000-000000000010"
        val extraer = ::extraerCodigoQr
        assertEquals(codigo, extraer("https://www.lavanderiaelcobrespa.com/seguimiento?qr=$codigo"))
        assertEquals(codigo, extraer("https://lavanderiaelcobrespa.com/seguimiento?qr=$codigo"))
        // Comprobantes impresos antes del cambio de dominio siguen abriendo su comanda.
        assertEquals(codigo, extraer("https://lavanderia-elcobre.vercel.app/seguimiento?qr=$codigo"))
        assertEquals(null, extraer("http://www.lavanderiaelcobrespa.com/seguimiento?qr=$codigo"))
        assertEquals(null, extraer("https://www.lavanderiaelcobrespa.com.evil.test/seguimiento?qr=$codigo"))
        assertEquals(null, extraer("https://www.lavanderiaelcobrespa.com/seguimiento?qr=$codigo&qr=$codigo"))
        assertEquals(null, extraer("http://lavanderia-elcobre.vercel.app/seguimiento?qr=$codigo"))
        assertEquals(null, extraer("https://otro.test/seguimiento?qr=$codigo"))
        assertEquals(null, extraer("https://lavanderia-elcobre.vercel.app/seguimiento?qr=$codigo&qr=$codigo"))
    }
}
