package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.ConteoFisico
import com.elcobre.lavanderiaelcobre.data.EstadoRevisionConteo
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.data.InventarioRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.ui.InventarioState
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerCodigoQr
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerQrInsumo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioTest {
    private val codigo = "00000000-0000-4000-8000-000000000099"
    private val item = InsumoInventario("id", codigo, "Nuevo producto", "kg", 20.0, 10.0, true)
    private class Fake(private val item: InsumoInventario) : InventarioRepository {
        var falla = false
        var existe = true
        override suspend fun catalogo(): List<InsumoInventario> {
            if (falla) throw OperacionComandaException("Sin conexión")
            return listOf(item, item, item.copy(id = "inactivo", activo = false))
        }
        override suspend fun porQr(codigo: String): InsumoInventario? {
            if (falla) throw OperacionComandaException("Sin conexión")
            return if (existe && codigo == item.codigoQr) item else null
        }
    }

    @Test fun umbralesSonLosMismosQueLaWeb() {
        assertEquals("OK", item.estado)
        assertEquals("Bajo", item.copy(stockActual = 10.0).estado)
        assertEquals("Crítico", item.copy(stockActual = 5.0).estado)
        assertEquals("OK", item.copy(stockActual = 0.0, stockMinimo = 0.0).estado)
        assertEquals("Inactivo", item.copy(activo = false).estado)
    }

    @Test fun distingueQrDeInsumoYComandaSinAceptarUrlsAjenas() {
        assertEquals(codigo, extraerQrInsumo(codigo))
        assertNull(extraerQrInsumo("https://otro.com/?qr=$codigo"))
        assertNull(extraerQrInsumo("https://www.lavanderiaelcobrespa.com/seguimiento?qr=$codigo"))
        assertNull(extraerCodigoQr(codigo))
        assertNull(extraerQrInsumo("no-es-uuid"))
    }

    @Test fun catalogoRealExcluyeInactivosYDuplicados() = runTest {
        val state = InventarioState(backgroundScope, Fake(item))
        state.cambiarSesion(true)
        state.refrescar()
        testScheduler.runCurrent()
        assertEquals(listOf(item), state.insumos)
        assertFalse(state.cargando)
    }

    @Test fun qrConsultaFirebaseYSeleccionaSinModificarStock() = runTest {
        val state = InventarioState(backgroundScope, Fake(item))
        state.cambiarSesion(true)
        var llamada = false
        state.escanear(codigo) { assertNull(it); llamada = true }
        testScheduler.runCurrent()
        assertTrue(llamada)
        assertEquals(item, state.seleccionado)
        assertEquals(20.0, state.insumos.single().stockActual)
        assertFalse(state.buscando)
    }

    @Test fun qrInexistenteNoInventaInsumo() = runTest {
        val repo = Fake(item).apply { existe = false }
        val state = InventarioState(backgroundScope, repo)
        state.cambiarSesion(true)
        state.escanear(codigo) { assertNotNull(it) }
        testScheduler.runCurrent()
        assertNull(state.seleccionado)
        assertTrue(state.insumos.isEmpty())
    }

    @Test fun errorConservaDatosYReintentoSeRecupera() = runTest {
        val repo = Fake(item)
        val state = InventarioState(backgroundScope, repo)
        state.cambiarSesion(true)
        state.refrescar()
        testScheduler.runCurrent()
        repo.falla = true
        state.refrescar()
        testScheduler.runCurrent()
        assertEquals(listOf(item), state.insumos)
        assertNotNull(state.error)
        repo.falla = false
        state.refrescar()
        testScheduler.runCurrent()
        assertNull(state.error)
        state.cambiarSesion(false)
        assertTrue(state.insumos.isEmpty())
        assertNull(state.seleccionado)
    }

    @Test fun respuestasTardiasDeOtraSesionNoReaparecen() = runTest {
        val respuesta = CompletableDeferred<List<InsumoInventario>>()
        val repo = object : InventarioRepository {
            override suspend fun catalogo() = withContext(NonCancellable) { respuesta.await() }
            override suspend fun porQr(codigo: String) = item
        }
        val state = InventarioState(backgroundScope, repo)
        state.cambiarSesion(true)
        state.refrescar()
        testScheduler.runCurrent()
        state.cambiarSesion(false)
        respuesta.complete(listOf(item))
        testScheduler.runCurrent()
        assertTrue(state.insumos.isEmpty())
        assertFalse(state.cargando)
    }

    @Test fun sinSesionNoCargaCatalogoNiEscanea() = runTest {
        val state = InventarioState(backgroundScope, Fake(item))
        state.refrescar()
        state.escanear(codigo) { error("No debería consultar sin sesión") }
        testScheduler.runCurrent()
        assertTrue(state.insumos.isEmpty())
        assertNull(state.seleccionado)
    }

    @Test fun conteoFisicoSeEnviaPendienteSinAlterarStockSistema() = runTest {
        val enviados = mutableListOf<Pair<String, Double>>()
        val repo = object : InventarioRepository {
            override suspend fun catalogo() = listOf(item)
            override suspend fun porQr(codigo: String) = item
            override suspend fun misConteos() = enviados.reversed().map { (id, cantidad) ->
                ConteoFisico(id, cantidad, item.stockActual, id = "c-$cantidad", estado = EstadoRevisionConteo.PENDIENTE)
            }
            override suspend fun registrarConteo(insumo: InsumoInventario, cantidad: Double) {
                if (cantidad == 99.0) throw OperacionComandaException("El stock cambió. Actualiza el insumo y vuelve a contar.")
                enviados += insumo.id to cantidad
            }
        }
        val state = InventarioState(backgroundScope, repo)
        state.cambiarSesion(true)
        state.refrescar()
        testScheduler.runCurrent()

        var resultado: String? = "sin respuesta"
        assertTrue(state.registrarConteoFisico(item.id, 17.5) { resultado = it })
        testScheduler.runCurrent()
        assertNull(resultado)
        assertEquals(listOf(item.id to 17.5), enviados)
        val faltante = state.conteoDe(item.id)
        assertNotNull(faltante)
        assertEquals(-2.5, faltante.diferencia)
        assertEquals("Faltante", faltante.estadoConteo)
        assertEquals(EstadoRevisionConteo.PENDIENTE, faltante.estado)
        assertEquals(20.0, state.insumos.single().stockActual)
        assertFalse(state.enviandoConteo)

        assertTrue(state.registrarConteoFisico(item.id, 99.0) { resultado = it })
        testScheduler.runCurrent()
        assertNotNull(resultado)
        assertNotNull(state.error)

        assertFalse(state.registrarConteoFisico(item.id, -1.0))
        assertFalse(state.registrarConteoFisico("no-existe", 10.0))

        state.cambiarSesion(false)
        assertNull(state.conteoDe(item.id))
    }
}
