package com.elcobre.lavanderiaelcobre.data.model

/** Estados de una comanda (`ComandaEstado`), con las etiquetas que usa la web. */
enum class EstadoComanda(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    FINALIZADA("Listo"),
    ENTREGADA("Entregado"),
    ANULADA("Anulado"),
    ;

    val esActiva: Boolean get() = this == PENDIENTE || this == EN_PROCESO || this == FINALIZADA

    companion object {
        fun desdeCodigo(codigo: String?): EstadoComanda = entries.firstOrNull { it.name == codigo } ?: PENDIENTE
    }
}

enum class TipoClienteFicha(val etiqueta: String) {
    PARTICULAR("Particular"),
    HOTEL("Hotel"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): TipoClienteFicha = entries.firstOrNull { it.name == codigo } ?: PARTICULAR
    }
}

/** Ficha de cliente (`Cliente`) con un resumen de su historial de comandas. */
data class ClienteFicha(
    val id: String,
    val nombre: String,
    val tipo: TipoClienteFicha = TipoClienteFicha.PARTICULAR,
    val rut: String? = null,
    val telefono: String? = null,
    val email: String? = null,
    val direccion: String? = null,
    val creadoEn: String = "",
    val comandas: Int = 0,
    val comandasActivas: Int = 0,
    val prendas: Int = 0,
    val totalFacturado: Double = 0.0,
    val ultimaComanda: String? = null,
)

data class TipoServicioCatalogo(val id: String, val nombre: String, val precioBase: Double, val unidad: String = "PRENDA")

data class TipoPrendaCatalogo(val id: String, val nombre: String)

/** Catálogos que necesita el formulario de comanda (`GetCatalogosComanda`). */
data class CatalogosComanda(
    val servicios: List<TipoServicioCatalogo> = emptyList(),
    val prendas: List<TipoPrendaCatalogo> = emptyList(),
    val clientes: List<ClienteFicha> = emptyList(),
)

/** Línea de detalle de una comanda: prenda, servicio, cantidad y precio. */
data class LineaComanda(
    val prenda: String,
    val servicio: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val detalle: String = "",
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

enum class EstadoEtapaProduccion(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    COMPLETADA("Completada"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): EstadoEtapaProduccion = entries.firstOrNull { it.name == codigo } ?: PENDIENTE
    }
}

/** Etapa del flujo de producción de una comanda, normalizada como `normalizarEtapas` de la web. */
data class EtapaComanda(
    val id: String,
    val nombre: String,
    val orden: Int,
    val estado: EstadoEtapaProduccion,
    val tiempoEstimadoMin: Int? = null,
    val responsable: String? = null,
    val asignadoA: String? = null,
    val asignadoAId: String? = null,
    val inicio: String? = null,
    val completado: String? = null,
    val inicioMs: Long? = null,
)

data class EventoComanda(val titulo: String, val detalle: String, val fecha: String, val autor: String)

/** Comanda vista desde recepción o administración (lista y detalle). */
data class ComandaGestion(
    val id: String,
    val numero: String,
    val estado: EstadoComanda,
    val cliente: ClienteFicha,
    val valorTotal: Double,
    val fechaRecepcion: String,
    val actualizadoEn: String,
    val codigoQr: String = "",
    val empresa: String? = null,
    val proyecto: String? = null,
    val observaciones: String? = null,
    val motivoAnulacion: String? = null,
    val fechaEntregaEstimada: String? = null,
    val fechaEntregaReal: String? = null,
    val lineas: List<LineaComanda> = emptyList(),
    val etapas: List<EtapaComanda> = emptyList(),
    val historial: List<EventoComanda> = emptyList(),
    val incidenciasAbiertas: Int = 0,
    val fechaRecepcionMs: Long = 0,
) {
    val prendas: Int get() = lineas.sumOf { it.cantidad }
    val progreso: Int get() = progresoEtapas(etapas)
    val etapaActual: EtapaComanda? get() = etapas.sortedBy { it.orden }.firstOrNull { it.estado != EstadoEtapaProduccion.COMPLETADA }

    /** La web solo permite editar comandas pendientes (`EditarComandaConDetalles`). */
    val puedeEditar: Boolean get() = estado == EstadoComanda.PENDIENTE

    /** Anular aplica a comandas aún no entregadas ni anuladas (`AnularComanda`). */
    val puedeAnular: Boolean get() = estado.esActiva

    /** Solo una comanda lista (FINALIZADA) se entrega al cliente (`EntregarComanda`). */
    val puedeEntregar: Boolean get() = estado == EstadoComanda.FINALIZADA

    /** Una comanda pendiente sin etapas necesita que recepción le asocie el flujo. */
    val necesitaFlujo: Boolean get() = estado == EstadoComanda.PENDIENTE && etapas.isEmpty()
}

