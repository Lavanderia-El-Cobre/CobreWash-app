package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Clase de ancho (breakpoints de Material 3): teléfono / tablet vertical o plegable / tablet horizontal. */
enum class WidthClass { Compact, Medium, Expanded }

/** Clase de alto: `Compact` es un teléfono en horizontal, donde el espacio vertical manda. */
enum class HeightClass { Compact, Medium, Expanded }

/**
 * Tamaño de la ventana y decisiones de layout derivadas. Lo calcula [CobreTheme] una
 * sola vez y lo publica por composición, así que ninguna pantalla vuelve a medir con
 * `BoxWithConstraints` ni repite breakpoints a mano.
 */
@Immutable
data class CobreWindow(val width: Dp, val height: Dp) {
    val widthClass: WidthClass = when {
        width < 600.dp -> WidthClass.Compact
        width < 840.dp -> WidthClass.Medium
        else -> WidthClass.Expanded
    }

    val heightClass: HeightClass = when {
        height < 480.dp -> HeightClass.Compact
        height < 900.dp -> HeightClass.Medium
        else -> HeightClass.Expanded
    }

    val isLandscape: Boolean get() = width > height

    /** Teléfono en horizontal: cabeceras en línea y navegación lateral para liberar alto. */
    val isPhoneLandscape: Boolean get() = heightClass == HeightClass.Compact

    /** Navegación lateral (rail/sidebar) en vez de la barra inferior flotante. */
    val useSideNavigation: Boolean get() = widthClass != WidthClass.Compact || isPhoneLandscape

    /** Tablet horizontal: lista y detalle lado a lado. */
    val useListDetail: Boolean get() = widthClass == WidthClass.Expanded && !isPhoneLandscape

    /** Margen lateral de pantalla. */
    val gutter: Dp get() = if (widthClass == WidthClass.Compact) CobreSpacing.gutter else CobreSpacing.gutterWide

    /** Ancho máximo de una columna de lectura, para que las tarjetas no se estiren en tablet. */
    val contentMaxWidth: Dp get() = 720.dp

    /** Columnas para cuadrículas de tarjetas. */
    val gridColumns: Int get() = when {
        widthClass == WidthClass.Compact -> 1
        // Teléfono horizontal: ancho de tablet pero tarjetas que necesitan aire; 2 columnas.
        isPhoneLandscape || widthClass == WidthClass.Medium -> 2
        // Lista-detalle: la lista ocupa ~40% del ancho, una columna.
        useListDetail -> 1
        else -> 3
    }
}

/** Valor por defecto (teléfono vertical) para previews y tests fuera de [CobreTheme]. */
internal val LocalCobreWindow = staticCompositionLocalOf { CobreWindow(width = 390.dp, height = 844.dp) }
