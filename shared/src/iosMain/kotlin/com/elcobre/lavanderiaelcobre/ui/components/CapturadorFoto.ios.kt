package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Implementación provisional del capturador de fotos en iOS.
 *
 * Mientras no se integre la cámara nativa (`UIImagePickerController`), devuelve un disparador
 * que genera rutas ficticias secuenciales para permitir probar el flujo de evidencia fotográfica.
 * Usa [rememberUpdatedState] para invocar siempre el último callback sin recrear la lambda.
 */
@Composable
actual fun rememberCapturadorFoto(
    onFotoCapturada: (uri: String) -> Unit,
): () -> Unit {
    val callbackActual by rememberUpdatedState(onFotoCapturada)
    var secuencia by remember { mutableIntStateOf(0) }
    return remember {
        {
            secuencia += 1
            callbackActual("vehiculos/evidencia/ios-captura-$secuencia.jpg")
        }
    }
}

/**
 * Implementación provisional de la miniatura de chequeo fotográfico en iOS.
 *
 * Muestra un ícono de cámara como marcador visual en lugar de decodificar [uri], en coherencia
 * con las rutas simuladas producidas por [rememberCapturadorFoto].
 */
@Composable
actual fun MiniaturaFotoChequeo(
    uri: String,
    descripcion: String?,
    tint: Color,
    modifier: Modifier,
) {
    Icon(
        imageVector = CobreIcons.PhotoCamera,
        contentDescription = descripcion,
        tint = tint,
        modifier = modifier,
    )
}
