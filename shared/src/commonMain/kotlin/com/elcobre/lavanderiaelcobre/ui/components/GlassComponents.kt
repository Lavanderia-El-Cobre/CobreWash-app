package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreSpacing
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Escala sutil al presionar; usa el mismo InteractionSource que el clickable del elemento. */
@Composable
fun Modifier.pressableScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "pressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Fondo de la app: el `background` del tema con dos manchas de color muy tenues
 * (cobre arriba, índigo abajo) para que el vidrio de la navegación tenga algo que
 * refractar sin romper el minimalismo. Se monta una sola vez en la raíz
 * ([com.elcobre.lavanderiaelcobre.ui.CobreApp]), detrás de la navegación, para no
 * recrearlo ni deslizarlo en cada cambio de pantalla.
 *
 * Las manchas son degradados radiales (ya difusos): sin `Modifier.blur`, que era
 * costoso y en Android < 12 no hacía nada.
 */
@Composable
fun CobreBackdrop(modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    val alphaCobre = if (colors.isDark) 0.10f else 0.14f
    val alphaIndigo = if (colors.isDark) 0.10f else 0.08f
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Box(
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 160f; translationY = -180f }
                .background(Brush.radialGradient(listOf(colors.accent.copy(alpha = alphaCobre), Color.Transparent))),
        )
        Box(
            modifier = Modifier
                .size(460.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -180f; translationY = 200f }
                .background(Brush.radialGradient(listOf(colors.info.copy(alpha = alphaIndigo), Color.Transparent))),
        )
    }
}

/**
 * Contenedor de pantalla: aplica el inset de `systemBars` (si [applySystemBarsInsets])
 * y el espacio de la navegación flotante ([LocalShellPadding]) para que nada quede
 * tapado por las barras del sistema ni por el vidrio.
 */
@Composable
fun CobreBackground(
    modifier: Modifier = Modifier,
    applySystemBarsInsets: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (applySystemBarsInsets) Modifier.windowInsetsPadding(WindowInsets.systemBars) else Modifier)
            .padding(LocalShellPadding.current),
    ) {
        content()
    }
}

/**
 * Tarjeta de contenido. Pese al nombre ya no es vidrio: con Liquid Glass el vidrio
 * queda para la capa de navegación y el contenido usa superficies sólidas con borde
 * fino (como [ContentCard]). [elevation] y [tint] se conservan por compatibilidad con
 * las llamadas existentes: la tarjeta minimal no lleva sombra teñida.
 */
@Suppress("UnusedParameter")
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CobreShapes.lg,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    elevation: Dp = 0.dp,
    tint: Color = Color.Unspecified,
    stripe: Color? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.superficieContenido(shape, stripe).padding(contentPadding)) {
        content()
    }
}

/**
 * Tarjeta de contenido: superficie sólida, borde fino, sin sombra. Con [onClick] se
 * hunde levemente al presionarla. [stripe] dibuja una franja de color a la izquierda
 * (estado o prioridad).
 */
@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(CobreSpacing.s16),
    stripe: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    // Las tarjetas son bloques: ocupan todo el ancho que les da su contenedor.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.pressableScale(interaction, pressedScale = 0.98f) else Modifier)
            .superficieContenido(CobreShapes.lg, stripe)
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(contentPadding),
        content = content,
    )
}

@Composable
private fun Modifier.superficieContenido(shape: Shape, stripe: Color?): Modifier {
    val colors = CobreTheme.colors
    return this
        .clip(shape)
        .background(colors.surface)
        .then(
            if (stripe != null) {
                Modifier.drawBehind { drawRect(color = stripe, size = Size(4.dp.toPx(), size.height)) }
            } else {
                Modifier
            },
        )
        .border(width = if (colors.isDark) 1.dp else 0.5.dp, color = colors.hairline, shape = shape)
}

/** Encabezado de sección de las listas agrupadas, en texto normal (sin mayúsculas forzadas). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = CobreTheme.text.label,
        color = CobreTheme.colors.textSecondary,
        modifier = modifier.semantics { heading() },
    )
}

/** Colores unificados para los campos de texto (login, búsqueda, comentario). */
@Composable
fun cobreFieldColors(): TextFieldColors {
    val colors = CobreTheme.colors
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.accent,
        unfocusedBorderColor = colors.fieldBorder,
        focusedLabelColor = colors.accentText,
        unfocusedLabelColor = colors.textSecondary,
        cursorColor = colors.accent,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface,
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedPlaceholderColor = colors.textSecondary,
        unfocusedPlaceholderColor = colors.textSecondary,
        focusedLeadingIconColor = colors.textSecondary,
        unfocusedLeadingIconColor = colors.textSecondary,
        focusedTrailingIconColor = colors.textSecondary,
        unfocusedTrailingIconColor = colors.textSecondary,
        errorBorderColor = colors.danger,
        errorLabelColor = colors.dangerText,
        errorCursorColor = colors.danger,
        errorSupportingTextColor = colors.dangerText,
        errorContainerColor = colors.surface,
    )
}

