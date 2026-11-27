package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Crea un lanzador multiplataforma que solicita el permiso de cámara (si es requerido)
 * y abre la cámara con la [GuiaFotoVehiculo] seleccionada para capturar evidencia.
 *
 * @param onFotoCapturada Callback invocado con la ruta local o URI y la guía aplicada.
 */
@Composable
expect fun rememberCapturadorFoto(
    onFotoCapturada: (uri: String, guia: GuiaFotoVehiculo) -> Unit,
): (GuiaFotoVehiculo) -> Unit

/**
 * Renderiza la miniatura de una fotografía de inspección si [uri] corresponde a un archivo
 * local accesible en el dispositivo, o un ícono de cámara en caso contrario.
 *
 * @param descripcion Texto accesible (`contentDescription`) para lectores de pantalla.
 * @param tint Color aplicado al ícono de respaldo cuando la imagen local no está disponible.
 */
@Composable
expect fun MiniaturaFotoChequeo(
    uri: String,
    descripcion: String?,
    tint: Color,
    modifier: Modifier = Modifier,
)
