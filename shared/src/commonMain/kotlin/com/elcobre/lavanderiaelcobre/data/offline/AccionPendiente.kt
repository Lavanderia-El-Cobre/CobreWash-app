package com.elcobre.lavanderiaelcobre.data.offline

import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import kotlinx.serialization.Serializable

/**
 * Acciones que la app puede hacer sin conexión y enviar después con las mutaciones
 * "…Movil" del backend (`dataconnect/example/movil.gql`). Login, cuentas, términos,
 * privacidad, la estimación de IA, crear, editar o anular comandas exigen conexión.
 */
@Serializable
enum class TipoAccion(val etiqueta: String) {
    COMPLETAR_ETAPA("Completar etapa"),
    REGISTRAR_INCIDENCIA("Registrar incidencia"),
    ENTREGAR("Entregar comanda"),
    REPORTAR_PROBLEMA("Problema de entrega"),
    SALIDA_FLOTA("Salida de vehículo"),
    RETORNO_FLOTA("Regreso de vehículo"),
    CONTEO("Conteo de inventario"),
}

/** Ciclo de vida local de una [AccionPendiente] dentro de la cola sin conexión. */
@Serializable
enum class EstadoAccion {
    /** En cola: se enviará en cuanto haya conexión. */
    PENDIENTE,

    /** El servidor la rechazó después de trabajar sin conexión: hay que revisarla. */
    REQUIERE_ATENCION,
}

/** Fotografía de evidencia guardada en el dispositivo hasta que se suba. */
@Serializable
data class FotoPendiente(val id: String, val uri: String, val descripcion: String? = null)

/**
 * Una acción registrada en el teléfono. [id] es el `idOperacion` que el servidor usa para
 * no duplicarla si se reintenta. La hora original se guarda con el reloj del sistema
 * ([ocurridoEnMs], [zonaHoraria]) y con el reloj monotónico ([monotonicoMs], que no cambia
 * aunque se ajuste la hora del teléfono) del arranque [arranque]. Nunca guarda contraseñas.
 */
@Serializable
data class AccionPendiente(
    val id: String,
    val tipo: TipoAccion,
    /** Cuenta que hizo la acción: solo se envía con esa misma sesión. */
    val uid: String,
    /** Comanda, salida o insumo afectado: las acciones de una misma entidad se envían en orden. */
    val entidad: String,
    val datos: Map<String, String>,
    /** Texto para la persona, por ejemplo "Completar etapa · ELCOBRE-A1B2". */
    val descripcion: String,
    val fotos: List<FotoPendiente> = emptyList(),
    val ocurridoEnMs: Long,
    val zonaHoraria: String,
    val monotonicoMs: Long,
    val arranque: String,
    val estado: EstadoAccion = EstadoAccion.PENDIENTE,
    /** La mutación ya se aplicó en el servidor; solo faltan las fotografías. */
    val aplicada: Boolean = false,
    val intentos: Int = 0,
    val ultimoError: String? = null,
    val proximoIntentoMs: Long = 0,
)

/**
 * La acción no se pudo enviar por falta de conexión y quedó guardada en el teléfono.
 * Las pantallas la muestran como aviso, no como error.
 */
class AccionEnColaException(message: String = MENSAJE_EN_COLA) : OperacionComandaException(message) {
    companion object {
        const val MENSAJE_EN_COLA = "Sin conexión: la acción quedó guardada en el teléfono y se enviará automáticamente."
    }
}
