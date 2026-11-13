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
    private var consulta = 0

    val hayMas: Boolean get() = comandas.size < total

    fun refrescar(mas: Boolean = false) {
        if (mas && !hayMas) return
        if (mas) limite += PAGINA_COMANDAS
        val solicitud = ++consulta
        val texto = busqueda
        val limiteActual = limite
        cargar {
            val pagina = repository.seguimiento(texto, 0, limiteActual)
            if (solicitud == consulta) {
                comandas = pagina.comandas
                total = pagina.total
                operarios = pagina.operarios
            }
        }
    }

    fun buscar(texto: String) {
        busqueda = texto
        limite = PAGINA_COMANDAS
        busquedaJob?.cancel()
        busquedaJob = scope.launch {
            delay(350)
            refrescar()
        }
    }

    fun completarEtapa(comanda: ComandaGestion) {
        val etapa = comanda.etapaActual ?: return
        operar("${etapa.nombre} completada en ${comanda.numero}.", alTerminar = {
            if (it != null) error = "$it Actualiza la vista: otra persona pudo haber avanzado esta comanda."
            refrescar()
        }) { repository.completarEtapa(comanda) }
    }

    fun asociarFlujo(comanda: ComandaGestion) {
        operar("Flujo asociado a ${comanda.numero}.", alTerminar = {
            if (it != null) error = it
            refrescar()
        }) { recepcion.asociarFlujo(comanda.id) }
    }

    fun reasignar(comanda: ComandaGestion, etapaId: String, operario: PersonaRef, motivo: String, alTerminar: (String?) -> Unit) {
        operar("${operario.nombre} quedó a cargo en ${comanda.numero}.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) { repository.reasignar(comanda.id, etapaId, operario.id, motivo) }
    }

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
    val abiertas: Int get() = incidencias.count { it.estado != EstadoIncidencia.RESUELTA }

    fun refrescar() = cargar { incidencias = repository.incidencias() }

    fun filtrar(estado: EstadoIncidencia?) {
        filtro = estado
    }

    fun cambiarEstado(incidencia: IncidenciaGestion, estado: EstadoIncidencia) {
        if (incidencia.estado == estado) return
        operar("Incidencia de ${incidencia.numeroComanda}: ${estado.etiqueta.lowercase()}.", alTerminar = {
            if (it != null) error = it
            refrescar()
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

    val hayMas: Boolean get() = solicitudes.size < total

    fun refrescar(mas: Boolean = false) {
        if (mas && !hayMas) return
        if (mas) limite += PAGINA_COMANDAS
        val estado = filtro
        val limiteActual = limite
        cargar {
            val pagina = repository.solicitudes(estado, 0, limiteActual)
            if (estado == filtro) {
                solicitudes = pagina.solicitudes
                total = pagina.total
            }
        }
    }

    fun filtrar(estado: EstadoSolicitud?) {
        filtro = estado
        limite = PAGINA_COMANDAS
        refrescar()
    }

    fun cambiarEstado(solicitud: SolicitudContacto, estado: EstadoSolicitud) {
        if (solicitud.estado == estado) return
        operar("Solicitud de ${solicitud.nombre}: ${estado.etiqueta.lowercase()}.", alTerminar = {
            if (it != null) error = "No se pudo guardar el estado. Reintenta."
            refrescar()
        }) { repository.actualizarSolicitud(solicitud.id, estado) }
    }

    override fun reiniciar() {
        super.reiniciar()
        solicitudes = emptyList()
        total = 0
        filtro = null
        limite = PAGINA_COMANDAS
    }
}
