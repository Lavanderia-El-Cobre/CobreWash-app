package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Aviso

/** Página de avisos internos devuelta por el servidor junto con el [total] disponible. */
data class PaginaAvisos(val avisos: List<Aviso>, val total: Int)

/** Contrato para consultar avisos internos dirigidos al personal de forma paginada. */
fun interface AvisosRepository {
    suspend fun consultar(offset: Int, limit: Int): PaginaAvisos
}

/** Implementación de [AvisosRepository] que delega en la consulta Firebase de cada plataforma. */
class FirebaseAvisosRepository : AvisosRepository {
    override suspend fun consultar(offset: Int, limit: Int): PaginaAvisos = consultarAvisosFirebase(offset, limit)
}

/** Puente `expect`/`actual` para leer los avisos paginados desde Firebase Data Connect. */
internal expect suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos
