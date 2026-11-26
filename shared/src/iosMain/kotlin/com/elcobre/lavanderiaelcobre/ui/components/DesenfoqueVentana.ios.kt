package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Indica que el desenfoque nativo detrás de la ventana no está disponible desde Compose en iOS.
 *
 * Devolver `false` permite que los diálogos y láminas glassmorphism activen su respaldo visual
 * (velo translúcido) en lugar de depender de un blur de ventana a nivel de sistema.
 */
@Composable
internal actual fun desenfocarDetrasDeVentana(radio: Dp): Boolean = false
