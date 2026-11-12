package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
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

/**
 * Campo de formulario: contorno con radio `md` (8dp) de DESIGN.md sobre superficie
 * sólida. Los formularios van sobre contenido, no sobre vidrio, para que se lean bien.
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it, style = MaterialTheme.typography.bodyMedium) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, modifier = Modifier.size(20.dp)) } },
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        shape = CobreShapes.md,
        colors = cobreFieldColors(),
    )
}
