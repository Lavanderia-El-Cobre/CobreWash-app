package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Implementación sin efecto en iOS.
 *
 * A diferencia de `FLAG_SECURE` en Android, iOS no ofrece una bandera directa para impedir
 * capturas de pantalla desde Compose.
 */
@Composable
actual fun ProtegerCapturas() = Unit
