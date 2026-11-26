package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Implementación provisional para iOS que siempre devuelve `null`.
 *
 * Sin SDK de Firebase Auth ni Data Connect en iOS no hay un token remoto que revalidar al iniciar
 * la app, por lo que el arranque continúa hacia la pantalla de ingreso.
 */
actual suspend fun restaurarPerfilAutenticado(): PerfilResultado? = null
