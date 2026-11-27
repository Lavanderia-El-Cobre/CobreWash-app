package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.IdentidadComanda
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.RecepcionRepository
import com.elcobre.lavanderiaelcobre.data.crearRecepcionRepository
import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.Cobro
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EstadoEstimacion
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.formularioDesde
import com.elcobre.lavanderiaelcobre.data.model.prendasSugeridas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Tamaño de página de la lista de comandas; "Cargar más" suma otra página. */
internal const val PAGINA_COMANDAS = 20

/**
 * Comandas de recepción y administración, como `app/intranet/comandas/page.tsx`:
 * lista con búsqueda por cliente y filtros por estado (con conteos), detalle,
 * creación, edición de pendientes, anulación con motivo y entrega de las listas.
 */
@Suppress("TooManyFunctions")
class ComandasViewModel(
    private val repository: RecepcionRepository = crearRecepcionRepository(),
    scopeOverride: CoroutineScope? = null,
    private val idNuevo: () -> String = ::uuidAleatorio,
) : GestionViewModel(scopeOverride) {

    var filtro by mutableStateOf(FiltroComandas())
        private set
    var comandas by mutableStateOf<List<ComandaGestion>>(emptyList())
        private set
    var total by mutableStateOf(0)
        private set
    var conteos by mutableStateOf<Map<EstadoComanda, Int>>(emptyMap())
        private set
    private var limite = PAGINA_COMANDAS
    private var busquedaJob: Job? = null

    // Número de consulta monotónico para descartar respuestas que lleguen fuera de orden.
    private var consulta = 0
    private var consultaDetalle = 0
    private var servicioElegidoPorUsuario = false

    val hayMas: Boolean get() = comandas.size < total

    var detalle by mutableStateOf<ComandaGestion?>(null)
        private set
    var cargandoDetalle by mutableStateOf(false)
        private set

    var catalogos by mutableStateOf(CatalogosComanda())
        private set

    /** Formulario abierto; [edicion] indica si edita una comanda existente. */
    var formulario by mutableStateOf<FormularioComanda?>(null)
        private set
    var edicion by mutableStateOf<ComandaGestion?>(null)
        private set
    var errorFormulario by mutableStateOf<String?>(null)
        private set

    // Identidad reutilizada en reintentos de alta para evitar duplicar la comanda en red inestable.
    private var identidad: IdentidadComanda? = null

    /** Consumo de insumos estimado por la web para el servicio y el peso del formulario. */
    var estimacion by mutableStateOf<EstadoEstimacion>(EstadoEstimacion.SinPeso)
        private set
    private var estimacionJob: Job? = null
    private var claveEstimacion: Pair<String, Double>? = null

    /** Consulta la página de comandas según [filtro]; con [mas] amplía el límite en una página. */
    fun refrescar(mas: Boolean = false) {
        if (mas && (cargando || !hayMas)) return
        if (mas) limite += PAGINA_COMANDAS
        val solicitud = ++consulta
        val filtroActual = filtro
        val limiteActual = limite
        cargar {
            val pagina = repository.comandas(filtroActual, 0, limiteActual)
            if (solicitud == consulta) {
                comandas = pagina.comandas
                total = pagina.total
                conteos = pagina.conteos
            }
        }
    }

    /** Activa o desactiva un [estado] en la selección múltiple del filtro y reinicia la paginación. */
    fun alternarEstado(estado: EstadoComanda) {
        val estados = if (estado in filtro.estados) filtro.estados - estado else filtro.estados + estado
        filtro = filtro.copy(estados = estados)
        limite = PAGINA_COMANDAS
        refrescar()
    }

    /** Limpia todos los filtros de estado seleccionados y vuelve a la primera página. */
    fun limpiarEstados() {
        filtro = filtro.copy(estados = emptySet())
        limite = PAGINA_COMANDAS
        refrescar()
    }

    /** La búsqueda espera a que se deje de escribir para no consultar por cada letra. */
    fun buscar(texto: String) {
        filtro = filtro.copy(cliente = texto)
        limite = PAGINA_COMANDAS
        busquedaJob?.cancel()
        busquedaJob = scope.launch {
            delay(ESPERA_BUSQUEDA_MS)
            refrescar()
        }
    }

    /** Muestra de inmediato la versión en caché del listado y solicita el detalle completo al servidor. */
    fun abrirDetalle(id: String) {
        val solicitud = ++consultaDetalle
        detalle = comandas.firstOrNull { it.id == id }
        cargandoDetalle = true
        cargar {
            try {
                val recibido = repository.detalle(id)
                if (solicitud == consultaDetalle) detalle = recibido ?: detalle
            } finally {
                if (solicitud == consultaDetalle) cargandoDetalle = false
            }
        }
    }

    fun cerrarDetalle() {
        consultaDetalle++
        cargandoDetalle = false
        detalle = null
    }

    /**
     * Carga los catálogos y, si el formulario aún no tiene precio por kg, propone el
     * del servicio elegido (como la web al abrir el formulario).
     */
    private fun cargarCatalogos() = cargar {
        catalogos = repository.catalogos()
        formulario?.let { f ->
            val servicio = f.servicio.takeIf { it.isNotBlank() && (edicion != null || servicioElegidoPorUsuario) }
                ?: catalogos.servicios.firstOrNull()?.nombre ?: f.servicio
            val precio = f.precioKg.ifBlank { Cobro.formatearPrecio(precioKgDe(servicio)) }
            formulario = f.copy(servicio = servicio, precioKg = precio)
            formulario?.let(::pedirEstimacion)
        }
    }

    /** Abre el formulario vacío para crear una nueva comanda y refresca los catálogos. */
    fun abrirNueva() {
        identidad = null
        edicion = null
        servicioElegidoPorUsuario = false
        errorFormulario = null
        formulario = FormularioComanda()
        cargarCatalogos()
    }

    /** Abre la edición solo si la comanda sigue editable (estado pendiente). */
    fun abrirEdicion(comanda: ComandaGestion) {
        if (!comanda.puedeEditar) return
        edicion = comanda
        servicioElegidoPorUsuario = false
        errorFormulario = null
        formulario = formularioDesde(comanda)
        cargarCatalogos()
    }

    /** Servicio de toda la comanda; propone su precio por kg si el campo está vacío o era el sugerido. */
    fun seleccionarServicio(nombre: String) {
        val f = formulario ?: return
        servicioElegidoPorUsuario = true
        val anteriorSugerido = Cobro.formatearPrecio(precioKgDe(f.servicio))
        val precio = if (f.precioKg.isBlank() || f.precioKg == anteriorSugerido) {
            Cobro.formatearPrecio(precioKgDe(nombre)).ifBlank { f.precioKg }
        } else {
            f.precioKg
        }
        val base = precioBase(nombre)
        actualizarFormulario(
            f.copy(
                servicio = nombre,
                precioKg = precio,
                lineas = if (base == null) f.lineas else f.lineas.map { it.copy(servicio = nombre, precioUnitario = base) },
            ),
        )
    }

    private fun precioKgDe(servicio: String): Double? =
        catalogos.servicios
            .firstOrNull { Validaciones.normalizarCatalogo(it.nombre) == Validaciones.normalizarCatalogo(servicio) }
            ?.precioKg

    fun cerrarFormulario() {
        if (guardando) return
        formulario = null
        edicion = null
        errorFormulario = null
        limpiarEstimacion()
    }

    private fun limpiarEstimacion() {
        estimacionJob?.cancel()
        claveEstimacion = null
        estimacion = EstadoEstimacion.SinPeso
    }

    fun actualizarFormulario(nuevo: FormularioComanda) {
        formulario = nuevo
        errorFormulario = null
        pedirEstimacion(nuevo)
    }

    /**
     * Pide la estimación a la web cuando cambian el servicio o el peso, esperando a que se
     * deje de escribir. Solo informa: sin conexión muestra "no disponible" y la comanda
     * se puede guardar igual.
     */
    private fun pedirEstimacion(f: FormularioComanda) {
        val kg = Cobro.leerPesoKg(f.pesoKg)
        val servicioId = catalogos.servicios
            .firstOrNull { Validaciones.normalizarCatalogo(it.nombre) == Validaciones.normalizarCatalogo(f.servicio) }?.id
        if (kg == null || servicioId == null) {
            estimacionJob?.cancel()
            claveEstimacion = null
            estimacion = EstadoEstimacion.SinPeso
            return
        }
        val clave = servicioId to kg
        if (clave == claveEstimacion) return
        claveEstimacion = clave
        estimacionJob?.cancel()
        estimacion = EstadoEstimacion.Cargando
        estimacionJob = scope.launch {
            delay(ESPERA_ESTIMACION_MS)
            estimacion = try {
                EstadoEstimacion.Lista(repository.estimarConsumo(servicioId, kg))
            } catch (_: OperacionComandaException) {
                EstadoEstimacion.NoDisponible
            }
        }
    }

    /** Al elegir un cliente del catálogo se adopta su tipo (hotel o particular). */
    fun seleccionarCliente(id: String) {
        val f = formulario ?: return
        val cliente = catalogos.clientes.firstOrNull { it.id == id } ?: return
        actualizarFormulario(
            f.copy(
                clienteId = id,
                nuevoCliente = false,
                cliente = f.cliente.copy(
                    nombre = cliente.nombre, tipo = cliente.tipo, rut = cliente.rut.orEmpty(),
                    telefono = cliente.telefono.orEmpty(), email = cliente.email.orEmpty(), direccion = cliente.direccion.orEmpty(),
                ),
            ),
        )
    }

    /** Línea nueva con la primera prenda sugerida y el precio base del servicio de la comanda. */
    fun agregarLinea() {
        val f = formulario ?: return
        val linea = LineaFormulario(
            prenda = prendasSugeridas(f.tipoCliente).first(),
            servicio = f.servicio,
            precioUnitario = precioBase(f.servicio) ?: 0.0,
        )
        actualizarFormulario(f.copy(lineas = f.lineas + linea))
    }

    /** Al cambiar el servicio de una línea se propone su precio base del catálogo. */
    fun precioBase(servicio: String): Double? =
        catalogos.servicios
            .firstOrNull { Validaciones.normalizarCatalogo(it.nombre) == Validaciones.normalizarCatalogo(servicio) }
            ?.precioBase

    /**
     * Valida y persiste el formulario actual. En edición envía `actualizadoEn` para detectar
     * conflictos de concurrencia; en creación puede dar de alta primero al cliente nuevo.
     */
    fun guardar() {
        val f = formulario ?: return
        f.validar()?.let {
            errorFormulario = it
            return
        }
        val editando = edicion
        operar(
            confirmacion = if (editando != null) "Comanda ${editando.numero} actualizada correctamente." else null,
            alTerminar = { problema ->
                if (problema == null) {
                    formulario = null
                    edicion = null
                    refrescar()
                } else {
                    errorFormulario = if (editando != null) {
                        "$problema Revisa la comanda actualizada antes de reintentar; " +
                            "otra persona pudo modificarla o iniciar su producción."
                    } else {
                        problema
                    }
                }
            },
        ) {
            if (editando != null) {
                repository.editarComanda(editando.id, editando.actualizadoEn, f)
                detalle = repository.detalle(editando.id)
            } else {
                crear(f)
            }
        }
    }

    private suspend fun crear(inicial: FormularioComanda) {
        var f = inicial
        var avisoCuenta: String? = null
        if (f.nuevoCliente) {
            // La ficha queda creada aunque falle la comanda: el reintento la reutiliza.
            val clienteId = if (f.registrarCuenta) {
                // Con cuenta: el cliente recibe un enlace para crear su contraseña.
                val resultado = repository.crearClienteConCuenta(f.cliente)
                avisoCuenta = resultado.aviso(f.cliente.email.trim().lowercase())
                resultado.id
            } else {
                repository.crearCliente(f.cliente)
            }
            f = f.copy(clienteId = clienteId, nuevoCliente = false, registrarCuenta = false, clienteInformado = false)
            formulario = f
        }
        val id = identidad ?: IdentidadComanda(idNuevo(), Validaciones.generarNumeroComanda()).also { identidad = it }
        val creada = repository.crearComanda(id, f)
        // Una vez confirmada la creación en servidor, se cierra el formulario y se limpia la identidad
        // para que un fallo al leer el comprobante nunca provoque un reintento duplicado.
        identidad = null
        formulario = null
        edicion = null
        mensaje = listOfNotNull("Comanda ${id.numero} creada correctamente.", avisoCuenta).joinToString(" ")
        detalle = try {
            repository.detalle(creada)
        } catch (_: OperacionComandaException) {
            mensaje = listOfNotNull(
                "Comanda ${id.numero} creada. No se pudo cargar su comprobante; ábrela desde la lista.",
                avisoCuenta,
            ).joinToString(" ")
            null
        }
    }

    /** Anula [comanda] dejando registro del [motivo] obligatorio, si su estado lo permite. */
    fun anular(comanda: ComandaGestion, motivo: String, alTerminar: (String?) -> Unit = {}) {
        if (!comanda.puedeAnular) return
        operar("Comanda ${comanda.numero} anulada correctamente.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) {
            repository.anular(comanda.id, motivo)
            detalle = repository.detalle(comanda.id)
        }
    }

    /** Marca como entregada una comanda finalizada y actualiza su detalle e historial. */
    fun entregar(comanda: ComandaGestion, alTerminar: (String?) -> Unit = {}) {
        if (!comanda.puedeEntregar) return
        operar("Comanda ${comanda.numero} entregada. Se registró la fecha y el historial.", alTerminar = {
            if (it != null) error = it
            alTerminar(it)
            if (it == null) refrescar()
        }) {
            repository.entregar(comanda.id)
            detalle = repository.detalle(comanda.id)
        }
    }

    /** Asocia el flujo de etapas de producción a una comanda que todavía no tiene etapas. */
    fun asociarFlujo(comanda: ComandaGestion) {
        if (!comanda.necesitaFlujo) return
        operar("Flujo asociado correctamente.", alTerminar = { if (it == null) refrescar() else error = it }) {
            repository.asociarFlujo(comanda.id)
            detalle = repository.detalle(comanda.id)
        }
    }

    override fun reiniciar() {
        super.reiniciar()
        busquedaJob?.cancel()
        consulta++
        consultaDetalle++
        cargandoDetalle = false
        servicioElegidoPorUsuario = false
        filtro = FiltroComandas()
        comandas = emptyList()
        total = 0
        conteos = emptyMap()
        limite = PAGINA_COMANDAS
        detalle = null
        formulario = null
        edicion = null
        identidad = null
        errorFormulario = null
        catalogos = CatalogosComanda()
        limpiarEstimacion()
    }

    private companion object {
        const val ESPERA_BUSQUEDA_MS = 350L
        const val ESPERA_ESTIMACION_MS = 600L
    }
}

@OptIn(ExperimentalUuidApi::class)
internal fun uuidAleatorio(): String = Uuid.random().toString()
