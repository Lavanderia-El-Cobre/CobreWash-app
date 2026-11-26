package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Implementación sin efecto del manejador de retroceso del sistema en iOS.
 *
 * A diferencia de Android, iOS no posee botón físico ni gesto global de retroceso interceptable
 * desde Compose; el regreso se resuelve con botones en pantalla y gestos nativos de UIKit.
 */
@Composable
actual fun CobreBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Sin acción: UIKit y los controles visibles de la UI gestionan la navegación hacia atrás.
}
