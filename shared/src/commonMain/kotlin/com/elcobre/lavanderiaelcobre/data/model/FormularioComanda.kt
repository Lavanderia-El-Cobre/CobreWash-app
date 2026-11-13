package com.elcobre.lavanderiaelcobre.data.model

/** Prendas sugeridas para clientes particulares (las mismas de la web). */
val PrendasParticular = listOf(
    "CHAQUETA", "GEÓLOGO", "GORRO/CAPUCHÓN", "CAMISA", "PANTALÓN", "POLERA",
    "TOALLA", "CALCETA", "BOLSO", "BOXER", "POLERÓN", "PIJAMA",
)

/** Prendas sugeridas para hoteles (las mismas de la web). */
val PrendasHotel = listOf(
    "TOALLA BAÑO", "TOALLA MANO", "TOALLA PISO", "TOALLA FACIAL",
    "SÁBANAS KING / MATRIMONIO", "FUNDAS KING",
    "SÁBANAS 1 1/2 PLAZA / TWIN / SOLTERO", "FUNDAS 1 1/2 PLAZA / QUEEN",
    "FUNDAS CON CIERRE", "DUVET KING / MATRIMONIO",
    "DUVET 1 1/2 PLAZA / SOLTERO", "ALMOHADA", "CORTINA", "VISILLO",
    "PLUMÓN / COBERTOR", "FRAZADAS", "CUBRE COLCHÓN", "MANTEL", "CARPETA",
    "FUNDA SILLA", "FUNDA MESA", "SERVILLETA", "AVIÓN",
)

fun prendasSugeridas(tipo: TipoClienteFicha): List<String> =
    if (tipo == TipoClienteFicha.HOTEL) PrendasHotel else PrendasParticular

