package com.elcobre.lavanderiaelcobre.ui.components

import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo

/**
 * Guía visual que indica qué parte del vehículo fotografiar durante una inspección.
 *
 * @property etiqueta Nombre breve guardado en la evidencia y mostrado bajo la miniatura.
 * @property tituloCamara Encabezado destacado sobre el visor de la cámara (ej. "Frente del vehículo").
 * @property indicacion Instrucción breve de encuadre mostrada bajo [tituloCamara].
 * @property paso Número de paso dentro de la secuencia sugerida (1..N), o `null` para fotos extra.
 */
data class GuiaFotoVehiculo(
    val etiqueta: String,
    val tituloCamara: String,
    val indicacion: String,
    val paso: Int? = null,
)

/** Secuencia sugerida de vistas al inspeccionar un vehículo, comenzando por el frente. */
val GUIAS_FOTOS_VEHICULO: List<GuiaFotoVehiculo> = listOf(
    GuiaFotoVehiculo(
        etiqueta = "Frente",
        tituloCamara = "Frente del vehículo",
        indicacion = "Encuadra el parachoques delantero, capó y patente",
        paso = 1,
    ),
    GuiaFotoVehiculo(
        etiqueta = "Lateral derecho",
        tituloCamara = "Lateral derecho del vehículo",
        indicacion = "Muestra el costado derecho completo y puertas",
        paso = 2,
    ),
    GuiaFotoVehiculo(
        etiqueta = "Lateral izquierdo",
        tituloCamara = "Lateral izquierdo del vehículo",
        indicacion = "Muestra el costado izquierdo completo y espejos",
        paso = 3,
    ),
    GuiaFotoVehiculo(
        etiqueta = "Posterior",
        tituloCamara = "Parte trasera del vehículo",
        indicacion = "Encuadra el portalón trasero, luces y patente",
        paso = 4,
    ),
    GuiaFotoVehiculo(
        etiqueta = "Tablero / Odómetro",
        tituloCamara = "Tablero y odómetro",
        indicacion = "Enfoca el kilometraje visible en el tablero",
        paso = 5,
    ),
)

/**
 * Determina la siguiente vista sugerida según [fotosActuales].
 * Si alguna vista de [GUIAS_FOTOS_VEHICULO] aún no se ha capturado (o fue descartada),
 * la propone primero; una vez cubiertas todas, sugiere evidencias adicionales numeradas.
 */
fun siguienteGuiaFoto(fotosActuales: List<FotoChequeo>): GuiaFotoVehiculo {
    val cubiertas = fotosActuales.mapNotNull { it.descripcion?.trim() }.toSet()
    val pendiente = GUIAS_FOTOS_VEHICULO.firstOrNull { it.etiqueta !in cubiertas }
    if (pendiente != null) return pendiente
    val numero = fotosActuales.size + 1
    return GuiaFotoVehiculo(
        etiqueta = "Evidencia $numero",
        tituloCamara = "Evidencia adicional #$numero",
        indicacion = "Captura cualquier detalle adicional o daño observado",
        paso = null,
    )
}
