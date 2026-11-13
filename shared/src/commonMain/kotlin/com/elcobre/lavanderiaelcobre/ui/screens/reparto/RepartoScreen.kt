package com.elcobre.lavanderiaelcobre.ui.screens.reparto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
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
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.RecordatorioPrivacidadFotos
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.RepartoViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.SelectorCondicionVehiculo
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.SelectorFotos
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private enum class VistaReparto(val etiqueta: String) { SALIDAS("Mis salidas"), VEHICULOS("Vehículos") }

private fun tonoSalida(estado: EstadoSalida): Tono = when (estado) {
    EstadoSalida.PROGRAMADA -> Tono.MARCA
    EstadoSalida.EN_SERVICIO -> Tono.INFO
    EstadoSalida.FINALIZADA -> Tono.EXITO
    EstadoSalida.CANCELADA -> Tono.NEUTRO
}

/**
 * Recorridos del repartidor: administración le programa o registra la salida; aquí
 * hace la inspección previa con fotos e inicia, y al volver registra el retorno.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepartoScreen(vm: RepartoViewModel, nombre: String, applySystemBarsInsets: Boolean = true) {
    var vista by rememberSaveable { mutableStateOf(VistaReparto.SALIDAS) }
    var inspeccionando by remember { mutableStateOf<SalidaReparto?>(null) }
    LargeTitleScreen(
        title = "Recorridos",
        subtitle = "Salidas asignadas a ${nombre.substringBefore(' ')}",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(CobreIcons.Refresh, "Actualizar recorridos", { vm.refrescar() }, size = 44.dp, enabled = !vm.cargando)
        },
    ) {
        item(key = "kpis") {
            FlowRow(
                modifier = Modifier.readableWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KpiTile(vm.enRuta.size.toString(), "En ruta", Tono.INFO)
                KpiTile(vm.programadas.size.toString(), "Programadas", Tono.MARCA)
                KpiTile(vm.bitacora.size.toString(), "Finalizadas", Tono.EXITO)
                KpiTile("${vm.kilometrosRecorridos} km", "Recorridos", Tono.NEUTRO)
            }
        }
        item(key = "vista") {
            GlassSegmentedControl(VistaReparto.entries, vista, { vista = it }, { it.etiqueta }, Modifier.readableWidth())
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        if (vista == VistaReparto.SALIDAS) listaSalidas(vm) { inspeccionando = it } else listaVehiculos(vm.vehiculos)
    }
    inspeccionando?.let { salida -> InspeccionSheet(vm, salida, onCerrar = { inspeccionando = null }) }
}

private fun LazyListScope.listaSalidas(vm: RepartoViewModel, onInspeccionar: (SalidaReparto) -> Unit) {
    if (vm.salidas.isEmpty() && !vm.cargando) {
        item(key = "vacio") {
            EstadoVacio(
                CobreIcons.LocalShipping,
                "No tienes salidas asignadas. Administración programa los recorridos desde Flota.",
                modifier = Modifier.readableWidth(),
            )
        }
    }
    grupo("En ruta", vm.enRuta, onInspeccionar)
    grupo("Programadas", vm.programadas, onInspeccionar)
    grupo("Bitácora", vm.bitacora, onInspeccionar)
}

private fun LazyListScope.grupo(titulo: String, salidas: List<SalidaReparto>, onInspeccionar: (SalidaReparto) -> Unit) {
    if (salidas.isEmpty()) return
    item(key = "t-$titulo") { TituloGrupo(titulo, Modifier.readableWidth()) }
    items(
        salidas,
        key = { it.id },
    ) { salida -> SalidaCard(salida, onInspeccionar = { onInspeccionar(salida) }, modifier = Modifier.readableWidth()) }
}

@Composable
private fun SalidaCard(salida: SalidaReparto, onInspeccionar: () -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, stripe = if (salida.estado == EstadoSalida.EN_SERVICIO) colors.accent else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    salida.patente,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(salida.vehiculo, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            ChipTono(salida.estado.etiqueta, tonoSalida(salida.estado))
        }
        Spacer(Modifier.height(8.dp))
        FilaDato("Asignada", salida.creadoEn)
        FilaDato("Salida", salida.fechaSalida)
        FilaDato("Retorno", salida.fechaRetorno)
        salida.antes?.let { FilaDato("Al salir", resumenInspeccion(it)) }
        salida.despues?.let { FilaDato("Al volver", resumenInspeccion(it)) }
        salida.kilometrosRecorridos?.let { FilaDato("Recorrido", "$it km") }
        FilaDato("Notas", salida.observaciones)
        when {
            salida.puedeIniciar -> {
                Spacer(Modifier.height(8.dp))
                CobreButton(
                    text = if (salida.antes == null) "Inspeccionar e iniciar" else "Iniciar recorrido",
                    onClick = onInspeccionar,
                    icon = CobreIcons.LocalShipping,
                    fillWidth = true,
                )
            }
            salida.puedeRetornar -> {
                Spacer(Modifier.height(8.dp))
                CobreButton(
                    text = "Registrar retorno",
                    onClick = onInspeccionar,
                    icon = CobreIcons.CheckCircle,
                    variant = ButtonVariant.Primary,
                    fillWidth = true,
                )
            }
        }
    }
}

private fun resumenInspeccion(i: InspeccionReparto): String =
    listOfNotNull("${i.kilometraje} km", i.estado.etiqueta, if (i.fotos > 0) "${i.fotos} foto(s)" else null, i.observaciones)
        .joinToString(" · ")

private fun LazyListScope.listaVehiculos(vehiculos: List<Vehiculo>) {
    if (vehiculos.isEmpty()) {
        item(key = "sin-vehiculos") {
            EstadoVacio(CobreIcons.LocalShipping, "No hay vehículos registrados.", modifier = Modifier.readableWidth())
        }
    }
    items(vehiculos, key = { it.id }) { vehiculo ->
        val colors = CobreTheme.colors
        ContentCard(modifier = Modifier.readableWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(vehiculo.patente, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Text(vehiculo.nombreCompleto, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    vehiculo.descripcion?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                }
                ChipTono(if (vehiculo.activo) "Operativo" else "En mantención", if (vehiculo.activo) Tono.EXITO else Tono.NEUTRO)
            }
        }
    }
}

/** Inspección técnica con odómetro y fotos, al iniciar o al cerrar el recorrido. */
@Composable
private fun InspeccionSheet(vm: RepartoViewModel, salida: SalidaReparto, onCerrar: () -> Unit) {
    val esSalida = salida.puedeIniciar
    val minimo = vm.kilometrajeMinimo(salida)
    val yaInspeccionada = esSalida && salida.antes != null
    var km by remember { mutableStateOf(salida.antes?.kilometraje?.takeIf { yaInspeccionada }?.toString() ?: minimo.toString()) }
    var estado by remember { mutableStateOf(salida.antes?.estado?.takeIf { yaInspeccionada } ?: EstadoCondicionVehiculo.APTO) }
    var observaciones by remember { mutableStateOf("") }
    val fotos = remember { mutableStateOf(emptyList<FotoChequeo>()) }
    var error by remember { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current
    val accent = CobreTheme.colors.accent
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = if (esSalida) "Iniciar · ${salida.patente}" else "Retorno · ${salida.patente}",
        subtitle = "${salida.vehiculo} · Odómetro mínimo: $minimo km",
    ) {
        if (yaInspeccionada) {
            Text(
                "La inspección previa ya está registrada. Solo falta confirmar el inicio.",
                style = MaterialTheme.typography.bodySmall,
                color = CobreTheme.colors.textSecondary,
            )
        } else {
            SelectorCondicionVehiculo(esEntrada = !esSalida, seleccionado = estado, onSeleccionar = { estado = it })
            CobreTextField(
                km,
                { km = it.filter(Char::isDigit).take(7) },
                label = if (esSalida) "Kilometraje de salida" else "Kilometraje de retorno",
                leadingIcon = CobreIcons.Speed,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            SelectorFotos(accent, fotos, haptic, enabled = !vm.guardando)
            RecordatorioPrivacidadFotos()
            CobreTextField(
                observaciones,
                { if (it.length <= 500) observaciones = it },
                label = "Observaciones (opcional)",
                placeholder = "Ej. Rayón leve en parachoques trasero",
                singleLine = false,
                minLines = 2,
            )
        }
        ErrorHoja(error)
        CobreButton(
            text = if (esSalida) "Confirmar salida" else "Confirmar retorno",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                val input = InspeccionInput(salida.id, km.toIntOrNull() ?: -1, estado, observaciones.trim().ifEmpty { null }, fotos.value)
                val alTerminar: (String?) -> Unit = { p -> if (p == null) onCerrar() else error = p }
                if (esSalida) vm.iniciar(salida, input, alTerminar) else vm.registrarRetorno(salida, input, alTerminar)
            },
        )
    }
}
