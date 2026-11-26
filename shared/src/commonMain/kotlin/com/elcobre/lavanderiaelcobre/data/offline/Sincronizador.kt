package com.elcobre.lavanderiaelcobre.data.offline

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Envía la cola en orden. Reglas:
 * - Solo se envían las acciones de la cuenta con sesión activa ([uidActual]).
 * - Las acciones de una misma entidad van en orden: si una queda rechazada, las
 *   siguientes de esa entidad esperan (no se envían fuera de orden).
 * - Sin conexión se detiene y reintenta después con espera creciente.
 * - Si la sesión venció o la cuenta perdió acceso, se pausa sin borrar nada.
 * - Un rechazo del servidor deja la acción en "Requiere atención" con el motivo; nunca se
 *   pierde en silencio. Solo la persona puede descartarla.
 */
class Sincronizador(
    private val cola: ColaAcciones,
    private val ejecutor: EjecutorAcciones,
    private val reloj: RelojDispositivo,
    private val uidActual: () -> String?,
) {
    private val mutex = Mutex()

    /** Crea una acción con la hora actual del teléfono (reloj del sistema y monotónico). */
    @Suppress("LongParameterList") // Los datos de una acción, sin agruparlos en otro tipo.
    fun nueva(
        id: String,
        tipo: TipoAccion,
        uid: String,
        entidad: String,
        datos: Map<String, String>,
        descripcion: String,
        fotos: List<FotoPendiente> = emptyList(),
    ) = AccionPendiente(
        id = id, tipo = tipo, uid = uid, entidad = entidad, datos = datos, descripcion = descripcion, fotos = fotos,
        ocurridoEnMs = reloj.ahoraMs(), zonaHoraria = reloj.zonaHoraria(), monotonicoMs = reloj.monotonicoMs(),
        arranque = reloj.arranque(),
    )

    /**
     * Guarda la acción en la cola y la envía junto con las anteriores. Si se aplicó, la
     * pantalla sigue como siempre; si el servidor la rechaza en este primer intento, se
     * quita de la cola y se informa el motivo (la persona está viendo la pantalla).
     */
    suspend fun registrar(accion: AccionPendiente): ResultadoRegistro {
        cola.agregar(accion)
        val resumen = sincronizar(forzar = true)
        val estado = cola.buscar(accion.id) ?: return ResultadoRegistro.Enviada
        return when {
            estado.estado == EstadoAccion.REQUIERE_ATENCION && estado.intentos <= 1 && !estado.aplicada -> {
                cola.quitar(accion.id)
                ResultadoRegistro.Rechazada(estado.ultimoError ?: "El servidor rechazó la acción.")
            }
            estado.aplicada -> ResultadoRegistro.EnCola("La acción se guardó; las fotografías se enviarán al recuperar la conexión.")
            resumen.pausada != null -> ResultadoRegistro.EnCola(resumen.pausada)
            else -> ResultadoRegistro.EnCola(AccionEnColaException.MENSAJE_EN_COLA)
        }
    }

    /** Envía lo pendiente de la sesión actual. [forzar] ignora la espera entre reintentos. */
    @Suppress("CyclomaticComplexMethod", "LoopWithTooManyJumpStatements", "NestedBlockDepth")
    suspend fun sincronizar(forzar: Boolean = false): ResumenSincronizacion = mutex.withLock {
        val uid = uidActual()
        var enviadas = 0
        var pausada: String? = null
        val bloqueadas = mutableSetOf<String>()
        var servidor: Long? = null
        var servidorConsultado = false
        for (inicial in cola.acciones.value) {
            var accion = cola.buscar(inicial.id) ?: continue
            if (accion.uid != uid) continue
            if (accion.estado == EstadoAccion.REQUIERE_ATENCION || accion.entidad in bloqueadas) {
                // Mantiene estricto orden por entidad: las acciones posteriores esperan resolución.
                bloqueadas += accion.entidad
                continue
            }
            if (!forzar && accion.proximoIntentoMs > reloj.ahoraMs()) {
                bloqueadas += accion.entidad
                continue
            }
            if (!servidorConsultado) {
                // La hora del servidor se consulta una sola vez por pasada y solo si hay algo listo.
                servidor = ejecutor.horaServidorMs()
                servidorConsultado = true
            }
            if (servidor == null) {
                reintentarDespues(accion, "Sin conexión.")
                break
            }
            if (!accion.aplicada) {
                val hora = RelojAcciones.horaOriginal(accion, servidor, reloj.ahoraMs(), reloj.monotonicoMs(), reloj.arranque())
                when (val r = ejecutor.aplicar(accion, hora)) {
                    ResultadoEnvio.Aplicada -> {
                        accion = accion.copy(aplicada = true, ultimoError = null)
                        if (accion.fotos.isEmpty()) {
                            cola.quitar(accion.id)
                            enviadas++
                            continue
                        }
                        cola.actualizar(accion)
                    }
                    ResultadoEnvio.SinRed -> {
                        reintentarDespues(accion, "Sin conexión.")
                        break
                    }
                    is ResultadoEnvio.Sesion -> {
                        pausada = r.mensaje
                        break
                    }
                    is ResultadoEnvio.Rechazada -> {
                        marcarRechazada(accion, r.mensaje)
                        bloqueadas += accion.entidad
                        continue
                    }
                }
            }
            when (val r = ejecutor.subirFotos(accion)) {
                ResultadoEnvio.Aplicada -> {
                    ejecutor.fotosConfirmadas(accion)
                    cola.quitar(accion.id)
                    enviadas++
                }
                ResultadoEnvio.SinRed -> {
                    reintentarDespues(accion, "Fotografías pendientes: sin conexión.")
                    break
                }
                is ResultadoEnvio.Sesion -> {
                    pausada = r.mensaje
                    break
                }
                is ResultadoEnvio.Rechazada -> {
                    marcarRechazada(accion, "La acción se guardó, pero una fotografía fue rechazada: ${r.mensaje}")
                    bloqueadas += accion.entidad
                }
            }
        }
        val propias = cola.acciones.value.filter { it.uid == uid }
        ResumenSincronizacion(
            enviadas = enviadas,
            pendientes = propias.count { it.estado == EstadoAccion.PENDIENTE },
            requierenAtencion = propias.count { it.estado == EstadoAccion.REQUIERE_ATENCION },
            pausada = pausada,
        )
    }

    private suspend fun reintentarDespues(accion: AccionPendiente, motivo: String) {
        val intentos = accion.intentos + 1
        val proximo = reloj.ahoraMs() + RelojAcciones.espera(intentos)
        cola.actualizar(accion.copy(intentos = intentos, ultimoError = motivo, proximoIntentoMs = proximo))
    }

    private suspend fun marcarRechazada(accion: AccionPendiente, motivo: String) {
        cola.actualizar(accion.copy(estado = EstadoAccion.REQUIERE_ATENCION, intentos = accion.intentos + 1, ultimoError = motivo))
    }

    /** Vuelve a poner en cola una acción que requería atención (por ejemplo, tras corregir algo en la web). */
    suspend fun reintentar(id: String) {
        val accion = cola.buscar(id) ?: return
        cola.actualizar(accion.copy(estado = EstadoAccion.PENDIENTE, proximoIntentoMs = 0))
    }

    /** Descarta una acción por decisión de la persona; sus fotos se borran del teléfono. */
    suspend fun descartar(id: String) {
        val accion = cola.buscar(id) ?: return
        ejecutor.fotosConfirmadas(accion)
        cola.quitar(id)
    }
}