/** Línea editable del formulario. En hoteles se registra además cuántas prendas se recibieron. */
data class LineaFormulario(
    val prenda: String = "",
    val servicio: String = "Lavado",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0,
    val recibido: Int = 0,
    val detalle: String = "",
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

/** Datos del cliente cuando la recepción crea una ficha nueva junto con la comanda. */
data class NuevoClienteInput(
    val nombre: String,
    val tipo: TipoClienteFicha,
    val rut: String,
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
)

/**
 * Estado del formulario de crear/editar comanda. Las validaciones replican las de
 * `app/intranet/comandas/page.tsx` y los `@check` de `CrearComandaConFlujo`.
 */
data class FormularioComanda(
    val clienteId: String = "",
    val nuevoCliente: Boolean = false,
    val cliente: NuevoClienteInput = NuevoClienteInput("", TipoClienteFicha.PARTICULAR, ""),
    val empresa: String = "",
    val proyecto: String = "",
    val observaciones: String = "",
    val lineas: List<LineaFormulario> = listOf(LineaFormulario()),
) {
    val tipoCliente: TipoClienteFicha get() = cliente.tipo
    val total: Double get() = lineasValidas.sumOf { it.subtotal }
    val lineasValidas: List<LineaFormulario> get() = lineas.filter { it.prenda.isNotBlank() }

    /** Mensaje del primer problema que impide guardar, o `null` si se puede enviar. */
    fun validar(): String? {
        val sinCliente = if (nuevoCliente) cliente.nombre.isBlank() else clienteId.isBlank()
        if (sinCliente || lineasValidas.isEmpty()) return "Selecciona un cliente y agrega al menos una prenda."
        return (if (nuevoCliente) validarClienteNuevo() else null) ?: validarLineasYTextos()
    }

    private fun validarClienteNuevo(): String? = when {
        !Validaciones.rutValido(cliente.rut) -> "Ingresa un RUT chileno válido para el cliente."
        cliente.telefono.isNotBlank() && !Validaciones.telefonoValido(cliente.telefono) ->
            "Ingresa un celular o teléfono fijo chileno válido."
        cliente.email.isNotBlank() && !Validaciones.correoValido(cliente.email) -> "Ingresa un correo válido."
        else -> null
    }

    private fun validarLineasYTextos(): String? = when {
        lineasValidas.any { it.cantidad <= 0 } -> "La cantidad de cada prenda debe ser mayor que cero."
        lineasValidas.any { it.precioUnitario < 0 } -> "El precio unitario no puede ser negativo."
        lineasValidas.size > MAX_LINEAS -> "Una comanda admite hasta $MAX_LINEAS líneas."
        empresa.length > 120 || proyecto.length > 120 -> "Empresa y proyecto admiten hasta 120 caracteres."
        observaciones.length > 300 -> "Las observaciones admiten hasta 300 caracteres."
        else -> null
    }

    companion object {
        const val MAX_LINEAS = 100
    }
}

/** Texto que se guarda en `ComandaDetalle.detalle`; en hoteles resume entregado/recibido/pendiente. */
fun detalleParaGuardar(linea: LineaFormulario, tipo: TipoClienteFicha): String? {
    val nota = linea.detalle.trim()
    if (tipo != TipoClienteFicha.HOTEL) return nota.ifEmpty { null }
    val pendiente = (linea.cantidad - linea.recibido).coerceAtLeast(0)
    val base = "Entregado: ${linea.cantidad} · Recibido: ${linea.recibido} · Pendiente: $pendiente"
    return if (nota.isEmpty()) base else "$base · $nota"
}

private val DetalleHotel = Regex("^Entregado: \\d+ · Recibido: (\\d+) · Pendiente: \\d+(?: · (.*))?$")

/** Inverso de [detalleParaGuardar] para hoteles: recibido y la nota libre. */
fun leerDetalleHotel(valor: String?): Pair<Int, String> {
    val match = valor?.let { DetalleHotel.matchEntire(it) } ?: return 0 to valor.orEmpty()
    return (match.groupValues[1].toIntOrNull() ?: 0) to match.groupValues[2]
}

/** Formulario precargado para editar una comanda pendiente. */
fun formularioDesde(comanda: ComandaGestion): FormularioComanda = FormularioComanda(
    clienteId = comanda.cliente.id,
    cliente = NuevoClienteInput(
        nombre = comanda.cliente.nombre,
        tipo = comanda.cliente.tipo,
        rut = comanda.cliente.rut.orEmpty(),
        telefono = comanda.cliente.telefono.orEmpty(),
        email = comanda.cliente.email.orEmpty(),
        direccion = comanda.cliente.direccion.orEmpty(),
    ),
    empresa = comanda.empresa.orEmpty(),
    proyecto = comanda.proyecto.orEmpty(),
    observaciones = comanda.observaciones.orEmpty(),
    lineas = comanda.lineas.map {
        val (recibido, nota) = if (comanda.cliente.tipo == TipoClienteFicha.HOTEL) leerDetalleHotel(it.detalle) else 0 to it.detalle
        LineaFormulario(it.prenda, it.servicio, it.cantidad, it.precioUnitario, recibido, nota)
    }.ifEmpty { listOf(LineaFormulario()) },
)

/** Valida los datos de una ficha de cliente (crear o editar desde Clientes). */
fun validarFichaCliente(input: NuevoClienteInput): String? = when {
    input.nombre.isBlank() -> "El nombre del cliente es obligatorio."
    input.nombre.length > 120 -> "El nombre admite hasta 120 caracteres."
    input.rut.isNotBlank() && !Validaciones.rutValido(input.rut) -> "El RUT ingresado no es válido."
    input.telefono.isNotBlank() && !Validaciones.telefonoValido(input.telefono) -> "Ingresa un celular o teléfono fijo chileno válido."
    input.email.isNotBlank() && !Validaciones.correoValido(input.email) -> "Ingresa un correo válido."
    input.direccion.length > 200 -> "La dirección admite hasta 200 caracteres."
    else -> null
}

/** Formatea montos en pesos chilenos: $12.500. */
fun clp(valor: Double): String {
    val entero = kotlin.math.round(valor).toLong()
    val signo = if (entero < 0) "-" else ""
    val texto = kotlin.math.abs(entero).toString().reversed().chunked(3).joinToString(".").reversed()
    return "$signo$$texto"
}