/** Límites iniciales por etapa en minutos, como `LIMITES_ETAPAS` de la web. */
private val LimitesEtapasMin = mapOf(1 to 15, 2 to 90, 3 to 60, 4 to 60, 5 to 1440)

fun limiteEtapaMin(orden: Int, minutos: Int?): Int? = minutos ?: LimitesEtapasMin[orden]

/**
 * Minutos que la etapa activa lleva por sobre su tiempo estimado (0 si va a tiempo),
 * igual que `alertaComanda` de la web: la recepción cuenta desde la fecha de recepción.
 */
fun minutosAtraso(comanda: ComandaGestion, ahoraMs: Long): Int {
    val vencimiento = vencimientoEtapaActual(comanda) ?: return 0
    val excedido = ahoraMs - vencimiento
    return if (excedido <= 0) 0 else ((excedido + 59_999) / 60_000).toInt()
}

/** Instante en que vence la etapa en curso, o `null` si no hay una etapa activa con límite. */
private fun vencimientoEtapaActual(comanda: ComandaGestion): Long? {
    val etapa = comanda.etapaActual?.takeIf { comanda.estado.esActiva }
    val enCurso = etapa != null && (
        etapa.estado == EstadoEtapaProduccion.EN_PROCESO || (etapa.estado == EstadoEtapaProduccion.PENDIENTE && etapa.orden == 1)
        )
    if (etapa == null || !enCurso) return null
    val inicio = etapa.inicioMs ?: comanda.fechaRecepcionMs.takeIf { etapa.orden == 1 && it > 0 }
    val limite = limiteEtapaMin(etapa.orden, etapa.tiempoEstimadoMin)?.takeIf { it > 0 }
    return if (inicio != null && limite != null) inicio + limite * 60_000L else null
}

fun progresoEtapas(etapas: List<EtapaComanda>): Int {
    if (etapas.isEmpty()) return 0
    return etapas.count { it.estado == EstadoEtapaProduccion.COMPLETADA } * 100 / etapas.size
}

/** Estado de etapa para mostrar, como `estadoEtapa` de la web. */
fun descripcionEtapas(etapas: List<EtapaComanda>): String {
    val actual = etapas.sortedBy { it.orden }.firstOrNull { it.estado != EstadoEtapaProduccion.COMPLETADA }
    return actual?.nombre ?: if (etapas.isNotEmpty()) "Flujo completado" else "En espera"
}

/** Filtros de la lista de comandas (`GetComandasPaginadas`). */
data class FiltroComandas(
    val estados: Set<EstadoComanda> = emptySet(),
    val cliente: String = "",
)

data class PaginaComandas(
    val comandas: List<ComandaGestion>,
    val total: Int,
    val conteos: Map<EstadoComanda, Int>,
)

/** Persona que puede recibir asignaciones (operario) o salidas (repartidor). */
data class PersonaRef(val id: String, val nombre: String)

data class PaginaProduccion(
    val comandas: List<ComandaGestion>,
    val total: Int,
    val operarios: List<PersonaRef>,
)

enum class EstadoIncidencia(val etiqueta: String) {
    ABIERTA("Abierta"),
    EN_REVISION("En revisión"),
    RESUELTA("Resuelta"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): EstadoIncidencia = entries.firstOrNull { it.name == codigo } ?: ABIERTA
    }
}

data class IncidenciaGestion(
    val id: String,
    val motivo: String,
    val descripcion: String,
    val estado: EstadoIncidencia,
    val fecha: String,
    val comandaId: String,
    val numeroComanda: String,
    val cliente: String,
    val reportadaPor: String,
)

/** Motivos de incidencia, iguales a `MOTIVOS_INCIDENCIA` de la web. */
val MotivosIncidencia = listOf(
    "Prenda dañada",
    "Mancha persistente",
    "Prenda extraviada",
    "Falla de maquinaria",
    "Retraso en producción",
    "Otro",
)
