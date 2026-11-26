package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Implementación provisional de [PerfilRepository] para iOS.
 *
 * Firebase Data Connect aún no está integrado en esta plataforma (ver [AuthRepository]), por lo
 * que la consulta del perfil devuelve un error explícito en lugar de datos simulados.
 */
actual class PerfilRepository actual constructor() {
    /** Devuelve [PerfilResultado.Error] indicando que el perfil no está disponible en iOS. */
    actual suspend fun obtenerMiPerfil(): PerfilResultado =
        PerfilResultado.Error("El perfil aún no está disponible en iOS.")
}
