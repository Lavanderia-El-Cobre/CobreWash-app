package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.EventoHistorial
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetComandaDetalleOperarioQuery
import com.elcobre.lavanderiaelcobre.dataconnect.GetSeguimientoProduccionQuery
import com.elcobre.lavanderiaelcobre.dataconnect.GetMisComandasAsignadasQuery
import com.elcobre.lavanderiaelcobre.dataconnect.EtapaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.IncidenciaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Locale

/** Busca una comanda operativa por su UUID de QR y carga su detalle completo desde el servidor. */
actual suspend fun buscarComandaPorQrFirebase(codigoQr: String): Pedido? = solicitud {
    val data = ExampleConnector.instance.getComandaOperativaPorQr.ref(UUID.fromString(codigoQr))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comanda
    data?.let { getComandaDetalleFirebase(it.id.toString()) }
}

/**
 * Obtiene todas las comandas activas de planta paginando de a 50 registros (`SERVER_ONLY`)
 * y resolviendo el detalle operativo de cada una.
 */
actual suspend fun getComandasActivasFirebase(): List<Pedido> = solicitud {
    val resultado = mutableListOf<Pedido>()
    var pagina: List<GetSeguimientoProduccionQuery.Data.ComandasItem>
    var desplazamiento = 0
    do {
        pagina = ExampleConnector.instance.getSeguimientoProduccion.ref {
            limit = 50
            offset = desplazamiento
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comandas
        for (item in pagina) getComandaDetalleFirebase(item.id.toString())?.let(resultado::add)
        desplazamiento += pagina.size
    } while (pagina.size == 50)
    resultado
}

/**
 * Obtiene las comandas asignadas al operario autenticado paginando de a 50 registros
 * y deduplicando por ID por si la lista cambia mientras se recorren páginas.
 */
actual suspend fun getMisComandasAsignadasFirebase(): List<Pedido> = solicitud {
    val resultado = mutableListOf<Pedido>()
    var pagina: List<GetMisComandasAsignadasQuery.Data.ComandasItem>
    var desplazamiento = 0
    do {
        pagina = ExampleConnector.instance.getMisComandasAsignadas.ref {
            limit = 50
            offset = desplazamiento
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comandas
        resultado.addAll(pagina.map(::mapearAsignada))
        desplazamiento += pagina.size
    } while (pagina.size == 50)
    resultado.distinctBy { it.id }
}

private fun fecha(timestamp: com.google.firebase.Timestamp?): String = timestamp?.let {
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(it.toDate())
} ?: "Sin fecha definida"

private fun etapa(orden: Int): EtapaProceso = when (orden) {
    1 -> EtapaProceso.RECEPCION
    2 -> EtapaProceso.LAVADO
    3 -> EtapaProceso.SECADO
    4 -> EtapaProceso.PLANCHADO
    else -> EtapaProceso.ENTREGA
}

/** Consulta en Data Connect (`SERVER_ONLY`) el detalle operativo de una comanda por su UUID. */
actual suspend fun getComandaDetalleFirebase(id: String): Pedido? = solicitud {
    ExampleConnector.instance.getComandaDetalleOperario.ref(UUID.fromString(id))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comanda?.let(::mapearComanda)
}

private fun mapearComanda(c: GetComandaDetalleOperarioQuery.Data.Comanda): Pedido {
    val etapas = c.comandaEtapas_on_comanda.sortedBy { it.ordenEtapa ?: it.etapa.orden }
    val activa = etapas.firstOrNull { it.estado.value != EtapaEstado.COMPLETADA }
    val orden = activa?.let { it.ordenEtapa ?: it.etapa.orden }
    return Pedido(
        id = c.id.toString(), comanda = c.numeroComanda, cliente = c.cliente.nombre,
        tipo = TipoComanda.EN_TIENDA, tipoServicio = c.comandaDetalles_on_comanda
            .map { it.tipoServicio.nombre }.distinct().joinToString(", "),
        prioridad = Prioridad.MEDIA, piezas = c.comandaDetalles_on_comanda.sumOf { it.cantidad },
        plazoEntrega = fecha(c.fechaEntregaEstimada), etapaActual = etapa(orden ?: 5),
        historial = c.comandaHistorialEstados_on_comanda.map {
            EventoHistorial(if (it.motivo?.startsWith("Comentario:") == true) TipoEvento.COMENTARIO else TipoEvento.CAMBIO_ETAPA,
                it.estadoNuevo.stringValue,
                it.motivo.orEmpty(), fecha(it.fecha), it.usuario?.nombre ?: "Sistema")
        } + c.incidenciaComandas_on_comanda.map {
            EventoHistorial(if (it.estado.value == IncidenciaEstado.RESUELTA) TipoEvento.ALERTA_RESUELTA else TipoEvento.ALERTA,
                "${it.motivo} - ${it.estado.stringValue}", it.descripcion.orEmpty(), fecha(it.fecha), it.reportadaPor.nombre)
        },
        alertaActiva = if (c.incidenciaComandas_on_comanda.any { it.estado.value != IncidenciaEstado.RESUELTA }) {
            TipoAlerta.INCIDENCIA
        } else null,
        etapaIdDb = activa?.etapaId?.toString(), ordenEtapaDb = orden, estadoComandaDb = c.estado.stringValue,
        datosComanda = listOfNotNull(
            "Número: ${c.numeroComanda}",
            "Recepción: ${fecha(c.fechaRecepcion)}",
            c.observaciones?.takeIf { it.isNotBlank() }?.let { "Observaciones: $it" }
        ),
        prendas = c.comandaDetalles_on_comanda.map {
            val d = it.detalle?.takeIf { d -> d.isNotBlank() }?.let { d -> " ($d)" } ?: ""
            "${it.cantidad}x ${it.tipoPrenda.nombre} - ${it.tipoServicio.nombre}$d"
        },
        etapasDetalle = emptyList(),
    )
}

private fun mapearAsignada(c: GetMisComandasAsignadasQuery.Data.ComandasItem): Pedido {
    val etapas = c.comandaEtapas_on_comanda.sortedBy { it.ordenEtapa ?: it.etapa.orden }
    val activa = etapas.firstOrNull { it.estado.value != EtapaEstado.COMPLETADA }
    val orden = activa?.let { it.ordenEtapa ?: it.etapa.orden }
    return Pedido(
        id = c.id.toString(), comanda = c.numeroComanda, cliente = c.cliente.nombre,
        tipo = TipoComanda.EN_TIENDA, tipoServicio = c.comandaDetalles_on_comanda
            .map { it.tipoServicio.nombre }.distinct().joinToString(", "),
        prioridad = Prioridad.MEDIA, piezas = c.comandaDetalles_on_comanda.sumOf { it.cantidad },
        plazoEntrega = fecha(c.fechaEntregaEstimada), etapaActual = etapa(orden ?: 5),
        historial = c.comandaHistorialEstados_on_comanda.map {
            EventoHistorial(if (it.motivo?.startsWith("Comentario:") == true) TipoEvento.COMENTARIO else TipoEvento.CAMBIO_ETAPA,
                it.estadoNuevo.stringValue,
                it.motivo.orEmpty(), fecha(it.fecha), it.usuario?.nombre ?: "Sistema")
        } + c.incidenciaComandas_on_comanda.map {
            EventoHistorial(if (it.estado.value == IncidenciaEstado.RESUELTA) TipoEvento.ALERTA_RESUELTA else TipoEvento.ALERTA,
                "${it.motivo} - ${it.estado.stringValue}", it.descripcion.orEmpty(), fecha(it.fecha), it.reportadaPor.nombre)
        },
        alertaActiva = if (c.incidenciaComandas_on_comanda.any { it.estado.value != IncidenciaEstado.RESUELTA }) {
            TipoAlerta.INCIDENCIA
        } else null,
        etapaIdDb = activa?.etapaId?.toString(), ordenEtapaDb = orden, estadoComandaDb = c.estado.stringValue,
        datosComanda = listOfNotNull(
            "Número: ${c.numeroComanda}",
            "Recepción: ${fecha(c.fechaRecepcion)}",
            c.observaciones?.takeIf { it.isNotBlank() }?.let { "Observaciones: $it" }
        ),
        prendas = c.comandaDetalles_on_comanda.map {
            val d = it.detalle?.takeIf { d -> d.isNotBlank() }?.let { d -> " ($d)" } ?: ""
            "${it.cantidad}x ${it.tipoPrenda.nombre} - ${it.tipoServicio.nombre}$d"
        },
        etapasDetalle = emptyList(),
    )
}

/** Autoasigna al operario autenticado todas las etapas pendientes de una comanda. */
actual suspend fun autoAsignarComandaFirebase(comandaId: String) = solicitud {
    val cantidad = ExampleConnector.instance.autoAsignarComandaOperario.execute(UUID.fromString(comandaId))
        .data.comandaEtapa_updateMany
    if (cantidad == 0) throw OperacionComandaException("La comanda no tiene etapas pendientes para asignar.")
}

/** Marca una etapa productiva como completada y actualiza el estado global de la comanda. */
actual suspend fun completarEtapaFirebase(
    comandaId: String, etapaId: String, orden: Int, estadoComandaActual: String,
): Boolean = solicitud {
    ExampleConnector.instance.completarEtapaComanda.execute(
        UUID.fromString(comandaId), UUID.fromString(etapaId), orden, ComandaEstado.valueOf(estadoComandaActual),
    )
    true
}
