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

/** Devuelve la lista de prendas sugeridas según el tipo de cliente (hotel o particular). */
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

/** Línea con su parte del peso y su cobro ya calculados, lista para guardar. */
data class LineaCobrada(
    val linea: LineaFormulario,
    val pesoKg: Double,
    val precioUnitario: Double,
    val subtotal: Double,
)

/** Cobro resuelto de una comanda: modalidad, precio por kg y cada línea con su peso. */
data class CobroResuelto(
    val modalidad: ModalidadCobro,
    val precioKg: Double?,
    val lineas: List<LineaCobrada>,
) {
    val total: Double get() = lineas.sumOf { it.subtotal }
}

/**
 * Estado del formulario de crear/editar comanda. Las validaciones replican las de
 * `app/intranet/comandas/page.tsx` y los `@check` de `CrearComandaConFlujo` y
 * `EditarComandaConDetalles`: un solo servicio por comanda, peso total obligatorio y
 * cobro por prendas (cantidad × precio) o por kg (peso × precio por kg).
 */
data class FormularioComanda(
    val clienteId: String = "",
    val nuevoCliente: Boolean = false,
    val cliente: NuevoClienteInput = NuevoClienteInput("", TipoClienteFicha.PARTICULAR, ""),
    val empresa: String = "",
    val proyecto: String = "",
    val observaciones: String = "",
    val lineas: List<LineaFormulario> = listOf(LineaFormulario()),
    /** Servicio de toda la comanda; todas las líneas lo llevan, como en la web. */
    val servicio: String = "Lavado",
    val modalidad: ModalidadCobro = ModalidadCobro.PRENDA,
    /** Peso total tal como se escribe ("12,5"). */
    val pesoKg: String = "",
    /** Precio por kg tal como se escribe; solo se usa en la modalidad KILO. */
    val precioKg: String = "",
    /** Cliente nuevo: además de la ficha, crear su cuenta en la plataforma web. */
    val registrarCuenta: Boolean = false,
    /** Confirmación de que se informó al cliente sobre su cuenta, términos y privacidad. */
    val clienteInformado: Boolean = false,
) {
    val tipoCliente: TipoClienteFicha get() = cliente.tipo
    val porKg: Boolean get() = modalidad == ModalidadCobro.KILO
    val lineasValidas: List<LineaFormulario> get() = lineas.filter { it.prenda.isNotBlank() }

    /** Por kg las prendas son opcionales: sin detalle se registra la ropa a granel. */
    val lineasParaGuardar: List<LineaFormulario>
        get() = lineasValidas.ifEmpty {
            if (porKg) listOf(LineaFormulario(prenda = Cobro.PRENDA_POR_KILO, servicio = servicio, cantidad = 1)) else emptyList()
        }

    /** Total a mostrar: el cobro resuelto o, mientras falten datos, la suma por prendas. */
    val total: Double
        get() = resolverCobro()?.total ?: if (porKg) 0.0 else lineasValidas.sumOf { it.subtotal }

    /** Reparte el peso y calcula cada subtotal; `null` si falta el peso o el precio por kg. */
    @Suppress("ReturnCount") // Salidas tempranas por cada dato faltante.
    fun resolverCobro(): CobroResuelto? {
        val lineasGuardar = lineasParaGuardar.ifEmpty { return null }
        val pesoTotal = Cobro.leerPesoKg(pesoKg) ?: return null
        val precio = if (porKg) Cobro.leerPrecioKg(precioKg) ?: return null else null
        val pesos = Cobro.repartirPeso(pesoTotal, lineasGuardar.map { it.cantidad })
        val subtotalesKg = precio?.let { Cobro.subtotalesPorKg(pesos, it) }
        return CobroResuelto(
            modalidad = modalidad,
            precioKg = precio,
            lineas = lineasGuardar.mapIndexed { i, linea ->
                if (precio != null && subtotalesKg != null) {
                    LineaCobrada(linea, pesos[i], precio, subtotalesKg[i])
                } else {
                    LineaCobrada(linea, pesos[i], linea.precioUnitario, linea.subtotal)
                }
            },
        )
    }

    /** Mensaje del primer problema que impide guardar, o `null` si se puede enviar. */
    @Suppress("ReturnCount") // Validaciones en el mismo orden que la web.
    fun validar(): String? {
        val sinCliente = if (nuevoCliente) cliente.nombre.isBlank() else clienteId.isBlank()
        if (sinCliente || (!porKg && lineasValidas.isEmpty())) return "Selecciona un cliente y agrega al menos una prenda."
        if (porKg && Cobro.leerPrecioKg(precioKg) == null) return "Ingresa el precio por kg (mayor que 0)."
        if (Cobro.leerPesoKg(pesoKg) == null) return "Ingresa el peso total de la comanda en kg (mayor que 0 y hasta 10.000)."
        return (if (nuevoCliente) validarClienteNuevo() else null) ?: validarLineasYTextos()
    }

    private fun validarClienteNuevo(): String? {
        val correo = cliente.email.trim()
        return when {
            !Validaciones.rutValido(cliente.rut) -> "Ingresa un RUT chileno válido para el cliente."
            Telefonos.validar(cliente.telefono) != null -> Telefonos.validar(cliente.telefono)
            registrarCuenta && !Validaciones.correoValido(correo) -> "Ingresa el correo del cliente: se usa para crear su cuenta."
            correo.isNotEmpty() && !Validaciones.correoValido(correo) -> "Ingresa un correo válido o déjalo en blanco."
            registrarCuenta && !clienteInformado -> "Confirma que informaste al cliente sobre su cuenta."
            else -> null
        }
    }

    private fun validarLineasYTextos(): String? = when {
        lineasValidas.any { it.cantidad <= 0 } -> "La cantidad de cada prenda debe ser mayor que cero."
        lineasValidas.any { it.precioUnitario < 0 } -> "El precio unitario no puede ser negativo."
        lineasValidas.any { Cobro.normalizarPrendaNueva(it.prenda).length < Cobro.PRENDA_NOMBRE_MINIMO } ->
            "El nombre de cada prenda debe tener entre 2 y 60 caracteres."
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
        // En una comanda por kg el precio unitario guardado es el precio por kg, no el de la prenda.
        val precio = if (comanda.modalidad == ModalidadCobro.KILO) 0.0 else it.precioUnitario
        LineaFormulario(it.prenda, it.servicio, it.cantidad, precio, recibido, nota)
    }.ifEmpty { listOf(LineaFormulario()) },
    servicio = comanda.lineas.firstOrNull()?.servicio ?: "Lavado",
    modalidad = comanda.modalidad,
    pesoKg = comanda.lineas.sumOf { it.pesoKg ?: 0.0 }.takeIf { it > 0 }?.let(Cobro::formatearPeso).orEmpty(),
    precioKg = Cobro.formatearPrecio(comanda.precioKg),
)

/** Valida los datos de una ficha de cliente (crear o editar desde Clientes). */
fun validarFichaCliente(input: NuevoClienteInput): String? = when {
    input.nombre.isBlank() -> "El nombre del cliente es obligatorio."
    input.nombre.length > 120 -> "El nombre admite hasta 120 caracteres."
    input.rut.isNotBlank() && !Validaciones.rutValido(input.rut) -> "El RUT ingresado no es válido."
    Telefonos.validar(input.telefono) != null -> Telefonos.validar(input.telefono)
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
