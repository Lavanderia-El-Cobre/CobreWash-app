package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente multiplataforma que renderiza la vista nativa de cámara y escanea códigos QR
 * (en Android mediante CameraX y ML Kit Barcode Scanning).
 *
 * @param isActive Controla si el escáner procesa fotogramas o permanece pausado.
 * @param onQrCodeScanned Callback invocado con el contenido (`rawValue`) del QR detectado.
 */
@Composable
expect fun QrScannerView(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    onQrCodeScanned: (String) -> Unit,
)
