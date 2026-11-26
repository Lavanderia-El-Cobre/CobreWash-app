package com.elcobre.lavanderiaelcobre.data.auth

/** Resultado de un intento de autenticación contra Firebase Auth. */
sealed interface AuthResultado {
    /** Autenticación aceptada con el identificador y correo de la cuenta. */
    data class Exito(val uid: String, val email: String) : AuthResultado

    /** Fallo legible y clasificado en [fallo] para alimentar el bloqueo progresivo ([IntentosIngreso]). */
    data class Error(val mensaje: String, val fallo: FalloIngreso = FalloIngreso.OTRO) : AuthResultado
}

/**
 * Las cuentas creadas desde esta fecha deben verificar su correo antes de usarse, igual
 * que `INICIO_VERIFICACION_OBLIGATORIA` de la web (2026-11-24 00:00, hora de Chile).
 */
const val INICIO_VERIFICACION_OBLIGATORIA_MS = 1_795_489_200_000L

/** Igual que `requiereVerificacion` de la web: sin fecha confiable se exige la verificación. */
fun requiereVerificacion(correoVerificado: Boolean, creadaEnMs: Long?): Boolean =
    !correoVerificado && (creadaEnMs == null || creadaEnMs <= 0 || creadaEnMs >= INICIO_VERIFICACION_OBLIGATORIA_MS)

/**
 * Puerta de entrada a Firebase Authentication. Vive como expect/actual porque el
 * SDK oficial de Firebase Auth es Android-only: la actual de iOS es un stub hasta
 * que se decida su integración (ver LoginScreen/CobreViewModel).
 */
expect class AuthRepository() {
    suspend fun iniciarSesion(correo: String, password: String): AuthResultado
    fun cerrarSesion()

    /** Vuelve a leer la cuenta del servidor y dice si debe verificar su correo antes de entrar. */
    suspend fun correoPorVerificar(): Boolean

    /** Reenvía el correo de verificación a la cuenta actual. Devuelve un error o `null` si salió. */
    suspend fun enviarVerificacion(): String?

    /**
     * Envía el enlace para restablecer la contraseña. No revela si el correo tiene cuenta:
     * solo devuelve error ante fallas de red o formato.
     */
    suspend fun recuperarContrasena(correo: String): String?
}
