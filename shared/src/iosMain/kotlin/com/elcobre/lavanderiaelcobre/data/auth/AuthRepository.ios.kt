package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Firebase Auth aún no está integrado en iOS (fase 1 del proyecto es Android-only,
 * ver conversación de integración). Devuelve error explícito en vez de simular éxito.
 */
actual class AuthRepository actual constructor() {
    actual suspend fun iniciarSesion(correo: String, password: String): AuthResultado =
        AuthResultado.Error("El inicio de sesión aún no está disponible en iOS.")

    actual fun cerrarSesion() = Unit

    actual suspend fun correoPorVerificar(): Boolean = false

    actual suspend fun enviarVerificacion(): String? = "No disponible en iOS."

    actual suspend fun recuperarContrasena(correo: String): String? = "No disponible en iOS."
}
