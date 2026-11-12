package com.elcobre.lavanderiaelcobre.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipColors
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.CompactTitleBar
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EtapasTimeline
import com.elcobre.lavanderiaelcobre.ui.components.FlujoProduccionCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassStack
import com.elcobre.lavanderiaelcobre.ui.components.HistorialCard
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitle
import com.elcobre.lavanderiaelcobre.ui.components.LocalShellPadding
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.StageProgress
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.etapaColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaIcon
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.prioridadColors
import com.elcobre.lavanderiaelcobre.ui.components.rememberTitleCollapsed
import com.elcobre.lavanderiaelcobre.ui.components.sharedTag
import com.elcobre.lavanderiaelcobre.ui.components.tipoIcon
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private val ANCHO_TABLET_SPLIT = 720.dp

/** Ancho de la lista de comandas junto al detalle en tablet. */
private val AnchoListaTablet = 360.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DashboardScreen(
    operario: Operario,
    pedidos: List<Pedido>,
    onAbrirPedido: (Pedido) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onComentar: (String, String) -> Unit = { _, _ -> },
    onAlerta: (String, TipoAlerta, String) -> Unit = { _, _, _ -> },
    onResolverAlerta: (String) -> Unit = {},
    onCerrarSesion: () -> Unit,
    mostrarTopBar: Boolean = true,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var filtro by remember { mutableStateOf<TipoComanda?>(null) }
    var busqueda by remember { mutableStateOf("") }
    var expandidoId by remember { mutableStateOf<String?>(null) }
    var pedidoSeleccionadoId by remember { mutableStateOf<String?>(pedidos.firstOrNull()?.id) }

    // derivedStateOf y no remember(pedidos): la lista del ViewModel es la misma instancia
    // observable aunque cambie su contenido, así que como clave de remember nunca invalidaba
    // y las comandas recién cargadas no aparecían hasta salir y volver a la pestaña.
    val visibles by remember(pedidos) {
        derivedStateOf {
            val consulta = busqueda.trim()
            pedidos.filter { p ->
                (filtro == null || p.tipo == filtro) &&
                    (
                        consulta.isEmpty() ||
                            p.comanda.contains(consulta, ignoreCase = true) ||
                            p.cliente.contains(consulta, ignoreCase = true)
                        )
            }
        }
    }
    // El espacio de la barra flotante va como contentPadding de la lista, para que las
    // comandas pasen por debajo del vidrio, y no como padding del contenedor.
    val espacioNav = LocalShellPadding.current.calculateBottomPadding()
    CompositionLocalProvider(LocalShellPadding provides PaddingValues(0.dp)) {
        CobreBackground(applySystemBarsInsets = mostrarTopBar) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                if (maxWidth >= ANCHO_TABLET_SPLIT) {
                    TabletMasterDetailLayout(
                        espacioInferior = espacioNav,
                        pedidos = pedidos,
                        visibles = visibles,
                        filtro = filtro,
                        onFiltroChange = { filtro = it },
                        busqueda = busqueda,
                        onBusquedaChange = { busqueda = it },
                        pedidoSeleccionadoId = pedidoSeleccionadoId,
                        onSeleccionarPedido = { pedidoSeleccionadoId = it },
                        onSeleccionarPedidoDobleClick = { pedidoSeleccionadoId = it },
                        onAvanzarEtapa = onAvanzarEtapa,
                        onComentar = onComentar,
                        onAlerta = onAlerta,
                        onResolverAlerta = onResolverAlerta,
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                    )
                } else {
                    MobileDashboardLayout(
                        espacioInferior = espacioNav,
                        operario = operario,
                        pedidos = pedidos,
                        visibles = visibles,
                        filtro = filtro,
                        onFiltroChange = { filtro = it },
                        busqueda = busqueda,
                        onBusquedaChange = { busqueda = it },
                        expandidoId = expandidoId,
                        onToggleExpand = { expandidoId = if (expandidoId == it) null else it },
                        onAvanzarEtapa = onAvanzarEtapa,
                        onAbrirPedido = onAbrirPedido,
                        onCerrarSesion = onCerrarSesion,
                        mostrarTopBar = mostrarTopBar,
                        sharedScope = sharedScope,
                        animatedScope = animatedScope,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TabletMasterDetailLayout(
    espacioInferior: Dp,
    pedidos: List<Pedido>,
    visibles: List<Pedido>,
    filtro: TipoComanda?,
    onFiltroChange: (TipoComanda?) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    pedidoSeleccionadoId: String?,
    onSeleccionarPedido: (String) -> Unit,
    onSeleccionarPedidoDobleClick: (String) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onComentar: (String, String) -> Unit,
    onAlerta: (String, TipoAlerta, String) -> Unit,
    onResolverAlerta: (String) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val haptic = LocalHapticFeedback.current
    Row(modifier = Modifier.fillMaxSize().padding(bottom = espacioInferior)) {
        // Lista fija (no plegable): ocupa su propio espacio junto al detalle.
        Box(modifier = Modifier.width(AnchoListaTablet).fillMaxHeight()) {
            // Resumen, búsqueda y filtro se desplazan con la lista: fijos arriba dejaban casi
            // sin alto a las comandas en un teléfono horizontal.
            PedidosListColumn(
                encabezado = {
                    item(key = "resumen") {
                        MisTareasResumen(pedidos = pedidos, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                    }
                    item(key = "busqueda") { SearchBar(query = busqueda, onQueryChange = onBusquedaChange) }
                    item(key = "filtros") {
                        FiltroTipos(
                            seleccion = filtro,
                            onSeleccion = {
                                onFiltroChange(it)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        )
                    }
                },
                visibles = visibles,
                expandidoId = null,
                seleccionadoId = pedidoSeleccionadoId,
                onToggleExpand = {},
                onAvanzarEtapa = onAvanzarEtapa,
                onSeleccionar = { p ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSeleccionarPedido(p.id)
                },
                onDoubleClick = { p ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSeleccionarPedidoDobleClick(p.id)
                },
                onLimpiar = {
                    onFiltroChange(null)
                    onBusquedaChange("")
                },
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }

        val pedidoActivo = pedidos.firstOrNull { it.id == pedidoSeleccionadoId }
        TabletDetailPane(
            pedido = pedidoActivo,
            onAvanzar = { if (pedidoActivo != null) onAvanzarEtapa(pedidoActivo) },
            onComentar = { texto -> if (pedidoActivo != null) onComentar(pedidoActivo.id, texto) },
            onAlerta = { tipo, nota -> if (pedidoActivo != null) onAlerta(pedidoActivo.id, tipo, nota) },
            onResolver = { if (pedidoActivo != null) onResolverAlerta(pedidoActivo.id) },
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MobileDashboardLayout(
    espacioInferior: Dp,
    operario: Operario,
    pedidos: List<Pedido>,
    visibles: List<Pedido>,
    filtro: TipoComanda?,
    onFiltroChange: (TipoComanda?) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    expandidoId: String?,
    onToggleExpand: (String) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onAbrirPedido: (Pedido) -> Unit,
    onCerrarSesion: () -> Unit,
    mostrarTopBar: Boolean,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    val colapsado by rememberTitleCollapsed(listState)
    // Título grande, resumen, búsqueda y filtro van dentro de la lista y se desplazan con
    // ella: fijos arriba le quitaban casi todo el alto a la lista en horizontal. Al
    // colapsar el título aparece una barra de vidrio por encima del contenido.
    GlassStack(
        modifier = Modifier.fillMaxSize(),
        background = { fuente ->
        PedidosListColumn(
            state = listState,
            extraInferior = espacioInferior,
            encabezado = {
                item(key = "titulo") {
                    LargeTitle(
                        title = "Mis tareas",
                        subtitle = operario.nombre,
                        trailing = if (mostrarTopBar) {
                            {
                                GlassIconButton(
                                    icon = CobreIcons.Logout,
                                    contentDescription = "Cerrar sesión",
                                    onClick = onCerrarSesion,
                                    size = 44.dp,
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
                item(key = "resumen") {
                    MisTareasResumen(pedidos = pedidos, conTitulo = false, modifier = Modifier.padding(bottom = 4.dp))
                }
                item(key = "busqueda") { SearchBar(query = busqueda, onQueryChange = onBusquedaChange) }
                item(key = "filtros") {
                    FiltroTipos(
                        seleccion = filtro,
                        onSeleccion = {
                            onFiltroChange(it)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            },
            visibles = visibles,
            expandidoId = expandidoId,
            seleccionadoId = null,
            onToggleExpand = { id ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggleExpand(id)
            },
            onAvanzarEtapa = onAvanzarEtapa,
            onSeleccionar = onAbrirPedido,
            onDoubleClick = { }, // En móvil no usamos doble clic para ocultar sidebar
            onLimpiar = {
                onFiltroChange(null)
                onBusquedaChange("")
            },
            sharedScope = sharedScope,
            animatedScope = animatedScope,
            modifier = fuente,
        )
        },
        overlay = {
            CompactTitleBar(title = "Mis tareas", visible = colapsado, modifier = Modifier.align(Alignment.TopCenter))
        },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PedidosListColumn(
    visibles: List<Pedido>,
    state: LazyListState = rememberLazyListState(),
    encabezado: LazyListScope.() -> Unit = {},
    extraInferior: Dp = 0.dp,
    expandidoId: String?,
    seleccionadoId: String?,
    onToggleExpand: (String) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onSeleccionar: (Pedido) -> Unit,
    onDoubleClick: (Pedido) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        state = state,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp + extraInferior, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        encabezado()
        if (visibles.isEmpty()) {
            item {
                EmptyState(
                    onLimpiar = onLimpiar,
                    modifier = Modifier.padding(top = 40.dp),
                )
            }
        } else {
            item {
                SectionLabel("Comandas · ${visibles.size}", modifier = Modifier.padding(top = 4.dp, start = 4.dp))
            }
            items(visibles, key = { p -> p.id }) { pedido ->
                val seleccionado = seleccionadoId == pedido.id
                PedidoCard(
                    pedido = pedido,
                    expandido = expandidoId == pedido.id,
                    seleccionado = seleccionado,
                    onToggleExpand = { onToggleExpand(pedido.id) },
                    onAvanzar = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAvanzarEtapa(pedido)
                    },
                    onVerDetalle = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSeleccionar(pedido)
                    },
                    onDoubleClick = {
                        onDoubleClick(pedido)
                    },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(250),
                        placementSpec = tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                        fadeOutSpec = tween(250)
                    ),
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            }
        }
    }
}

/**
 * Panel derecho en tablets: inspección profunda de la comanda con Flujo de Producción web.
 */
@Composable
private fun TabletDetailPane(
    pedido: Pedido?,
    onAvanzar: () -> Unit,
    onComentar: (String) -> Unit,
    onAlerta: (TipoAlerta, String) -> Unit,
    onResolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var comentarioTexto by remember(pedido?.id) { mutableStateOf("") }
    val colors = CobreTheme.colors

    Box(
        modifier = modifier
            .drawBehind {
                drawLine(colors.hairline, start = Offset(0f, 0f), end = Offset(0f, size.height))
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        if (pedido == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CobreShapes.lg).background(colors.surfaceMuted),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            CobreIcons.LocalLaundry,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Selecciona una comanda", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toca cualquier comanda de la lista para ver su flujo de avance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TabletDetailHeader(pedido)

                // Componente idéntico a FlujoProduccion.tsx de la web
                FlujoProduccionCard(
                    pedido = pedido,
                    onAvanzar = onAvanzar,
                    onResolverAlerta = onResolver,
                    onReportarIncidencia = { onAlerta(TipoAlerta.INCIDENCIA, "Reporte rápido desde tablet") },
                )

                TabletComentarioCard(
                    comentario = comentarioTexto,
                    onComentarioChange = { comentarioTexto = it },
                    onEnviar = {
                        onComentar(comentarioTexto.trim())
                        comentarioTexto = ""
                    },
                )

                HistorialCard(pedido = pedido)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TabletDetailHeader(pedido: Pedido) {
    val colors = CobreTheme.colors
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(pedido.comanda, style = CobreTheme.text.largeTitle, color = colors.textPrimary)
                Text(
                    "${pedido.cliente} · ${pedido.piezas} prendas · ${pedido.tipoServicio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                label = pedido.etapaActual.corto,
                container = etapa.container,
                content = etapa.content,
                icon = etapaIcon(pedido.etapaActual),
            )
            StatusChip(
                label = if (pedido.estadoComandaDb == null) "Prioridad ${pedido.prioridad.displayName}" else "Prioridad no definida",
                container = prioridad.container,
                content = prioridad.content,
            )
        }
    }
}

@Composable
private fun TabletComentarioCard(
    comentario: String,
    onComentarioChange: (String) -> Unit,
    onEnviar: () -> Unit,
) {
    val colors = CobreTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Nota de proceso", modifier = Modifier.padding(start = 4.dp))
        ContentCard {
            CobreTextField(
                value = comentario,
                onValueChange = onComentarioChange,
                placeholder = "Escribe un comentario sobre esta carga o prenda…",
                singleLine = false,
                trailingIcon = {
                    IconButton(onClick = onEnviar, enabled = comentario.isNotBlank()) {
                        Icon(CobreIcons.Send, contentDescription = "Enviar", tint = colors.accent, modifier = Modifier.size(18.dp))
                    }
                },
            )
        }
    }
}

/**
 * Conteo de comandas en proceso y alertas activas. Con [conTitulo] muestra además el
 * título (panel de tablet); en teléfono el título lo pone el [LargeTitle] de la lista.
 */
@Composable
private fun MisTareasResumen(pedidos: List<Pedido>, modifier: Modifier = Modifier, conTitulo: Boolean = true) {
    val colors = CobreTheme.colors
    val pendientes = pedidos.count { !it.esFinal }
    // Solo cuentan las alertas que están actualmente bloqueando una comanda.
    val alertas = pedidos.count { it.estaBloqueado }

    Column(modifier = modifier.fillMaxWidth()) {
        if (conTitulo) {
            Text(
                "Seguimiento de producción",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(2.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                if (pendientes == 1) "1 comanda en proceso" else "$pendientes comandas en proceso",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            if (alertas > 0) {
                Row(
                    modifier = Modifier
                        .clip(CobreShapes.sm)
                        .background(colors.danger.copy(alpha = if (colors.isDark) 0.18f else 0.10f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = if (alertas == 1) "1 alerta activa" else "$alertas alertas activas"
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(CobreIcons.Warning, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(14.dp))
                    Text("$alertas", style = MaterialTheme.typography.labelMedium, color = colors.dangerText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSearchField(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = "Buscar comanda o cliente",
        modifier = modifier,
    )
}

@Composable
private fun EmptyState(onLimpiar: () -> Unit, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CobreShapes.lg).background(colors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.Inventory, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("No hay comandas aquí", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        Spacer(Modifier.height(4.dp))
        Text(
            "Ninguna comanda coincide con el filtro o la búsqueda actual.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        CobreButton(text = "Ver todas las comandas", onClick = onLimpiar, variant = ButtonVariant.Primary)
    }
}

/** Filtro por tipo de comanda: control segmentado (Todas / Tienda / Domicilio). */
@Composable
private fun FiltroTipos(
    seleccion: TipoComanda?,
    onSeleccion: (TipoComanda?) -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSegmentedControl(
        options = listOf<TipoComanda?>(null) + TipoComanda.entries,
        selected = seleccion,
        onSelect = onSeleccion,
        label = { it?.corto ?: "Todas" },
        modifier = modifier,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun PedidoCard(
    pedido: Pedido,
    expandido: Boolean,
    seleccionado: Boolean = false,
    onToggleExpand: () -> Unit,
    onAvanzar: () -> Unit,
    onVerDetalle: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val colors = CobreTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(if (seleccionado) Modifier.border(2.dp, colors.accent, CobreShapes.lg) else Modifier)
            .pressableScale(interaction, pressedScale = 0.98f)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = { if (seleccionado) onToggleExpand() else onVerDetalle() },
                onDoubleClick = { onDoubleClick?.invoke() },
            ),
        contentPadding = PaddingValues(16.dp),
        stripe = prioridad.accent,
    ) {
        Column {
            PedidoCardHeader(pedido, etapa, expandido, sharedScope, animatedScope)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoPill(pedido.tipo)
                Text(
                    pedido.tipoServicio,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            StageProgress(
                flujo = pedido.flujo,
                etapaActual = pedido.etapaActual,
                accent = etapa.accent,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            PedidoCardChips(pedido, etapa, prioridad)
            AnimatedVisibility(visible = expandido) {
                PedidoCardExpandido(pedido, onAvanzar, onVerDetalle)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PedidoCardHeader(
    pedido: Pedido,
    etapa: ChipColors,
    expandido: Boolean,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val colors = CobreTheme.colors
    val rotacion by animateFloatAsState(if (expandido) 90f else 0f, tween(300), label = "chevron")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .sharedTag("icono-${pedido.comanda}", sharedScope, animatedScope)
                .size(40.dp)
                .clip(CobreShapes.md)
                .background(etapa.container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(etapaIcon(pedido.etapaActual), contentDescription = null, tint = etapa.accent, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(pedido.comanda, style = CobreTheme.text.label, color = colors.textSecondary)
            Text(pedido.cliente, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        }
        Icon(
            CobreIcons.ArrowForward,
            contentDescription = if (expandido) "Contraer" else "Expandir",
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotacion },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PedidoCardChips(pedido: Pedido, etapa: ChipColors, prioridad: ChipColors) {
    val colors = CobreTheme.colors
    // FlowRow: en tarjetas angostas el plazo baja de línea en vez de cortarse.
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        StatusChip(
            label = pedido.etapaActual.corto,
            container = etapa.container,
            content = etapa.content,
            icon = etapaIcon(pedido.etapaActual),
        )
        StatusChip(
            label = if (pedido.estadoComandaDb == null) "Prioridad ${pedido.prioridad.displayName}" else "Prioridad no definida",
            container = prioridad.container,
            content = prioridad.content,
        )
        val urgente = pedido.plazoEntrega.startsWith("Hoy")
        val plazoColor = if (urgente) colors.warningText else colors.textSecondary
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(CobreIcons.Schedule, contentDescription = null, tint = plazoColor, modifier = Modifier.size(14.dp))
            Text(
                pedido.plazoEntrega,
                style = MaterialTheme.typography.labelSmall,
                color = plazoColor,
                fontWeight = if (urgente) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun PedidoCardExpandido(pedido: Pedido, onAvanzar: () -> Unit, onVerDetalle: () -> Unit) {
    val colors = CobreTheme.colors
    Column(modifier = Modifier.padding(top = 14.dp)) {
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(colors.hairline))
        Spacer(Modifier.height(14.dp))
        EtapasTimeline(flujo = pedido.flujo, indiceActual = pedido.indiceEtapa)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val siguiente = pedido.siguienteEtapa
            CobreButton(
                text = when {
                    pedido.estaBloqueado -> "Resuelve la alerta"
                    siguiente != null -> "Avanzar a ${siguiente.corto}"
                    else -> "Completado"
                },
                onClick = onAvanzar,
                enabled = !pedido.esFinal && !pedido.estaBloqueado,
                modifier = Modifier.weight(1f),
            )
            CobreButton(text = "Detalle", onClick = onVerDetalle, variant = ButtonVariant.Tonal)
        }
    }
}

/** Distintivo compacto del tipo de comanda (tienda / domicilio). */
@Composable
private fun TipoPill(tipo: TipoComanda) {
    val colors = CobreTheme.colors
    Row(
        modifier = Modifier
            .clip(CobreShapes.sm)
            .background(colors.surfaceMuted)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(tipoIcon(tipo), contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(13.dp))
        Text(tipo.corto, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, fontWeight = FontWeight.SemiBold)
    }
}
