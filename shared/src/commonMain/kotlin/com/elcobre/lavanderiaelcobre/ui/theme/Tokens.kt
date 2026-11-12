package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Roles de color semánticos, ya resueltos para el tema activo. Es lo único que las
 * pantallas deberían leer (`CobreTheme.colors.x`), en vez de `if (isDark) A else B`.
 *
 * Reparto de acentos:
 * - **accent (cobre)**: identidad y acciones importantes — CTA, QR, pestaña activa, foco.
 * - **info (índigo)**: estado del proceso — etapa en curso, cambios de etapa, información.
 * - **primary (Ink)**: texto y acciones secundarias contundentes (botón negro).
 */
@Immutable
data class CobreColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val hairline: Color,
    val fieldBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primaryFill: Color,
    val onPrimaryFill: Color,
    /** Cobre para íconos e indicadores (no-texto, ≥3:1). */
    val accent: Color,
    /** Cobre legible como texto (AA). */
    val accentText: Color,
    /** Relleno del CTA principal. */
    val accentFill: Color,
    val onAccentFill: Color,
    val accentContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val success: Color,
    val successText: Color,
    val warning: Color,
    val warningText: Color,
    val danger: Color,
    val dangerText: Color,
)

internal val CobreLightRoles = CobreColors(
    isDark = false,
    background = Paper,
    surface = Color.White,
    surfaceMuted = PaperDeep,
    hairline = PaperLine,
    fieldBorder = InkFaint,
    textPrimary = Ink,
    textSecondary = InkSoft,
    primaryFill = Ink,
    onPrimaryFill = Paper,
    accent = Brand600,
    accentText = Copper700,
    accentFill = Copper600,
    onAccentFill = Color.White,
    accentContainer = Brand600.copy(alpha = 0.12f),
    info = Indigo,
    infoContainer = Indigo.copy(alpha = 0.10f),
    success = Success,
    successText = SuccessText,
    warning = Warning,
    warningText = WarningText,
    danger = Danger,
    dangerText = DangerText,
)

internal val CobreDarkRoles = CobreColors(
    isDark = true,
    background = Ink,
    surface = Graphite2,
    surfaceMuted = Graphite3,
    hairline = Graphite4,
    fieldBorder = GraphiteFaint,
    textPrimary = Paper,
    textSecondary = PaperSoft,
    primaryFill = Paper,
    onPrimaryFill = Ink,
    accent = Brand400,
    accentText = Brand400,
    accentFill = Brand500,
    onAccentFill = Ink,
    accentContainer = Brand400.copy(alpha = 0.16f),
    info = IndigoLight,
    infoContainer = IndigoLight.copy(alpha = 0.16f),
    success = SuccessLight,
    successText = SuccessLight,
    warning = WarningLight,
    warningText = WarningLight,
    danger = DangerLight,
    dangerText = DangerLight,
)

/** Escala de espaciado de DESIGN.md (4/8/12/16/24/32), nombrada por múltiplos de 4dp. */
object CobreSpacing {
    val s4: Dp = 4.dp
    val s8: Dp = 8.dp
    val s12: Dp = 12.dp
    val s16: Dp = 16.dp
    val s24: Dp = 24.dp
    val s32: Dp = 32.dp

    /** Margen lateral de pantalla. */
    val gutter: Dp = s16

    /** Margen lateral de pantalla en tablet. */
    val gutterWide: Dp = s32
}

/**
 * Radios: `sm`/`md` son los de DESIGN.md (4/8) para etiquetas, campos y chips; `lg`
 * (16) para tarjetas y hojas y `full` (cápsula) para la capa de vidrio — barra de
 * pestañas, QR, botones flotantes — siguiendo las esquinas concéntricas de Liquid Glass.
 */
object CobreShapes {
    val sm = RoundedCornerShape(4.dp)
    val md = RoundedCornerShape(8.dp)
    val lg = RoundedCornerShape(16.dp)
    val full = RoundedCornerShape(percent = 50)
}

/** Formas de Material 3 alineadas a la escala, para que los componentes M3 (diálogos, menús) no usen sus 28dp por defecto. */
internal val CobreMaterialShapes = Shapes(
    extraSmall = CobreShapes.sm,
    small = CobreShapes.md,
    medium = CobreShapes.lg,
    large = CobreShapes.lg,
    extraLarge = CobreShapes.lg,
)

/** Grosor del vidrio: cuanto más grueso, más opaco y más desenfoque (y más legible el texto encima). */
enum class GlassLevel { Thin, Regular, Thick }

/**
 * Material de vidrio resuelto para el tema. [tint] se aplica sobre el fondo desenfocado;
 * [fallback] reemplaza todo el efecto cuando no hay blur (Android < 12, o "Reducir
 * transparencia"), por eso es casi opaco.
 */
@Immutable
data class GlassMaterial(
    val blur: Dp,
    val tint: Color,
    val fallback: Color,
    val strokeTop: Color,
    val strokeBottom: Color,
)

internal fun glassMaterial(level: GlassLevel, dark: Boolean): GlassMaterial {
    val base = if (dark) Graphite2 else Color.White
    val (blur, tintAlpha) = when (level) {
        GlassLevel.Thin -> 16.dp to if (dark) 0.40f else 0.45f
        GlassLevel.Regular -> 24.dp to if (dark) 0.55f else 0.62f
        GlassLevel.Thick -> 32.dp to if (dark) 0.72f else 0.80f
    }
    return GlassMaterial(
        blur = blur,
        tint = base.copy(alpha = tintAlpha),
        fallback = base.copy(alpha = if (dark) 0.94f else 0.92f),
        strokeTop = Color.White.copy(alpha = if (dark) 0.14f else 0.75f),
        strokeBottom = Color.White.copy(alpha = if (dark) 0.04f else 0.20f),
    )
}

internal val LocalCobreColors = staticCompositionLocalOf { CobreLightRoles }
internal val LocalReduceTransparency = staticCompositionLocalOf { false }
