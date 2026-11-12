package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Espacio que ocupa la navegación flotante de vidrio (barra inferior o rail lateral)
 * **además** de las barras del sistema. La publica la raíz de la app para cada destino;
 * es cero en pantallas sin navegación (Login, Escáner, Detalle, Vehículos).
 */
val LocalShellPadding = staticCompositionLocalOf { PaddingValues(0.dp) }

/**
 * Padding total para contenido de borde a borde que pasa por debajo del vidrio:
 * barras del sistema + navegación flotante + [extra]. Va como `contentPadding` de la
 * lista, no como padding del contenedor, para que el contenido se vea a través del vidrio.
 */
@Composable
fun rememberShellContentPadding(
    extra: PaddingValues = PaddingValues(0.dp),
    incluirSistema: Boolean = true,
): PaddingValues {
    val dir = LocalLayoutDirection.current
    // Sin [incluirSistema] (p. ej. en tablet, donde el contenedor ya aplicó las barras del
    // sistema): asPaddingValues lee los insets crudos y los duplicaría.
    val sistema = if (incluirSistema) WindowInsets.systemBars.asPaddingValues() else PaddingValues(0.dp)
    val shell = LocalShellPadding.current
    return PaddingValues(
        start = sistema.calculateStartPadding(dir) + shell.calculateStartPadding(dir) + extra.calculateStartPadding(dir),
        top = sistema.calculateTopPadding() + shell.calculateTopPadding() + extra.calculateTopPadding(),
        end = sistema.calculateEndPadding(dir) + shell.calculateEndPadding(dir) + extra.calculateEndPadding(dir),
        bottom = sistema.calculateBottomPadding() + shell.calculateBottomPadding() + extra.calculateBottomPadding(),
    )
}
