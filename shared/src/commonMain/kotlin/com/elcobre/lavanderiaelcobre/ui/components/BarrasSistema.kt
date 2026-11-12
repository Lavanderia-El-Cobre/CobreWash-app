package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Ajusta el color de los íconos de la barra de estado y de navegación del sistema al
 * tema de la app (no al del teléfono): con tema oscuro, íconos claros, y al revés.
 * Sin esto, al cambiar de tema dentro de la app los íconos podían quedar invisibles.
 */
@Composable
expect fun BarrasDelSistema(temaOscuro: Boolean)
