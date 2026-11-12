package com.elcobre.lavanderiaelcobre.ui.screens.insumos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.ConteoFisico
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.ui.InventarioState
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.QrScannerView
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.theme.CobreColors
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private enum class FiltroInsumo(val etiqueta: String) {
    TODOS("Todos"),
    ALERTA("Stock bajo"),
    CONTADOS("Verificados"),
}

/**
 * Catálogo real de Firebase con verificación de conteo físico por QR para el operario
 * (RF-IN10). Título grande, KPIs, búsqueda y filtro segmentado; el escaneo se abre en
 * una hoja inferior.
 */
@Composable
fun InsumosScreen(inventario: InventarioState, applySystemBarsInsets: Boolean = true) {
    var buscar by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInsumo.TODOS) }
    var escaner by remember { mutableStateOf(false) }
    val visibles = remember(inventario.insumos, inventario.conteos, buscar, filtro) {
        filtrarInsumos(inventario, buscar, filtro)
    }
    LargeTitleScreen(
        title = "Inventario",
        subtitle = "Stock disponible, mínimos de reposición y conteo físico por QR.",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(
                icon = CobreIcons.History,
                contentDescription = "Actualizar inventario",
                onClick = { inventario.refrescar() },
                size = 44.dp,
                enabled = !inventario.cargando,
            )
        },
    ) {
        item(key = "encabezado") {
            EncabezadoInventario(
                inventario = inventario,
                buscar = buscar,
                filtro = filtro,
                onBuscar = { buscar = it },
                onFiltro = { filtro = it },
                onEscanear = { escaner = true },
                modifier = Modifier.readableWidth(),
            )
        }
        inventario.seleccionado?.let { elegido ->
            item(key = "seleccionado") {
                DetalleInsumoSeleccionado(elegido = elegido, inventario = inventario, modifier = Modifier.readableWidth())
            }
        }
        if (visibles.isEmpty()) {
            item(key = "vacio") { EstadoVacioInsumos(inventario = inventario, buscar = buscar, modifier = Modifier.readableWidth()) }
        } else {
            item(key = "etiqueta") { SectionLabel("Insumos · ${visibles.size}", modifier = Modifier.readableWidth().padding(start = 4.dp)) }
        }
        items(visibles, key = { it.id }) { insumo ->
            InsumoCard(
                insumo = insumo,
                conteo = inventario.conteoDe(insumo.id),
                onSeleccionar = { inventario.seleccionar(insumo) },
                modifier = Modifier.readableWidth(),
            )
        }
    }
    if (escaner) EscanerInsumo(inventario, onCerrar = { escaner = false })
}

private fun filtrarInsumos(
    inventario: InventarioState,
    buscar: String,
    filtro: FiltroInsumo,
): List<InsumoInventario> {
    val termino = buscar.trim()
    return inventario.insumos.filter { insumo ->
        val coincideTexto = termino.isEmpty() ||
            insumo.nombre.contains(termino, ignoreCase = true) ||
            insumo.codigoQr.contains(termino, ignoreCase = true)
        val coincideFiltro = when (filtro) {
            FiltroInsumo.TODOS -> true
            FiltroInsumo.ALERTA -> insumo.estado != "OK"
            FiltroInsumo.CONTADOS -> inventario.conteoDe(insumo.id) != null
        }
        coincideTexto && coincideFiltro
    }
}

@Composable
private fun DetalleInsumoSeleccionado(elegido: InsumoInventario, inventario: InventarioState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SectionLabel("Seleccionado · conteo físico", modifier = Modifier.padding(start = 4.dp))
            GlassIconButton(
                icon = CobreIcons.Close,
                contentDescription = "Cerrar insumo seleccionado",
                onClick = { inventario.seleccionar(null) },
                size = 36.dp,
            )
        }
        InsumoCard(elegido, conteo = inventario.conteoDe(elegido.id))
        ConteoFisicoCard(
            insumo = elegido,
            conteoActual = inventario.conteoDe(elegido.id),
            onRegistrar = { cantidad -> inventario.registrarConteoFisico(elegido.id, cantidad) },
        )
    }
}

