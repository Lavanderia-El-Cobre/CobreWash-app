package com.elcobre.lavanderiaelcobre.data.offline

import kotlin.math.abs
import kotlin.math.min
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Lectura y escritura atómica del archivo de la cola en el dispositivo. */
interface AlmacenCola {
    fun leer(): String?
    fun escribir(contenido: String)
}

/** Almacén en memoria: pruebas y plataformas sin almacenamiento persistente. */
class AlmacenEnMemoria : AlmacenCola {
    private var contenido: String? = null
    override fun leer(): String? = contenido
    override fun escribir(contenido: String) {
        this.contenido = contenido
    }
}

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
private val serializador = ListSerializer(AccionPendiente.serializer())

/**
 * Cola persistente de acciones hechas sin conexión. Sobrevive al cierre de la app: se
 * guarda completa en cada cambio. Las acciones se mantienen en el orden en que ocurrieron.
 */
class ColaAcciones(private val almacen: AlmacenCola) {
    private val mutex = Mutex()
    private val _acciones = MutableStateFlow(cargar())
    val acciones: StateFlow<List<AccionPendiente>> = _acciones.asStateFlow()

    private fun cargar(): List<AccionPendiente> =
        almacen.leer()?.let { runCatching { json.decodeFromString(serializador, it) }.getOrNull() }.orEmpty()

    private fun guardar(lista: List<AccionPendiente>) {
        almacen.escribir(json.encodeToString(serializador, lista))
        _acciones.value = lista
    }

    suspend fun agregar(accion: AccionPendiente) = mutex.withLock { guardar(_acciones.value + accion) }

    suspend fun actualizar(accion: AccionPendiente) = mutex.withLock {
        guardar(_acciones.value.map { if (it.id == accion.id) accion else it })
    }

    suspend fun quitar(id: String) = mutex.withLock { guardar(_acciones.value.filterNot { it.id == id }) }

    fun buscar(id: String): AccionPendiente? = _acciones.value.firstOrNull { it.id == id }

    /** Archivos de fotos que todavía deben subirse: la limpieza de capturas no los borra. */
    fun rutasFotos(): Set<String> = _acciones.value.flatMap { a -> a.fotos.map { it.uri } }.toSet()

    /** Entidades (comandas, salidas, insumos) de [uid] con acciones sin enviar. */
    fun entidadesPendientes(uid: String?): Set<String> =
        _acciones.value.filter { it.uid == uid }.map { it.entidad }.toSet()
}

/** Hora original de una acción, ya corregida con el reloj del servidor cuando se pudo. */
data class HoraOriginal(val ocurridoEnMs: Long, val relojDudoso: Boolean)

/**
 * Corrige la hora de una acción con el reloj del servidor:
 * - Mismo arranque del teléfono: la hora se calcula con el reloj monotónico
 *   (hora del servidor ahora − tiempo transcurrido), así que un reloj del teléfono mal
 *   puesto o cambiado no la afecta.
 * - El teléfono se reinició: se usa su reloj del sistema corregido por la diferencia
 *   actual con el servidor, y se marca como dudosa si esa diferencia es grande.
 * - Sin hora del servidor: se usa el reloj del teléfono y se marca como dudosa.
 */
object RelojAcciones {
    const val DESFASE_TOLERADO_MS = 2 * 60_000L

    fun horaOriginal(
        accion: AccionPendiente,
        servidorAhoraMs: Long?,
        dispositivoAhoraMs: Long,
        monotonicoAhoraMs: Long,
        arranqueActual: String,
    ): HoraOriginal = when {
        servidorAhoraMs == null -> HoraOriginal(accion.ocurridoEnMs, relojDudoso = true)
        accion.arranque == arranqueActual && monotonicoAhoraMs >= accion.monotonicoMs ->
            HoraOriginal(servidorAhoraMs - (monotonicoAhoraMs - accion.monotonicoMs), relojDudoso = false)
        else -> {
            val desfase = servidorAhoraMs - dispositivoAhoraMs
            HoraOriginal(accion.ocurridoEnMs + desfase, relojDudoso = abs(desfase) > DESFASE_TOLERADO_MS)
        }
    }

    /** Espera antes del siguiente reintento: 15 s, 30 s, 1 min… hasta 5 min. */
    fun espera(intentos: Int): Long = min(15_000L shl (intentos - 1).coerceIn(0, 5), 5 * 60_000L)
}

/** Resultado de enviar una acción o sus fotos al servidor. */
sealed interface ResultadoEnvio {
    /** Aplicada ahora o ya aplicada antes (reintento con el mismo id). */
    data object Aplicada : ResultadoEnvio

    /** Sin conexión o el servidor no respondió: se reintenta después. */
    data object SinRed : ResultadoEnvio

    /** La sesión venció o la cuenta ya no tiene acceso: la cola se pausa. */
    data class Sesion(val mensaje: String) : ResultadoEnvio

    /** El servidor rechazó la acción por sus reglas: hay que revisarla. */
    data class Rechazada(val mensaje: String) : ResultadoEnvio
}

/** Conexión con el servidor; la implementa cada plataforma. */
interface EjecutorAcciones {
    /** Hora actual del servidor en ms, o `null` si no se pudo obtener (sin conexión). */
    suspend fun horaServidorMs(): Long?
    suspend fun aplicar(accion: AccionPendiente, hora: HoraOriginal): ResultadoEnvio
    suspend fun subirFotos(accion: AccionPendiente): ResultadoEnvio
    /** Borra del dispositivo las fotos ya subidas. */
    fun fotosConfirmadas(accion: AccionPendiente) = Unit
}

/** Reloj del dispositivo: hora del sistema, reloj monotónico y arranque actual. */
interface RelojDispositivo {
    fun ahoraMs(): Long
    fun monotonicoMs(): Long
    fun arranque(): String
    fun zonaHoraria(): String
}

/** Resultado de registrar una acción desde una pantalla. */
sealed interface ResultadoRegistro {
    data object Enviada : ResultadoRegistro
    data class EnCola(val motivo: String) : ResultadoRegistro
    data class Rechazada(val mensaje: String) : ResultadoRegistro
}

data class ResumenSincronizacion(val enviadas: Int, val pendientes: Int, val requierenAtencion: Int, val pausada: String?)
