package com.elcobre.lavanderiaelcobre.ui.screens.detalle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.CompactTitleBar
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EtapasTimeline
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassStack
import com.elcobre.lavanderiaelcobre.ui.components.GlassSurface
import com.elcobre.lavanderiaelcobre.ui.components.HistorialCard
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.StageProgress
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.alertaColor
import com.elcobre.lavanderiaelcobre.ui.components.alertaIcon
import com.elcobre.lavanderiaelcobre.ui.components.etapaColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaIcon
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.prioridadColors
import com.elcobre.lavanderiaelcobre.ui.components.rememberShellContentPadding
import com.elcobre.lavanderiaelcobre.ui.components.rememberTitleCollapsed
import com.elcobre.lavanderiaelcobre.ui.components.sharedTag
import com.elcobre.lavanderiaelcobre.ui.components.tipoIcon
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.GlassLevel

/** Ancho a partir del cual el detalle se divide en dos columnas (estado | acciones e historial). */
private val AnchoDosColumnas = 720.dp

/** Alto reservado arriba para la barra con el botón de volver. */
private val AltoBarraSuperior = 56.dp

/** Alto reservado abajo para la barra de acción flotante. */
private val AltoBarraAccion = 92.dp

/**
 * Detalle de un pedido. De borde a borde con dos capas de vidrio flotando encima: la
 * barra superior (volver + comanda al colapsar) y la barra de acción inferior (avanzar o
 * resolver). En pantallas o paneles anchos el contenido se divide en dos columnas.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetalleScreen(
    pedido: Pedido,
    onVolver: () -> Unit,
    onAvanzar: (comentario: String) -> Unit,
    onComentar: (texto: String) -> Unit,
    onAlerta: (tipo: TipoAlerta, comentario: String) -> Unit,
    onResolver: () -> Unit,
    operacionPendiente: Boolean = false,
    errorOperacion: String? = null,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var comentario by remember { mutableStateOf("") }
    var alertaActiva by remember { mutableStateOf<TipoAlerta?>(null) }
    val haptic = LocalHapticFeedback.current

    AutoDescartarAlerta(alertaActiva) { alertaActiva = null }

    // Protege avanzar/resolver/generar alerta de un doble-tap (frecuente con
    // guantes en planta): se deshabilita al primer tap y se reactiva recién
    // cuando el pedido recompuesto refleja el cambio real.
    var accionEnCurso by remember { mutableStateOf(false) }
    LaunchedEffect(pedido.etapaActual, pedido.estaBloqueado, pedido.historial.size, operacionPendiente, errorOperacion) {
        accionEnCurso = false
    }

    val scroll = rememberScrollState()
    val colapsado by rememberTitleCollapsed(scroll)
    val gutter = CobreTheme.window.gutter

    GlassStack(
        modifier = Modifier.fillMaxSize(),
        background = { fuente ->
            BoxWithConstraints(modifier = fuente) {
                val dosColumnas = maxWidth >= AnchoDosColumnas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(
                            rememberShellContentPadding(
                                PaddingValues(start = gutter, end = gutter, top = AltoBarraSuperior, bottom = AltoBarraAccion),
                            ),
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Encabezado(pedido = pedido, sharedScope = sharedScope, animatedScope = animatedScope)
                    val acciones: @Composable ColumnScope.() -> Unit = {
                        AccionesCard(
                            pedido = pedido,
                            comentario = comentario,
                            onComentarioChange = { comentario = it },
                            onComentar = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onComentar(comentario)
                                comentario = ""
                            },
                            alertaHabilitada = !pedido.estaBloqueado && !accionEnCurso && !operacionPendiente,
                            onAlerta = { tipo ->
                                accionEnCurso = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAlerta(tipo, comentario)
                                comentario = ""
                                alertaActiva = tipo
                            },
                        )
                        HistorialCard(pedido = pedido)
                    }
                    errorOperacion?.let { MensajeError(it) }
                    if (dosColumnas) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                DetalleInfo(pedido)
                                DatosFirebase(pedido)
                                ProcesoCard(pedido)
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                acciones()
                            }
                        }
                    } else {
                        DetalleInfo(pedido)
                        DatosFirebase(pedido)
                        ProcesoCard(pedido)
                        acciones()
                    }
                }
            }
        },
        overlay = {
            CapasDeVidrio(
                pedido = pedido,
                colapsado = colapsado,
                alertaActiva = alertaActiva,
                onCerrarAlerta = { alertaActiva = null },
                onVolver = onVolver,
                accionHabilitada = !accionEnCurso && !operacionPendiente,
                onAvanzar = {
                    accionEnCurso = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAvanzar(comentario)
                    comentario = ""
                },
                onResolver = {
                    accionEnCurso = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onResolver()
                    comentario = ""
                    alertaActiva = null
                },
            )
        },
    )
}

/** Barra superior (volver + comanda al colapsar), aviso de alerta y barra de acción: la capa de vidrio del detalle. */
@Composable
private fun BoxScope.CapasDeVidrio(
    pedido: Pedido,
    colapsado: Boolean,
    alertaActiva: TipoAlerta?,
    onCerrarAlerta: () -> Unit,
    onVolver: () -> Unit,
    accionHabilitada: Boolean,
    onAvanzar: () -> Unit,
    onResolver: () -> Unit,
) {
    Column(modifier = Modifier.align(Alignment.TopCenter)) {
        CompactTitleBar(
            title = pedido.comanda,
            visible = colapsado,
            navigation = {
                GlassIconButton(icon = CobreIcons.ArrowBack, contentDescription = "Volver", onClick = onVolver, size = 44.dp)
            },
        )
        AlertaBanner(
            alerta = alertaActiva,
            onCerrar = onCerrarAlerta,
            modifier = Modifier.padding(horizontal = CobreTheme.window.gutter, vertical = 8.dp),
        )
    }
    BarraAccion(
        pedido = pedido,
        habilitado = accionHabilitada,
        onAvanzar = onAvanzar,
        onResolver = onResolver,
        modifier = Modifier.align(Alignment.BottomCenter),
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Encabezado(
    pedido: Pedido,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val colors = CobreTheme.colors
    val etapa = etapaColors(pedido.etapaActual)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        // Elemento compartido que "vuela" desde la tarjeta del Dashboard.
        Box(
            modifier = Modifier
                .sharedTag("icono-${pedido.comanda}", sharedScope, animatedScope)
                .size(52.dp)
                .clip(CobreShapes.lg)
                .background(etapa.container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(etapaIcon(pedido.etapaActual), contentDescription = null, tint = etapa.accent, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(pedido.comanda, style = CobreTheme.text.largeTitle, color = colors.textPrimary)
            Text(pedido.cliente, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetalleInfo(pedido: Pedido) {
    val colors = CobreTheme.colors
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)
    ContentCard(modifier = Modifier.fillMaxWidth()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(pedido.etapaActual.corto, etapa.container, etapa.content, icon = etapaIcon(pedido.etapaActual))
            StatusChip(
                if (pedido.estadoComandaDb == null) "Prioridad ${pedido.prioridad.displayName}" else "Prioridad no definida",
                prioridad.container,
                prioridad.content,
            )
            StatusChip(
                if (pedido.estadoComandaDb == null) pedido.tipo.displayName else "Comanda registrada",
                colors.surfaceMuted,
                colors.textSecondary,
                icon = tipoIcon(pedido.tipo),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(pedido.tipoServicio, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            DatoColumna("Piezas", "${pedido.piezas}")
            DatoColumna("Plazo de entrega", pedido.plazoEntrega)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Etapa ${pedido.indiceEtapa + 1} de ${pedido.totalEtapas} · ${pedido.etapaActual.displayName}".uppercase(),
            style = CobreTheme.text.labelCaps,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(8.dp))
        StageProgress(
            flujo = pedido.flujo,
            etapaActual = pedido.etapaActual,
            accent = etapa.accent,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DatoColumna(titulo: String, valor: String) {
    val colors = CobreTheme.colors
    Column {
        Text(titulo.uppercase(), style = CobreTheme.text.labelCaps, color = colors.textSecondary)
        Spacer(Modifier.height(2.dp))
        Text(valor, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
    }
}

@Composable
private fun ProcesoCard(pedido: Pedido) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Proceso productivo", modifier = Modifier.padding(start = 4.dp))
        ContentCard(modifier = Modifier.fillMaxWidth()) {
            EtapasTimeline(flujo = pedido.flujo, indiceActual = pedido.indiceEtapa)
        }
    }
}

@Composable
private fun AccionesCard(
    pedido: Pedido,
    comentario: String,
    onComentarioChange: (String) -> Unit,
    onComentar: () -> Unit,
    alertaHabilitada: Boolean,
    onAlerta: (TipoAlerta) -> Unit,
) {
    val colors = CobreTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Actualizar estado", modifier = Modifier.padding(start = 4.dp))
        ContentCard(modifier = Modifier.fillMaxWidth()) {
            val alerta = pedido.alertaActiva
            if (alerta != null) {
                val color = alertaColor(alerta)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CobreShapes.md)
                        .background(color.copy(alpha = 0.10f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(CobreIcons.Warning, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Proceso detenido por: ${alerta.displayName}. Resuelve para continuar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = color,
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            CobreTextField(
                value = comentario,
                onValueChange = onComentarioChange,
                label = "Comentario del avance (opcional)",
                singleLine = false,
                minLines = 2,
                trailingIcon = {
                    IconButton(onClick = onComentar, enabled = comentario.isNotBlank()) {
                        Icon(
                            CobreIcons.Send,
                            contentDescription = "Agregar comentario",
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )
            Spacer(Modifier.height(20.dp))
            Text("Generar alerta", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoAlerta.entries.forEach { tipo ->
                    BotonAlerta(
                        tipo = tipo,
                        onClick = { onAlerta(tipo) },
                        modifier = Modifier.weight(1f),
                        enabled = alertaHabilitada,
                    )
                }
            }
        }
    }
}

/**
 * Barra de acción flotante de vidrio con la acción principal en cobre: avanzar de
 * etapa, o resolver la incidencia si el pedido está bloqueado.
 */
@Composable
private fun BarraAccion(
    pedido: Pedido,
    habilitado: Boolean,
    onAvanzar: () -> Unit,
    onResolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val siguiente = pedido.siguienteEtapa
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        GlassSurface(
            modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
            shape = CobreShapes.full,
            floating = true,
            contentPadding = PaddingValues(6.dp),
        ) {
            if (pedido.estaBloqueado) {
                CobreButton(
                    text = "Resolver incidencia",
                    onClick = onResolver,
                    icon = CobreIcons.CheckCircle,
                    enabled = habilitado,
                    fillWidth = true,
                )
            } else {
                CobreButton(
                    text = when {
                        siguiente != null -> "Avanzar a ${siguiente.corto}"
                        pedido.puedeCompletar -> "Marcar entregada"
                        else -> "Proceso completado"
                    },
                    onClick = onAvanzar,
                    icon = if (pedido.puedeCompletar) CobreIcons.ArrowForward else CobreIcons.DoneAll,
                    enabled = pedido.puedeCompletar && habilitado,
                    fillWidth = true,
                )
            }
        }
    }
}

@Composable
private fun BotonAlerta(tipo: TipoAlerta, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val color = alertaColor(tipo)
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .pressableScale(interaction)
            .clip(CobreShapes.md)
            .background(color.copy(alpha = 0.10f))
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClickLabel = "Reportar ${tipo.displayName}",
                onClick = onClick,
            )
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(alertaIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            tipo.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** Aviso flotante de vidrio tras generar una alerta; se auto-descarta. */
@Composable
private fun AlertaBanner(
    alerta: TipoAlerta?,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = alerta != null,
        enter = slideInVertically(tween(400)) { -it } + fadeIn(tween(400)),
        exit = slideOutVertically(tween(300)) { -it } + fadeOut(tween(300)),
        modifier = modifier,
    ) {
        val tipo = alerta ?: TipoAlerta.RETRASO
        val color = alertaColor(tipo)
        val colors = CobreTheme.colors
        GlassSurface(
            modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
            level = GlassLevel.Thick,
            floating = true,
            contentPadding = PaddingValues(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CobreShapes.md).background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(alertaIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Alerta generada: ${tipo.displayName}", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Text(tipo.descripcionCorta, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
                IconButton(onClick = onCerrar) {
                    Icon(CobreIcons.Close, contentDescription = "Cerrar", tint = colors.textSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

/** Datos de la comanda que vienen de Firebase (información operativa y prendas). */
@Composable
private fun DatosFirebase(pedido: Pedido) {
    if (pedido.datosComanda.isEmpty()) return
    val colors = CobreTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Información operativa", modifier = Modifier.padding(start = 4.dp))
        ContentCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pedido.datosComanda.forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary) }
                if (pedido.prendas.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text("Prendas", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    pedido.prendas.forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary) }
                }
            }
        }
    }
}

@Composable
private fun MensajeError(mensaje: String) {
    val colors = CobreTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CobreShapes.md)
            .background(colors.danger.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(18.dp))
        Text(mensaje, style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
    }
}

/** El banner de alerta se auto-descarta tras unos segundos, con vibración al aparecer. */
@Composable
private fun AutoDescartarAlerta(alerta: TipoAlerta?, descartar: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(alerta) {
        if (alerta != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(3800)
            descartar()
        }
    }
}
