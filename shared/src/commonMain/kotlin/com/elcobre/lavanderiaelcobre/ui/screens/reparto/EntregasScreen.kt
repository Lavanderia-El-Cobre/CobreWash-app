package com.elcobre.lavanderiaelcobre.ui.screens.reparto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ComandaEntrega
import com.elcobre.lavanderiaelcobre.data.model.DETALLE_ENTREGA_MAXIMO
import com.elcobre.lavanderiaelcobre.data.model.DETALLE_PROBLEMA_MAXIMO
import com.elcobre.lavanderiaelcobre.data.model.MotivosProblemaEntrega
import com.elcobre.lavanderiaelcobre.data.model.motivoVisibleEntrega
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.FilaDato
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.ProtegerCapturas
import com.elcobre.lavanderiaelcobre.ui.components.SelectorConBusqueda
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.EntregasViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.components.rememberAccionesPendientes

private sealed interface AccionEntrega {
    data class Entregar(val comanda: ComandaEntrega) : AccionEntrega
    data class Problema(val comanda: ComandaEntrega) : AccionEntrega
}

/**
 * Entregas del repartidor, como `/intranet/entregas` de la web: comandas listas para
 * entregar con los datos de contacto del cliente, marcar la entrega con un detalle
 * opcional y reportar un problema (queda en incidencias sin cerrar la comanda).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EntregasScreen(vm: EntregasViewModel, applySystemBarsInsets: Boolean = true) {
    ProtegerCapturas()
    var accion by remember { mutableStateOf<AccionEntrega?>(null) }
    val sinEnviar = rememberAccionesPendientes().map { it.entidad }.toSet()
    LargeTitleScreen(
        title = "Entregas",
        subtitle = "Comandas listas para entregar al cliente",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(CobreIcons.Refresh, "Actualizar entregas", { vm.refrescar() }, size = 44.dp, enabled = !vm.cargando)
        },
    ) {
        item(key = "kpis") {
            FlowRow(
                modifier = Modifier.readableWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KpiTile(vm.datos.comandas.size.toString(), "Por entregar", Tono.MARCA)
                KpiTile(vm.conProblemas.toString(), "Con problemas", Tono.PELIGRO)
                KpiTile(vm.datos.misEntregas.size.toString(), "Mis entregas", Tono.EXITO)
            }
        }
        item(key = "buscar") {
            GlassSearchField(vm.busqueda, vm::buscar, "Buscar por número, cliente o dirección", Modifier.readableWidth())
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        if (vm.comandas.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(
                    CobreIcons.LocalShipping,
                    if (vm.busqueda.isBlank()) "No hay comandas listas para entregar." else "Ninguna comanda coincide con la búsqueda.",
                    modifier = Modifier.readableWidth(),
                )
            }
        }
        items(vm.comandas, key = { it.id }) { comanda ->
            ComandaEntregaCard(
                comanda,
                onEntregar = { accion = AccionEntrega.Entregar(comanda) },
                onProblema = { accion = AccionEntrega.Problema(comanda) },
                enabled = !vm.guardando,
                sinEnviar = comanda.id in sinEnviar,
                modifier = Modifier.readableWidth(),
            )
        }
        if (vm.datos.misEntregas.isNotEmpty()) {
            item(key = "t-mis-entregas") { TituloGrupo("Mis últimas entregas", Modifier.readableWidth()) }
            items(vm.datos.misEntregas, key = { "e-${it.id}" }) { entrega ->
                ContentCard(modifier = Modifier.readableWidth()) {
                    FilaDato(entrega.numero, entrega.cliente)
                    FilaDato("Entregada", entrega.fecha)
                    FilaDato("Detalle", entrega.detalle)
                }
            }
        }
    }
    when (val a = accion) {
        is AccionEntrega.Entregar -> EntregarSheet(vm, a.comanda) { accion = null }
        is AccionEntrega.Problema -> ProblemaSheet(vm, a.comanda) { accion = null }
        null -> Unit
    }
}

@Composable
private fun ComandaEntregaCard(
    comanda: ComandaEntrega,
    onEntregar: () -> Unit,
    onProblema: () -> Unit,
    enabled: Boolean,
    sinEnviar: Boolean,
    modifier: Modifier,
) {
    val colors = CobreTheme.colors
    val abiertos = comanda.problemasAbiertos
    ContentCard(modifier = modifier, stripe = if (abiertos.isNotEmpty()) colors.danger else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    comanda.numero,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${comanda.cliente} · ${comanda.tipoCliente.etiqueta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            if (sinEnviar) ChipTono("Sin enviar", Tono.INFO, icono = CobreIcons.Schedule)
            if (abiertos.isNotEmpty()) ChipTono("${abiertos.size} problema(s)", Tono.PELIGRO, icono = CobreIcons.Warning)
        }
        Spacer(Modifier.height(8.dp))
        FilaDato("Dirección", comanda.direccion)
        FilaDato("Teléfono", comanda.telefono)
        FilaDato("Prendas", comanda.prendas)
        FilaDato("Entrega estimada", comanda.fechaEntregaEstimada)
        FilaDato("Empresa", comanda.empresa)
        FilaDato("Proyecto", comanda.proyecto)
        FilaDato("Observaciones", comanda.observaciones)
        abiertos.forEach { p ->
            FilaDato(motivoVisibleEntrega(p.motivo), listOfNotNull(p.descripcion, "${p.reportadaPor} · ${p.fecha}").joinToString(" · "))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CobreButton(
                text = "Entregar",
                onClick = onEntregar,
                icon = CobreIcons.DoneAll,
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            CobreButton(
                text = "Problema",
                onClick = onProblema,
                icon = CobreIcons.Warning,
                variant = ButtonVariant.Tonal,
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EntregarSheet(vm: EntregasViewModel, comanda: ComandaEntrega, onCerrar: () -> Unit) {
    var detalle by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Entregar ${comanda.numero}",
        subtitle = "Se registrará la entrega con tu nombre y la hora actual.",
    ) {
        CobreTextField(
            detalle,
            { if (it.length <= DETALLE_ENTREGA_MAXIMO) detalle = it },
            label = "Detalle (opcional)",
            placeholder = "Ej. Recibió la conserjería",
            singleLine = false,
            minLines = 2,
            supportingText = "${detalle.length}/$DETALLE_ENTREGA_MAXIMO",
        )
        ErrorHoja(error)
        CobreButton(
            text = "Confirmar entrega",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = { vm.entregar(comanda, detalle) { p -> if (p == null) onCerrar() else error = p } },
        )
    }
}

@Composable
private fun ProblemaSheet(vm: EntregasViewModel, comanda: ComandaEntrega, onCerrar: () -> Unit) {
    var motivo by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Problema con ${comanda.numero}",
        subtitle = "La comanda sigue pendiente de entrega y administración lo verá en incidencias.",
    ) {
        SelectorConBusqueda(
            etiqueta = "Motivo",
            valorActual = motivo,
            opciones = MotivosProblemaEntrega,
            texto = { it },
            onSeleccionar = {
                motivo = it
                error = null
            },
            placeholder = "Elegir motivo",
        )
        CobreTextField(
            detalle,
            { if (it.length <= DETALLE_PROBLEMA_MAXIMO) detalle = it },
            label = if (motivo == "Otro") "Describe el problema" else "Detalle (opcional)",
            singleLine = false,
            minLines = 2,
            supportingText = "${detalle.length}/$DETALLE_PROBLEMA_MAXIMO",
        )
        ErrorHoja(error)
        CobreButton(
            text = "Reportar problema",
            icon = CobreIcons.Warning,
            variant = ButtonVariant.Danger,
            loading = vm.guardando,
            fillWidth = true,
            onClick = { vm.reportarProblema(comanda, motivo, detalle) { p -> if (p == null) onCerrar() else error = p } },
        )
    }
}