@Composable
private fun EstadoVacioInsumos(inventario: InventarioState, buscar: String, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    val mensaje = when {
        inventario.cargando -> "Cargando inventario de planta…"
        inventario.error != null -> "No se pudo actualizar el inventario."
        buscar.isNotBlank() -> "No encontramos insumos para \"$buscar\"."
        else -> "No hay insumos en esta vista."
    }
    ContentCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = if (inventario.error != null) CobreIcons.Warning else CobreIcons.Inventory,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(28.dp),
            )
            Text(mensaje, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EncabezadoInventario(
    inventario: InventarioState,
    buscar: String,
    filtro: FiltroInsumo,
    onBuscar: (String) -> Unit,
    onFiltro: (FiltroInsumo) -> Unit,
    onEscanear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ResumenKpiInventario(inventario)
        GlassSearchField(query = buscar, onQueryChange = onBuscar, placeholder = "Buscar insumo o código QR")
        GlassSegmentedControl(
            options = FiltroInsumo.entries,
            selected = filtro,
            onSelect = onFiltro,
            label = { it.etiqueta },
        )
        CobreButton(
            text = "Escanear QR para conteo físico",
            onClick = onEscanear,
            icon = CobreIcons.QrCodeScanner,
            enabled = !inventario.buscando,
            fillWidth = true,
        )
        if (inventario.cargando) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.full)
                    .semantics { contentDescription = "Actualizando inventario" },
                color = colors.accent,
                trackColor = colors.surfaceMuted,
            )
        }
        inventario.error?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        Text(
            "Las entradas, salidas y edición de stock se gestionan desde administración.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun ResumenKpiInventario(inventario: InventarioState) {
    val colors = CobreTheme.colors
    val total = inventario.insumos.size
    val alertas = inventario.insumos.count { it.estado != "OK" }
    val verificados = inventario.conteos.size
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        KpiInsumo("Total", "$total", colors.textPrimary, Modifier.weight(1f))
        KpiInsumo("Stock bajo", "$alertas", if (alertas > 0) colors.warningText else colors.textPrimary, Modifier.weight(1f))
        KpiInsumo("Verificados", "$verificados", if (verificados > 0) colors.successText else colors.textPrimary, Modifier.weight(1f))
    }
}

@Composable
private fun KpiInsumo(titulo: String, valor: String, colorValor: Color, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    ContentCard(
        modifier = modifier.semantics(mergeDescendants = true) {},
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(titulo.uppercase(), style = CobreTheme.text.labelCaps, color = colors.textSecondary)
        Spacer(Modifier.height(2.dp))
        Text(valor, style = MaterialTheme.typography.headlineSmall, color = colorValor)
    }
}

private fun cantidad(valor: Double): String = valor.toString().removeSuffix(".0").replace('.', ',')

/** Color del estado de stock: OK = éxito, crítico = peligro, resto = aviso. */
private fun colorParaEstadoInsumo(estado: String, colors: CobreColors): Color = when {
    estado.equals("OK", ignoreCase = true) -> colors.success
    estado.contains("Crítico", ignoreCase = true) -> colors.danger
    else -> colors.warning
}

private fun textoParaEstadoInsumo(estado: String, colors: CobreColors): Color = when {
    estado.equals("OK", ignoreCase = true) -> colors.successText
    estado.contains("Crítico", ignoreCase = true) -> colors.dangerText
    else -> colors.warningText
}

@Composable
private fun InsumoCard(
    insumo: InsumoInventario,
    conteo: ConteoFisico? = null,
    onSeleccionar: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    val colorEstado = colorParaEstadoInsumo(insumo.estado, colors)
    val textoEstado = textoParaEstadoInsumo(insumo.estado, colors)
    val iconoEstado = if (insumo.estado == "OK") CobreIcons.CheckCircle else CobreIcons.Warning
    val proporcion = if (insumo.stockMinimo > 0.0) {
        (insumo.stockActual / (insumo.stockMinimo * 2.0)).toFloat().coerceIn(0.05f, 1f)
    } else {
        1f
    }

    ContentCard(
        modifier = modifier,
        onClick = onSeleccionar,
        stripe = if (insumo.estado != "OK") colorEstado else null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    insumo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                StatusChip(label = insumo.estado, container = colorEstado.copy(alpha = 0.12f), content = textoEstado, icon = iconoEstado)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${cantidad(insumo.stockActual)} ${insumo.unidad}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                )
                Text(
                    "Mínimo ${cantidad(insumo.stockMinimo)} ${insumo.unidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CobreShapes.full)
                    .background(colorEstado.copy(alpha = 0.16f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(proporcion)
                        .height(6.dp)
                        .clip(CobreShapes.full)
                        .background(colorEstado),
                )
            }
            conteo?.let { ResumenConteoEnTarjeta(it, insumo.unidad) }
            Text("QR ${insumo.codigoQr}".uppercase(), style = CobreTheme.text.labelCaps, color = colors.textSecondary)
        }
    }
}

