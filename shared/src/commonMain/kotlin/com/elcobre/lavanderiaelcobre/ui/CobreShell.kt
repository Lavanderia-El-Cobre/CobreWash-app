package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSurface
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Ítems de navegación del Operario, compartidos entre la barra flotante (teléfono) y el rail (horizontal, tablet). */
private data class NavItem(val icon: ImageVector, val label: String, val pantalla: Pantalla)

private val navItems = listOf(
    NavItem(CobreIcons.LocalLaundry, "Pedidos", Pantalla.Dashboard),
    NavItem(CobreIcons.Campaign, "Avisos", Pantalla.Avisos),
    NavItem(CobreIcons.Inventory, "Inventario", Pantalla.Insumos),
    NavItem(CobreIcons.Person, "Ajustes", Pantalla.Configuracion),
)

// Medidas de la navegación flotante.
private val AltoBarra = 64.dp
private val AnchoRail = 72.dp
private val MargenFlotante = 12.dp
private val SeparacionContenido = 8.dp
private val AnchoMaxBarra = 520.dp

/** Espacio que la barra inferior flotante le quita al contenido, además de la barra de navegación del sistema. */
internal val EspacioBarraInferior: Dp = AltoBarra + MargenFlotante + SeparacionContenido

/** Espacio que el rail flotante le quita al contenido por la izquierda. */
internal val EspacioRail: Dp = AnchoRail + MargenFlotante + SeparacionContenido

/** Contadores que se muestran sobre cada tab (p. ej. avisos sin leer); los ceros no se dibujan. */
internal typealias BadgesNav = Map<Pantalla, Int>

/**
 * Barra de pestañas flotante de vidrio (teléfono vertical): una cápsula con los 4 tabs
 * y, al lado, el botón de QR en vidrio cobre — la acción más frecuente en planta
 * (RF-SP02/SP09) separada de la navegación, como el botón de búsqueda de iOS.
 */
@Composable
internal fun FloatingTabBar(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
    badges: BadgesNav = emptyMap(),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .padding(horizontal = 16.dp)
            .padding(bottom = MargenFlotante),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier.widthIn(max = AnchoMaxBarra).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MargenFlotante),
        ) {
            GlassSurface(
                modifier = Modifier.weight(1f).height(AltoBarra),
                shape = CobreShapes.full,
                floating = true,
                contentPadding = PaddingValues(6.dp),
            ) {
                PestanasConPildora(tabActual = tabActual, onSeleccionarTab = onSeleccionarTab, badges = badges)
            }
            GlassIconButton(
                icon = CobreIcons.QrCodeScanner,
                contentDescription = "Escanear QR",
                onClick = onEscanearQR,
                size = AltoBarra,
                accent = true,
                floating = true,
            )
        }
    }
}

/** Tabs de igual ancho con una píldora cobre tenue que se desliza con spring hasta el seleccionado. */
@Composable
private fun PestanasConPildora(tabActual: Pantalla, onSeleccionarTab: (Pantalla) -> Unit, badges: BadgesNav) {
    val colors = CobreTheme.colors
    val indice = navItems.indexOfFirst { it.pantalla == tabActual }.coerceAtLeast(0)
    val posicion by animateFloatAsState(
        targetValue = indice.toFloat(),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
        label = "pildora",
    )
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val ancho = maxWidth / navItems.size
        Box(
            modifier = Modifier
                .offset(x = ancho * posicion)
                .width(ancho)
                .fillMaxHeight()
                .clip(CobreShapes.full)
                .background(colors.accentContainer),
        )
        Row(modifier = Modifier.fillMaxSize()) {
            navItems.forEach { item ->
                TabItem(
                    item = item,
                    seleccionado = item.pantalla == tabActual,
                    badge = badges[item.pantalla] ?: 0,
                    onClick = { onSeleccionarTab(item.pantalla) },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

/**
 * Rail flotante de vidrio (teléfono horizontal y tablet): el QR arriba y los tabs en
 * una cápsula vertical, centrados a la izquierda. En horizontal libera el alto que la
 * barra inferior le quitaría a las listas.
 */
@Composable
internal fun FloatingRail(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
    compacto: Boolean,
    badges: BadgesNav = emptyMap(),
    modifier: Modifier = Modifier,
) {
    val altoItem = if (compacto) 52.dp else 64.dp
    Box(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start))
            .padding(start = MargenFlotante, top = MargenFlotante, bottom = MargenFlotante),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MargenFlotante),
        ) {
            GlassIconButton(
                icon = CobreIcons.QrCodeScanner,
                contentDescription = "Escanear QR",
                onClick = onEscanearQR,
                size = if (compacto) 52.dp else AnchoRail,
                accent = true,
                floating = true,
            )
            GlassSurface(
                modifier = Modifier.width(AnchoRail),
                shape = CobreShapes.full,
                floating = true,
                contentPadding = PaddingValues(6.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    navItems.forEach { item ->
                        TabItem(
                            item = item,
                            seleccionado = item.pantalla == tabActual,
                            badge = badges[item.pantalla] ?: 0,
                            onClick = { onSeleccionarTab(item.pantalla) },
                            conFondo = true,
                            modifier = Modifier.fillMaxWidth().height(altoItem),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Un tab: ícono + etiqueta, cobre si está seleccionado. Con [conFondo] pinta su propia
 * píldora (rail); en la barra inferior la píldora es compartida y se desliza.
 */
@Composable
private fun TabItem(
    item: NavItem,
    seleccionado: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    conFondo: Boolean = false,
) {
    val colors = CobreTheme.colors
    val tinte by animateColorAsState(
        if (seleccionado) colors.accentText else colors.textSecondary,
        tween(200),
        label = "tabTinte",
    )
    val fondo by animateColorAsState(
        if (conFondo && seleccionado) colors.accentContainer else Color.Transparent,
        tween(200),
        label = "tabFondo",
    )
    Column(
        modifier = modifier
            .clip(CobreShapes.full)
            .background(fondo)
            .selectable(
                selected = seleccionado,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = if (badge > 0) "${item.label}, $badge sin leer" else item.label
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(item.icon, contentDescription = null, tint = tinte, modifier = Modifier.padding(horizontal = 6.dp).size(IconoTab))
            if (badge > 0) BadgeTab(badge)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            item.label,
            style = MaterialTheme.typography.labelSmall,
            color = tinte,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val IconoTab: Dp = 22.dp

/** Contador cobre sobre el ícono del tab. */
@Composable
private fun BadgeTab(cantidad: Int) {
    val colors = CobreTheme.colors
    Box(
        modifier = Modifier
            .clip(CobreShapes.full)
            .background(colors.accentFill)
            .padding(horizontal = 5.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (cantidad > 9) "9+" else cantidad.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.onAccentFill,
        )
    }
}
