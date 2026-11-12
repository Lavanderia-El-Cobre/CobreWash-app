package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

/**
 * Barra de aplicación inspirada en el encabezado móvil de la intranet web:
 * marca a la izquierda y cuenta activa a la derecha, sobre una única superficie
 * liviana. Evita convertir la cabecera completa en otra tarjeta flotante.
 */
@Composable
fun CobreTopBar(
    nombre: String,
    subtitulo: String,
    onCerrarSesion: () -> Unit,
    onToggleTema: (() -> Unit)? = null,
    avatar: @Composable () -> Unit,
) {
    val isDark = cobreIsDark()
    val shell = CobreTheme.colors.surface
    val divider = CobreTheme.colors.hairline
    val innerHighlight = Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(shell)
            .drawBehind {
                drawLine(divider, start = Offset(0f, size.height), end = Offset(size.width, size.height))
                // Inset highlight para efecto cristal
                drawLine(innerHighlight, start = Offset(0f, 1f), end = Offset(size.width, 1f))
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Lavandería El Cobre",
            modifier = Modifier.size(38.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "Lavandería",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
                maxLines = 1,
            )
            Text(
                "El Cobre",
                style = MaterialTheme.typography.titleSmall,
                color = CobreTheme.colors.accent,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.weight(1f, fill = false).padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                nombre,
                style = MaterialTheme.typography.labelMedium,
                color = brandHeadingColor(),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (onToggleTema != null) {
            TemaTopBarButton(isDark = isDark, onToggleTema = onToggleTema)
            Spacer(Modifier.width(6.dp))
        }
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) { avatar() }
        Spacer(Modifier.width(6.dp))
        CerrarSesionButton(onCerrarSesion)
    }
}

@Composable
private fun TemaTopBarButton(isDark: Boolean, onToggleTema: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val accent = CobreTheme.colors.accent
    Box(
        modifier = Modifier
            .size(40.dp)
            .pressableScale(interaction)
            .clip(CobreShapes.md)
            .background(accent.copy(alpha = 0.12f))
            .clickable(interactionSource = interaction, indication = null, onClick = onToggleTema),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isDark) CobreIcons.Sun else CobreIcons.Moon,
            contentDescription = if (isDark) "Cambiar a modo claro" else "Cambiar a modo oscuro",
            tint = accent,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun CerrarSesionButton(onCerrarSesion: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val accent = if (cobreIsDark()) StatusRedDark else StatusRed
    Box(
        modifier = Modifier
            .size(40.dp)
            .pressableScale(interaction)
            .clip(CobreShapes.md)
            .background(accent.copy(alpha = 0.10f))
            .clickable(interactionSource = interaction, indication = null) { onCerrarSesion() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(CobreIcons.Logout, contentDescription = "Cerrar sesión", tint = accent, modifier = Modifier.size(19.dp))
    }
}
