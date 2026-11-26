package com.elcobre.lavanderiaelcobre

/** Contrato multiplataforma que expone información básica del sistema operativo anfitrión. */
interface Platform {
    /** Nombre y versión legible de la plataforma en ejecución (ej. `"Android 35"`, `"iOS 18.0"`). */
    val name: String
}

/**
 * Devuelve la implementación de [Platform] correspondiente al target activo
 * (`androidMain` o `iosMain`).
 */
expect fun getPlatform(): Platform
