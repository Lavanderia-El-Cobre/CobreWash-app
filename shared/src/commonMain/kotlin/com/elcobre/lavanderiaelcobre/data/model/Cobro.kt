package com.elcobre.lavanderiaelcobre.data.model

import kotlin.math.floor
import kotlin.math.round
import kotlinx.serialization.Serializable

/**
 * Cobro de comandas por prendas o por peso, portado de `lib/comandas/cobro.ts` y de
 * `leerPesoKg`/`repartirPeso` de `lib/ia/comanda.ts`. Los `@check` de
 * `CrearComandaConFlujo` y `EditarComandaConDetalles` validan lo mismo en el servidor.
 */
@Serializable
enum class ModalidadCobro(val etiqueta: String) {
    PRENDA("Por prendas"),
    KILO("Por kg"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): ModalidadCobro = entries.firstOrNull { it.name == codigo } ?: PRENDA
    }
}

/**
 * Utilidades de cálculo, parseo y formateo para cobros por prenda y por kilogramo.
 * Replica el redondeo de JavaScript (`Math.round`) y el reparto proporcional de la web.
 */
@Suppress("MagicNumber")
object Cobro {
    const val PRECIO_KG_MAXIMO = 1_000_000
    const val PESO_MAXIMO_KG = 10_000.0
    const val PRENDA_NOMBRE_MINIMO = 2
    const val PRENDA_NOMBRE_MAXIMO = 60

    /** Línea que se guarda cuando una comanda por kg no detalla sus prendas. */
    const val PRENDA_POR_KILO = "ROPA POR KILO"

    /** Redondeo como `Math.round` de JavaScript (las mitades suben). */
    private fun redondear(valor: Double): Double = floor(valor + 0.5)

    private fun centesimas(valor: Double): Double = redondear(valor * 100) / 100

    /** Total de una comanda por kg: peso × precio, redondeado al peso. */
    fun totalPorKg(pesoKg: Double, precioKg: Double): Double = redondear(pesoKg * precioKg)

    /**
     * Subtotales de cada línea por kg. Cada línea cobra su parte del peso; los
     * redondeos se reparten para que la suma sea el total por kg y ninguna línea se
     * aleje más de $1 de su peso × precio.
     */
    fun subtotalesPorKg(pesos: List<Double>, precioKg: Double): List<Double> {
        val exactos = pesos.map { it * precioKg }
        val base = exactos.map { floor(it) }.toMutableList()
        var faltante = totalPorKg(pesos.sum(), precioKg) - base.sum()
        val orden = exactos.indices.sortedByDescending { exactos[it] - base[it] }
        for (i in orden) {
            if (faltante <= 0) break
            base[i] += 1.0
            faltante -= 1
        }
        return base
    }

    /** Lee el precio por kg escrito ("2.500", "2500"); `null` si no es válido. */
    fun leerPrecioKg(texto: String): Double? {
        val limpio = texto.trim().filterNot { it == '$' || it == '.' || it.isWhitespace() }.replace(',', '.')
        if (!Regex("^\\d+(\\.\\d+)?$").matches(limpio)) return null
        val precio = redondear(limpio.toDouble())
        return precio.takeIf { it > 0 && it <= PRECIO_KG_MAXIMO }
    }

    /** Lee el peso escrito ("12,5" o "12.5"); `null` si no es válido. */
    fun leerPesoKg(texto: String): Double? {
        val limpio = texto.trim().replace(',', '.')
        if (!Regex("^\\d+(\\.\\d+)?$").matches(limpio)) return null
        val kg = limpio.toDouble()
        return kg.takeIf { it > 0 && it <= PESO_MAXIMO_KG }
    }

    /**
     * Reparte el peso total entre las líneas según la cantidad de prendas usando el método
     * del resto mayor en centésimas con piso de 0,01 kg por línea (sin ceros ni negativos).
     */
    fun repartirPeso(totalKg: Double, cantidades: List<Int>): List<Double> {
        if (totalKg <= 0 || cantidades.isEmpty()) return cantidades.map { 0.0 }
        val unidades = cantidades.map { maxOf(1, it) }
        val suma = unidades.sum().toDouble()
        val totalCent = redondear(totalKg * 100).toInt()
        if (totalCent < unidades.size) return repartirMicroPeso(totalKg, unidades, suma)
        val exactos = unidades.map { totalCent * it / suma }
        val base = exactos.map { maxOf(1, floor(it).toInt()) }.toMutableList()
        var resto = totalCent - base.sum()
        if (resto > 0) {
            val orden = exactos.indices.sortedWith(
                compareByDescending<Int> { exactos[it] - base[it] }.thenByDescending { it },
            )
            for (k in 0 until resto) base[orden[k % orden.size]] += 1
        } else if (resto < 0) {
            val orden = base.indices.sortedWith(
                compareByDescending<Int> { base[it] - exactos[it] }.thenBy { it },
            )
            for (i in orden) {
                while (resto < 0 && base[i] > 1) {
                    base[i] -= 1
                    resto += 1
                }
            }
        }
        return base.map { it / 100.0 }
    }

    /** Nombre de una prenda escrita a mano: sin espacios de más y en mayúsculas. */
    fun normalizarPrendaNueva(texto: String): String =
        texto.replace(Regex("\\s+"), " ").trim().uppercase().take(PRENDA_NOMBRE_MAXIMO).trim()

    /** Peso con coma decimal, como lo muestra la web ("3,3"). */
    fun formatearPeso(kg: Double): String {
        val texto = centesimas(kg).toString().removeSuffix(".0")
        return texto.replace('.', ',')
    }

    /** Precio por kg sin decimales, para precargar el campo. */
    fun formatearPrecio(precio: Double?): String =
        precio?.takeIf { it > 0 }?.let { round(it).toLong().toString() }.orEmpty()
}

private fun repartirMicroPeso(totalKg: Double, unidades: List<Int>, suma: Double): List<Double> {
    val partes = unidades.map { maxOf(0.0001, round(totalKg * it / suma * 10_000) / 10_000) }.toMutableList()
    val diff = round((totalKg - partes.sum()) * 10_000) / 10_000
    val idxMax = partes.indices.maxByOrNull { partes[it] } ?: 0
    if (partes[idxMax] + diff > 0) {
        partes[idxMax] = round((partes[idxMax] + diff) * 10_000) / 10_000
    }
    return partes
}
