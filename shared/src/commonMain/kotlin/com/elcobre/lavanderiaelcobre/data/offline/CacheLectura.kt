package com.elcobre.lavanderiaelcobre.data.offline

import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _desdeCache = MutableStateFlow(false)

    /** `true` si lo que se muestra viene de la copia guardada (sin conexión). */
    val desdeCache: StateFlow<Boolean> = _desdeCache.asStateFlow()

    fun configurar(fabrica: (String) -> AlmacenCola, borrarTodo: () -> Unit) {
        this.fabrica = fabrica
        this.borrarTodo = borrarTodo
    }

    fun limpiar() {
        borrarTodo()
        _desdeCache.value = false
    }

    private fun esSinConexion(error: OperacionComandaException): Boolean {
        val mensaje = error.message.orEmpty().lowercase()
        return !AccionesSinConexion.conectado.value || "conexión" in mensaje || "conectar" in mensaje
    }

    /** Lee del servidor y guarda la copia; sin conexión devuelve la última copia de esta cuenta. */
    suspend fun <T> leer(clave: String, serializador: KSerializer<T>, remoto: suspend () -> T): T {
        val uid = AccionesSinConexion.uid()
        val almacen = uid?.let { fabrica?.invoke("${clave}_${it.filter(Char::isLetterOrDigit)}") } ?: return remoto()
        return try {
            remoto().also {
                almacen.escribir(json.encodeToString(serializador, it))
                _desdeCache.value = false
            }
        } catch (error: OperacionComandaException) {
            if (!esSinConexion(error)) throw error
            val guardado = almacen.leer()?.let { runCatching { json.decodeFromString(serializador, it) }.getOrNull() }
                ?: throw error
            _desdeCache.value = true
            guardado
        }
    }

    /** Copia guardada sin consultar el servidor (por ejemplo, para resolver un QR sin conexión). */
    fun <T> guardado(clave: String, serializador: KSerializer<T>): T? {
        val uid = AccionesSinConexion.uid() ?: return null
        val almacen = fabrica?.invoke("${clave}_${uid.filter(Char::isLetterOrDigit)}") ?: return null
        return almacen.leer()?.let { runCatching { json.decodeFromString(serializador, it) }.getOrNull() }
    }
}
