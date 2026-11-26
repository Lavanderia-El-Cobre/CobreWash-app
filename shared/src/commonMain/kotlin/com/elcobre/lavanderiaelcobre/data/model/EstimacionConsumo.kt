package com.elcobre.lavanderiaelcobre.data.model

import kotlin.math.abs
import kotlin.math.round

/**
 * Estimación de consumo de insumos de una comanda, calculada por la web
 * (`POST /api/ia/estimar-comanda`). La app no incluye el modelo de IA ni claves: solo
 * envía el servicio y el peso total. Solo informa; no impide guardar la comanda.
 */
enum class NivelStock(val etiqueta: String) {
    OK("Alcanza"),
    JUSTO("Justo, sin margen"),
    INSUFICIENTE("No alcanza"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): NivelStock = when (codigo) {
            "insuficiente" -> INSUFICIENTE
            "justo" -> JUSTO
            else -> OK
        }
    }
}

data class InsumoEstimado(
    val nombre: String,
    val unidad: String,
    val minimo: Double,
    val maximo: Double,
    val nivel: NivelStock,
    /** Solo para administración, como en la web; `null` para recepción. */
    val stockActual: Double? = null,
)

data class EstimacionConsumo(
    val insumos: List<InsumoEstimado>,
    val alerta: Boolean,
    /** El servicio aún no tiene tasas de consumo en el Módulo IA. */
    val sinTasas: Boolean,
) {
    val faltantes: List<InsumoEstimado> get() = insumos.filter { it.nivel == NivelStock.INSUFICIENTE }
}

/** Estado del panel de estimación en el formulario de comanda. */
sealed interface EstadoEstimacion {
    data object SinPeso : EstadoEstimacion
    data object Cargando : EstadoEstimacion
    data class Lista(val estimacion: EstimacionConsumo) : EstadoEstimacion

    /** Sin conexión o falla del servidor: la comanda se puede guardar igual. */
    data object NoDisponible : EstadoEstimacion
}

/** Cantidad con coma decimal y la precisión de la web (2, 1 o 0 decimales según su tamaño). */
@Suppress("MagicNumber")
fun formatearCantidad(valor: Double): String {
    val decimales = when {
        abs(valor) < 10 -> 2
        abs(valor) < 100 -> 1
        else -> 0
    }
    var factor = 1.0
    repeat(decimales) { factor *= 10 }
    val redondeado = round(valor * factor) / factor
    val entero = redondeado.toLong()
    val parteEntera = abs(entero).toString().reversed().chunked(3).joinToString(".").reversed()
    val signo = if (redondeado < 0) "-" else ""
    val fraccion = ((abs(redondeado) - abs(entero)) * factor).let { round(it).toLong() }
    if (decimales == 0 || fraccion == 0L) return "$signo$parteEntera"
    val texto = fraccion.toString().padStart(decimales, '0').trimEnd('0')
    return "$signo$parteEntera,$texto"
}

/** Rango en una misma unidad legible, como la web: "150 a 180 ml" o "2,1 a 2,6 L". */
@Suppress("MagicNumber")
fun rangoConsumo(minimo: Double, maximo: Double, unidad: String): String {
    val sub = when (unidad) {
        "L" -> "ml"
        "kg" -> "g"
        else -> null
    }
    val factor = if (sub != null && maximo < 1) 1000.0 else 1.0
    return "${formatearCantidad(minimo * factor)} a ${formatearCantidad(maximo * factor)} ${if (factor == 1000.0) sub else unidad}"
}
