package com.elcobre.lavanderiaelcobre.ui.screens.comandas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.data.model.descripcionEtapas
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.components.rememberShellContentPadding
import com.elcobre.lavanderiaelcobre.ui.components.tonoEstadoComanda
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Comandas de recepción y administración. En tablet horizontal el detalle queda al
 * lado de la lista; en teléfono y tablet vertical se abre en una hoja de vidrio.
 */
@Composable
fun ComandasScreen(vm: ComandasViewModel, applySystemBarsInsets: Boolean = true) {
    val window = CobreTheme.window
    val detalle = vm.detalle
    if (window.useListDetail) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(0.46f).fillMaxHeight()) { ListaComandas(vm, applySystemBarsInsets) }
            PanelDetalle(vm = vm, modifier = Modifier.weight(0.54f).fillMaxHeight())
        }
    } else {
        ListaComandas(vm, applySystemBarsInsets)
        // Con el formulario abierto, la hoja de detalle espera debajo en vez de apilarse.
        if (detalle != null && vm.formulario == null) {
            GlassSheet(onDismiss = vm::cerrarDetalle, title = detalle.numero, subtitle = detalle.cliente.nombre) {
                ComandaDetalleContenido(comanda = detalle, vm = vm)
            }
        }
    }
    vm.formulario?.let { ComandaFormularioSheet(vm = vm, formulario = it) }
}

@Composable
private fun ListaComandas(vm: ComandasViewModel, applySystemBarsInsets: Boolean) {
    LargeTitleScreen(
        title = "Comandas",
        subtitle = "${vm.total} órdenes · ${vm.comandas.size} en pantalla",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(
                icon = CobreIcons.Refresh,
                contentDescription = "Actualizar comandas",
                onClick = { vm.refrescar() },
                size = 44.dp,
                enabled = !vm.cargando,
            )
            Spacer(Modifier.width(8.dp))
            CobreButton(text = "Nueva", onClick = vm::abrirNueva, icon = CobreIcons.Add)
        },
    ) {
        item(key = "buscar") {
            GlassSearchField(
                query = vm.filtro.cliente,
                onQueryChange = vm::buscar,
                placeholder = "Buscar por cliente",
                modifier = Modifier.readableWidth(),
            )
        }
        item(key = "estados") { FiltroEstados(vm, Modifier.readableWidth()) }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(cargando = vm.cargando, error = vm.error)
            }
        }
        if (vm.comandas.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(
                    icono = CobreIcons.Assignment,
                    texto = if (vm.filtro.estados.isEmpty() && vm.filtro.cliente.isBlank()) {
                        "Aún no hay comandas registradas."
                    } else {
                        "No hay comandas con estos filtros."
                    },
                    accion = "Nueva comanda",
                    onAccion = vm::abrirNueva,
                    modifier = Modifier.readableWidth(),
                )
            }
        }
        items(vm.comandas, key = { it.id }) { comanda ->
            ComandaCard(
                comanda = comanda,
                seleccionada = vm.detalle?.id == comanda.id,
                onClick = { vm.abrirDetalle(comanda.id) },
                modifier = Modifier.readableWidth(),
            )
        }
        if (vm.hayMas) {
            item(key = "mas") {
                CobreButton(
                    text = "Cargar más comandas",
                    onClick = { vm.refrescar(mas = true) },
                    variant = ButtonVariant.Tonal,
                    enabled = !vm.cargando,
                    fillWidth = true,
                    modifier = Modifier.readableWidth(),
                )
            }
        }
    }
}

/** Tarjetas de conteo por estado: tocarlas filtra (selección múltiple), como en la web. */
@Composable
private fun FiltroEstados(vm: ComandasViewModel, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
        items(EstadoComanda.entries, key = { it.name }) { estado ->
            KpiTile(
                valor = (vm.conteos[estado] ?: 0).toString(),
                etiqueta = estado.etiqueta,
                tono = tonoEstadoComanda(estado),
                seleccionado = estado in vm.filtro.estados,
                onClick = { vm.alternarEstado(estado) },
            )
        }
    }
}

@Composable
internal fun ComandaCard(comanda: ComandaGestion, seleccionada: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    ContentCard(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "Comanda ${comanda.numero}, ${comanda.cliente.nombre}, ${comanda.estado.etiqueta}"
        },
        onClick = onClick,
        stripe = if (seleccionada) colors.accent else null,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    comanda.numero,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    comanda.cliente.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChipTono(comanda.estado.etiqueta, tonoEstadoComanda(comanda.estado))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Dato("Prendas", comanda.prendas.toString())
            Dato("Total", clp(comanda.valorTotal))
            Dato("Recepción", comanda.fechaRecepcion)
        }
        if (comanda.estado != EstadoComanda.ANULADA && comanda.etapas.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            ProgresoEtapas(comanda)
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    val colors = CobreTheme.colors
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
        Text(valor, style = MaterialTheme.typography.labelLarge, color = colors.textPrimary)
    }
}

@Composable
internal fun ProgresoEtapas(comanda: ComandaGestion) {
    val colors = CobreTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
            Text(
                descripcionEtapas(comanda.etapas),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Text("${comanda.progreso}%", style = MaterialTheme.typography.labelMedium, color = colors.accentText)
        }
        LinearProgressIndicator(
            progress = { comanda.progreso / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CobreShapes.full),
            color = colors.accent,
            trackColor = colors.surfaceMuted,
            drawStopIndicator = {},
        )
    }
}

/** Detalle al costado de la lista en tablet horizontal. */
@Composable
private fun PanelDetalle(vm: ComandasViewModel, modifier: Modifier = Modifier) {
    val window = CobreTheme.window
    val detalle = vm.detalle
    val padding = rememberShellContentPadding(PaddingValues(end = window.gutter, top = 16.dp, bottom = 24.dp))
    Box(modifier = modifier.padding(padding)) {
        if (detalle == null) {
            EstadoVacio(
                icono = CobreIcons.Assignment,
                texto = "Elige una comanda para ver su detalle.",
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            ContentCard(modifier = Modifier.fillMaxHeight()) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(detalle.numero, style = MaterialTheme.typography.titleLarge, color = CobreTheme.colors.textPrimary)
                            Text(
                                detalle.cliente.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CobreTheme.colors.textSecondary,
                            )
                        }
                        GlassIconButton(
                            icon = CobreIcons.Close,
                            contentDescription = "Cerrar detalle",
                            onClick = vm::cerrarDetalle,
                            size = 40.dp,
                        )
                    }
                    ComandaDetalleContenido(comanda = detalle, vm = vm)
                }
            }
        }
    }
}
