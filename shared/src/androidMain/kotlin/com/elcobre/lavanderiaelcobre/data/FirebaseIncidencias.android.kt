package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.IncidenciaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth

/** Registra un comentario operativo en el historial de la comanda vía Data Connect. */
actual suspend fun comentarComandaFirebase(id: String, texto: String): Unit = solicitud<Unit> {
    ExampleConnector.instance.agregarComentarioComanda.execute(UUID.fromString(id), texto.trim())
}

/** Registra una incidencia operativa sobre una comanda en Firebase Data Connect. */
actual suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String): Unit = solicitud<Unit> {
    ExampleConnector.instance.registrarIncidenciaComanda.execute(UUID.fromString(id), motivo) { descripcion = texto }
}

/**
 * Resuelve las incidencias abiertas de una comanda desde la vista de operario.
 * Valida antes de mutar que todas pertenezcan al usuario autenticado, respetando la
 * regla de autorización que reserva a administración las reportadas por terceros.
 */
actual suspend fun resolverComandaFirebase(id: String): Unit = solicitud<Unit> {
    val incidencias = ExampleConnector.instance.getComandaDetalleOperario.ref(UUID.fromString(id))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comanda?.incidenciaComandas_on_comanda.orEmpty()
        .filter { it.estado.value != IncidenciaEstado.RESUELTA }
    val usuario = FirebaseAuth.getInstance().currentUser?.uid
    if (incidencias.any { it.reportadaPor.id != usuario }) {
        throw OperacionComandaException("Esta incidencia debe resolverla administración o quien la reportó.")
    }
    for (incidencia in incidencias) ExampleConnector.instance.resolverMiIncidencia.execute(incidencia.id)
}

/**
 * Busca una comanda por su número exacto (sin distinguir mayúsculas) consultando
 * el seguimiento de producción y cargando luego su detalle operativo completo.
 */
actual suspend fun buscarNumeroComandaFirebase(numero: String): Pedido? = solicitud {
    val filas = ExampleConnector.instance.getSeguimientoProduccion.ref { buscar = numero.trim(); limit = 100 }
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comandas
    filas.firstOrNull { it.numeroComanda.equals(numero.trim(), ignoreCase = true) }
        ?.let { getComandaDetalleFirebase(it.id.toString()) }
}
