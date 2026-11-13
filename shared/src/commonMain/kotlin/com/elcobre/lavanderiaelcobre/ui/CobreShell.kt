@file:Suppress("MatchingDeclarationName")

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

/** Ítem de navegación, compartido entre la barra flotante (vertical) y el rail (horizontal). */
internal data class NavItem(val icon: ImageVector, val label: String, val pantalla: Pantalla)

/** Pestañas del Operario: sus comandas, la producción de la planta, avisos, inventario y ajustes. */
internal val NavOperario = listOf(
    NavItem(CobreIcons.LocalLaundry, "Pedidos", Pantalla.Dashboard),
    NavItem(CobreIcons.Timeline, "Planta", Pantalla.Produccion),
    NavItem(CobreIcons.Campaign, "Avisos", Pantalla.Avisos),
    NavItem(CobreIcons.Inventory, "Inventario", Pantalla.Insumos),
    NavItem(CobreIcons.Person, "Ajustes", Pantalla.Configuracion),
)

/** Pestañas de recepción; incidencias, cotizaciones y ajustes van en "Más". */
internal val NavRecepcion = listOf(
    NavItem(CobreIcons.Assignment, "Comandas", Pantalla.Comandas),
    NavItem(CobreIcons.Timeline, "Producción", Pantalla.Produccion),
    NavItem(CobreIcons.Contacts, "Clientes", Pantalla.Clientes),
    NavItem(CobreIcons.Campaign, "Avisos", Pantalla.Avisos),
    NavItem(CobreIcons.MoreHoriz, "Más", Pantalla.Mas),
)

/** Pestañas de administración; el resto de la intranet va agrupado en "Más". */
internal val NavAdmin = listOf(
    NavItem(CobreIcons.Dashboard, "Inicio", Pantalla.Inicio),
    NavItem(CobreIcons.Assignment, "Comandas", Pantalla.Comandas),
    NavItem(CobreIcons.Inventory, "Inventario", Pantalla.Stock),
    NavItem(CobreIcons.LocalShipping, "Flota", Pantalla.Vehiculos),
    NavItem(CobreIcons.MoreHoriz, "Más", Pantalla.Mas),
)

/** Pestañas del repartidor: sus recorridos y ajustes. */
internal val NavRepartidor = listOf(
    NavItem(CobreIcons.LocalShipping, "Recorridos", Pantalla.Reparto),
    NavItem(CobreIcons.Person, "Ajustes", Pantalla.Configuracion),
)

/** Todos los destinos que son pestañas de algún rol (comparten un solo destino raíz animado). */
internal val TodasLasPestanas: Set<Pantalla> =
    (NavOperario + NavRecepcion + NavAdmin + NavRepartidor).map { it.pantalla }.toSet()

// Medidas de la navegación flotante.
private val AltoBarra = 64.dp
private val AnchoRail = 72.dp
private val MargenFlotante = 12.dp
private val SeparacionContenido = 8.dp
private val AnchoMaxBarra = 520.dp
private val EspacioItemsRail = 4.dp

// Aire entre el rail y el contenido: en teléfono horizontal las tarjetas quedaban pegadas al vidrio.
private val SeparacionRail = 20.dp

/** Resorte de la píldora: se desliza y asienta con un leve rebote, como el selector de iOS. */
private fun <T> resortePildora() = spring<T>(dampingRatio = 0.78f, stiffness = 380f)

/** Espacio que la barra inferior flotante le quita al contenido, además de la barra de navegación del sistema. */
internal val EspacioBarraInferior: Dp = AltoBarra + MargenFlotante + SeparacionContenido

/** Contadores que se muestran sobre cada tab (p. ej. avisos sin leer); los ceros no se dibujan. */
internal typealias BadgesNav = Map<Pantalla, Int>

/**
 * Barra de pestañas flotante de vidrio (pantallas en vertical): una cápsula con los tabs
 * y, al lado, el botón de QR en vidrio cobre cuando el rol lo usa — la acción más
 * frecuente en planta, separada de la navegación como el botón de búsqueda de iOS.
 */
