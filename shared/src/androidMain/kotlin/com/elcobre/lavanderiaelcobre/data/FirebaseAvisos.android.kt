package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef

/** Implementación Android que consulta avisos internos para el equipo operativo por defecto. */
internal actual suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos =
    avisosEquipoFirebase("operario", offset, limit)

/** Traduce el código de rol destinatario a su etiqueta visible, equivalente a la web. */
internal fun etiquetaDestinatario(rol: String?): String = when (rol) {
    null -> "Todos los equipos"
    "operario" -> "Operarios"
    "recepcionista" -> "Recepción"
    else -> rol
}

/**
 * Consulta en Data Connect (`SERVER_ONLY`) los avisos visibles para [rol], ordenados
 * del más reciente al más antiguo y acompañados del total para paginación.
 */
internal suspend fun avisosEquipoFirebase(rol: String, offset: Int, limit: Int): PaginaAvisos = solicitud {
    val data = ExampleConnector.instance.getAvisosParaEquipo.ref(rol) {
        this.offset = offset
        this.limit = limit
    }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data
    PaginaAvisos(data.avisos.map {
        Aviso(
            id = it.id.toString(), titulo = it.titulo, mensaje = it.contenido,
            hora = fechaHora(it.fechaPublicacion),
            dirigidoA = etiquetaDestinatario(it.rolDestinatario?.nombre),
            autor = nombreCompleto(it.autor.nombre, it.autor.apellido),
        )
    }, data.total.firstOrNull()?._count ?: 0)
}
