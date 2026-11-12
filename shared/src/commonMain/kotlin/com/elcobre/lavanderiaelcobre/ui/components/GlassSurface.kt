package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.GlassLevel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/**
 * Fondo que las superficies de vidrio desenfocan. Lo provee el contenedor de la app y
 * lo alimenta el contenido con [glassBackdropSource]; `null` (previews, tests) hace que
 * [GlassSurface] use su versión opaca.
 */
val LocalGlassBackdrop = staticCompositionLocalOf<HazeState?> { null }

/**
 * Fondo de la raíz (las manchas de color de [CobreBackdrop]). No contiene ninguna
 * pantalla, así que cualquier vidrio puede desenfocarlo sin desenfocarse a sí mismo.
 */
val LocalRootGlassBackdrop = staticCompositionLocalOf<HazeState?> { null }

/** Crea el estado del fondo desenfocable; se recuerda una vez en la raíz de la app. */
@Composable
fun rememberGlassBackdrop(): HazeState = rememberHazeState()

/**
 * Marca este nodo como contenido que el vidrio desenfoca (lo que "pasa por debajo"
 * de la barra de pestañas, las cabeceras, etc.). Las [GlassSurface] deben quedar fuera
 * de este nodo — como hermanas, no como hijas — o se dibujarían dentro de su propio fondo.
 */
fun Modifier.glassBackdropSource(state: HazeState?): Modifier =
    if (state == null) this else this.hazeSource(state)

/**
 * Apila una capa de vidrio sobre un contenido que desenfoca, con la relación correcta
 * entre ambos: [background] recibe el modifier que lo marca como fuente del desenfoque
 * y [overlay] (barras, botones flotantes) se dibuja como hermano, con su propio
 * [LocalGlassBackdrop]. Cada pantalla o contenedor que tenga vidrio usa uno.
 */
@Composable
fun GlassStack(
    modifier: Modifier = Modifier,
    background: @Composable (sourceModifier: Modifier) -> Unit,
    overlay: @Composable BoxScope.() -> Unit,
) {
    val state = rememberGlassBackdrop()
    Box(modifier = modifier) {
        // El vidrio dentro del contenido (búsqueda, segmentados) no puede desenfocar el
        // propio contenido que lo contiene; desenfoca el fondo de la raíz. El desenfoque
        // del contenido queda para la capa [overlay], que es la de navegación.
        CompositionLocalProvider(LocalGlassBackdrop provides LocalRootGlassBackdrop.current) {
            background(Modifier.fillMaxSize().glassBackdropSource(state))
        }
        CompositionLocalProvider(LocalGlassBackdrop provides state) {
            overlay()
        }
    }
}

/**
 * Superficie Liquid Glass: desenfoca lo que tiene detrás, aplica un tinte del material,
 * un borde fino con brillo arriba y, si flota, una sombra muy suave.
 *
 * Es para la **capa de navegación y controles** (barras, botones flotantes, búsqueda
 * fija, hojas). El contenido (tarjetas de pedidos, listas) usa superficies sólidas.
 *
 * Sin blur disponible (Android < 12), sin [LocalGlassBackdrop] o con "Reducir
 * transparencia", dibuja la versión casi opaca del material.
 *
 * @param tint color de vidrio teñido (p. ej. `CobreTheme.colors.accentFill` para el QR); `null` = vidrio neutro.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = CobreShapes.lg,
    level: GlassLevel = GlassLevel.Regular,
    tint: Color? = null,
    floating: Boolean = false,
    bordered: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    val colors = CobreTheme.colors
    val material = CobreTheme.glass(level)
    val backdrop = LocalGlassBackdrop.current
    val usarBlur = backdrop != null && !CobreTheme.reduceTransparency

    val tintColor = tint?.copy(alpha = 0.78f) ?: material.tint
    val fallbackColor = tint ?: material.fallback
    val contentColor = if (tint != null) colors.onAccentFill else colors.textPrimary
    val stroke = Brush.verticalGradient(listOf(material.strokeTop, material.strokeBottom))

    Box(
        modifier = modifier
            .then(
                if (floating) {
                    Modifier.shadow(
                        elevation = 16.dp,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = if (colors.isDark) 0.5f else 0.12f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .then(
                if (usarBlur) {
                    Modifier.hazeEffect(
                        state = backdrop,
                        style = HazeStyle(
                            backgroundColor = colors.background,
                            tint = HazeTint(tintColor),
                            blurRadius = material.blur,
                            noiseFactor = 0f,
                            fallbackTint = HazeTint(fallbackColor),
                        ),
                    )
                } else {
                    Modifier.background(fallbackColor)
                },
            )
            .then(
                if (bordered) Modifier.border(width = if (colors.isDark) 1.dp else 0.5.dp, brush = stroke, shape = shape) else Modifier,
            )
            .padding(contentPadding),
        contentAlignment = contentAlignment,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}
