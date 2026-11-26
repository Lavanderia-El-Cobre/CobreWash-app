package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Implementación sin efecto en iOS.
 *
 * El estilo de la barra de estado (íconos claros u oscuros) lo controla el `UIViewController`
 * anfitrión desde la capa iOS/SwiftUI y no se muta dinámicamente desde Compose.
 */
@Composable
actual fun BarrasDelSistema(temaOscuro: Boolean) = Unit
