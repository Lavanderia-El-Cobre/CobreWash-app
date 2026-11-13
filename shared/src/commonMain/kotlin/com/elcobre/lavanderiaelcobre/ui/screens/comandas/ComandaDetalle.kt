package com.elcobre.lavanderiaelcobre.ui.screens.comandas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoEtapaProduccion
import com.elcobre.lavanderiaelcobre.data.model.EtapaComanda
import com.elcobre.lavanderiaelcobre.data.model.EventoComanda
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.FilaDato
import com.elcobre.lavanderiaelcobre.ui.components.GlassAlert
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.tonoEstadoComanda
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Ficha completa de una comanda con sus acciones según el estado, como `ComandaDetalle.tsx`. */
@Composable
internal fun ComandaDetalleContenido(comanda: ComandaGestion, vm: ComandasViewModel) {
    var anulando by remember(comanda.id) { mutableStateOf(false) }
    var entregando by remember(comanda.id) { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            ChipTono(comanda.estado.etiqueta, tonoEstadoComanda(comanda.estado))
            if (comanda.incidenciasAbiertas > 0) ChipTono("${comanda.incidenciasAbiertas} incidencia(s) abierta(s)", Tono.PELIGRO)
            if (vm.cargandoDetalle) Text(
                "Actualizando…",
                style = MaterialTheme.typography.labelSmall,
                color = CobreTheme.colors.textSecondary,
            )
        }
        AccionesComanda(comanda, vm, onAnular = { anulando = true }, onEntregar = { entregando = true })
        Bloque("Cliente") {
            FilaDato("Nombre", comanda.cliente.nombre)
            FilaDato("Tipo", comanda.cliente.tipo.etiqueta)
            FilaDato("RUT", comanda.cliente.rut)
            FilaDato("Teléfono", comanda.cliente.telefono)
            FilaDato("Correo", comanda.cliente.email)
            FilaDato("Dirección", comanda.cliente.direccion)
            FilaDato("Empresa", comanda.empresa)
            FilaDato("Proyecto", comanda.proyecto)
        }
        Bloque("Fechas") {
            FilaDato("Recepción", comanda.fechaRecepcion)
            FilaDato("Entrega estimada", comanda.fechaEntregaEstimada)
            FilaDato("Entregada", comanda.fechaEntregaReal)
        }
        Bloque("Prendas") { LineasDetalle(comanda) }
        comanda.observaciones?.takeIf { it.isNotBlank() }?.let { Bloque("Observaciones") { Texto(it) } }
        comanda.motivoAnulacion?.takeIf { it.isNotBlank() }?.let { Bloque("Motivo de anulación") { Texto(it) } }
        if (comanda.etapas.isNotEmpty()) Bloque("Producción") { comanda.etapas.forEach { EtapaFila(it) } }
        if (comanda.historial.isNotEmpty()) Bloque("Historial") { comanda.historial.forEach { EventoFila(it) } }
    }
    if (anulando) {
        AnularComandaSheet(comanda = comanda, vm = vm, onCerrar = { anulando = false })
    }
    if (entregando) {
        GlassAlert(
            title = "¿Entregar ${comanda.numero}?",
            message = "Se registrará la entrega al cliente con la fecha de hoy y quedará en el historial.",
            confirmText = "Entregar",
            onConfirm = {
                entregando = false
                vm.entregar(comanda)
            },
            onDismiss = { entregando = false },
        )
    }
}

