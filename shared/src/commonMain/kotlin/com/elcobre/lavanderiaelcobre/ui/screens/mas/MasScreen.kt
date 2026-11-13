package com.elcobre.lavanderiaelcobre.ui.screens.mas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.SeccionMas
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.LocalNavegacionAtras
import com.elcobre.lavanderiaelcobre.ui.components.NavegacionAtras
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

fun iconoSeccion(seccion: SeccionMas): ImageVector = when (seccion) {
    SeccionMas.PRODUCCION -> CobreIcons.Timeline
    SeccionMas.INCIDENCIAS -> CobreIcons.Warning
    SeccionMas.CLIENTES -> CobreIcons.Contacts
    SeccionMas.COTIZACIONES -> CobreIcons.RequestQuote
    SeccionMas.AVISOS -> CobreIcons.Campaign
    SeccionMas.USUARIOS -> CobreIcons.Group
    SeccionMas.REPORTES -> CobreIcons.BarChart
    SeccionMas.ETAPAS -> CobreIcons.Schedule
    SeccionMas.AJUSTES -> CobreIcons.Person
}

/**
 * Pestaña "Más": agrupa las secciones que no caben en la barra. En teléfono y tablet
 * vertical la sección reemplaza al menú (con botón de volver); en tablet horizontal el
 * menú queda a la izquierda y la sección a la derecha.
 */
@Composable
fun MasScreen(
    secciones: List<SeccionMas>,
    seccion: SeccionMas?,
    onAbrir: (SeccionMas?) -> Unit,
    insignias: Map<SeccionMas, Int> = emptyMap(),
    contenido: @Composable (SeccionMas) -> Unit,
) {
    val window = CobreTheme.window
    if (window.useListDetail) {
        val actual = seccion ?: secciones.first()
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(320.dp).fillMaxHeight()) {
                MenuMas(secciones, actual, insignias, onAbrir)
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                AnimatedContent(actual, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) }, label = "seccionMas") {
                    contenido(it)
                }
            }
        }
    } else {
        AnimatedContent(
            targetState = seccion,
            transitionSpec = {
                val adelante = targetState != null
                (slideInHorizontally(tween(320)) { w -> if (adelante) w else -w / 3 } + fadeIn(tween(200))) togetherWith
                    (slideOutHorizontally(tween(320)) { w -> if (adelante) -w / 3 else w } + fadeOut(tween(160)))
            },
            label = "masTelefono",
        ) { abierta ->
            if (abierta == null) {
                MenuMas(secciones, null, insignias, onAbrir)
            } else {
                CompositionLocalProvider(LocalNavegacionAtras provides NavegacionAtras("Más") { onAbrir(null) }) {
                    contenido(abierta)
                }
            }
        }
    }
}

@Composable
private fun MenuMas(
    secciones: List<SeccionMas>,
    seleccionada: SeccionMas?,
    insignias: Map<SeccionMas, Int>,
    onAbrir: (SeccionMas) -> Unit,
) {
    LargeTitleScreen(title = "Más", subtitle = "Todas las secciones de tu rol") {
        items(secciones, key = { it.name }) { seccion ->
            FilaSeccion(
                seccion = seccion,
                seleccionada = seccion == seleccionada,
                insignia = insignias[seccion] ?: 0,
                onClick = { onAbrir(seccion) },
                modifier = Modifier.readableWidth(),
            )
        }
    }
}

@Composable
private fun FilaSeccion(seccion: SeccionMas, seleccionada: Boolean, insignia: Int, onClick: () -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(
        modifier = modifier.semantics { selected = seleccionada },
        onClick = onClick,
        stripe = if (seleccionada) colors.accent else null,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CobreShapes.md).background(colors.accentContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(iconoSeccion(seccion), contentDescription = null, tint = colors.accent, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(seccion.titulo, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(seccion.descripcion, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            if (insignia > 0) {
                Box(
                    modifier = Modifier.clip(CobreShapes.full).background(colors.accentFill),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (insignia > 99) "99+" else insignia.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onAccentFill,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Icon(CobreIcons.ChevronRight, contentDescription = null, tint = colors.textSecondary)
        }
    }
}
