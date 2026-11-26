package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetInsumosOperarioQuery
import com.elcobre.lavanderiaelcobre.dataconnect.GetMisConteosInventarioQuery
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

private const val PAGINA = 50

/**
 * Descarga el catálogo completo de insumos en páginas de [PAGINA] elementos directo
 * del servidor y elimina posibles duplicados si el catálogo cambia entre páginas.
 */
internal actual suspend fun catalogoInsumosFirebase(): List<InsumoInventario> = solicitud {
    val resultado = mutableListOf<InsumoInventario>()
    var pagina: List<GetInsumosOperarioQuery.Data.InsumosItem>
    var offset = 0
    do {
        pagina = ExampleConnector.instance.getInsumosOperario.ref {
            limit = PAGINA
            this.offset = offset
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data.insumos
        resultado.addAll(pagina.map {
            InsumoInventario(it.id.toString(), it.codigoQr.toString(), it.nombre, it.unidadMedida,
                it.stockActual, it.stockMinimo, it.activo, it.revisionStock)
        })
        offset += pagina.size
    } while (pagina.size == PAGINA)
    resultado.distinctBy { it.id }
}

/** Busca un insumo por su UUID de código QR directamente en el servidor. */
internal actual suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario? = solicitud {
    ExampleConnector.instance.getInsumoPorQr.ref(UUID.fromString(codigo))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.insumo?.let {
            InsumoInventario(it.id.toString(), it.codigoQr.toString(), it.nombre, it.unidadMedida,
                it.stockActual, it.stockMinimo, it.activo, it.revisionStock)
        }
}

/** Obtiene los últimos conteos físicos enviados por el usuario autenticado. */
internal actual suspend fun misConteosInventarioFirebase(): List<ConteoFisico> = solicitud {
    ExampleConnector.instance.getMisConteosInventario.ref { limit = PAGINA }
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.conteoInventarios
        .map(::aConteo)
}

/**
 * Registra un conteo físico de inventario en Data Connect adjuntando [revisionStock]
 * para que administración detecte si el stock cambió antes de aprobar el ajuste.
 */
internal actual suspend fun registrarConteoInventarioFirebase(insumoId: String, cantidad: Double, revisionStock: Int) {
    solicitud {
        ExampleConnector.instance.registrarConteoInventario
            .ref(UUID.randomUUID(), UUID.fromString(insumoId), cantidad, revisionStock)
            .execute()
    }
}

private fun aConteo(it: GetMisConteosInventarioQuery.Data.ConteoInventariosItem) = ConteoFisico(
    insumoId = it.insumoId.toString(),
    cantidadContada = it.cantidadContada,
    stockSistema = it.stockSistema,
    id = it.id.toString(),
    estado = EstadoRevisionConteo.entries.firstOrNull { e -> e.name == it.estado.stringValue }
        ?: EstadoRevisionConteo.PENDIENTE,
    creadoEn = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(it.creadoEn.toDate()),
    motivoResolucion = it.motivoResolucion,
)
