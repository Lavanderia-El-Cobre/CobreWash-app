package com.elcobre.lavanderiaelcobre.data.model

import kotlin.random.Random

/**
 * Validaciones de formularios de la intranet, portadas de `lib/validators.ts` y
 * `lib/auth-validation.ts` de la web para que la app rechace lo mismo antes de llamar
 * a Firebase (las reglas `@check` del servidor siguen siendo la última palabra).
 */
@Suppress("TooManyFunctions") // Catálogo de validadores independientes, como lib/validators.ts.
object Validaciones {

    private val CODIGOS_AREA_FIJO = setOf(
        "2", "32", "33", "34", "35", "41", "42", "43", "44", "45",
        "51", "52", "53", "55", "57", "58", "61", "63", "64", "65",
        "67", "71", "72", "73", "75",
    )

    private val CORREO = Regex("^\\S+@\\S+\\.\\S+$")

    fun limpiarRut(valor: String): String = valor.filter { it.isDigit() || it == 'k' || it == 'K' }.uppercase()

    private fun digitoVerificador(cuerpo: String): String {
        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.indices.reversed()) {
            suma += cuerpo[i].digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        return when (val resto = 11 - (suma % 11)) {
            11 -> "0"
            10 -> "K"
            else -> resto.toString()
        }
    }

    /** RUT con puntos y guion (12.345.678-5), mientras se escribe. */
    fun formatearRut(valor: String): String {
        val limpio = limpiarRut(valor).take(9)
        if (limpio.length <= 1) return limpio
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        val agrupado = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        return "$agrupado-$dv"
    }

    fun rutValido(valor: String): Boolean {
        val limpio = limpiarRut(valor)
        val cuerpo = limpio.dropLast(1)
        return limpio.length >= 2 && cuerpo.all { it.isDigit() } && digitoVerificador(cuerpo) == limpio.takeLast(1)
    }

    /** Los nueve dígitos nacionales, sin +56 ni prefijo de carrier. */
    fun limpiarTelefono(valor: String): String {
        var digitos = valor.filter { it.isDigit() }
        val conCodigoPais = valor.trimStart().startsWith("+56") || Regex("^\\s*\\+?56[\\s()-]").containsMatchIn(valor)
        if ((conCodigoPais || digitos.length >= 10) && digitos.startsWith("56")) digitos = digitos.drop(2)
        if (digitos.length >= 10 && digitos.startsWith("0")) digitos = digitos.drop(1)
        return digitos.take(9)
    }

    /** "Celular", "Teléfono fijo" o `null` si no es un número chileno válido. */
    fun tipoTelefono(valor: String): String? {
        val d = limpiarTelefono(valor)
        return when {
            d.length != 9 -> null
            d.startsWith("9") -> "Celular"
            d.startsWith("2") -> "Teléfono fijo"
            d.take(2) in CODIGOS_AREA_FIJO -> "Teléfono fijo"
            else -> null
        }
    }

    fun telefonoValido(valor: String): Boolean = tipoTelefono(valor) != null

    fun formatearTelefono(valor: String): String {
        val d = limpiarTelefono(valor)
        if (d.isEmpty()) return ""
        val largoArea = if (d.startsWith("9") || d.startsWith("2")) 1 else minOf(2, d.length)
        return listOf("+56 ${d.take(largoArea)}", d.drop(largoArea).take(if (largoArea == 1) 4 else 3), d.drop(5).take(4))
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    fun correoValido(valor: String): Boolean = CORREO.matches(valor.trim())

    /** `null` si la contraseña cumple las reglas de la web; si no, el motivo. */
    fun problemaContrasena(contrasena: String): String? = when {
        contrasena.length < 8 -> "Mínimo 8 caracteres"
        contrasena.none { it.isLetter() } -> "Debe incluir al menos una letra"
        contrasena.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    /** Compara nombres de catálogo sin tildes ni mayúsculas, como `normalizarCatalogo` de la web. */
    fun normalizarCatalogo(valor: String): String = valor.trim().lowercase()
        .map { TILDES[it] ?: it }
        .joinToString("")

    private val TILDES = mapOf(
        'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ú' to 'u', 'ü' to 'u', 'ñ' to 'n',
        'à' to 'a', 'è' to 'e', 'ì' to 'i', 'ò' to 'o', 'ù' to 'u',
    )

    private const val ALFABETO = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /** Número público `ELCOBRE-XXXX`: cuatro caracteres con al menos una letra y un número. */
    fun generarNumeroComanda(random: Random = Random.Default): String {
        while (true) {
            val sufijo = (1..4).map { ALFABETO[random.nextInt(ALFABETO.length)] }.joinToString("")
            if (sufijo.any { it.isLetter() } && sufijo.any { it.isDigit() }) return "ELCOBRE-$sufijo"
        }
    }
}
