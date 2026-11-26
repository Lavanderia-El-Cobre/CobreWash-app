package com.elcobre.lavanderiaelcobre.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Implementación Android de [CobreBackHandler] que delega en `BackHandler` de AndroidX
 * para interceptar el botón o gesto de retroceso del sistema.
 */
@Composable
actual fun CobreBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
