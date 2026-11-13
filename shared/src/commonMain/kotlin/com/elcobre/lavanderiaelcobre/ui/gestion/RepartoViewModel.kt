package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.RepartoRepository
import com.elcobre.lavanderiaelcobre.data.crearRepartoRepository
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.validarInspeccion
import kotlinx.coroutines.CoroutineScope

/**
 * Recorridos del repartidor: salidas asignadas (programadas, en ruta y la bitácora de
 * finalizadas) y el catálogo de vehículos. Iniciar exige una inspección previa apta;
 * el retorno no puede registrar menos kilómetros que la salida.
 */
class RepartoViewModel(
    private val repository: RepartoRepository = crearRepartoRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var salidas by mutableStateOf<List<SalidaReparto>>(emptyList())
        private set
    var vehiculos by mutableStateOf<List<Vehiculo>>(emptyList())
        private set

    val enRuta: List<SalidaReparto> get() = salidas.filter { it.estado == EstadoSalida.EN_SERVICIO }
    val programadas: List<SalidaReparto> get() = salidas.filter { it.estado == EstadoSalida.PROGRAMADA }
    val bitacora: List<SalidaReparto> get() = salidas.filter { it.estado == EstadoSalida.FINALIZADA || it.estado == EstadoSalida.CANCELADA }
    val kilometrosRecorridos: Int get() = salidas.sumOf { it.kilometrosRecorridos ?: 0 }

    fun refrescar() = cargar {
        salidas = repository.misSalidas()
        vehiculos = repository.vehiculos()
    }

    /** Kilometraje mínimo aceptable: el mayor registrado para el vehículo en las salidas visibles. */
    fun kilometrajeMinimo(salida: SalidaReparto): Int {
        if (salida.estado == EstadoSalida.EN_SERVICIO) return salida.antes?.kilometraje ?: 0
        return salidas.filter { it.vehiculoId == salida.vehiculoId }
            .flatMap { listOfNotNull(it.antes?.kilometraje, it.despues?.kilometraje) }
            .maxOrNull() ?: 0
    }

    fun iniciar(salida: SalidaReparto, input: InspeccionInput, alTerminar: (String?) -> Unit) {
        validarInspeccion(input.kilometraje, kilometrajeMinimo(salida), input.estado, esSalida = true, input.observaciones.orEmpty())
            ?.let {
                alTerminar(it)
                return
            }
        operar("Salida de ${salida.patente} iniciada. Buen viaje.", alTerminar = {
            alTerminar(it)
            refrescar()
        }) { repository.iniciarSalida(salida, input) }
    }

    fun registrarRetorno(salida: SalidaReparto, input: InspeccionInput, alTerminar: (String?) -> Unit) {
        validarInspeccion(input.kilometraje, kilometrajeMinimo(salida), input.estado, esSalida = false, input.observaciones.orEmpty())
            ?.let {
                alTerminar(it)
                return
            }
        operar("Retorno de ${salida.patente} registrado.", alTerminar = {
            alTerminar(it)
            refrescar()
        }) { repository.registrarRetorno(input) }
    }

    override fun reiniciar() {
        super.reiniciar()
        repository.descartarReintentos()
        salidas = emptyList()
        vehiculos = emptyList()
    }
}
