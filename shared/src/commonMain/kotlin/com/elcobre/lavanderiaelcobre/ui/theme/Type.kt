package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.inconsolata_bold
import lavanderiaelcobre.shared.generated.resources.inconsolata_medium
import lavanderiaelcobre.shared.generated.resources.inter_bold
import lavanderiaelcobre.shared.generated.resources.inter_medium
import lavanderiaelcobre.shared.generated.resources.inter_regular
import lavanderiaelcobre.shared.generated.resources.inter_semibold
import lavanderiaelcobre.shared.generated.resources.open_sans_regular
import lavanderiaelcobre.shared.generated.resources.open_sans_semibold
import org.jetbrains.compose.resources.Font

// Tipografía de DESIGN.md: display = Inter, cuerpo = Open Sans, etiquetas = Inconsolata.
// Solo se incluyen los pesos que usa la escala (licencias OFL en docs/licenses/).

@Composable
private fun interFamily() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

@Composable
private fun bodyFamily() = FontFamily(
    Font(Res.font.open_sans_regular, FontWeight.Normal),
    Font(Res.font.open_sans_semibold, FontWeight.SemiBold),
)

@Composable
private fun monoFamily() = FontFamily(
    Font(Res.font.inconsolata_medium, FontWeight.Medium),
    Font(Res.font.inconsolata_bold, FontWeight.Bold),
)

/** Estilos fuera de la escala de Material 3. */
@Immutable
data class CobreTextStyles(
    /** Título grande de pantalla estilo iOS, que se colapsa al hacer scroll. */
    val largeTitle: TextStyle,
    /** Etiquetas cortas (encabezados de sección, códigos, metadatos): Inter seminegrita, sin mayúsculas forzadas ni tracking. */
    val label: TextStyle,
    /** Cifras y códigos que deben alinearse (monoespaciado). */
    val mono: TextStyle,
)

internal val LocalCobreTextStyles = staticCompositionLocalOf {
    CobreTextStyles(largeTitle = TextStyle.Default, label = TextStyle.Default, mono = TextStyle.Default)
}

private fun estilo(familia: FontFamily, peso: FontWeight, tamano: Float, alto: Float, tracking: Float = 0f) =
    TextStyle(fontFamily = familia, fontWeight = peso, fontSize = tamano.sp, lineHeight = alto.sp, letterSpacing = tracking.sp)

/** Tracking de títulos: -0.02em, igual que el CSS de la web. */
private fun tight(tamano: Float) = -0.02f * tamano

/**
 * Escala tipográfica. DESIGN.md parte de una escala de escritorio (h1 = 48px); en
 * teléfono [compact] la reduce para que un título no ocupe media pantalla.
 */
@Composable
fun rememberCobreTypography(compact: Boolean): Typography {
    val display = interFamily()
    val body = bodyFamily()
    val h1 = if (compact) 34f else 48f
    val h2 = if (compact) 28f else 36f
    val h3 = if (compact) 24f else 28f

    return Typography(
        displayLarge = estilo(display, FontWeight.Bold, h1, h1 * 1.15f, tight(h1)),
        displayMedium = estilo(display, FontWeight.Bold, h2, h2 * 1.18f, tight(h2)),
        displaySmall = estilo(display, FontWeight.Bold, h3, h3 * 1.2f, tight(h3)),
        headlineLarge = estilo(display, FontWeight.Bold, h3, h3 * 1.2f, tight(h3)),
        headlineMedium = estilo(display, FontWeight.Bold, 22f, 28f, tight(22f)),
        headlineSmall = estilo(display, FontWeight.SemiBold, 20f, 26f, tight(20f)),
        titleLarge = estilo(display, FontWeight.SemiBold, 18f, 24f, tight(18f)),
        titleMedium = estilo(display, FontWeight.SemiBold, 16f, 22f, tight(16f)),
        titleSmall = estilo(display, FontWeight.SemiBold, 14f, 20f, tight(14f)),
        bodyLarge = estilo(body, FontWeight.Normal, 16f, 24f),
        bodyMedium = estilo(body, FontWeight.Normal, 14f, 20f),
        bodySmall = estilo(body, FontWeight.Normal, 12f, 16f),
        labelLarge = estilo(display, FontWeight.SemiBold, 15f, 20f),
        labelMedium = estilo(display, FontWeight.SemiBold, 13f, 18f),
        labelSmall = estilo(display, FontWeight.Medium, 11f, 14f, 0.1f),
    )
}

@Composable
internal fun rememberCobreTextStyles(compact: Boolean): CobreTextStyles {
    val display = interFamily()
    val mono = monoFamily()
    val large = if (compact) 34f else 40f
    return CobreTextStyles(
        largeTitle = estilo(display, FontWeight.Bold, large, large * 1.15f, tight(large)),
        label = estilo(display, FontWeight.SemiBold, 13f, 18f),
        mono = estilo(mono, FontWeight.Medium, 14f, 20f),
    )
}
