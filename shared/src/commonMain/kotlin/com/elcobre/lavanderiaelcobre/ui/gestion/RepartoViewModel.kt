package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.RepartoRepository
import com.elcobre.lavanderiaelcobre.data.RetornoFlotaInput
import com.elcobre.lavanderiaelcobre.data.SalidaFlotaInput
import com.elcobre.lavanderiaelcobre.data.crearRepartoRepository
import com.elcobre.lavanderiaelcobre.data.model.ComandaEntrega
import com.elcobre.lavanderiaelcobre.data.model.EntregasReparto
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.PanelReparto
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.VehiculoReparto
import com.elcobre.lavanderiaelcobre.data.model.filtrarComandasEntrega
import com.elcobre.lavanderiaelcobre.data.model.motivoVisibleEntrega
import com.elcobre.lavanderiaelcobre.data.model.normalizarDetalleEntrega
import com.elcobre.lavanderiaelcobre.data.model.validarEntrega
import com.elcobre.lavanderiaelcobre.data.model.validarInspeccion
import com.elcobre.lavanderiaelcobre.data.model.validarMovimiento
import com.elcobre.lavanderiaelcobre.data.model.validarProblemaEntrega
import kotlinx.coroutines.CoroutineScope

/** Datos capturados en el formulario de salida o regreso de vehículo del repartidor. */
data class MovimientoInput(
    val kilometraje: Int?,
    val estado: EstadoCondicionVehiculo,
    val observaciones: String,
    val fotos: List<FotoChequeo>,
)

/**
 * "Mi vehículo" del repartidor, como `/intranet/reparto`: elige un vehículo disponible,
 * registra la salida con su inspección y fotos y, al volver, el regreso. Las salidas que
 * administración programa desde la app se inician con su inspección previa.
 */
class RepartoViewModel(
    private val repository: RepartoRepository = crearRepartoRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var panel by mutableStateOf(PanelReparto())
        private set

    val salidaEnCurso: SalidaReparto? get() = panel.salidaEnCurso
    val programadas: List<SalidaReparto> get() = panel.programadas
    val historial: List<SalidaReparto> get() = panel.historial
    val kilometrosRecorridos: Int get() = panel.salidas.sumOf { it.kilometrosRecorridos ?: 0 }

    fun refrescar() = cargar { panel = repository.panel() }

    /** Kilometraje mínimo de una salida programada: el mayor registrado para su vehículo. */
    fun kilometrajeMinimo(salida: SalidaReparto): Int {
        if (salida.antes != null) return salida.antes.kilometraje
        val delVehiculo = panel.vehiculos.firstOrNull { it.id == salida.vehiculoId }?.kilometrajeActual ?: 0
        val enSalidas = panel.salidas.filter { it.vehiculoId == salida.vehiculoId }
            .flatMap { listOfNotNull(it.antes?.kilometraje, it.despues?.kilometraje) }
            .maxOrNull() ?: 0
        return maxOf(delVehiculo, enSalidas)
    }

    /**
     * Registra la salida de un vehículo disponible. Impide abrir una nueva salida mientras
     * el repartidor mantenga otra en curso y valida que el odómetro no retroceda.
     */
    fun registrarSalida(vehiculo: VehiculoReparto, input: MovimientoInput, alTerminar: (String?) -> Unit) {
        val problema = when {
            salidaEnCurso != null -> "Ya tienes una salida en curso. Registra su regreso antes de sacar otro vehículo."
            else -> validarMovimiento(input.kilometraje, vehiculo.kilometrajeActual, input.estado, true, input.observaciones)
        }
        if (problema != null) {
            alTerminar(problema)
            return
        }
        operar("Salida de ${vehiculo.patente} registrada. Buen viaje.", alTerminar = {
            alTerminar(it)
            refrescar()
        }) {
            repository.registrarSalida(
                SalidaFlotaInput(
                    vehiculoId = vehiculo.id,
                    repartidorId = "",
                    kilometraje = input.kilometraje ?: 0,
                    estadoVehiculo = input.estado,
                    observaciones = input.observaciones.trim().ifEmpty { null },
                    fotos = input.fotos,
                ),
            )
        }
    }

    /** Registra el retorno de [salida] exigiendo un kilometraje mayor o igual al de salida. */
    fun registrarRetorno(salida: SalidaReparto, input: MovimientoInput, alTerminar: (String?) -> Unit) {
        val minimo = salida.antes?.kilometraje ?: 0
        validarMovimiento(input.kilometraje, minimo, input.estado, false, input.observaciones)?.let {
            alTerminar(it)
            return
        }
        operar("Regreso de ${salida.patente} registrado.", alTerminar = {
            alTerminar(it)
            refrescar()
        }) {
            repository.registrarRetorno(
                RetornoFlotaInput(
                    salidaId = salida.id,
                    kilometraje = input.kilometraje ?: 0,
                    estadoVehiculo = input.estado,
                    observaciones = input.observaciones.trim().ifEmpty { null },
                    fotos = input.fotos,
                ),
            )
        }
    }

    /** Valida la inspección previa e inicia una salida previamente programada por administración. */
    fun iniciarProgramada(salida: SalidaReparto, input: InspeccionInput, alTerminar: (String?) -> Unit) {
        validarInspeccion(input.kilometraje, kilometrajeMinimo(salida), input.estado, esSalida = true, input.observaciones.orEmpty())
            ?.let {
                alTerminar(it)
                return
            }
        operar("Salida de ${salida.patente} iniciada. Buen viaje.", alTerminar = {
            alTerminar(it)
            refrescar()
        }) { repository.iniciarProgramada(salida, input) }
    }

    /** Limpia el estado del panel y descarta reintentos en memoria asociados a la sesión previa. */
    override fun reiniciar() {
        super.reiniciar()
        repository.descartarReintentos()
        panel = PanelReparto()
    }
}

