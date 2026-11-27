package com.elcobre.lavanderiaelcobre.data.model

/**
 * Teléfonos de Chile y del resto de Latinoamérica, portado de `lib/telefonos.ts`.
 *
 * El valor guardado queda en formato internacional con el prefijo del país y los
 * dígitos agrupados con espacios (`+56 9 1234 5678`, `+51 912 345 678`…). La persona
 * solo escribe su número nacional; los valores antiguos sin `+` se leen como chilenos.
 */
data class PaisTelefono(
    /** Opción del selector: país, o "CL-FIJO" para los fijos chilenos. */
    val id: String,
    val nombre: String,
    val etiqueta: String,
    /** Código de país sin `+`. */
    val codigo: String,
    /** Dígito fijo tras el código que la persona no escribe (Chile celular: 9). */
    val prefijo: String? = null,
    val largos: List<Int>,
    val grupos: Map<Int, List<Int>> = emptyMap(),
    val patron: Regex? = null,
    val ejemplo: String,
) {
    val largoMaximo: Int get() = largos.max()

    /** Prefijo visible del selector, por ejemplo `+56 9` o `+51`. */
    val prefijoVisible: String get() = "+$codigo" + (prefijo?.let { " $it" } ?: "")
}

/** Resultado de analizar un teléfono: país detectado, dígitos nacionales y si el prefijo es válido. */
data class TelefonoDetectado(val pais: PaisTelefono, val nacional: String, val reconocido: Boolean)

/** Catálogo latinoamericano, detección de país, validación y formateo canónico de teléfonos. */
// Tabla de países y validaciones con salidas tempranas, portadas línea a línea de lib/telefonos.ts.
@Suppress("TooManyFunctions", "MagicNumber", "ReturnCount", "MaxLineLength")
object Telefonos {
    const val PAIS_PREDETERMINADO = "CL"

    private val OCHO = listOf(8)
    private val GRUPO_OCHO = mapOf(8 to listOf(4, 4))

