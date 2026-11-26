package com.elcobre.lavanderiaelcobre.data

/**
 * Implementación provisional de consulta de avisos en iOS.
 *
 * Firebase Data Connect aún no está integrado en la compilación de iOS, por lo que falla de forma
 * explícita con [OperacionComandaException] en lugar de devolver una lista vacía silenciosa.
 */
internal actual suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos =
    throw OperacionComandaException("Los avisos de Firebase aún no están disponibles en iOS.")
