package com.elcobre.lavanderiaelcobre.data.offline

import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Punto de entrada al modo sin conexión. Cada plataforma lo configura al iniciar la app
 * (Android: `inicializarAccionesSinConexion`). Sin configurar (iOS, pruebas) las acciones
 * se informan como no disponibles.
 */
object AccionesSinConexion {
    var cola: ColaAcciones? = null
        private set
    var sincronizador: Sincronizador? = null
        private set
    private var uidActual: () -> String? = { null }
    private var programarEnvio: () -> Unit = {}

    private val _conectado = MutableStateFlow(true)

    /** Estado de la red según la plataforma (para el aviso "Sin conexión"). */
    val conectado: StateFlow<Boolean> = _conectado.asStateFlow()

    /** Registra la cola, el sincronizador y los hooks nativos al iniciar la aplicación. */
    fun configurar(cola: ColaAcciones, sincronizador: Sincronizador, uidActual: () -> String?, programarEnvio: () -> Unit) {
        this.cola = cola
        this.sincronizador = sincronizador
        this.uidActual = uidActual
        this.programarEnvio = programarEnvio
    }

    /** Actualiza el estado de red reportado por el monitor de conectividad de la plataforma. */
    fun informarConexion(conectado: Boolean) {
        _conectado.value = conectado
    }

    /** UID de la cuenta autenticada actualmente, o `null` si no hay sesión activa. */
    fun uid(): String? = uidActual()

    /**
     * Registra una acción y la envía. Si se aplicó, vuelve normalmente. Si quedó en cola
     * lanza [AccionEnColaException] (las pantallas la muestran como aviso). Si el servidor
     * la rechazó, lanza [OperacionComandaException] con el motivo, como antes.
     */
    @OptIn(ExperimentalUuidApi::class)
    @Suppress("ThrowsCount") // No disponible, sin sesión, duplicada, en cola o rechazada: cada una con su mensaje.
    suspend fun ejecutar(
        tipo: TipoAccion,
        entidad: String,
        datos: Map<String, String?>,
        descripcion: String,
        fotos: List<FotoPendiente> = emptyList(),
    ) {
        val s = sincronizador ?: throw OperacionComandaException("Esta función aún no está disponible en esta plataforma.")
        val uid = uidActual() ?: throw OperacionComandaException("Inicia sesión nuevamente.")
        duplicada(tipo, entidad, uid)?.let { throw OperacionComandaException(it) }
        val limpios = datos.filterValues { it != null }.mapValues { it.value!! }
        val accion = s.nueva(Uuid.random().toString(), tipo, uid, entidad, limpios, descripcion, fotos)
        when (val r = s.registrar(accion)) {
            ResultadoRegistro.Enviada -> Unit
            is ResultadoRegistro.EnCola -> {
                programarEnvio()
                throw AccionEnColaException(r.motivo)
            }
            is ResultadoRegistro.Rechazada -> throw OperacionComandaException(r.mensaje)
        }
    }

    /**
     * Mientras una acción de cambio de estado no se envía, la pantalla sigue mostrando el
     * estado anterior: repetirla crearía un duplicado que el servidor rechazaría después.
     */
    private fun duplicada(tipo: TipoAccion, entidad: String, uid: String): String? {
        val propias = cola?.acciones?.value.orEmpty().filter { it.uid == uid }
        return when {
            tipo in UNA_POR_ENTIDAD && propias.any { it.tipo == tipo && it.entidad == entidad } ->
                "Ya hay una acción «${tipo.etiqueta}» sin enviar para esta comanda o salida. Se enviará al recuperar la conexión."
            tipo == TipoAccion.SALIDA_FLOTA && propias.any { it.tipo == TipoAccion.SALIDA_FLOTA } ->
                "Ya hay una salida de vehículo sin enviar. Espera a recuperar la conexión antes de registrar otra."
            else -> null
        }
    }

    private val UNA_POR_ENTIDAD = setOf(TipoAccion.COMPLETAR_ETAPA, TipoAccion.ENTREGAR, TipoAccion.RETORNO_FLOTA)

    /** Envía lo pendiente (al recuperar la red, al volver a la app o desde la bandeja). */
    suspend fun sincronizar(forzar: Boolean = false): ResumenSincronizacion? {
        val resumen = sincronizador?.sincronizar(forzar)
        if (resumen != null && resumen.pendientes > 0) programarEnvio()
        return resumen
    }
}
