package com.elcobre.lavanderiaelcobre.data.model

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
