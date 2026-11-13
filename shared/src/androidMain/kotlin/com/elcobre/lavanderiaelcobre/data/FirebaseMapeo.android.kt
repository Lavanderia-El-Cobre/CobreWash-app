package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.EstadoEtapaProduccion
import com.elcobre.lavanderiaelcobre.data.model.EtapaComanda
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val ZonaPlanta: TimeZone = TimeZone.getTimeZone("America/Santiago")
private val LocalePlanta: Locale = Locale.forLanguageTag("es-CL")

/** Fecha corta en la zona de la lavandería, aunque el equipo esté en otra zona. */
internal fun fechaCorta(ts: Timestamp?): String = ts?.let {
    SimpleDateFormat("dd/MM/yyyy", LocalePlanta).apply { timeZone = ZonaPlanta }.format(it.toDate())
} ?: ""

internal fun fechaHora(ts: Timestamp?): String = ts?.let {
    SimpleDateFormat("dd/MM/yyyy HH:mm", LocalePlanta).apply { timeZone = ZonaPlanta }.format(it.toDate())
} ?: ""

internal fun Timestamp.millis(): Long = seconds * 1000 + nanoseconds / 1_000_000

/** La versión de una fila (`actualizadoEn`) viaja como texto exacto para la edición optimista. */
internal fun versionDe(ts: Timestamp): String = "${ts.seconds}.${ts.nanoseconds}"

internal fun timestampDeVersion(version: String): Timestamp {
    val (segundos, nanos) = version.split('.').let { (it.getOrNull(0)?.toLongOrNull() ?: 0L) to (it.getOrNull(1)?.toIntOrNull() ?: 0) }
    return Timestamp(segundos, nanos)
}

internal fun nombreCompleto(nombre: String?, apellido: String?): String =
    listOfNotNull(nombre, apellido).joinToString(" ").trim()

/** Datos de una etapa persistida, comunes a las distintas consultas generadas. */
@Suppress("LongParameterList") // Copia plana de los campos de una etapa persistida.
internal class EtapaCruda(
    val etapaId: String,
    val nombreEtapa: String?,
    val ordenEtapa: Int?,
    val tiempoEstimadoMin: Int?,
    val estado: String?,
    val fechaInicio: Timestamp?,
    val fechaCompletado: Timestamp?,
    val operario: String?,
    val asignadoA: String?,
    val asignadoAId: String?,
    val catalogoNombre: String,
    val catalogoOrden: Int,
    val catalogoTiempo: Int?,
)

/** Igual que `normalizarEtapas` de la web: la copia guardada en la comanda prevalece sobre el catálogo. */
internal fun normalizarEtapas(etapas: List<EtapaCruda>): List<EtapaComanda> = etapas.map {
    EtapaComanda(
        id = it.etapaId,
        nombre = it.nombreEtapa ?: it.catalogoNombre,
        orden = it.ordenEtapa ?: it.catalogoOrden,
        estado = EstadoEtapaProduccion.desdeCodigo(it.estado),
        tiempoEstimadoMin = if (it.nombreEtapa != null) it.tiempoEstimadoMin else it.catalogoTiempo,
        responsable = it.operario,
        asignadoA = it.asignadoA,
        asignadoAId = it.asignadoAId,
        inicio = it.fechaInicio?.let(::fechaHora),
        completado = it.fechaCompletado?.let(::fechaHora),
        inicioMs = it.fechaInicio?.millis(),
    )
}.sortedBy { it.orden }