/**
 * Entregas del repartidor, como `/intranet/entregas`: comandas listas (FINALIZADA) con
 * búsqueda, marcar la entrega con un detalle opcional y reportar un problema.
 */
class EntregasViewModel(
    private val repository: RepartoRepository = crearRepartoRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var datos by mutableStateOf(EntregasReparto())
        private set
    var busqueda by mutableStateOf("")
        private set

    val comandas: List<ComandaEntrega> get() = filtrarComandasEntrega(datos.comandas, busqueda)
    val conProblemas: Int get() = datos.comandas.count { it.problemasAbiertos.isNotEmpty() }

    fun refrescar() = cargar { datos = repository.entregas() }

    fun buscar(texto: String) {
        busqueda = texto
    }

    /** Confirma la entrega de [comanda] con un detalle opcional y recarga la bandeja. */
    fun entregar(comanda: ComandaEntrega, detalle: String, alTerminar: (String?) -> Unit) {
        validarEntrega(detalle)?.let {
            alTerminar(it)
            return
        }
        operar("Comanda ${comanda.numero} entregada a ${comanda.cliente}.", alTerminar = {
            alTerminar(it?.let(::mensajeEntrega))
            refrescar()
        }) { repository.entregar(comanda.id, normalizarDetalleEntrega(detalle)) }
    }

    /** Reporta un problema de reparto sin cerrar [comanda]; queda visible en incidencias. */
    fun reportarProblema(comanda: ComandaEntrega, motivo: String, detalle: String, alTerminar: (String?) -> Unit) {
        validarProblemaEntrega(motivo, detalle)?.let {
            alTerminar(it)
            return
        }
        val confirmacion = "Problema reportado en ${comanda.numero}: ${motivoVisibleEntrega(motivo)}. " +
            "Administración lo verá en incidencias."
        operar(confirmacion, alTerminar = {
            alTerminar(it?.let(::mensajeEntrega))
            refrescar()
        }) { repository.reportarProblema(comanda.id, motivo, normalizarDetalleEntrega(detalle)) }
    }

    override fun reiniciar() {
        super.reiniciar()
        datos = EntregasReparto()
        busqueda = ""
    }
}

// Patrón para detectar rechazos por cambios de estado previos en entregas concurrentes.
private val YA_NO_PENDIENTE = Regex(
    "Solo se puede entregar una comanda finalizada|ya fue registrada|no está listo para entregar|Solo se pueden reportar problemas",
    RegexOption.IGNORE_CASE,
)

/** Traduce los rechazos del servidor a mensajes accionables, igual que `mapearErrorEntrega` web. */
internal fun mensajeEntrega(error: String): String = when {
    YA_NO_PENDIENTE.containsMatchIn(error) -> "Esta comanda ya no está pendiente de entrega. Actualiza la lista."
    Regex("inactiva", RegexOption.IGNORE_CASE).containsMatchIn(error) -> "Tu cuenta está inactiva. Contacta a administración."
    Regex("No autorizado|reservada", RegexOption.IGNORE_CASE).containsMatchIn(error) -> "No tienes permisos para registrar entregas."
    else -> error
}
