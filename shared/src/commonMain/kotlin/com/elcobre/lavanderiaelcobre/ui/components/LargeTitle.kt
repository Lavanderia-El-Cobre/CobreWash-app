package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.GlassLevel

/** Desplazamiento a partir del cual el título grande se considera fuera de vista. */
private val UmbralColapso = 40.dp

/**
 * Título grande de pantalla estilo iOS. Va como primer ítem del contenido con scroll;
 * al salir de vista aparece [CompactTitleBar]. En teléfono horizontal conviene no
 * usarlo (`CobreTheme.window.isPhoneLandscape`) y mostrar directamente la barra.
 */
@Composable
fun LargeTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = CobreTheme.colors
    Row(
        // Aire bajo la barra de estado: con 8 dp el título quedaba pegado a la hora y los íconos.
        modifier = modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = CobreTheme.text.largeTitle,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }
        }
        if (trailing != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                trailing()
            }
        }
    }
}

/** Limita un bloque al ancho de lectura de la ventana (`CobreTheme.window.contentMaxWidth`) para que no se estire en tablet. */
@Composable
fun Modifier.readableWidth(): Modifier = this.widthIn(max = CobreTheme.window.contentMaxWidth).fillMaxWidth()

/**
 * Pantalla de pestaña con título grande: lista de borde a borde que pasa por debajo de
 * la navegación de vidrio, título que se colapsa en una [CompactTitleBar] y contenido
 * centrado con ancho de lectura. Cada ítem de [content] debería usar [readableWidth].
 */
@Composable
fun LargeTitleScreen(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    applySystemBarsInsets: Boolean = true,
    content: LazyListScope.() -> Unit,
) {
    val window = CobreTheme.window
    val listState = rememberLazyListState()
    val colapsado by rememberTitleCollapsed(listState)
    GlassStack(
        modifier = modifier.fillMaxSize(),
        background = { fuente ->
            LazyColumn(
                modifier = fuente,
                state = listState,
                contentPadding = rememberShellContentPadding(
                    PaddingValues(start = window.gutter, end = window.gutter, top = 8.dp, bottom = 24.dp),
                    incluirSistema = applySystemBarsInsets,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item(key = "titulo") {
                    LargeTitle(title = title, subtitle = subtitle, trailing = trailing, modifier = Modifier.readableWidth())
                }
                content()
            }
        },
        overlay = {
            CompactTitleBar(title = title, visible = colapsado, modifier = Modifier.align(Alignment.TopCenter))
        },
    )
}

/** `true` cuando el primer ítem (el [LargeTitle]) ya salió de vista. */
@Composable
fun rememberTitleCollapsed(listState: LazyListState): State<Boolean> {
    val umbral = with(LocalDensity.current) { UmbralColapso.roundToPx() }
    return remember(listState, umbral) {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > umbral }
    }
}

/** Variante para cuadrículas escalonadas (Dashboard en tablet y horizontal). */
@Composable
fun rememberTitleCollapsed(gridState: LazyStaggeredGridState): State<Boolean> {
    val umbral = with(LocalDensity.current) { UmbralColapso.roundToPx() }
    return remember(gridState, umbral) {
        derivedStateOf { gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > umbral }
    }
}

/** Variante para pantallas con `verticalScroll`. */
@Composable
fun rememberTitleCollapsed(scrollState: ScrollState): State<Boolean> {
    val umbral = with(LocalDensity.current) { UmbralColapso.roundToPx() }
    return remember(scrollState, umbral) { derivedStateOf { scrollState.value > umbral } }
}

/**
 * Barra superior de vidrio con el título en pequeño. El fondo y el título aparecen
 * con [visible] (cuando el título grande se colapsa); la navegación y las acciones
 * flotan siempre. Reserva su propio inset de status bar, así que la pantalla debe
 * dibujarse de borde a borde debajo de ella dentro de un [GlassStack].
 */
@Composable
fun CompactTitleBar(
    title: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    navigation: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = CobreTheme.colors
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(180), label = "barraTitulo")
    Box(modifier = modifier.fillMaxWidth()) {
        GlassSurface(
            modifier = Modifier.matchParentSize().graphicsLayer { this.alpha = alpha },
            shape = RoundedCornerShape(0.dp),
            level = GlassLevel.Regular,
            bordered = false,
        ) {}
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(56.dp)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 64.dp)
                    .graphicsLayer { this.alpha = alpha },
            )
            if (navigation != null) {
                Box(modifier = Modifier.align(Alignment.CenterStart)) { navigation() }
            }
            if (actions != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions()
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(0.5.dp)
                .graphicsLayer { this.alpha = alpha }
                .background(colors.hairline),
        )
    }
}
