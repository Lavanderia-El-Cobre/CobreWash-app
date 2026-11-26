package com.elcobre.lavanderiaelcobre.data.auth

/** Perfil real traído desde Data Connect tras autenticar (mismo esquema que la web). */
sealed interface PerfilResultado {
    /** Perfil activo en el servidor con el nombre visible y el rol asignado. */
    data class Exito(val nombre: String, val rolNombre: String) : PerfilResultado

    /**
     * [definitivo] indica que la cuenta no puede usar la app (sin perfil, inactiva o sin
     * verificar): la sesión de Firebase se cierra. Un error de red no es definitivo.
     */
    data class Error(val mensaje: String, val definitivo: Boolean = false) : PerfilResultado
}

/**
 * Trae el perfil del usuario autenticado (nombre, rol) usando el SDK de Kotlin de
 * Data Connect generado desde el mismo esquema que usa la web (`GetMiPerfil`).
 * Android-only por ahora: ver [AuthRepository] para la razón del expect/actual.
 */
expect class PerfilRepository() {
    /** Consulta el perfil de la cuenta autenticada en Firebase Data Connect. */
    suspend fun obtenerMiPerfil(): PerfilResultado
}