    val paises: List<PaisTelefono> = listOf(
        PaisTelefono("CL", "Chile", "Chile · celular", "56", "9", listOf(8), mapOf(8 to listOf(4, 4)), ejemplo = "1234 5678"),
        PaisTelefono("CL-FIJO", "Chile", "Chile · teléfono fijo", "56", null, listOf(9), ejemplo = "55 234 5678"),
        PaisTelefono(
            "AR", "Argentina", "Argentina", "54", null, listOf(10, 11),
            mapOf(10 to listOf(2, 4, 4), 11 to listOf(1, 2, 4, 4)), Regex("^(9[1-9]\\d{9}|[1-8]\\d{9})$"), "11 2345 6789",
        ),
        PaisTelefono("BO", "Bolivia", "Bolivia", "591", null, OCHO, GRUPO_OCHO, Regex("^[2-7]\\d{7}$"), "7123 4567"),
        PaisTelefono(
            "BR", "Brasil", "Brasil", "55", null, listOf(10, 11),
            mapOf(10 to listOf(2, 4, 4), 11 to listOf(2, 5, 4)), Regex("^[1-9]{2}(9\\d{8}|[2-8]\\d{7})$"), "11 91234 5678",
        ),
        PaisTelefono("CO", "Colombia", "Colombia", "57", null, listOf(10), mapOf(10 to listOf(3, 3, 4)), Regex("^(3\\d{9}|60\\d{8})$"), "300 123 4567"),
        PaisTelefono("CR", "Costa Rica", "Costa Rica", "506", null, OCHO, GRUPO_OCHO, ejemplo = "8312 3456"),
        PaisTelefono("CU", "Cuba", "Cuba", "53", null, OCHO, GRUPO_OCHO, ejemplo = "5123 4567"),
        PaisTelefono(
            "EC", "Ecuador", "Ecuador", "593", null, listOf(8, 9),
            mapOf(8 to listOf(1, 3, 4), 9 to listOf(2, 3, 4)), Regex("^(9\\d{8}|[2-7]\\d{7})$"), "99 123 4567",
        ),
        PaisTelefono("SV", "El Salvador", "El Salvador", "503", null, OCHO, GRUPO_OCHO, ejemplo = "7012 3456"),
        PaisTelefono("GT", "Guatemala", "Guatemala", "502", null, OCHO, GRUPO_OCHO, ejemplo = "5123 4567"),
        PaisTelefono("HN", "Honduras", "Honduras", "504", null, OCHO, GRUPO_OCHO, ejemplo = "9123 4567"),
        PaisTelefono("MX", "México", "México", "52", null, listOf(10), mapOf(10 to listOf(2, 4, 4)), ejemplo = "55 1234 5678"),
        PaisTelefono("NI", "Nicaragua", "Nicaragua", "505", null, OCHO, GRUPO_OCHO, ejemplo = "8123 4567"),
        PaisTelefono("PA", "Panamá", "Panamá", "507", null, listOf(7, 8), mapOf(7 to listOf(3, 4), 8 to listOf(4, 4)), ejemplo = "6123 4567"),
        PaisTelefono("PY", "Paraguay", "Paraguay", "595", null, listOf(8, 9), mapOf(8 to listOf(2, 3, 3), 9 to listOf(3, 3, 3)), ejemplo = "981 123 456"),
        PaisTelefono(
            "PE", "Perú", "Perú", "51", null, listOf(8, 9),
            mapOf(8 to listOf(2, 3, 3), 9 to listOf(3, 3, 3)), Regex("^(9\\d{8}|[1-8]\\d{7})$"), "912 345 678",
        ),
        PaisTelefono("PR", "Puerto Rico", "Puerto Rico", "1", null, listOf(10), mapOf(10 to listOf(3, 3, 4)), Regex("^(787|939)\\d{7}$"), "787 123 4567"),
        PaisTelefono(
            "DO", "República Dominicana", "República Dominicana", "1", null, listOf(10),
            mapOf(10 to listOf(3, 3, 4)), Regex("^(809|829|849)\\d{7}$"), "809 123 4567",
        ),
        PaisTelefono("UY", "Uruguay", "Uruguay", "598", null, listOf(8), mapOf(8 to listOf(2, 3, 3)), Regex("^[249]\\d{7}$"), "94 123 456"),
        PaisTelefono("VE", "Venezuela", "Venezuela", "58", null, listOf(10), mapOf(10 to listOf(3, 3, 4)), Regex("^[24-9]\\d{9}$"), "412 123 4567"),
    )

    private val porId = paises.associateBy { it.id }

    // Códigos más largos primero: ninguno de tres dígitos (50x, 59x) empieza como uno de dos.
    private val codigos = paises.map { it.codigo }.distinct().sortedByDescending { it.length }

    fun pais(id: String): PaisTelefono = porId[id] ?: porId.getValue(PAIS_PREDETERMINADO)

    private fun esFijoChileno(pais: PaisTelefono) = pais.id == "CL-FIJO"
    private fun soloDigitos(valor: String) = valor.filter(Char::isDigit)
    private fun esInternacional(texto: String) = texto.startsWith("+") || texto.startsWith("00")
    private fun digitosInternacionales(texto: String) =
        if (texto.startsWith("00")) soloDigitos(texto).drop(2) else soloDigitos(texto)

    private fun agrupar(digitos: String, grupos: List<Int>): String {
        val partes = mutableListOf<String>()
        var inicio = 0
        for (largo in grupos) {
            if (inicio >= digitos.length) break
            partes += digitos.substring(inicio, minOf(digitos.length, inicio + largo))
            inicio += largo
        }
        if (inicio < digitos.length) partes += digitos.substring(inicio)
        return partes.joinToString(" ")
    }

