package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elcobre.lavanderiaelcobre.data.EditarVehiculoInput
import com.elcobre.lavanderiaelcobre.data.NuevoVehiculoInput
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.RetornoFlotaInput
import com.elcobre.lavanderiaelcobre.data.SalidaFlotaInput
import com.elcobre.lavanderiaelcobre.data.VehiculosRepository
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FiltroFlota
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.KpisFlota
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.calcularKpisFlota
import com.elcobre.lavanderiaelcobre.data.model.esPatenteChilenaValida
import com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor
import com.elcobre.lavanderiaelcobre.data.model.extraerMarcaYModelo
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.data.model.obtenerHoraFormateadaActual
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Estado de la flota de reparto (`RF-VE01` a `RF-VE04`), separado de [CobreViewModel]
 * porque lo gobierna la sesión de Administrador. Cuando recibe un [VehiculosRepository]
 * sincroniza vehículos, conductores, salidas e inspecciones con Firebase Data Connect;
 * sin repositorio opera en memoria para pruebas unitarias síncronas.
 */
@Suppress("TooManyFunctions", "LongParameterList")
class VehiculosViewModel(
    private val repository: VehiculosRepository? = null,
    private val scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val scope: CoroutineScope get() = scopeOverride ?: viewModelScope
    private val salidasConFotosPendientes = mutableMapOf<String, SalidaFlotaInput>()
    private val retornosConFotosPendientes = mutableMapOf<String, RetornoFlotaInput>()

    private val _vehiculos: SnapshotStateList<Vehiculo> = mutableStateListOf()
    val vehiculos: List<Vehiculo> get() = _vehiculos

    private val _conductores: SnapshotStateList<ConductorFlota> = mutableStateListOf()
    val conductores: List<ConductorFlota> get() = _conductores

    private val _chequeosVehiculo: SnapshotStateList<ChequeoVehiculo> = mutableStateListOf()

    /** Chequeos de vehículo (check-in/checkout), más reciente primero. */
    val chequeosVehiculo: List<ChequeoVehiculo> get() = _chequeosVehiculo

    var cargando by mutableStateOf(false)
        private set
    var operacionEnCurso by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var mensajeExito by mutableStateOf<String?>(null)
        private set
    var busqueda by mutableStateOf("")
        private set
    var filtro by mutableStateOf(FiltroFlota.TODOS)
        private set

    private var generacion = 0
    private var habilitado = repository == null
    private var chequeoContador = _chequeosVehiculo.size
    private var vehiculoContador = _vehiculos.size

    val kpis: KpisFlota get() = calcularKpisFlota(_vehiculos, _chequeosVehiculo)

    val vehiculosFiltrados: List<Vehiculo>
        get() {
            val termino = busqueda.trim().lowercase()
            return _vehiculos.filter { vehiculo ->
                val enRuta = chequeoEnRuta(vehiculo.id) != null
                val cumpleFiltro = when (filtro) {
                    FiltroFlota.TODOS -> true
                    FiltroFlota.DISPONIBLES -> vehiculo.activo && !enRuta
                    FiltroFlota.EN_RUTA -> enRuta
                    FiltroFlota.INACTIVOS -> !vehiculo.activo && !enRuta
                }
                val conductorEnRuta = chequeoEnRuta(vehiculo.id)?.responsable.orEmpty().lowercase()
                val cumpleBusqueda = termino.isEmpty() ||
                    vehiculo.patente.lowercase().contains(termino) ||
                    vehiculo.nombreCompleto.lowercase().contains(termino) ||
                    conductorEnRuta.contains(termino)
                cumpleFiltro && cumpleBusqueda
            }
        }

    fun cambiarSesion(activo: Boolean) {
        generacion++
        salidasConFotosPendientes.clear()
        retornosConFotosPendientes.clear()
        repository?.descartarReintentos()
        habilitado = activo
        cargando = false
        operacionEnCurso = false
        error = null
        mensajeExito = null
        busqueda = ""
        filtro = FiltroFlota.TODOS
        if (!activo && repository != null) {
            _vehiculos.clear()
            _conductores.clear()
            _chequeosVehiculo.clear()
        }
    }

    fun actualizarBusqueda(texto: String) {
        busqueda = texto
    }

    fun seleccionarFiltro(nuevo: FiltroFlota) {
        filtro = nuevo
    }

    fun limpiarMensajes() {
        error = null
        mensajeExito = null
    }

    fun tieneFotosPendientes(vehiculoId: String): Boolean =
        salidasConFotosPendientes.containsKey(vehiculoId) || retornosConFotosPendientes.containsKey(vehiculoId)

    fun descartarReintentos() {
        salidasConFotosPendientes.clear()
        retornosConFotosPendientes.clear()
        repository?.descartarReintentos()
    }

    fun refrescarFlota() {
        val repo = repository ?: return
        if (!habilitado || cargando) return
        val inicio = generacion
        cargando = true
        error = null
        scope.launch {
            try {
                val remota = repo.consultarFlota()
                if (inicio == generacion) {
                    aplicarFlotaRemota(remota.vehiculos, remota.conductores, remota.chequeos)
                }
            } catch (causa: OperacionComandaException) {
                if (inicio == generacion) {
                    error = causa.message ?: "No se pudo sincronizar la flota de vehículos."
                }
            } finally {
                if (inicio == generacion) cargando = false
            }
        }
    }

    private fun aplicarFlotaRemota(
        nuevosVehiculos: List<Vehiculo>,
        nuevosConductores: List<ConductorFlota>,
        nuevosChequeos: List<ChequeoVehiculo>,
    ) {
        _vehiculos.clear()
        _vehiculos.addAll(nuevosVehiculos.distinctBy { it.id })
        _conductores.clear()
        _conductores.addAll(
            nuevosConductores
                .filter { esRolConductorORepartidor(it.rol) }
                .distinctBy { it.id },
        )
        _chequeosVehiculo.clear()
        _chequeosVehiculo.addAll(nuevosChequeos.distinctBy { it.id })
    }

    /** RF-VE01: agrega un vehículo nuevo a la flota, normalizando la patente chilena. */
    fun registrarVehiculo(
        patente: String,
        modelo: String,
        kilometrajeInicial: Int,
        marca: String = "",
        anio: Int? = null,
        descripcion: String? = null,
    ) {
        val patenteNormalizada = normalizarPatenteChilena(patente)
        val errorValidacion = when {
            !esPatenteChilenaValida(patenteNormalizada) ->
                "Ingresa una patente chilena válida (ej. HLRT-24 o AB-1234)."
            _vehiculos.any { normalizarPatenteChilena(it.patente) == patenteNormalizada } ->
                "Ya existe un vehículo registrado con esa patente."
            else -> null
        }
        if (errorValidacion != null) {
            error = errorValidacion
            return
        }
        val (marcaFinal, modeloFinal) = extraerMarcaYModelo(marca, modelo)
        val repo = repository
        if (repo == null) {
            vehiculoContador += 1
            error = null
            _vehiculos.add(
                Vehiculo(
                    id = "V${vehiculoContador.toString().padStart(2, '0')}",
                    patente = patenteNormalizada,
                    marca = marcaFinal,
                    modelo = modelo.trim(),
                    anio = anio,
                    descripcion = descripcion?.trim()?.takeIf { it.isNotEmpty() },
                    kilometrajeActual = kilometrajeInicial.coerceAtLeast(0),
                ),
            )
            mensajeExito = "Vehículo $patenteNormalizada agregado a la flota."
        } else {
            ejecutarMutacion("Vehículo $patenteNormalizada registrado en la flota.") {
                repo.registrarVehiculo(
                    NuevoVehiculoInput(
                        patente = patenteNormalizada,
                        marca = marcaFinal,
                        modelo = modeloFinal,
                        anio = anio,
                        descripcion = descripcion,
                    ),
                )
            }
        }
    }

    /** RF-VE01: habilita o envía a mantención un vehículo, impidiendo desactivar si está en ruta. */
    fun cambiarEstadoVehiculo(vehiculoId: String, activo: Boolean) {
        val actual = _vehiculos.firstOrNull { it.id == vehiculoId } ?: return
        if (!activo && chequeoEnRuta(vehiculoId) != null) {
            error = "No puedes desactivar un vehículo que tiene una salida en curso."
            return
        }
        val confirmacion = if (activo) "Vehículo ${actual.patente} habilitado." else "Vehículo ${actual.patente} en mantención."
        val repo = repository
        if (repo == null) {
            error = null
            _vehiculos.reemplazarPrimero({ it.id == vehiculoId }) { it.copy(activo = activo) }
            mensajeExito = confirmacion
        } else {
            val (marcaFinal, modeloFinal) = extraerMarcaYModelo(actual.marca, actual.modelo)
            ejecutarMutacion(confirmacion) {
                repo.editarVehiculo(
                    EditarVehiculoInput(
                        id = actual.id,
                        patente = actual.patente,
                        marca = marcaFinal,
                        modelo = modeloFinal,
                        anio = actual.anio,
                        descripcion = actual.descripcion,
                        activo = activo,
                    ),
                )
            }
        }
    }

    fun chequeoEnRuta(vehiculoId: String): ChequeoVehiculo? =
        _chequeosVehiculo.firstOrNull { it.vehiculoId == vehiculoId && it.estaEnRuta }

    /** RF-VE04: historial de salidas/regresos de un vehículo, más reciente primero. */
    fun historialVehiculo(vehiculoId: String): List<ChequeoVehiculo> =
        _chequeosVehiculo.filter { it.vehiculoId == vehiculoId }

    private fun validarSalidaLocal(
        vehiculoId: String,
        km: Int,
        estadoVehiculo: EstadoCondicionVehiculo,
    ): String? {
        val vehiculo = _vehiculos.firstOrNull { it.id == vehiculoId }
        return when {
            vehiculo != null && !vehiculo.activo -> "El vehículo está inactivo o en mantención."
            chequeoEnRuta(vehiculoId) != null -> "El vehículo ya registra una salida en ruta."
            estadoVehiculo == EstadoCondicionVehiculo.NO_APTO ->
                "Un vehículo calificado como NO APTO no puede salir a terreno."
            vehiculo != null && km < vehiculo.kilometrajeActual ->
                "El kilometraje de salida no puede ser menor al último registrado (${vehiculo.kilometrajeActual} km)."
            else -> null
        }
    }

    /** RF-VE02: registra la salida del vehículo con inspección inicial y validación de condición/odómetro. */
    fun registrarSalidaVehiculo(
        vehiculoId: String,
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        responsable: String,
        repartidorId: String? = null,
        estadoVehiculo: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    ) {
        val errorValidacion = if (salidasConFotosPendientes.containsKey(vehiculoId)) null
            else validarSalidaLocal(vehiculoId, km, estadoVehiculo)
        if (errorValidacion != null) {
            error = errorValidacion
            return
        }
        val repo = repository
        if (repo == null) {
            chequeoContador += 1
            error = null
            _chequeosVehiculo.add(
                0,
                ChequeoVehiculo(
                    id = "CV${chequeoContador.toString().padStart(2, '0')}",
                    vehiculoId = vehiculoId,
                    fecha = "Hoy",
                    responsable = responsable,
                    repartidorId = repartidorId,
                ).registrarSalida(km, fotos, observaciones, obtenerHoraFormateadaActual(), estadoVehiculo),
            )
            _vehiculos.reemplazarPrimero({ it.id == vehiculoId }) {
                it.copy(kilometrajeActual = maxOf(it.kilometrajeActual, km))
            }
            mensajeExito = "Salida registrada correctamente."
        } else {
            val idConductor = repartidorId
                ?: _conductores.firstOrNull { it.nombreCompleto.equals(responsable, ignoreCase = true) }?.id
                ?: _conductores.firstOrNull()?.id
            if (idConductor.isNullOrBlank()) {
                error = "Selecciona un conductor habilitado para autorizar la salida."
            } else {
                ejecutarMutacion("Salida de flota registrada correctamente.") {
                    val input = salidasConFotosPendientes[vehiculoId]?.copy(fotos = fotos) ?: SalidaFlotaInput(
                            vehiculoId = vehiculoId,
                            repartidorId = idConductor,
                            kilometraje = km,
                            estadoVehiculo = estadoVehiculo,
                            observaciones = observaciones,
                            fotos = fotos,
                        )
                    try {
                        repo.registrarSalida(input)
                        salidasConFotosPendientes.remove(vehiculoId)
                    } catch (causa: OperacionComandaException) {
                        if (causa.message?.startsWith("La salida ya se guardó") == true) salidasConFotosPendientes[vehiculoId] = input
                        throw causa
                    }
                }
            }
        }
    }

    /** RF-VE03: registra el retorno del vehículo con inspección posterior y cierra la salida activa. */
    fun registrarEntradaVehiculo(
        vehiculoId: String,
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        estadoVehiculo: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    ) {
        val pendiente = retornosConFotosPendientes[vehiculoId]
        if (pendiente != null && repository != null) {
            ejecutarMutacion("Retorno de vehículo registrado correctamente.") {
                repository.registrarRetorno(pendiente.copy(fotos = fotos))
                retornosConFotosPendientes.remove(vehiculoId)
            }
        } else registrarRetornoNuevo(vehiculoId, km, fotos, observaciones, estadoVehiculo)
    }

    private fun registrarRetornoNuevo(
        vehiculoId: String,
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        estadoVehiculo: EstadoCondicionVehiculo,
    ) {
        val activo = chequeoEnRuta(vehiculoId) ?: return
        val kmMinimo = activo.kmSalida ?: _vehiculos.firstOrNull { it.id == vehiculoId }?.kilometrajeActual ?: 0
        if (km < kmMinimo) {
            error = "El kilometraje de retorno ($km km) no puede ser menor al de salida ($kmMinimo km)."
            return
        }
        val repo = repository
        if (repo == null) {
            val hora = obtenerHoraFormateadaActual()
            error = null
            _chequeosVehiculo.reemplazarPrimero({ it.vehiculoId == vehiculoId && it.estaEnRuta }) {
                it.registrarEntrada(km, fotos, observaciones, hora, estadoVehiculo)
            }
            _vehiculos.reemplazarPrimero({ it.id == vehiculoId }) { it.copy(kilometrajeActual = km) }
            mensajeExito = "Retorno del vehículo registrado."
        } else {
            ejecutarMutacion("Retorno de vehículo registrado correctamente.") {
                val input = RetornoFlotaInput(
                        salidaId = activo.id,
                        kilometraje = km,
                        estadoVehiculo = estadoVehiculo,
                        observaciones = observaciones,
                        fotos = fotos,
                    )
                try { repo.registrarRetorno(input) }
                catch (causa: OperacionComandaException) {
                    if (causa.message?.startsWith("El retorno ya se guardó") == true) retornosConFotosPendientes[vehiculoId] = input
                    throw causa
                }
            }
        }
    }

    /** Programa una salida para que el repartidor la inspeccione e inicie desde su app. */
    fun programarSalida(vehiculoId: String, repartidorId: String, observaciones: String?) {
        val repo = repository ?: return
        ejecutarMutacion("Salida programada. El repartidor la verá en sus recorridos.") {
            repo.programarSalida(vehiculoId, repartidorId, observaciones)
        }
    }

    private fun ejecutarMutacion(confirmacion: String, accion: suspend () -> Unit) {
        val repo = repository ?: return
        if (!habilitado || operacionEnCurso) return
        val inicio = generacion
        operacionEnCurso = true
        error = null
        mensajeExito = null
        scope.launch {
            try {
                accion()
                val remota = repo.consultarFlota()
                if (inicio == generacion) {
                    aplicarFlotaRemota(remota.vehiculos, remota.conductores, remota.chequeos)
                    mensajeExito = confirmacion
                }
            } catch (causa: OperacionComandaException) {
                if (inicio == generacion) {
                    error = causa.message ?: "No se pudo completar la operación en la flota."
                }
            } finally {
                if (inicio == generacion) operacionEnCurso = false
            }
        }
    }
}
