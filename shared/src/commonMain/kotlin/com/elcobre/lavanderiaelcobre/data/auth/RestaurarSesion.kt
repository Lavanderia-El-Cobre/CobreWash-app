package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Valida la sesión vigente de Firebase Auth con el servidor y consulta el perfil en Data Connect.
 * Devuelve `null` si no hay una cuenta autenticada en el dispositivo; vive como `expect`/`actual`
 * por depender del SDK nativo de Firebase.
 */
expect suspend fun restaurarPerfilAutenticado(): PerfilResultado?
