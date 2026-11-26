package com.elcobre.lavanderiaelcobre.data.model

import kotlin.math.floor
import kotlin.math.round

/**
 * Cobro de comandas por prendas o por peso, portado de `lib/comandas/cobro.ts` y de
 * `leerPesoKg`/`repartirPeso` de `lib/ia/comanda.ts`. Los `@check` de
 * `CrearComandaConFlujo` y `EditarComandaConDetalles` validan lo mismo en el servidor.
 */
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
     * Reparte el peso total entre las líneas según la cantidad de prendas. La suma es
     * exacta (la última línea absorbe el redondeo).
     */
    fun repartirPeso(totalKg: Double, cantidades: List<Int>): List<Double> {
        if (totalKg <= 0 || cantidades.isEmpty()) return cantidades.map { 0.0 }
        val unidades = cantidades.map { maxOf(1, it) }
        val suma = unidades.sum().toDouble()
        val partes = unidades.map { centesimas(totalKg * it / suma) }.toMutableList()
        val resto = centesimas(totalKg - partes.sum())
        partes[partes.lastIndex] = centesimas(partes.last() + resto)
        return partes
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
