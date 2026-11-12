package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// ColorScheme de Material 3 derivado de DESIGN.md, para que los componentes M3 que no
// leen CobreTheme.colors (Button, TextField, AlertDialog...) queden alineados.
// primary = CTA cobre; secondary = índigo; tertiary = Ink (botón negro).
private val CobreLightColors = lightColorScheme(
    primary = Copper600,
    onPrimary = Color.White,
    primaryContainer = Brand100,
    onPrimaryContainer = Brand900,

    secondary = Indigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4E3F3),
    onSecondaryContainer = Color(0xFF1E1A5C),

    tertiary = Ink,
    onTertiary = Paper,
    tertiaryContainer = PaperDeep,
    onTertiaryContainer = Ink,

    background = Paper,
    onBackground = Ink,

    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkSoft,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAFAF8),
    surfaceContainer = Paper,
    surfaceContainerHigh = PaperDeep,
    surfaceContainerHighest = Color(0xFFE3E3DE),

    outline = InkFaint,
    outlineVariant = PaperLine,

    error = DangerText,
    onError = Color.White,
    errorContainer = Color(0xFFFDECEC),
    onErrorContainer = Color(0xFF7F1D1D),
)

private val CobreDarkColors = darkColorScheme(
    primary = Brand500,
    onPrimary = Ink,
    primaryContainer = Brand900,
    onPrimaryContainer = Brand100,

    secondary = IndigoLight,
    onSecondary = Color(0xFF15123F),
    secondaryContainer = Color(0xFF2A2760),
    onSecondaryContainer = Color(0xFFE4E3F3),

    tertiary = Paper,
    onTertiary = Ink,
    tertiaryContainer = Graphite3,
    onTertiaryContainer = Paper,

    background = Ink,
    onBackground = Paper,

    surface = Graphite2,
    onSurface = Paper,
    surfaceVariant = Graphite3,
    onSurfaceVariant = PaperSoft,
    surfaceContainerLowest = Ink,
    surfaceContainerLow = Graphite1,
    surfaceContainer = Graphite2,
    surfaceContainerHigh = Graphite3,
    surfaceContainerHighest = Graphite4,

    outline = GraphiteFaint,
    outlineVariant = Graphite4,

    error = DangerLight,
    onError = Color(0xFF2A0707),
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFD9D6),
)

/** Preferencia de apariencia elegida en Configuración; espeja el picker Claro/Oscuro/Sistema de la web. */
enum class ModoTema(val etiqueta: String, val descripcion: String) {
    CLARO("Claro", "Fondo claro en toda la app"),
    OSCURO("Oscuro", "Fondo oscuro en toda la app"),
    SISTEMA("Sistema", "Sigue la preferencia de tu dispositivo"),
}

// Tema oscuro *resuelto* (tras aplicar la preferencia de Configuración), no el del
// sistema operativo. El resto de la UI debe leer [cobreIsDark], no `isSystemInDarkTheme()`
// directamente, o el picker Claro/Oscuro/Sistema queda sin efecto fuera de MaterialTheme.
private val LocalCobreDarkTheme = compositionLocalOf { false }

/** Punto único de verdad para "¿está en oscuro?" dentro de la app. */
@Composable
fun cobreIsDark(): Boolean = LocalCobreDarkTheme.current

/**
 * Tema de la app. Además del MaterialTheme publica los tokens propios ([CobreTheme.colors],
 * [CobreTheme.window], [CobreTheme.text], etc.) y mide la ventana una sola vez.
 *
 * @param reduceTransparency accesibilidad: reemplaza el vidrio desenfocado por superficies casi opacas.
 */
@Composable
fun CobreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceTransparency: Boolean = false,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val window = remember(maxWidth, maxHeight) { CobreWindow(width = maxWidth, height = maxHeight) }
        val compact = window.widthClass == WidthClass.Compact
        CompositionLocalProvider(
            LocalCobreDarkTheme provides darkTheme,
            LocalCobreColors provides if (darkTheme) CobreDarkRoles else CobreLightRoles,
            LocalCobreWindow provides window,
            LocalReduceTransparency provides reduceTransparency,
            LocalCobreTextStyles provides rememberCobreTextStyles(compact),
        ) {
            MaterialTheme(
                colorScheme = if (darkTheme) CobreDarkColors else CobreLightColors,
                typography = rememberCobreTypography(compact),
                shapes = CobreMaterialShapes,
                content = content,
            )
        }
    }
}

/** Acceso a los tokens del tema, al estilo de `MaterialTheme.colorScheme`. */
object CobreTheme {
    val colors: CobreColors
        @Composable @ReadOnlyComposable get() = LocalCobreColors.current

    val window: CobreWindow
        @Composable @ReadOnlyComposable get() = LocalCobreWindow.current

    val text: CobreTextStyles
        @Composable @ReadOnlyComposable get() = LocalCobreTextStyles.current

    val spacing: CobreSpacing get() = CobreSpacing

    val shapes: CobreShapes get() = CobreShapes

    /** `true` si el usuario pidió reducir transparencia: el vidrio debe usar su versión opaca. */
    val reduceTransparency: Boolean
        @Composable @ReadOnlyComposable get() = LocalReduceTransparency.current

    @Composable
    @ReadOnlyComposable
    fun glass(level: GlassLevel = GlassLevel.Regular): GlassMaterial = glassMaterial(level, LocalCobreColors.current.isDark)
}