    /** Número nacional agrupado, sin el prefijo del país. */
    fun formatearNacional(pais: PaisTelefono, nacional: String): String {
        val digitos = soloDigitos(nacional).take(pais.largoMaximo)
        if (digitos.isEmpty()) return ""
        if (esFijoChileno(pais)) return Validaciones.formatearTelefono(digitos).removePrefix("+56 ")
        // Mientras se escribe se usa la agrupación del menor largo que aún cabe.
        val largo = pais.largos.firstOrNull { it >= digitos.length } ?: pais.largoMaximo
        return agrupar(digitos, pais.grupos[largo] ?: listOf(digitos.length))
    }

    /** Valor completo a guardar; vacío si no hay dígitos. */
    fun formatear(paisId: String, nacional: String): String {
        val pais = pais(paisId)
        val digitos = soloDigitos(nacional).take(pais.largoMaximo)
        if (digitos.isEmpty()) return ""
        if (esFijoChileno(pais)) return Validaciones.formatearTelefono(digitos)
        return "${pais.prefijoVisible} ${formatearNacional(pais, digitos)}"
    }

    private fun chileno(digitos: String): TelefonoDetectado =
        if (digitos.isEmpty() || digitos.startsWith("9")) {
            TelefonoDetectado(pais("CL"), digitos.drop(1).take(8), true)
        } else {
            TelefonoDetectado(pais("CL-FIJO"), digitos.take(9), true)
        }

    /** Detecta el país desde el prefijo guardado; sin `+` se asume Chile. */
    fun detectar(valor: String): TelefonoDetectado {
        val texto = valor.trim()
        if (texto.isEmpty()) return TelefonoDetectado(pais(PAIS_PREDETERMINADO), "", true)
        if (!esInternacional(texto)) {
            // Compatibilidad con números chilenos escritos a mano (9 dígitos, 56…, 0…).
            var digitos = soloDigitos(texto)
            if (digitos.length >= 10 && digitos.startsWith("56")) digitos = digitos.drop(2)
            if (digitos.length >= 10 && digitos.startsWith("0")) digitos = digitos.drop(1)
            return chileno(digitos)
        }
        val digitos = digitosInternacionales(texto)
        val codigo = codigos.firstOrNull { digitos.startsWith(it) }
            ?: return TelefonoDetectado(pais(PAIS_PREDETERMINADO), "", false)
        val resto = digitos.drop(codigo.length)
        if (codigo == "56") return chileno(resto)
        if (codigo == "1") {
            val pais = pais(if (Regex("^(787|939)").containsMatchIn(resto)) "PR" else "DO")
            return TelefonoDetectado(pais, resto.take(pais.largoMaximo), true)
        }
        val pais = paises.first { it.codigo == codigo }
        return TelefonoDetectado(pais, resto.take(pais.largoMaximo), true)
    }

    /** Dígitos nacionales de un valor guardado, leídos según la opción elegida. */
    fun extraerNacional(pais: PaisTelefono, valor: String): String {
        if (valor.isBlank()) return ""
        val detectado = detectar(valor)
        if (detectado.pais.id == pais.id) return detectado.nacional
        var digitos = soloDigitos(valor)
        if (digitos.startsWith(pais.codigo)) digitos = digitos.drop(pais.codigo.length)
        pais.prefijo?.let { if (digitos.startsWith(it)) digitos = digitos.drop(it.length) }
        return digitos.take(pais.largoMaximo)
    }

