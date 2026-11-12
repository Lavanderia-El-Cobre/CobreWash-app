// El archivo agrupa los botones (CobreButton, GlassIconButton); ButtonVariant es solo su parámetro.
@file:Suppress("MatchingDeclarationName")

package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.GlassLevel

/** Jerarquía de botones. Solo una acción [Accent] por pantalla: es la que lleva el cobre. */
enum class ButtonVariant {
    /** Acción principal de la pantalla (Avanzar etapa, Ingresar, Escanear): cobre sólido. */
    Accent,

    /** Acción fuerte pero no principal: negro (Ink) de DESIGN.md. */
    Primary,

    /** Acción secundaria con identidad: fondo cobre tenue, texto cobre. */
    Tonal,

    /** Acción sobre contenido (barras, flotantes): vidrio neutro. */
    Glass,

    /** Acción destructiva (cerrar sesión, reportar incidencia). */
    Danger,
}

/** Alto mínimo táctil de los botones: 52dp, cómodo con guantes en planta. */
private val AltoBoton = 52.dp

/**
 * Botón en cápsula de la app. Reemplaza los `Button` con degradado y los `Row`
 * clicables armados a mano en cada pantalla.
 */
@Composable
fun CobreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Accent,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    fillWidth: Boolean = false,
) {
    val colors = CobreTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val (fondo, texto) = when (variant) {
        ButtonVariant.Accent -> colors.accentFill to colors.onAccentFill
        ButtonVariant.Primary -> colors.primaryFill to colors.onPrimaryFill
        ButtonVariant.Tonal -> colors.accentContainer to colors.accentText
        ButtonVariant.Danger -> colors.danger.copy(alpha = if (colors.isDark) 0.18f else 0.10f) to colors.dangerText
        ButtonVariant.Glass -> Color.Unspecified to colors.textPrimary
    }
    val base = modifier
        .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
        .heightIn(min = AltoBoton)
        .alpha(if (enabled) 1f else 0.4f)
        .pressableScale(interaction)
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled && !loading,
            role = Role.Button,
            onClick = onClick,
        )
    val padding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)

    if (variant == ButtonVariant.Glass) {
        GlassSurface(
            modifier = base,
            shape = CobreShapes.full,
            level = GlassLevel.Regular,
            contentPadding = padding,
            contentAlignment = Alignment.Center,
        ) {
            BotonContenido(text = text, icon = icon, color = texto, loading = loading)
        }
    } else {
        Box(
            modifier = base
                .clip(CobreShapes.full)
                .background(fondo)
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            BotonContenido(text = text, icon = icon, color = texto, loading = loading)
        }
    }
}

@Composable
private fun BotonContenido(text: String, icon: ImageVector?, color: Color, loading: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(color = color, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Botón circular de vidrio: volver, cerrar, acciones de barra. Con [accent] es vidrio
 * teñido de cobre, para la acción insignia (escanear QR).
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    accent: Boolean = false,
    floating: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = CobreTheme.colors
    val interaction = remember { MutableInteractionSource() }
    GlassSurface(
        modifier = modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.4f)
            .pressableScale(interaction, pressedScale = 0.92f)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        shape = CobreShapes.full,
        level = GlassLevel.Regular,
        tint = if (accent) colors.accentFill else null,
        floating = floating,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (accent) colors.onAccentFill else colors.textPrimary,
            modifier = Modifier.size(size * 0.46f),
        )
    }
}
