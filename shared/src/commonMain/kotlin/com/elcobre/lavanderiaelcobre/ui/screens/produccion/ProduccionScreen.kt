package com.elcobre.lavanderiaelcobre.ui.screens.produccion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.MotivosIncidencia
import com.elcobre.lavanderiaelcobre.data.model.PersonaRef
import com.elcobre.lavanderiaelcobre.data.model.minutosAtraso
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.ChipsFiltro
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.SelectorConBusqueda
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.components.tonoEstadoComanda
import com.elcobre.lavanderiaelcobre.ui.gestion.ProduccionViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.comandas.EtapaFila
import com.elcobre.lavanderiaelcobre.ui.screens.comandas.ProgresoEtapas
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

@Composable
fun ProduccionScreen(
    vm: ProduccionViewModel,
    permisos: PermisosProduccion,
    ahoraMs: Long,
    titulo: String = "Producción",
    applySystemBarsInsets: Boolean = true,
) {
    var expandida by remember { mutableStateOf<String?>(null) }
    var reasignando by remember { mutableStateOf<ComandaGestion?>(null) }
    var reportando by remember { mutableStateOf<ComandaGestion?>(null) }
    LargeTitleScreen(
        title = titulo,
        subtitle = "${vm.total} comandas activas en planta",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(
                icon = CobreIcons.Refresh,
                contentDescription = "Actualizar producción",
                onClick = { vm.refrescar() },
                size = 44.dp,
                enabled = !vm.cargando,
            )
        },
    ) {
        item(key = "buscar") {
            GlassSearchField(vm.busqueda, vm::buscar, "Buscar por número o cliente", modifier = Modifier.readableWidth())
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        if (vm.comandas.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(CobreIcons.Timeline, "No hay comandas en producción.", modifier = Modifier.readableWidth())
            }
        }
        items(vm.comandas, key = { it.id }) { comanda ->
            ProduccionCard(
                comanda = comanda,
                atraso = minutosAtraso(comanda, ahoraMs),
                expandida = expandida == comanda.id,
                permisos = permisos,
                guardando = vm.guardando,
                onAlternar = { expandida = if (expandida == comanda.id) null else comanda.id },
                onCompletar = { vm.completarEtapa(comanda) },
                onAsociar = { vm.asociarFlujo(comanda) },
                onReasignar = { reasignando = comanda },
                onReportar = { reportando = comanda },
                modifier = Modifier.readableWidth(),
            )
        }
        if (vm.hayMas) {
            item(key = "mas") {
                CobreButton(
                    text = "Cargar más",
                    onClick = { vm.refrescar(mas = true) },
                    variant = ButtonVariant.Tonal,
                    fillWidth = true,
                    modifier = Modifier.readableWidth(),
                )
            }
        }
    }
    reasignando?.let { comanda -> ReasignarSheet(vm, comanda, onCerrar = { reasignando = null }) }
    reportando?.let { comanda -> IncidenciaSheet(vm, comanda, onCerrar = { reportando = null }) }
}

/** Texto del atraso: "15 min" o "2 h 5 min". */
internal fun textoAtraso(minutos: Int): String = if (minutos < 60) "$minutos min" else "${minutos / 60} h ${minutos % 60} min"

@Composable
private fun ProduccionCard(
    comanda: ComandaGestion,
    atraso: Int,
    expandida: Boolean,
    permisos: PermisosProduccion,
    guardando: Boolean,
    onAlternar: () -> Unit,
    onCompletar: () -> Unit,
    onAsociar: () -> Unit,
    onReasignar: () -> Unit,
    onReportar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, onClick = onAlternar, stripe = if (atraso > 0) colors.danger else null) {
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ChipTono(comanda.estado.etiqueta, tonoEstadoComanda(comanda.estado))
                if (atraso > 0) ChipTono("Atraso ${textoAtraso(atraso)}", Tono.PELIGRO, icono = CobreIcons.Schedule)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            comanda.lineas.joinToString(" · ") { "${it.cantidad} ${it.prenda.lowercase()}" }.ifEmpty { "Sin prendas registradas" },
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = if (expandida) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        if (comanda.etapas.isEmpty()) {
            Text("Sin flujo de producción asociado.", style = MaterialTheme.typography.labelMedium, color = colors.warningText)
        } else {
            ProgresoEtapas(comanda)
        }
        AnimatedVisibility(visible = expandida) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(Modifier.height(4.dp))
                comanda.etapas.forEach { EtapaFila(it) }
                AccionesProduccion(comanda, permisos, guardando, onCompletar, onAsociar, onReasignar, onReportar)
            }
        }
    }
}

