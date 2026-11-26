@file:Suppress("MaxLineLength") // Casos de prueba en una línea, como las pruebas de la web.

package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.ResultadoClienteConCuenta
import com.elcobre.lavanderiaelcobre.data.auth.DURACION_SESION_RECORDADA_MS
import com.elcobre.lavanderiaelcobre.data.auth.DatosSesionGuardada
import com.elcobre.lavanderiaelcobre.data.auth.FalloIngreso
import com.elcobre.lavanderiaelcobre.data.auth.INICIO_VERIFICACION_OBLIGATORIA_MS
import com.elcobre.lavanderiaelcobre.data.auth.IntentosIngreso
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.auth.requiereVerificacion
import com.elcobre.lavanderiaelcobre.ui.screens.login.mensajeIntentos
import com.elcobre.lavanderiaelcobre.ui.screens.login.sesionParaRol
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Login, sesión recordada y altas, como `lib/firebase` de la web. */
class CuentasSesionTest {

    @BeforeTest
    fun limpiar() {
        IntentosIngreso.limpiar()
        SesionPersistente.limpiar()
    }

    @AfterTest
    fun despues() = limpiar()

    @Test
    fun bloqueo_cincoFallosLibresYLuegoProgresivo() {
        val t = 1_000_000L
        repeat(4) { assertNull(IntentosIngreso.registrarFallo(FalloIngreso.CREDENCIALES, t + it).bloqueadoHasta) }
        assertEquals(1, IntentosIngreso.estado(t + 10).restantes)
        val quinto = IntentosIngreso.registrarFallo(FalloIngreso.CREDENCIALES, t + 10)
        assertEquals(t + 10 + 30_000, quinto.bloqueadoHasta)
        assertEquals(60_000, IntentosIngreso.duracionBloqueo(6))
        assertEquals(15 * 60_000L, IntentosIngreso.duracionBloqueo(30))
        // El bloqueo sigue vigente al "reabrir" (se lee del almacenamiento local).
        assertNotNull(IntentosIngreso.estado(t + 20_000).bloqueadoHasta)
        assertNull(IntentosIngreso.estado(t + 40_000).bloqueadoHasta)
    }

    @Test
    fun bloqueo_erroresDeRedNoCuentanYFirebaseBloqueaCincoMinutos() {
        val t = 5_000_000L
        repeat(10) { IntentosIngreso.registrarFallo(FalloIngreso.OTRO, t) }
        assertEquals(IntentosIngreso.INTENTOS_LIBRES, IntentosIngreso.estado(t).restantes)
        assertEquals(t + 5 * 60_000, IntentosIngreso.registrarFallo(FalloIngreso.BLOQUEO_SERVIDOR, t).bloqueadoHasta)
    }

    @Test
    fun bloqueo_seOlvidaTrasUnaHoraSinFallosYSeLimpiaAlEntrar() {
        val t = 9_000_000L
        repeat(3) { IntentosIngreso.registrarFallo(FalloIngreso.CREDENCIALES, t) }
        assertEquals(2, IntentosIngreso.estado(t).restantes)
        assertEquals(5, IntentosIngreso.estado(t + 61 * 60_000).restantes)
        IntentosIngreso.limpiar()
        assertEquals(5, IntentosIngreso.estado(t).restantes)
    }

    @Test
    fun bloqueo_avisaIntentosRestantesYEspera() {
        assertNull(mensajeIntentos(com.elcobre.lavanderiaelcobre.data.auth.EstadoIntentos(null, 4), 0))
        assertTrue(mensajeIntentos(com.elcobre.lavanderiaelcobre.data.auth.EstadoIntentos(null, 1), 0)!!.contains("1 intento"))
        assertTrue(mensajeIntentos(com.elcobre.lavanderiaelcobre.data.auth.EstadoIntentos(45_000, 0), 0)!!.contains("45 s"))
        assertEquals("1:05 min", IntentosIngreso.formatoEspera(65_000))
    }

    @Test
    fun verificacion_soloExigidaACuentasNuevasSinVerificar() {
        assertFalse(requiereVerificacion(correoVerificado = true, creadaEnMs = INICIO_VERIFICACION_OBLIGATORIA_MS + 1))
        assertTrue(requiereVerificacion(correoVerificado = false, creadaEnMs = INICIO_VERIFICACION_OBLIGATORIA_MS))
        assertFalse(requiereVerificacion(correoVerificado = false, creadaEnMs = INICIO_VERIFICACION_OBLIGATORIA_MS - 1))
        assertTrue(requiereVerificacion(correoVerificado = false, creadaEnMs = null), "Sin fecha confiable se exige")
    }

    @Test
    fun sesionRecordada_venceALos30Dias() {
        val t = 2_000_000_000L
        SesionPersistente.guardar(DatosSesionGuardada("op@cobre.cl", "Op", "operario"), t)
        assertNotNull(SesionPersistente.obtener(t + DURACION_SESION_RECORDADA_MS - 1))
        assertNull(SesionPersistente.obtener(t + DURACION_SESION_RECORDADA_MS))
        assertNull(SesionPersistente.obtener(t), "Vencida, se borra")
        assertFalse(SesionPersistente.getRecordarme(), "Desmarcado por defecto, como la web")
    }

    @Test
    fun roles_soloLosQueExistenEnElBackend() {
        assertNotNull(sesionParaRol("operario", "A"))
        assertNotNull(sesionParaRol("conductor", "A"))
        assertNull(sesionParaRol("administrador", "A"))
        assertNull(sesionParaRol("recepcion", "A"))
        assertNull(sesionParaRol("cliente", "A"))
    }

    @Test
    fun clienteConCuenta_avisosComoLaWeb() {
        assertTrue(ResultadoClienteConCuenta("f1", cuentaCreada = false, invitacionEnviada = false).aviso("a@b.cl").contains("ya tenía una cuenta"))
        assertTrue(ResultadoClienteConCuenta("f1", cuentaCreada = true, invitacionEnviada = true).aviso("a@b.cl").contains("Enviamos a a@b.cl"))
        assertTrue(ResultadoClienteConCuenta("f1", cuentaCreada = true, invitacionEnviada = false).aviso("a@b.cl").contains("no se pudo enviar"))
    }
}