@Composable
private fun ResumenConteoEnTarjeta(conteo: ConteoFisico, unidad: String) {
    val colors = CobreTheme.colors
    val signo = if (conteo.diferencia > 0) "+" else ""
    val colorConteo = if (conteo.coincide) colors.successText else colors.dangerText
    val fondo = if (conteo.coincide) colors.success else colors.danger
    val detalle = "${cantidad(conteo.cantidadContada)} $unidad (${conteo.estadoConteo}: $signo${cantidad(conteo.diferencia)})"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CobreShapes.md)
            .background(fondo.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = if (conteo.coincide) CobreIcons.CheckCircle else CobreIcons.Warning,
            contentDescription = null,
            tint = colorConteo,
            modifier = Modifier.size(16.dp),
        )
        Text("Conteo físico: $detalle", style = MaterialTheme.typography.bodySmall, color = colorConteo, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ConteoFisicoCard(
    insumo: InsumoInventario,
    conteoActual: ConteoFisico?,
    onRegistrar: (Double) -> Boolean,
) {
    val colors = CobreTheme.colors
    var textoCantidad by remember(insumo.id, conteoActual) {
        mutableStateOf(conteoActual?.let { cantidad(it.cantidadContada) } ?: "")
    }
    var mensajeLocal by remember(insumo.id) { mutableStateOf<String?>(null) }
    var esErrorLocal by remember(insumo.id) { mutableStateOf(false) }

    val guardarConteo = {
        val valor = textoCantidad.trim().replace(',', '.').toDoubleOrNull()
        if (valor == null || valor < 0.0) {
            esErrorLocal = true
            mensajeLocal = "Ingresa una cantidad numérica válida (0 o mayor)."
        } else if (onRegistrar(valor)) {
            esErrorLocal = false
            mensajeLocal = "Conteo físico registrado correctamente."
        }
    }

    ContentCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Conteo físico en bodega",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                "Ingresa la cantidad real contada físicamente para contrastarla con el stock del sistema.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            CobreTextField(
                value = textoCantidad,
                onValueChange = { textoCantidad = it; mensajeLocal = null },
                label = "Cantidad contada (${insumo.unidad})",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { guardarConteo() }),
            )
            CobreButton(
                text = "Registrar conteo físico",
                onClick = guardarConteo,
                variant = ButtonVariant.Primary,
                icon = CobreIcons.Check,
                fillWidth = true,
            )
            mensajeLocal?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (esErrorLocal) colors.dangerText else colors.successText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
    }
}

/** Escaneo de un insumo en hoja inferior: cámara, código manual de respaldo y estado de búsqueda. */
@Composable
private fun EscanerInsumo(inventario: InventarioState, onCerrar: () -> Unit) {
    val colors = CobreTheme.colors
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var activo by remember { mutableStateOf(true) }
    val consultar: (String) -> Unit = { raw ->
        activo = false
        error = null
        inventario.escanear(raw) { fallo ->
            if (fallo == null) onCerrar() else { error = fallo; activo = true }
        }
    }
    GlassSheet(onDismiss = onCerrar, title = "Escanear insumo") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            QrScannerView(
                modifier = Modifier.fillMaxWidth().height(260.dp).clip(CobreShapes.lg),
                isActive = activo && !inventario.buscando,
                onQrCodeScanned = consultar,
            )
            CobreTextField(
                value = codigo,
                onValueChange = { codigo = it; error = null },
                label = "Código del insumo",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { if (codigo.isNotBlank()) consultar(codigo) }),
            )
            CobreButton(
                text = "Buscar código",
                onClick = { consultar(codigo) },
                enabled = codigo.isNotBlank() && !inventario.buscando,
                fillWidth = true,
            )
            if (inventario.buscando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().clip(CobreShapes.full),
                    color = colors.accent,
                    trackColor = colors.surfaceMuted,
                )
            }
            error?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            CobreButton(text = "Cerrar", onClick = onCerrar, variant = ButtonVariant.Tonal, fillWidth = true)
        }
    }
}