/** Marca un elemento como compartido entre pantallas para la transición animada; no-op si no hay scopes disponibles. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedTag(
    key: String,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
): Modifier = if (sharedScope != null && animatedScope != null) {
    with(sharedScope) {
        this@sharedTag.sharedElement(
            rememberSharedContentState(key),
            animatedVisibilityScope = animatedScope,
        )
    }
} else {
    this
}

/** Barra de progreso segmentada del [flujo]: segmentos hasta la etapa actual en [accent], el resto atenuados. */
@Composable
fun StageProgress(
    flujo: List<EtapaProceso>,
    etapaActual: EtapaProceso,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val isDark = cobreIsDark()
    val vacio = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val indiceActual = flujo.indexOf(etapaActual)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        flujo.forEachIndexed { indice, _ ->
            val objetivo = if (indice <= indiceActual) accent else vacio
            val color by animateColorAsState(objetivo, tween(500), label = "segmento")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
    }
}

/** Timeline vertical del proceso productivo, compartido entre DetalleScreen y el Dashboard. */
@Composable
fun EtapasTimeline(flujo: List<EtapaProceso>, indiceActual: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        flujo.forEachIndexed { index, etapa ->
            EtapaTimelineRow(
                etapa = etapa,
                completada = index < indiceActual,
                activa = index == indiceActual,
                esUltimo = index == flujo.size - 1,
            )
        }
    }
}

/** Color del nodo por estado (no por tipo de etapa), igual que el timeline de la web: completada = verde, activa = marca. */
@Composable
private fun etapaEstadoAccent(completada: Boolean, activa: Boolean): Color? {
    val isDark = cobreIsDark()
    return when {
        completada -> if (isDark) StatusGreenDark else StatusGreen
        activa -> CobreTheme.colors.info
        else -> null
    }
}

private fun etapaEstadoLabel(completada: Boolean, activa: Boolean): String = when {
    completada -> "Completada"
    activa -> "En curso"
    else -> "Pendiente"
}

@Composable
private fun EtapaTimelineRow(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
) {
    val isDark = cobreIsDark()
    val estadoAccent = etapaEstadoAccent(completada, activa)
    val activo = completada || activa
    val inactiveCircle = CobreTheme.colors.surfaceMuted
    val inactiveContent = CobreTheme.colors.textSecondary
    val lineTrack = CobreTheme.colors.hairline

    Row(modifier = Modifier.fillMaxWidth()) {
        EtapaNodo(
            etapa = etapa,
            completada = completada,
            activa = activa,
            esUltimo = esUltimo,
            activo = activo,
            estadoAccent = estadoAccent,
            inactiveCircle = inactiveCircle,
            inactiveContent = inactiveContent,
            lineTrack = lineTrack,
        )
        Spacer(Modifier.width(14.dp))
        EtapaTexto(
            etapa = etapa,
            completada = completada,
            activa = activa,
            esUltimo = esUltimo,
            activo = activo,
            estadoAccent = estadoAccent,
            inactiveContent = inactiveContent,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun EtapaNodo(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
    activo: Boolean,
    estadoAccent: Color?,
    inactiveCircle: Color,
    inactiveContent: Color,
    lineTrack: Color,
) {
    val isDark = cobreIsDark()
    val circleColor by animateColorAsState(estadoAccent ?: inactiveCircle, tween(500), label = "nodeColor")
    val iconTint by animateColorAsState(if (activo) Color.White else inactiveContent, tween(500), label = "iconTint")
    val lineColor by animateColorAsState(
        targetValue = if (completada) (if (isDark) StatusGreenDark else StatusGreen) else lineTrack,
        animationSpec = tween(500),
        label = "lineColor",
    )
    val scale by animateFloatAsState(if (activa) 1.1f else 1f, tween(400), label = "nodeScale")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (activa) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background((estadoAccent ?: inactiveCircle).copy(alpha = 0.22f)),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(RoundedCornerShape(50))
                    .background(circleColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (completada) CobreIcons.Check else etapaIcon(etapa),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        if (!esUltimo) {
            Box(modifier = Modifier.width(2.dp).height(36.dp).background(lineColor))
        }
    }
}

@Composable
private fun EtapaTexto(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
    activo: Boolean,
    estadoAccent: Color?,
    inactiveContent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 6.dp, bottom = if (esUltimo) 0.dp else 20.dp),
    ) {
        Text(
            etapa.displayName,
            style = MaterialTheme.typography.titleSmall,
            color = if (activo) brandHeadingColor() else inactiveContent,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            etapaEstadoLabel(completada, activa),
            style = MaterialTheme.typography.labelSmall,
            color = estadoAccent ?: brandMutedColor(),
            fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

/** Chip/badge compacto con icono opcional, usado para etapa y prioridad. */
@Composable
fun StatusChip(
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    // Etiqueta (no control): radio `sm` de DESIGN.md, sin borde.
    Row(
        modifier = modifier
            .clip(CobreShapes.sm)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Chip de filtro/selección (no de estado): radio `md` de DESIGN.md. Seleccionado va en
 * negro (Ink), no en cobre, para no competir con la acción principal de la pantalla.
 */
@Composable
fun SelectableChip(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val colors = CobreTheme.colors
    val fondo = if (seleccionado) colors.primaryFill else colors.surface
    val texto = if (seleccionado) colors.onPrimaryFill else colors.textSecondary
    val borde = if (seleccionado) Color.Transparent else colors.hairline
    Row(
        modifier = Modifier
            .pressableScale(interaction)
            .clip(CobreShapes.md)
            .background(fondo)
            .border(1.dp, borde, CobreShapes.md)
            .selectable(selected = seleccionado, interactionSource = interaction, indication = null, role = Role.Tab) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = texto, fontWeight = FontWeight.SemiBold)
    }
}
