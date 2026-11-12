package com.elcobre.lavanderiaelcobre.ui.screens.avisos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Feed de solo lectura de avisos de administración; destino de navegación propio del
 * operario. Título grande que se colapsa y tarjetas con ancho de lectura en tablet.
 */
@Composable
fun AvisosScreen(
    avisos: List<Aviso>,
    applySystemBarsInsets: Boolean = true,
    cargando: Boolean = false,
    error: String? = null,
    hayMas: Boolean = false,
    onActualizar: () -> Unit = {},
    onMas: () -> Unit = {},
) {
    LargeTitleScreen(
        title = "Avisos de planta",
        subtitle = "Comunicados de administración para el equipo de planta.",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(
                icon = CobreIcons.History,
                contentDescription = "Actualizar avisos",
                onClick = onActualizar,
                size = 44.dp,
                enabled = !cargando,
            )
        },
    ) {
        item(key = "estado") { EstadoAvisos(cargando = cargando, error = error, modifier = Modifier.readableWidth()) }
        if (avisos.isEmpty()) {
            item(key = "vacio") {
                EstadoVacioAvisos(cargando = cargando, error = error, onActualizar = onActualizar, modifier = Modifier.readableWidth())
            }
        } else {
            items(avisos, key = { it.id }) { aviso -> AvisoCard(aviso, Modifier.readableWidth()) }
            if (hayMas) {
                item(key = "mas") {
                    CobreButton(
                        text = "Cargar más avisos",
                        onClick = onMas,
                        variant = ButtonVariant.Tonal,
                        enabled = !cargando,
                        fillWidth = true,
                        modifier = Modifier.readableWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoAvisos(cargando: Boolean, error: String?, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    Column(modifier = modifier) {
        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.full)
                    .semantics { contentDescription = "Cargando avisos" },
                color = colors.accent,
                trackColor = colors.surfaceMuted,
            )
        }
        error?.let {
            Text(
                it,
                color = colors.dangerText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp).semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

@Composable
private fun EstadoVacioAvisos(
    cargando: Boolean,
    error: String?,
    onActualizar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier.padding(top = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (error != null) CobreIcons.Warning else CobreIcons.Campaign,
                contentDescription = null,
                tint = if (error != null) colors.dangerText else colors.textSecondary,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = when {
                    cargando -> "Cargando avisos…"
                    error != null -> "Los avisos no están disponibles por ahora."
                    else -> "No hay avisos publicados por ahora."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            if (!cargando) {
                CobreButton(
                    text = if (error == null) "Actualizar" else "Reintentar",
                    onClick = onActualizar,
                    variant = ButtonVariant.Primary,
                )
            }
        }
    }
}

@Composable
private fun AvisoCard(aviso: Aviso, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    // Fijado en cobre (identidad, como el badge de la web); el ámbar queda para alertas
    // de comanda estancada.
    ContentCard(
        modifier = modifier.semantics(mergeDescendants = true) {},
        stripe = if (aviso.destacado) colors.accent else null,
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CobreShapes.md).background(colors.accentContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(CobreIcons.Campaign, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                CabeceraAvisoCard(aviso = aviso)
                Spacer(Modifier.height(4.dp))
                Text(aviso.mensaje, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                if (aviso.autor.isNotBlank()) {
                    Text("Publicado por ${aviso.autor}", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(CobreIcons.Person, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(13.dp))
                    Text(aviso.dirigidoA, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                    Spacer(Modifier.weight(1f))
                    Text(aviso.hora.uppercase(), style = CobreTheme.text.labelCaps, color = colors.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun CabeceraAvisoCard(aviso: Aviso) {
    val colors = CobreTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            aviso.titulo,
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        if (aviso.destacado) {
            Row(
                modifier = Modifier
                    .clip(CobreShapes.sm)
                    .background(colors.accentContainer)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Icon(CobreIcons.PushPin, contentDescription = null, tint = colors.accentText, modifier = Modifier.size(11.dp))
                Text("Fijado", style = MaterialTheme.typography.labelSmall, color = colors.accentText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
