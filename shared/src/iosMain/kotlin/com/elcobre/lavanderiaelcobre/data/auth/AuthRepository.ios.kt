package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Implementación provisional de [AuthRepository] para iOS.
 *
 * Firebase Auth aún no está integrado en esta plataforma (la integración inicial es exclusiva de
 * Android). Las operaciones que requieren backend devuelven un error explícito en lugar de simular
 * éxito, evitando estados de sesión inconsistentes.
 */
actual class AuthRepository actual constructor() {
    /** Devuelve [AuthResultado.Error] indicando que el inicio de sesión aún no opera en iOS. */
    actual suspend fun iniciarSesion(correo: String, password: String): AuthResultado =
        AuthResultado.Error("El inicio de sesión aún no está disponible en iOS.")

    /** Sin efecto en iOS porque no existe una sesión activa de Firebase Auth que cerrar. */
    actual fun cerrarSesion() = Unit

    /** Devuelve `false` al no existir un usuario de Firebase Auth pendiente de verificación. */
    actual suspend fun correoPorVerificar(): Boolean = false

    /** Informa al usuario que el envío de verificación de correo no está disponible en iOS. */
    actual suspend fun enviarVerificacion(): String? = "No disponible en iOS."

    /** Informa al usuario que la recuperación de contraseña no está disponible en iOS. */
    actual suspend fun recuperarContrasena(correo: String): String? = "No disponible en iOS."
}
