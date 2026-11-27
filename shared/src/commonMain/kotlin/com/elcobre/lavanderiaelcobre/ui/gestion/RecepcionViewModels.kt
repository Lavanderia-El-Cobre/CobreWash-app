package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.ComunicacionRepository
import com.elcobre.lavanderiaelcobre.data.ProduccionRepository
import com.elcobre.lavanderiaelcobre.data.RecepcionRepository
import com.elcobre.lavanderiaelcobre.data.crearComunicacionRepository
import com.elcobre.lavanderiaelcobre.data.crearProduccionRepository
import com.elcobre.lavanderiaelcobre.data.crearRecepcionRepository
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EstadoSolicitud
import com.elcobre.lavanderiaelcobre.data.model.IncidenciaGestion
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PersonaRef
import com.elcobre.lavanderiaelcobre.data.model.SolicitudContacto
import com.elcobre.lavanderiaelcobre.data.model.validarFichaCliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Fichas de clientes (`/intranet/clientes`): búsqueda, alta y edición. */
class ClientesViewModel(
    private val repository: RecepcionRepository = crearRecepcionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var clientes by mutableStateOf<List<ClienteFicha>>(emptyList())
        private set
    var busqueda by mutableStateOf("")
        private set

    /** Filtra localmente por nombre, correo o RUT normalizado sin puntos ni guion. */
    val filtrados: List<ClienteFicha>
        get() {
            val q = busqueda.trim().lowercase()
            if (q.isEmpty()) return clientes
            val rut = q.filter { it.isLetterOrDigit() }
            return clientes.filter {
                it.nombre.lowercase().contains(q) || it.email?.lowercase()?.contains(q) == true ||
                    (rut.isNotEmpty() && it.rut?.filter { c -> c.isLetterOrDigit() }?.lowercase()?.contains(rut) == true)
            }
        }

    fun refrescar() = cargar { clientes = repository.fichasClientes() }

    fun buscar(texto: String) {
        busqueda = texto
    }

    /** Crea ([id] nulo) o edita una ficha; [alTerminar] recibe el error a mostrar en la hoja. */
    fun guardar(id: String?, input: NuevoClienteInput, alTerminar: (String?) -> Unit) {
        validarFichaCliente(input)?.let {
            alTerminar(it)
            return
        }
        val confirmacion = if (id == null) "Cliente ${input.nombre.trim()} registrado." else "Ficha de ${input.nombre.trim()} actualizada."
        operar(confirmacion, alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) {
            if (id == null) repository.crearCliente(input) else repository.editarCliente(id, input)
        }
    }

    override fun reiniciar() {
        super.reiniciar()
        clientes = emptyList()
        busqueda = ""
    }
}

/**
 * Seguimiento de producción de toda la planta (`/intranet/seguimiento`). Qué puede hacer
 * cada rol lo deciden los flags, igual que la web: completar etapas (admin y operario),
 * reasignar operarios (admin), asociar flujo (admin y recepción) y reportar incidencias (operario).
 */