@Composable
internal fun FloatingTabBar(
    items: List<NavItem>,
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: (() -> Unit)?,
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
            // Con pocas pestañas (administración) la cápsula no se estira a todo el ancho.
            modifier = Modifier.widthIn(max = if (items.size > 2) AnchoMaxBarra else AnchoMaxBarra * 0.6f).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MargenFlotante),
        ) {
            GlassSurface(
                modifier = Modifier.weight(1f).height(AltoBarra),
                shape = CobreShapes.full,
                floating = true,
                contentPadding = PaddingValues(6.dp),
            ) {
                PestanasHorizontales(items = items, tabActual = tabActual, onSeleccionarTab = onSeleccionarTab, badges = badges)
            }
            if (onEscanearQR != null) {
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
}

/** Tabs de igual ancho con una píldora cobre tenue que se desliza con resorte hasta el seleccionado. */
@Composable
private fun PestanasHorizontales(
    items: List<NavItem>,
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    badges: BadgesNav,
) {
    val posicion by animateFloatAsState(indiceDe(items, tabActual), resortePildora(), label = "pildoraH")
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val ancho = maxWidth / items.size
        Pildora(modifier = Modifier.offset(x = ancho * posicion).width(ancho).fillMaxHeight())
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEach { item ->
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
 * Rail flotante de vidrio (pantallas en horizontal): el QR arriba y los tabs en una
 * cápsula vertical, centrados a la izquierda. La píldora se desliza verticalmente entre
 * los tabs, igual que en la barra inferior lo hace en horizontal.
 */
@Composable
internal fun FloatingRail(
    items: List<NavItem>,
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: (() -> Unit)?,
    compacto: Boolean,
    badges: BadgesNav = emptyMap(),
    modifier: Modifier = Modifier,
) {
    val altoItem = if (compacto) 52.dp else 64.dp
    Box(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start))
            .padding(start = MargenFlotante, top = MargenFlotante, bottom = MargenFlotante, end = SeparacionRail),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MargenFlotante),
        ) {
            if (onEscanearQR != null) {
                GlassIconButton(
                    icon = CobreIcons.QrCodeScanner,
                    contentDescription = "Escanear QR",
                    onClick = onEscanearQR,
                    size = if (compacto) 52.dp else AnchoRail,
                    accent = true,
                    floating = true,
                )
            }
            GlassSurface(
                modifier = Modifier.width(AnchoRail),
                shape = CobreShapes.full,
                floating = true,
                contentPadding = PaddingValues(6.dp),
            ) {
                PestanasVerticales(
                    items = items,
                    tabActual = tabActual,
                    altoItem = altoItem,
                    onSeleccionarTab = onSeleccionarTab,
                    badges = badges,
                )
            }
        }
    }
}

@Composable
private fun PestanasVerticales(
    items: List<NavItem>,
    tabActual: Pantalla,
    altoItem: Dp,
    onSeleccionarTab: (Pantalla) -> Unit,
    badges: BadgesNav,
) {
    val posicion by animateFloatAsState(indiceDe(items, tabActual), resortePildora(), label = "pildoraV")
    Box(modifier = Modifier.fillMaxWidth()) {
        Pildora(
            modifier = Modifier
                .offset(y = (altoItem + EspacioItemsRail) * posicion)
                .fillMaxWidth()
                .height(altoItem),
        )
        Column(verticalArrangement = Arrangement.spacedBy(EspacioItemsRail)) {
            items.forEach { item ->
                TabItem(
                    item = item,
                    seleccionado = item.pantalla == tabActual,
                    badge = badges[item.pantalla] ?: 0,
                    onClick = { onSeleccionarTab(item.pantalla) },
                    modifier = Modifier.fillMaxWidth().height(altoItem),
                )
            }
        }
    }
}

private fun indiceDe(items: List<NavItem>, tab: Pantalla): Float =
    items.indexOfFirst { it.pantalla == tab }.coerceAtLeast(0).toFloat()

/** Indicador cobre tenue de la pestaña activa; una sola instancia que se desplaza. */
@Composable
private fun Pildora(modifier: Modifier) {
    Box(modifier = modifier.clip(CobreShapes.full).background(CobreTheme.colors.accentContainer))
}

/** Un tab: ícono + etiqueta, cobre si está seleccionado. La píldora la dibuja el contenedor. */
@Composable
private fun TabItem(
    item: NavItem,
    seleccionado: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    val tinte by animateColorAsState(
        if (seleccionado) colors.accentText else colors.textSecondary,
        tween(220),
        label = "tabTinte",
    )
    Column(
        modifier = modifier
            .clip(CobreShapes.full)
            .selectable(
                selected = seleccionado,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = if (badge > 0) "${item.label}, $badge pendientes" else item.label
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
