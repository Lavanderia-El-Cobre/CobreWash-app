package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Pide al sistema que desenfoque lo que queda detrás de la ventana actual (hojas y
 * diálogos, que viven en su propia ventana y no ven el desenfoque de la app). Se llama
 * dentro del contenido de la hoja o diálogo. Devuelve `true` si el desenfoque quedó
 * activo; si no (Android < 12, emulador sin soporte, ahorro de batería) la superficie
 * debe ser casi opaca para seguir leyéndose bien.
 */
@Composable
internal expect fun desenfocarDetrasDeVentana(radio: Dp): Boolean