class ProduccionViewModel(
    private val repository: ProduccionRepository = crearProduccionRepository(),
    private val recepcion: RecepcionRepository = crearRecepcionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var comandas by mutableStateOf<List<ComandaGestion>>(emptyList())
        private set
    var total by mutableStateOf(0)
        private set
    var operarios by mutableStateOf<List<PersonaRef>>(emptyList())
        private set
    var busqueda by mutableStateOf("")
        private set
    private var limite = PAGINA_COMANDAS
    private var busquedaJob: Job? = null

    // Contador para descartar respuestas antiguas si el usuario cambia la búsqueda rápido.
    private var consulta = 0

    val hayMas: Boolean get() = comandas.size < total

    /** Carga las comandas activas en planta; con [mas] amplía el límite en una página. */
    fun refrescar(mas: Boolean = false, limpiarError: Boolean = true) {
        if (mas && (cargando || !hayMas)) return
        if (mas) limite += PAGINA_COMANDAS
        val solicitud = ++consulta
        val texto = busqueda
        val limiteActual = limite
        cargar(limpiarError = limpiarError) {
            val pagina = repository.seguimiento(texto, 0, limiteActual)
            if (solicitud == consulta) {
                comandas = pagina.comandas
                total = pagina.total
                operarios = pagina.operarios
            }
        }
    }

    /** Aplica espera de 350 ms antes de consultar para evitar solicitudes en cada pulsación. */
    fun buscar(texto: String) {
        busqueda = texto
        limite = PAGINA_COMANDAS
        busquedaJob?.cancel()
        busquedaJob = scope.launch {
            delay(350)
            refrescar()
        }
    }

    /** Completa la etapa en curso de [comanda] y refresca la vista incluso si hubo conflicto. */
    fun completarEtapa(comanda: ComandaGestion) {
        val etapa = comanda.etapaActual ?: return
        operar("${etapa.nombre} completada en ${comanda.numero}.", alTerminar = {
            if (it != null) error = "$it Actualiza la vista: otra persona pudo haber avanzado esta comanda."
            refrescar(limpiarError = it == null)
        }) { repository.completarEtapa(comanda) }
    }

    /** Asocia el flujo estándar de etapas a una comanda recibida sin producción iniciada. */
    fun asociarFlujo(comanda: ComandaGestion) {
        operar("Flujo asociado a ${comanda.numero}.", alTerminar = {
            if (it != null) error = it
            refrescar(limpiarError = it == null)
        }) { recepcion.asociarFlujo(comanda.id) }
    }

    /** Reasigna la etapa activa de [comanda] a otro [operario] (uso exclusivo de administración). */
    fun reasignar(comanda: ComandaGestion, etapaId: String, operario: PersonaRef, motivo: String, alTerminar: (String?) -> Unit) {
        operar("${operario.nombre} quedó a cargo en ${comanda.numero}.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) { repository.reasignar(comanda.id, etapaId, operario.id, motivo) }
    }

    /** Registra una incidencia sobre [comanda] para visibilidad de recepción y administración. */
    fun reportarIncidencia(comanda: ComandaGestion, motivo: String, descripcion: String, alTerminar: (String?) -> Unit) {
        operar("Incidencia registrada en ${comanda.numero}.", alTerminar = alTerminar) {
            repository.reportarIncidencia(comanda.id, motivo, descripcion)
        }
    }

    override fun reiniciar() {
        super.reiniciar()
        busquedaJob?.cancel()
        consulta++
        comandas = emptyList()
        total = 0
        operarios = emptyList()
        busqueda = ""
        limite = PAGINA_COMANDAS
    }
}

/** Incidencias reportadas por producción (`/intranet/incidencias`), con su gestión de estado. */
class IncidenciasViewModel(
    private val repository: RecepcionRepository = crearRecepcionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var incidencias by mutableStateOf<List<IncidenciaGestion>>(emptyList())
        private set
    var filtro by mutableStateOf<EstadoIncidencia?>(null)
        private set

    val filtradas: List<IncidenciaGestion> get() = filtro?.let { f -> incidencias.filter { it.estado == f } } ?: incidencias

    /** Cantidad de incidencias aún pendientes (abiertas o en revisión). */
    val abiertas: Int get() = incidencias.count { it.estado != EstadoIncidencia.RESUELTA }

    fun refrescar(limpiarError: Boolean = true) = cargar(limpiarError = limpiarError) { incidencias = repository.incidencias() }

    fun filtrar(estado: EstadoIncidencia?) {
        filtro = estado
    }

    /** Actualiza el estado de [incidencia]; ignora el envío si ya se encuentra en [estado]. */
    fun cambiarEstado(incidencia: IncidenciaGestion, estado: EstadoIncidencia) {
        if (incidencia.estado == estado) return
        operar("Incidencia de ${incidencia.numeroComanda}: ${estado.etiqueta.lowercase()}.", alTerminar = {
            if (it != null) error = it
            refrescar(limpiarError = it == null)
        }) { repository.actualizarIncidencia(incidencia.id, estado) }
    }

    override fun reiniciar() {
        super.reiniciar()
        incidencias = emptyList()
        filtro = null
    }
}

/** Bandeja de cotizaciones del formulario público (`/intranet/solicitudes`). */
class CotizacionesViewModel(
    private val repository: ComunicacionRepository = crearComunicacionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var solicitudes by mutableStateOf<List<SolicitudContacto>>(emptyList())
        private set
    var total by mutableStateOf(0)
        private set
    var filtro by mutableStateOf<EstadoSolicitud?>(null)
        private set
    private var limite = PAGINA_COMANDAS
    private var consulta = 0

    val hayMas: Boolean get() = solicitudes.size < total

    /** Carga cotizaciones para el [filtro] vigente descartando respuestas si cambió en vuelo. */
    fun refrescar(mas: Boolean = false, limpiarError: Boolean = true) {
        if (mas && (cargando || !hayMas)) return
        if (mas) limite += PAGINA_COMANDAS
        val solicitud = ++consulta
        val estado = filtro
        val limiteActual = limite
        cargar(limpiarError = limpiarError) {
            val pagina = repository.solicitudes(estado, 0, limiteActual)
            if (solicitud == consulta && estado == filtro) {
                solicitudes = pagina.solicitudes
                total = pagina.total
            }
        }
    }

    /** Cambia el filtro de estado, reinicia la paginación y recarga desde el servidor. */
    fun filtrar(estado: EstadoSolicitud?) {
        filtro = estado
        limite = PAGINA_COMANDAS
        refrescar()
    }

    /** Actualiza el estado de atención de [solicitud] si difiere del actual. */
    fun cambiarEstado(solicitud: SolicitudContacto, estado: EstadoSolicitud) {
        if (solicitud.estado == estado) return
        operar("Solicitud de ${solicitud.nombre}: ${estado.etiqueta.lowercase()}.", alTerminar = {
            if (it != null) error = "No se pudo guardar el estado. Reintenta."
            refrescar(limpiarError = it == null)
        }) { repository.actualizarSolicitud(solicitud.id, estado) }
    }

    override fun reiniciar() {
        super.reiniciar()
        consulta++
        solicitudes = emptyList()
        total = 0
        filtro = null
        limite = PAGINA_COMANDAS
    }
}
