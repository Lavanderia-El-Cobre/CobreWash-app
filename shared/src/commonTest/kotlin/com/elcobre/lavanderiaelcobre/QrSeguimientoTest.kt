package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerCodigoQr
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.urlSeguimientoComanda
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** El QR que muestra la app debe ser el mismo enlace que emite la web y que el escáner acepta. */
class QrSeguimientoTest {
    private val uuid = "3f2a9c1e-7b4d-4e8a-9c2f-1a2b3c4d5e6f"

    @Test
    fun urlSeguimiento_usaElDominioYUuidCanonicoDeLaWeb() {
        assertEquals("https://lavanderia-elcobre.vercel.app/seguimiento?qr=$uuid", urlSeguimientoComanda(uuid))
        assertEquals(urlSeguimientoComanda(uuid), urlSeguimientoComanda(uuid.uppercase().replace("-", "")))
    }

    @Test
    fun urlSeguimiento_esLegiblePorElEscanerDeLaApp() {
        assertEquals(uuid, extraerCodigoQr(requireNotNull(urlSeguimientoComanda(uuid))))
    }

    @Test
    fun urlSeguimiento_rechazaCodigosInvalidos() {
        assertNull(urlSeguimientoComanda(""))
        assertNull(urlSeguimientoComanda("ELCOBRE-AB12"))
    }
}
