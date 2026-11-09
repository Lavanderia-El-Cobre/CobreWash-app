package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.data.InventarioRepository
import com.elcobre.lavanderiaelcobre.ui.InventarioState
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerCodigoQr
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerQrInsumo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CodigoQrInsumoTest {
    // Contenido del QR real que la app rechazaba por no incluir guiones.
    private val compacto = "948359d2736045f890a4dfa6796997c0"
    private val canonico = "948359d2-7360-45f8-90a4-dfa6796997c0"

    @Test fun aceptaAmbosFormatosDelMismoInsumo() {
        assertEquals(canonico, extraerQrInsumo(compacto))
        assertEquals(canonico, extraerQrInsumo(canonico))
        assertEquals(canonico, extraerQrInsumo(" \n${compacto.uppercase()}\t"))
        assertEquals(canonico, extraerQrInsumo(canonico.uppercase()))
        assertNull(extraerCodigoQr(compacto))
    }

    @Test fun rechazaCodigosIncompletosYTextoAjeno() {
        listOf(
            compacto.dropLast(1), "${compacto}0", "g${compacto.drop(1)}",
            "${compacto.take(8)}-${compacto.drop(8)}", "insumo:$compacto",
            "https://otro.com/?qr=$compacto",
            "https://lavanderia-elcobre.vercel.app/seguimiento?qr=$canonico",
        ).forEach { assertNull(extraerQrInsumo(it), it) }
    }

    @Test fun qrCompactoConsultaElUuidCanonicoYSeleccionaSinCambiarStock() = runTest {
        val insumo = InsumoInventario("id", canonico, "Insumo", "kg", 20.0, 10.0, true)
        val consultas = mutableListOf<String>()
        val repository = object : InventarioRepository {
            override suspend fun catalogo() = listOf(insumo)
            override suspend fun porQr(codigo: String): InsumoInventario? {
                consultas += codigo
                return insumo.takeIf { codigo == canonico }
            }
        }
        val state = InventarioState(backgroundScope, repository)
        state.cambiarSesion(true)
        var respondio = false
        state.escanear(compacto) { assertNull(it); respondio = true }
        testScheduler.runCurrent()

        assertTrue(respondio)
        assertEquals(listOf(canonico), consultas)
        assertEquals(insumo, state.seleccionado)
        assertEquals(20.0, state.insumos.single().stockActual)
        assertFalse(state.buscando)
    }
}
