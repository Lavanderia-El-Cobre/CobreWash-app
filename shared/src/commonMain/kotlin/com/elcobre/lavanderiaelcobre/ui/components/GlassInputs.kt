package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.GlassLevel

/**
 * Control segmentado de vidrio (filtros, selector de tema, severidad): segmentos de
 * igual ancho y una cápsula sólida que se desliza con spring hasta la opción elegida.
 */
@Composable
fun <T> GlassSegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    val indice = options.indexOf(selected).coerceAtLeast(0)
    val posicion by animateFloatAsState(
        targetValue = indice.toFloat(),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "segmento",
    )
    GlassSurface(
        modifier = modifier.fillMaxWidth().height(44.dp),
        shape = CobreShapes.full,
        level = GlassLevel.Thin,
        contentPadding = PaddingValues(4.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val segmento = maxWidth / options.size.coerceAtLeast(1)
            Box(
                modifier = Modifier
                    .offset(x = segmento * posicion)
                    .width(segmento)
                    .fillMaxHeight()
                    .shadow(
                        elevation = 3.dp,
                        shape = CobreShapes.full,
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = 0.10f),
                    )
                    .clip(CobreShapes.full)
                    .background(if (colors.isDark) colors.surfaceMuted else colors.surface),
            )
            Row(modifier = Modifier.fillMaxSize()) {
                options.forEachIndexed { i, opcion ->
                    val activo = i == indice
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CobreShapes.full)
                            .selectable(
                                selected = activo,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Tab,
                                onClick = { onSelect(opcion) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            label(opcion),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (activo) colors.textPrimary else colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/** Búsqueda en cápsula de vidrio, para fijarla sobre una lista que pasa por debajo. */
@Composable
fun GlassSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    val estiloTexto = MaterialTheme.typography.bodyMedium
    GlassSurface(
        modifier = modifier.fillMaxWidth().height(44.dp),
        shape = CobreShapes.full,
        level = GlassLevel.Regular,
        contentPadding = PaddingValues(start = 14.dp, end = 6.dp),
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Icon(CobreIcons.Search, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(placeholder, style = estiloTexto, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = estiloTexto.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.accent),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CobreShapes.full)
                        .clickable(role = Role.Button) { onQueryChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        CobreIcons.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/** Forma de los campos de formulario: un poco más amplia que la de las tarjetas, como iOS. */
private val FormaCampo = RoundedCornerShape(12.dp)

/**
 * Campo de formulario de la app: etiqueta arriba y caja rellena de esquinas suaves que se
 * enmarca en cobre al enfocarse (o en rojo con [isError]). Reemplaza al campo con contorno
 * y etiqueta flotante de Material en login, formularios de flota, conteos y comentarios.
 */
@Composable
fun CobreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = CobreTheme.colors
    val interaccion = remember { MutableInteractionSource() }
    val enfocado by interaccion.collectIsFocusedAsState()
    val borde by animateColorAsState(
        when {
            isError -> colors.danger
            enfocado -> colors.accent
            else -> colors.hairline
        },
        tween(160),
        label = "bordeCampo",
    )
    val grosor by animateDpAsState(if (enfocado || isError) 1.5.dp else 1.dp, tween(160), label = "grosorCampo")
    val estilo = MaterialTheme.typography.bodyLarge.copy(color = if (enabled) colors.textPrimary else colors.textSecondary)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) {
            Text(
                label,
                style = CobreTheme.text.label,
                color = if (isError) colors.dangerText else colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            textStyle = estilo,
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interaccion,
            modifier = Modifier.fillMaxWidth().semantics { if (label != null) contentDescription = label },
            decorationBox = { campo ->
                CajaCampo(
                    singleLine = singleLine,
                    enfocado = enfocado,
                    borde = borde,
                    grosor = grosor,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(placeholder, style = estilo, color = colors.textSecondary.copy(alpha = 0.7f), maxLines = 1)
                    }
                    campo()
                }
            },
        )
        if (supportingText != null) {
            Text(
                supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) colors.dangerText else colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun CajaCampo(
    singleLine: Boolean,
    enfocado: Boolean,
    borde: Color,
    grosor: Dp,
    leadingIcon: ImageVector?,
    trailingIcon: (@Composable () -> Unit)?,
    campo: @Composable () -> Unit,
) {
    val colors = CobreTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(FormaCampo)
            .background(colors.surfaceMuted)
            .border(grosor, borde, FormaCampo)
            .padding(start = 14.dp, end = if (trailingIcon != null) 4.dp else 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
    ) {
        if (leadingIcon != null) {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = if (enfocado) colors.accentText else colors.textSecondary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) { campo() }
        if (trailingIcon != null) trailingIcon()
    }
}

/**
 * Casilla de la app: cuadrado de esquinas suaves que se rellena en cobre con un check
 * que aparece con un leve rebote. Toda la fila (casilla y texto) es el área táctil.
 */
@Composable
fun CobreCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = CobreTheme.colors
    val forma = RoundedCornerShape(7.dp)
    val fondo by animateColorAsState(if (checked) colors.accentFill else colors.surfaceMuted, tween(160), label = "fondoCheck")
    val borde by animateColorAsState(
        if (checked) colors.accentFill else colors.textSecondary.copy(alpha = 0.45f),
        tween(160),
        label = "bordeCheck",
    )
    val escala by animateFloatAsState(
        if (checked) 1f else 0.4f,
        spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "escalaCheck",
    )
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onValueChange = onCheckedChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(forma).background(fondo).border(1.5.dp, borde, forma),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    CobreIcons.Check,
                    contentDescription = null,
                    tint = colors.onAccentFill,
                    modifier = Modifier.size(16.dp).graphicsLayer {
                        scaleX = escala
                        scaleY = escala
                    },
                )
            }
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
    }
}

/**
 * Interruptor de la app al estilo iOS: cápsula que se rellena en cobre y una perilla
 * blanca que se desliza con resorte. Solo dibuja el estado; el toque lo maneja la fila
 * que lo contiene (con `toggleable`), para que toda la fila sea el área táctil.
 */
@Composable
fun CobreSwitch(checked: Boolean, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    val pista by animateColorAsState(if (checked) colors.accentFill else colors.surfaceMuted, tween(200), label = "pistaSwitch")
    val desplazamiento by animateDpAsState(
        if (checked) 20.dp else 0.dp,
        spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "perillaSwitch",
    )
    Box(
        modifier = modifier
            .size(width = 51.dp, height = 31.dp)
            .clip(CobreShapes.full)
            .background(pista)
            .border(1.dp, if (checked) Color.Transparent else colors.hairline, CobreShapes.full)
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = desplazamiento)
                .size(27.dp)
                .shadow(2.dp, CobreShapes.full)
                .clip(CobreShapes.full)
                .background(Color.White),
        )
    }
}
