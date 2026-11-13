package com.elcobre.lavanderiaelcobre.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.EtapaConfig
import com.elcobre.lavanderiaelcobre.data.model.FilaReporte
import com.elcobre.lavanderiaelcobre.data.model.PeriodoReporte
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.data.model.limiteEtapaMin
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.EtapasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ReportesViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Reportes por periodo: comandas recibidas, prendas y facturación por cuenta y por servicio. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportesScreen(vm: ReportesViewModel, applySystemBarsInsets: Boolean = true) {
    val reporte = vm.reporte
    LargeTitleScreen(
        title = "Reportes",
        subtitle = reporte?.let { "Del ${it.desde} al ${it.hasta}" } ?: "Volumen, cuentas y servicios",
        applySystemBarsInsets = applySystemBarsInsets,
    ) {
        item(key = "periodo") {
            GlassSegmentedControl(PeriodoReporte.entries, vm.periodo, vm::elegirPeriodo, { it.etiqueta }, Modifier.readableWidth())
        }
        item(key = "estado") { EstadoCarga(vm.cargando, vm.error, Modifier.readableWidth()) }
        if (reporte == null) {
            if (!vm.cargando) item(key = "vacio") {
                EstadoVacio(CobreIcons.BarChart, "Sin datos para el periodo.", modifier = Modifier.readableWidth())
            }
            return@LargeTitleScreen
        }
        item(key = "kpis") {
            FlowRow(
                modifier = Modifier.readableWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KpiTile(reporte.totalComandas.toString(), "Comandas recibidas", Tono.MARCA)
                KpiTile(reporte.cerradas.toString(), "Terminadas o entregadas", Tono.EXITO)
                KpiTile(reporte.totalPrendas.toString(), "Prendas", Tono.INFO)
                KpiTile(clp(reporte.totalMonto), "Facturación", Tono.MARCA)
            }
        }
        item(key = "t-cuentas") { TituloGrupo("Por cliente y empresa", Modifier.readableWidth()) }
        item(key = "cuentas") { TablaReporte(reporte.porCuenta, Modifier.readableWidth()) }
        item(key = "t-servicios") { TituloGrupo("Por servicio", Modifier.readableWidth()) }
        item(key = "servicios") { TablaReporte(reporte.porServicio, Modifier.readableWidth()) }
    }
}

/** Filas con barra proporcional al monto: se lee de un vistazo en tablet y en teléfono. */
@Composable
private fun TablaReporte(filas: List<FilaReporte>, modifier: Modifier) {
    val colors = CobreTheme.colors
    val maximo = filas.maxOfOrNull { it.monto }?.takeIf { it > 0 } ?: 1.0
    ContentCard(modifier = modifier) {
        if (filas.isEmpty()) {
            Text("Sin movimientos en el periodo.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
        filas.take(MAX_FILAS).forEach { fila ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).semantics(mergeDescendants = true) {
                    contentDescription = "${fila.etiqueta}: ${fila.comandas} comandas, ${fila.prendas} prendas, ${clp(fila.monto)}"
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        fila.etiqueta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Text(clp(fila.monto), style = MaterialTheme.typography.labelLarge, color = colors.textPrimary)
                }
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CobreShapes.full).background(colors.surfaceMuted)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((fila.monto / maximo).toFloat().coerceIn(0.02f, 1f))
                            .fillMaxHeight()
                            .clip(CobreShapes.full)
                            .background(colors.accent),
                    )
                }
                Text(
                    "${fila.comandas} comandas · ${fila.prendas} prendas",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

private const val MAX_FILAS = 25

/** Configuración de las etapas del flujo: nombre, descripción y tiempo estimado. */
@Composable
fun EtapasScreen(vm: EtapasViewModel, applySystemBarsInsets: Boolean = true) {
    var editando by remember { mutableStateOf<EtapaConfig?>(null) }
    LargeTitleScreen(
        title = "Etapas del flujo",
        subtitle = "Los cambios se aplican a las comandas nuevas.",
        applySystemBarsInsets = applySystemBarsInsets,
    ) {
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        vm.etapas.forEach { etapa ->
            item(key = etapa.id) {
                val colors = CobreTheme.colors
                ContentCard(modifier = Modifier.readableWidth(), onClick = { editando = etapa }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${etapa.orden}. ${etapa.nombre}", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            etapa.descripcion?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                        }
                        val minutos = limiteEtapaMin(etapa.orden, etapa.tiempoEstimadoMin)
                        Text(
                            minutos?.let { "$it min" } ?: "Sin límite",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.accentText,
                        )
                    }
                }
            }
        }
    }
    editando?.let { etapa -> EditarEtapaSheet(vm, etapa, onCerrar = { editando = null }) }
}

@Composable
private fun EditarEtapaSheet(vm: EtapasViewModel, etapa: EtapaConfig, onCerrar: () -> Unit) {
    var nombre by remember { mutableStateOf(etapa.nombre) }
    var descripcion by remember { mutableStateOf(etapa.descripcion.orEmpty()) }
    var minutos by remember { mutableStateOf(etapa.tiempoEstimadoMin?.toString().orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Etapa ${etapa.orden}",
        subtitle = "Tiempo estimado para avisar atrasos.",
    ) {
        CobreTextField(nombre, { if (it.length <= 40) nombre = it }, label = "Nombre")
        CobreTextField(
            descripcion,
            { if (it.length <= 200) descripcion = it },
            label = "Descripción (opcional)",
            singleLine = false,
            minLines = 2,
        )
        CobreTextField(
            minutos,
            { minutos = it.filter(Char::isDigit).take(5) },
            label = "Tiempo estimado (minutos)",
            placeholder = limiteEtapaMin(etapa.orden, null)?.toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        ErrorHoja(error)
        CobreButton(
            text = "Guardar etapa",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                vm.guardar(etapa.copy(nombre = nombre, descripcion = descripcion, tiempoEstimadoMin = minutos.toIntOrNull())) { p ->
                    if (p == null) onCerrar() else error = p
                }
            },
        )
    }
}
