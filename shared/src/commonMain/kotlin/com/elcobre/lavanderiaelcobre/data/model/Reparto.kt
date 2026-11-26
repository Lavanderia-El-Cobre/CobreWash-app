package com.elcobre.lavanderiaelcobre.data.model

import kotlinx.serialization.Serializable

/** Estados de una salida de vehículo (`SalidaVehiculoEstado`). */
enum class EstadoSalida(val etiqueta: String) {
    PROGRAMADA("Programada"),
    EN_SERVICIO("En ruta"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): EstadoSalida = entries.firstOrNull { it.name == codigo } ?: PROGRAMADA
    }
}

/** Inspección técnica de una salida (ANTES o DESPUES). */
@Serializable
data class InspeccionReparto(
    val id: String,
    val estado: EstadoCondicionVehiculo,
    val kilometraje: Int,
    val observaciones: String?,
    val registradoEn: String,
    val fotos: Int,
)

/**
 * Salida asignada al repartidor (`GetMisSalidasVehiculo`). Administración la programa o
 * la registra en ruta; el repartidor inspecciona y la inicia, y registra el retorno.
 */
@Serializable
data class SalidaReparto(
    val id: String,
    val estado: EstadoSalida,
    val vehiculoId: String,
    val patente: String,
    val vehiculo: String,
    val creadoEn: String,
    val fechaSalida: String?,
    val fechaRetorno: String?,
    val observaciones: String?,
    val antes: InspeccionReparto?,
    val despues: InspeccionReparto?,
) {
    /** Programada: falta la inspección previa o, si ya está, iniciar el recorrido. */
    val puedeIniciar: Boolean get() = estado == EstadoSalida.PROGRAMADA

    val puedeRetornar: Boolean get() = estado == EstadoSalida.EN_SERVICIO

    val kilometrosRecorridos: Int?
        get() = if (antes != null && despues != null) (despues.kilometraje - antes.kilometraje).coerceAtLeast(0) else null
}

/** Inspección que registra el repartidor al iniciar o cerrar un recorrido. */
data class InspeccionInput(
    val salidaId: String,
    val kilometraje: Int,
    val estado: EstadoCondicionVehiculo,
    val observaciones: String?,
    val fotos: List<FotoChequeo>,
)

/** `null` si la inspección es válida; si no, el motivo (km mínimo, estado o largo de observaciones). */
fun validarInspeccion(km: Int?, kmMinimo: Int, estado: EstadoCondicionVehiculo, esSalida: Boolean, observaciones: String): String? =
    when {
        km == null -> "Ingresa el kilometraje del odómetro."
        km < kmMinimo -> "No puede ser menor al kilometraje anterior registrado ($kmMinimo km)."
        esSalida && estado == EstadoCondicionVehiculo.NO_APTO -> "Un vehículo no apto no puede salir a ruta. Avisa a administración."
        observaciones.length > 500 -> "Las observaciones admiten hasta 500 caracteres."
        else -> null
    }

/** Disponibilidad de un vehículo para el repartidor, como `construirPanelReparto` de la web. */
enum class DisponibilidadVehiculo(val etiqueta: String) {
    CONTIGO("Contigo"),
    DISPONIBLE("Disponible"),
    EN_RUTA("En ruta"),
}

/** Vehículo activo visto por el repartidor (`GetPanelRepartidor`). */
@Serializable
data class VehiculoReparto(
    val id: String,
    val patente: String,
    val vehiculo: String,
    val descripcion: String?,
    val disponibilidad: DisponibilidadVehiculo,
    /** Último kilometraje registrado; es el mínimo aceptado para una nueva salida. */
    val kilometrajeActual: Int,
    /** Quién tiene hoy el vehículo cuando no está disponible. */
    val conductorActual: String?,
)

/**
 * Sección "Mi vehículo" del repartidor (`/intranet/reparto`): vehículos activos con su
 * disponibilidad, la salida propia en curso (a lo más una) y el historial de salidas.
 */
@Serializable
data class PanelReparto(
    val vehiculos: List<VehiculoReparto> = emptyList(),
    val salidas: List<SalidaReparto> = emptyList(),
) {
    val salidaEnCurso: SalidaReparto? get() = salidas.firstOrNull { it.estado == EstadoSalida.EN_SERVICIO }

    /** Salidas que administración programó desde la app y aún no se inician. */
    val programadas: List<SalidaReparto> get() = salidas.filter { it.estado == EstadoSalida.PROGRAMADA }
    val historial: List<SalidaReparto>
        get() = salidas.filter { it.estado == EstadoSalida.FINALIZADA || it.estado == EstadoSalida.CANCELADA }
}

