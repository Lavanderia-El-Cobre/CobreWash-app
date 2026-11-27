package com.elcobre.lavanderiaelcobre.data.offline

import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

/**
 * Última respuesta de cada lista que usa el personal en terreno (comandas del operario,
 * vehículos y entregas del repartidor, insumos para contar), guardada por cuenta. Solo se
 * usa cuando no hay conexión, y se borra al cerrar sesión porque contiene datos de
 * clientes (Ley 21.719).
 */
object CacheLectura {
    private var fabrica: ((String) -> AlmacenCola)? = null
    private var borrarTodo: () -> Unit = {}
    private var dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mutex = Mutex()
    private val generacion = MutableStateFlow(0L)
    private val _desdeCache = MutableStateFlow(false)

    /** `true` si lo que se muestra viene de la copia guardada (sin conexión). */
    val desdeCache: StateFlow<Boolean> = _desdeCache.asStateFlow()

    /** Inyecta la fábrica de archivos por clave/UID y la rutina de borrado global al iniciar la app. */
    fun configurar(
        fabrica: (String) -> AlmacenCola,
        borrarTodo: () -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        this.fabrica = fabrica
        this.borrarTodo = borrarTodo
        this.dispatcher = dispatcher
    }

    /** Borra todas las copias locales y reinicia [desdeCache]; obligatorio al cerrar sesión. */
    fun limpiar() {
        generacion.value += 1L
        runCatching { borrarTodo() }
        _desdeCache.value = false
    }

    /** Determina si [error] corresponde a pérdida de conectividad y autoriza respaldo en caché. */
    fun esErrorSinConexion(error: OperacionComandaException): Boolean {
        val mensaje = error.message.orEmpty().lowercase()
        return !AccionesSinConexion.conectado.value || "conexión" in mensaje || "conectar" in mensaje
    }

    /** Lee del servidor y guarda la copia; sin conexión devuelve la última copia de esta cuenta. */
    suspend fun <T> leer(clave: String, serializador: KSerializer<T>, remoto: suspend () -> T): T {
        val uid = AccionesSinConexion.uid()
        val genInicial = generacion.value
        val almacen = uid?.let { fabrica?.invoke("${clave}_${it.filter(Char::isLetterOrDigit)}") } ?: return remoto()
        return try {
            val resultado = remoto()
            withContext(dispatcher) {
                mutex.withLock {
                    // Evita escribir datos de una sesión previa si se cerró sesión mientras la petición estaba en vuelo.
                    if (generacion.value == genInicial && AccionesSinConexion.uid() == uid) {
                        runCatching { almacen.escribir(json.encodeToString(serializador, resultado)) }
                        _desdeCache.value = false
                    }
                }
            }
            resultado
        } catch (error: OperacionComandaException) {
            if (!esErrorSinConexion(error)) throw error
            val texto = withContext(dispatcher) { mutex.withLock { runCatching { almacen.leer() }.getOrNull() } }
            val guardado = texto?.let { runCatching { json.decodeFromString(serializador, it) }.getOrNull() }
                ?: throw error
            _desdeCache.value = true
            guardado
        }
    }

    /** Copia guardada sin consultar el servidor (por ejemplo, para resolver un QR sin conexión). */
    fun <T> guardado(clave: String, serializador: KSerializer<T>): T? {
        val uid = AccionesSinConexion.uid() ?: return null
        val almacen = fabrica?.invoke("${clave}_${uid.filter(Char::isLetterOrDigit)}") ?: return null
        return runCatching { almacen.leer() }.getOrNull()
            ?.let { runCatching { json.decodeFromString(serializador, it) }.getOrNull() }
    }
}