    /**
     * Interpreta lo que se escribe o pega en el campo del número: un número con `+`
     * cambia de país; el código de país o el 9 del celular chileno se quitan.
     */
    fun interpretarEntrada(pais: PaisTelefono, texto: String): Pair<PaisTelefono, String> {
        if ('+' in texto) {
            val detectado = detectar(texto.substring(texto.indexOf('+')))
            if (detectado.reconocido) return detectado.pais to detectado.nacional
        }
        var digitos = soloDigitos(texto)
        val maximo = pais.largoMaximo
        val completo = pais.codigo + pais.prefijo.orEmpty()
        val prefijo = pais.prefijo
        if (digitos.length > maximo && digitos.startsWith(completo) && digitos.length - completo.length <= maximo) {
            digitos = digitos.drop(completo.length)
        } else if (prefijo != null && digitos.length == maximo + prefijo.length && digitos.startsWith(prefijo)) {
            digitos = digitos.drop(prefijo.length)
        }
        return pais to digitos.take(maximo)
    }

    private fun listaDeLargos(largos: List<Int>): String =
        largos.foldIndexed("") { i, texto, largo ->
            if (i == 0) "$largo" else "$texto ${if (largo == 8 || largo == 11) "u" else "o"} $largo"
        }

    /** Error del número nacional para la opción elegida, o `null` si es válido o está vacío. */
    fun validarNacional(pais: PaisTelefono, nacional: String): String? {
        val digitos = soloDigitos(nacional)
        if (digitos.isEmpty()) return null
        if (pais.id == "CL") return if (digitos.length == 8) null else "El celular chileno debe tener 8 dígitos después de +56 9."
        if (esFijoChileno(pais)) {
            if (digitos.startsWith("9")) return "Los teléfonos fijos no empiezan con 9: elige «Chile · celular»."
            return if (Validaciones.tipoTelefono(digitos) == "Teléfono fijo") {
                null
            } else {
                "Ingresa un teléfono fijo chileno válido: código de área y número, 9 dígitos en total."
            }
        }
        if (digitos.length !in pais.largos) {
            return "El número de ${pais.nombre} debe tener ${listaDeLargos(pais.largos)} dígitos después de +${pais.codigo}."
        }
        if (pais.patron?.matches(digitos) == false) return "Revisa el número: no corresponde a un teléfono de ${pais.nombre}."
        return null
    }

    /** Error de un valor guardado o recibido, o `null` si es válido o está vacío. */
    fun validar(valor: String): String? {
        if (valor.isBlank()) return null
        val detectado = detectar(valor)
        if (!detectado.reconocido) return "Usa un teléfono de Chile o de otro país de Latinoamérica, con su prefijo."
        // El número completo debe calzar exactamente: no se aceptan dígitos sobrantes.
        if (detectado.nacional.isNotEmpty() &&
            soloDigitos(formatear(detectado.pais.id, detectado.nacional)) != normalizarDigitos(valor)
        ) {
            return "Revisa el teléfono: tiene más dígitos de los esperados."
        }
        return validarNacional(detectado.pais, detectado.nacional)
            ?: if (detectado.nacional.isNotEmpty()) null else "Ingresa el número de teléfono."
    }

    private fun normalizarDigitos(valor: String): String {
        val texto = valor.trim()
        if (esInternacional(texto)) return digitosInternacionales(texto)
        var digitos = soloDigitos(texto)
        if (digitos.length >= 10 && digitos.startsWith("56")) digitos = digitos.drop(2)
        if (digitos.length >= 10 && digitos.startsWith("0")) digitos = digitos.drop(1)
        return "56$digitos"
    }

    fun esValido(valor: String): Boolean = valor.isNotBlank() && validar(valor) == null

    /** Formato canónico de un valor válido; si no es válido se devuelve recortado tal cual. */
    fun normalizar(valor: String): String {
        val texto = valor.trim()
        if (texto.isEmpty() || validar(texto) != null) return texto
        val detectado = detectar(texto)
        return formatear(detectado.pais.id, detectado.nacional)
    }

    /** Descripción corta de un número válido para la opción elegida. */
    fun describir(pais: PaisTelefono): String = when {
        pais.id == "CL" -> "Celular chileno"
        esFijoChileno(pais) -> "Teléfono fijo chileno"
        else -> "Número de ${pais.nombre}"
    }
}
