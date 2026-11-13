package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaComanda
import com.elcobre.lavanderiaelcobre.data.model.PaginaProduccion
import com.elcobre.lavanderiaelcobre.data.model.PersonaRef
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetSeguimientoProduccionQuery
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID

actual fun crearProduccionRepository(): ProduccionRepository = FirebaseProduccionRepository()

private fun GetSeguimientoProduccionQuery.Data.ComandasItem.aGestion(): ComandaGestion = ComandaGestion(
    id = id.toString(),
    numero = numeroComanda,
    estado = EstadoComanda.desdeCodigo(estado.stringValue),
    cliente = ClienteFicha(id = "", nombre = cliente.nombre),
    valorTotal = 0.0,
    fechaRecepcion = fechaHora(fechaRecepcion),
    fechaRecepcionMs = fechaRecepcion.millis(),
    actualizadoEn = "",
    lineas = comandaDetalles_on_comanda.map { LineaComanda(it.tipoPrenda.nombre, it.tipoServicio.nombre, it.cantidad, 0.0) },
    etapas = normalizarEtapas(
        comandaEtapas_on_comanda.map {
            EtapaCruda(
                it.etapaId.toString(), it.nombreEtapa, it.ordenEtapa, it.tiempoEstimadoMin, it.estado.stringValue,
                it.fechaInicio, it.fechaCompletado,
                it.operario?.let { o -> nombreCompleto(o.nombre, o.apellido) },
                it.asignadoA?.let { a -> nombreCompleto(a.nombre, a.apellido) }, it.asignadoA?.id,
                it.etapa.nombre, it.etapa.orden, it.etapa.tiempoEstimadoMin,
            )
        },
    ),
)

/** Seguimiento de producción de toda la planta (`GetSeguimientoProduccion`), como `/intranet/seguimiento`. */
private class FirebaseProduccionRepository : ProduccionRepository {
    private val dc get() = ExampleConnector.instance

    override suspend fun seguimiento(buscar: String, offset: Int, limit: Int): PaginaProduccion = solicitud {
        val data = dc.getSeguimientoProduccion.ref {
            this.limit = limit
            this.offset = offset
            this.buscar = buscar.trim()
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data
        PaginaProduccion(
            comandas = data.comandas.map { it.aGestion() },
            total = data.total.firstOrNull()?._count ?: data.comandas.size,
            operarios = data.operarios.map { PersonaRef(it.id, nombreCompleto(it.nombre, it.apellido)) },
        )
    }

    override suspend fun completarEtapa(comanda: ComandaGestion): Unit = solicitud {
        val etapa = comanda.etapaActual ?: throw OperacionComandaException("La comanda no tiene etapas pendientes.")
        dc.completarEtapaComanda.execute(
            UUID.fromString(comanda.id),
            UUID.fromString(etapa.id),
            etapa.orden,
            ComandaEstado.valueOf(estadoTrasCompletar(etapa.orden)),
        )
    }

    override suspend fun reasignar(comandaId: String, etapaId: String, operarioId: String, motivo: String): Unit = solicitud {
        dc.reasignarOperarioEtapa.execute(UUID.fromString(comandaId), UUID.fromString(etapaId), operarioId) {
            this.motivo = motivo.trim().ifEmpty { null }?.take(200)
        }
    }

    override suspend fun reportarIncidencia(comandaId: String, motivo: String, descripcion: String): Unit = solicitud {
        dc.registrarIncidenciaComanda.execute(UUID.fromString(comandaId), motivo) {
            this.descripcion = descripcion.trim().ifEmpty { null }?.take(300)
        }
    }
}