@Composable
private fun AccionesComanda(comanda: ComandaGestion, vm: ComandasViewModel, onAnular: () -> Unit, onEntregar: () -> Unit) {
    val hayAcciones = comanda.puedeEditar || comanda.puedeEntregar || comanda.puedeAnular || comanda.necesitaFlujo
    if (!hayAcciones) return
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        if (comanda.puedeEntregar) {
            CobreButton(
                text = "Entregar",
                onClick = onEntregar,
                icon = CobreIcons.DoneAll,
                loading = vm.guardando,
                modifier = Modifier.weight(1f),
            )
        }
        if (comanda.necesitaFlujo) {
            CobreButton(
                text = "Asociar flujo",
                onClick = { vm.asociarFlujo(comanda) },
                icon = CobreIcons.Timeline,
                variant = ButtonVariant.Tonal,
                enabled = !vm.guardando,
                modifier = Modifier.weight(1f),
            )
        }
        if (comanda.puedeEditar) {
            CobreButton(
                text = "Editar",
                onClick = { vm.abrirEdicion(comanda) },
                icon = CobreIcons.Edit,
                variant = ButtonVariant.Tonal,
                enabled = !vm.guardando,
                modifier = Modifier.weight(1f),
            )
        }
        if (comanda.puedeAnular) {
            CobreButton(
                text = "Anular",
                onClick = onAnular,
                icon = CobreIcons.Block,
                variant = ButtonVariant.Danger,
                enabled = !vm.guardando,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Bloque(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SectionLabel(titulo)
        contenido()
    }
}

@Composable
private fun Texto(texto: String) {
    Text(texto, style = MaterialTheme.typography.bodyMedium, color = CobreTheme.colors.textPrimary)
}

@Composable
private fun LineasDetalle(comanda: ComandaGestion) {
    val colors = CobreTheme.colors
    comanda.lineas.forEach { linea ->
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${linea.cantidad} × ${linea.prenda}", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                Text(
                    listOf(linea.servicio, linea.detalle).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            Text(clp(linea.subtotal), style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text("Total", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, modifier = Modifier.weight(1f))
        Text(clp(comanda.valorTotal), style = MaterialTheme.typography.titleSmall, color = colors.accentText, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun EtapaFila(etapa: EtapaComanda) {
    val colors = CobreTheme.colors
    val completada = etapa.estado == EstadoEtapaProduccion.COMPLETADA
    val enCurso = etapa.estado == EstadoEtapaProduccion.EN_PROCESO
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CobreShapes.full)
                .background(if (completada) colors.accentFill else if (enCurso) colors.accentContainer else colors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            if (completada) {
                Icon(CobreIcons.Check, contentDescription = null, tint = colors.onAccentFill, modifier = Modifier.size(16.dp))
            } else {
                Text(etapa.orden.toString(), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(etapa.nombre, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
            val quien = etapa.responsable ?: etapa.asignadoA?.let { "Asignada a $it" }
            val cuando = etapa.completado ?: etapa.inicio
            listOfNotNull(quien, cuando).joinToString(" · ").takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
        }
        Text(
            etapa.estado.etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = if (enCurso) colors.accentText else colors.textSecondary,
        )
    }
}

@Composable
private fun EventoFila(evento: EventoComanda) {
    val colors = CobreTheme.colors
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row {
            Text(evento.titulo, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, modifier = Modifier.weight(1f))
            Text(evento.fecha, style = CobreTheme.text.label, color = colors.textSecondary)
        }
        val detalle = listOf(evento.detalle, evento.autor).filter { it.isNotBlank() }.joinToString(" · ")
        if (detalle.isNotBlank()) Text(detalle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
    }
}

/** Anulación con motivo obligatorio para el historial, como el modal de la web. */
@Composable
private fun AnularComandaSheet(comanda: ComandaGestion, vm: ComandasViewModel, onCerrar: () -> Unit) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Anular ${comanda.numero}",
        subtitle = "La comanda queda cerrada y no se puede reabrir.",
    ) {
        CobreTextField(
            value = motivo,
            onValueChange = { if (it.length <= 300) motivo = it },
            label = "Motivo de la anulación",
            placeholder = "Ej. El cliente retiró las prendas sin lavar",
            singleLine = false,
            minLines = 3,
            supportingText = "${motivo.length}/300",
        )
        error?.let {
            Text(
                it,
                color = CobreTheme.colors.dangerText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        CobreButton(
            text = "Anular comanda",
            onClick = {
                if (motivo.isBlank()) {
                    error = "Escribe el motivo de la anulación."
                } else {
                    vm.anular(comanda, motivo) { problema ->
                        if (problema == null) onCerrar() else error = "$problema Verifica que la comanda siga activa."
                    }
                }
            },
            variant = ButtonVariant.Danger,
            icon = CobreIcons.Block,
            loading = vm.guardando,
            fillWidth = true,
        )
    }
}