/**
 * Reglas de la salida y el regreso del repartidor (`validarFormMovimiento` de la web):
 * kilometraje no menor al anterior, un vehículo no apto no sale y, si no está apto,
 * hay que describir lo observado.
 */
fun validarMovimiento(
    km: Int?,
    kmMinimo: Int,
    estado: EstadoCondicionVehiculo,
    esSalida: Boolean,
    observaciones: String,
): String? = validarInspeccion(km, kmMinimo, estado, esSalida, observaciones) ?: when {
    estado != EstadoCondicionVehiculo.APTO && observaciones.isBlank() -> "Describe lo que observaste en el vehículo."
    else -> null
}

/** Motivos de un problema de entrega; deben coincidir con los que acepta `ReportarProblemaEntrega`. */
val MotivosProblemaEntrega = listOf(
    "Cliente ausente",
    "Dirección errónea",
    "Dirección inaccesible",
    "Cliente rechaza la entrega",
    "Pedido incompleto o dañado",
    "Otro",
)

const val DETALLE_ENTREGA_MAXIMO = 280
const val DETALLE_PROBLEMA_MAXIMO = 250

/** Problema de entrega abierto o resuelto (incidencia con prefijo "Entrega: "). */
@Serializable
data class ProblemaEntrega(
    val id: String,
    val motivo: String,
    val descripcion: String?,
    val estado: EstadoIncidencia,
    val fecha: String,
    val reportadaPor: String,
)

/** Comanda lista para entregar (`GetComandasParaEntrega`). */
@Serializable
data class ComandaEntrega(
    val id: String,
    val numero: String,
    val cliente: String,
    val tipoCliente: TipoClienteFicha,
    val telefono: String?,
    val direccion: String?,
    val empresa: String?,
    val proyecto: String?,
    val observaciones: String?,
    val fechaEntregaEstimada: String?,
    /** "3 prendas · CAMISA ×2, SÁBANA ×1", como `resumirPrendas` de la web. */
    val prendas: String,
    val problemas: List<ProblemaEntrega>,
) {
    val problemasAbiertos: List<ProblemaEntrega> get() = problemas.filter { it.estado != EstadoIncidencia.RESUELTA }
}

/** Entrega que registró la persona (historial `ENTREGADA` propio). */
@Serializable
data class EntregaRegistrada(val id: String, val fecha: String, val detalle: String?, val numero: String, val cliente: String)

/** Comandas pendientes de entrega y el historial de entregas propias del repartidor. */
@Serializable
data class EntregasReparto(
    val comandas: List<ComandaEntrega> = emptyList(),
    val misEntregas: List<EntregaRegistrada> = emptyList(),
)

/** Recorta espacios y convierte en `null` un detalle vacío, como `normalizarDetalle` de la web. */
fun normalizarDetalleEntrega(texto: String): String? = texto.replace(Regex("\\s+"), " ").trim().ifEmpty { null }

/** Valida el detalle opcional al confirmar una entrega; devuelve el error o `null`. */
fun validarEntrega(detalle: String): String? {
    val limpio = normalizarDetalleEntrega(detalle)
    return if (limpio != null && limpio.length > DETALLE_ENTREGA_MAXIMO) {
        "El detalle de la entrega admite hasta $DETALLE_ENTREGA_MAXIMO caracteres."
    } else {
        null
    }
}

/** Valida motivo y detalle de un problema de entrega; exige descripción cuando el motivo es "Otro". */
fun validarProblemaEntrega(motivo: String, detalle: String): String? {
    val limpio = normalizarDetalleEntrega(detalle)
    return when {
        motivo !in MotivosProblemaEntrega -> "Selecciona el motivo del problema."
        motivo == "Otro" && limpio == null -> "Describe el problema de la entrega."
        limpio != null && limpio.length > DETALLE_PROBLEMA_MAXIMO -> "El detalle admite hasta $DETALLE_PROBLEMA_MAXIMO caracteres."
        else -> null
    }
}

/** Quita el prefijo técnico "Entrega: " que distingue estas incidencias. */
fun motivoVisibleEntrega(motivo: String): String = motivo.removePrefix("Entrega: ")

/** Busca por número, cliente, dirección, empresa o proyecto, sin tildes ni mayúsculas. */
fun filtrarComandasEntrega(comandas: List<ComandaEntrega>, busqueda: String): List<ComandaEntrega> {
    val termino = Validaciones.normalizarCatalogo(busqueda)
    if (termino.isEmpty()) return comandas
    return comandas.filter { c ->
        listOf(c.numero, c.cliente, c.direccion, c.empresa, c.proyecto)
            .any { Validaciones.normalizarCatalogo(it.orEmpty()).contains(termino) }
    }
}