@Composable
private fun AccionesProduccion(
    comanda: ComandaGestion,
    permisos: PermisosProduccion,
    guardando: Boolean,
    onCompletar: () -> Unit,
    onAsociar: () -> Unit,
    onReasignar: () -> Unit,
    onReportar: () -> Unit,
) {
    val etapa = comanda.etapaActual
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (permisos.completar && etapa != null) {
            CobreButton(
                text = "Completar ${etapa.nombre}",
                onClick = onCompletar,
                icon = CobreIcons.Check,
                loading = guardando,
                fillWidth = true,
            )
        }
        if (permisos.asociarFlujo && comanda.etapas.isEmpty()) {
            CobreButton(text = "Asociar flujo", onClick = onAsociar, icon = CobreIcons.Timeline, enabled = !guardando, fillWidth = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            if (permisos.reasignar && etapa != null) {
                CobreButton(
                    text = "Reasignar",
                    onClick = onReasignar,
                    icon = CobreIcons.Group,
                    variant = ButtonVariant.Tonal,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f),
                )
            }
            if (permisos.reportarIncidencia) {
                CobreButton(
                    text = "Incidencia",
                    onClick = onReportar,
                    icon = CobreIcons.Warning,
                    variant = ButtonVariant.Danger,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReasignarSheet(vm: ProduccionViewModel, comanda: ComandaGestion, onCerrar: () -> Unit) {
    val etapa = comanda.etapaActual ?: return
    var operario by remember { mutableStateOf<PersonaRef?>(vm.operarios.firstOrNull { it.id == etapa.asignadoAId }) }
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Asignar operario",
        subtitle = "${comanda.numero} · ${etapa.nombre}",
    ) {
        SelectorConBusqueda(
            etiqueta = "Operario responsable",
            valorActual = operario?.nombre.orEmpty(),
            opciones = vm.operarios,
            texto = { it.nombre },
            onSeleccionar = { operario = it },
        )
        CobreTextField(
            value = motivo,
            onValueChange = { if (it.length <= 200) motivo = it },
            label = "Motivo (opcional)",
            singleLine = false,
            minLines = 2,
        )
        ErrorHoja(error)
        CobreButton(
            text = "Guardar asignación",
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                val elegido = operario
                if (elegido == null) {
                    error = "Elige un operario."
                } else {
                    vm.reasignar(comanda, etapa.id, elegido, motivo) { problema -> if (problema == null) onCerrar() else error = problema }
                }
            },
        )
    }
}

@Composable
private fun IncidenciaSheet(vm: ProduccionViewModel, comanda: ComandaGestion, onCerrar: () -> Unit) {
    var motivo by remember { mutableStateOf(MotivosIncidencia.first()) }
    var descripcion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Reportar incidencia",
        subtitle = "${comanda.numero} · ${comanda.cliente.nombre}",
    ) {
        ChipsFiltro(MotivosIncidencia, motivo, { it }, { motivo = it })
        CobreTextField(
            value = descripcion,
            onValueChange = { if (it.length <= 300) descripcion = it },
            label = "Descripción (opcional)",
            singleLine = false,
            minLines = 3,
        )
        ErrorHoja(error)
        CobreButton(
            text = "Registrar incidencia",
            variant = ButtonVariant.Danger,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                vm.reportarIncidencia(comanda, motivo, descripcion) { problema ->
                    if (problema == null) onCerrar() else error = "$problema Verifica que la comanda siga activa."
                }
            },
        )
    }
}

@Composable
internal fun ErrorHoja(error: String?) {
    if (error == null) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.material3.Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = CobreTheme.colors.dangerText)
        Spacer(Modifier.width(8.dp))
        Text(
            error,
            color = CobreTheme.colors.dangerText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}
