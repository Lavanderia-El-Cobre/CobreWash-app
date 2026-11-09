package com.elcobre.lavanderiaelcobre.ui.screens.insumos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.ConteoFisico
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.ui.InventarioState
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.QrScannerView
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

private enum class FiltroInsumo(val etiqueta: String) {
    TODOS("Todos"),
    ALERTA("Stock bajo"),
    CONTADOS("Verificados"),
}

/** Catálogo real de Firebase con verificación de conteo físico por QR para el operario (RF-IN10). */
@Composable
fun InsumosScreen(inventario: InventarioState, applySystemBarsInsets: Boolean = true) {
    var buscar by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInsumo.TODOS) }
    var escaner by remember { mutableStateOf(false) }
    val visibles = remember(inventario.insumos, inventario.conteos, buscar, filtro) {
        filtrarInsumos(inventario, buscar, filtro)
    }
    CobreBackground(applySystemBarsInsets = applySystemBarsInsets) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                EncabezadoInventario(
                    inventario = inventario,
                    buscar = buscar,
                    filtro = filtro,
                    onBuscar = { buscar = it },
                    onFiltro = { filtro = it },
                    onEscanear = { escaner = true },
                )
            }
            inventario.seleccionado?.let { elegido ->
                item {
                    DetalleInsumoSeleccionado(
                        elegido = elegido,
                        inventario = inventario,
                    )
                }
            }
            if (visibles.isEmpty()) {
                item {
                    EstadoVacioInsumos(inventario = inventario, buscar = buscar)
                }
            }
            items(visibles, key = { it.id }) { insumo ->
                InsumoCard(
                    insumo = insumo,
                    conteo = inventario.conteoDe(insumo.id),
                    onSeleccionar = { inventario.seleccionar(insumo) },
                )
            }
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
private fun DetalleInsumoSeleccionado(elegido: InsumoInventario, inventario: InventarioState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Insumo seleccionado · Conteo físico",
                style = MaterialTheme.typography.titleMedium,
                color = brandHeadingColor(),
                modifier = Modifier.semantics { heading() },
            )
            TextButton(onClick = { inventario.seleccionar(null) }) {
                Icon(CobreIcons.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Cerrar")
            }
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
private fun EstadoVacioInsumos(inventario: InventarioState, buscar: String) {
    val mensaje = when {
        inventario.cargando -> "Cargando inventario de planta…"
        inventario.error != null -> "No se pudo actualizar el inventario."
        buscar.isNotBlank() -> "No encontramos insumos para \"$buscar\"."
        else -> "No hay insumos en esta vista."
    }
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 6.dp) {
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
                tint = brandMutedColor(),
                modifier = Modifier.size(28.dp),
            )
            Text(mensaje, color = brandMutedColor(), textAlign = TextAlign.Center)
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
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CabeceraTituloInventario(
            cargando = inventario.cargando,
            onRefrescar = { inventario.refrescar() },
        )
        ResumenKpiInventario(inventario)
        BuscadorInsumos(buscar = buscar, onBuscar = onBuscar)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroInsumo.entries.forEach { opcion ->
                SelectableChip(
                    label = opcion.etiqueta,
                    seleccionado = filtro == opcion,
                    onClick = { onFiltro(opcion) },
                )
            }
        }
        BotonEscanearInsumo(
            habilitado = !inventario.buscando,
            onClick = onEscanear,
        )
        if (inventario.cargando) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Actualizando inventario" },
                color = Brand500,
            )
        }
        inventario.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        Text(
            "Las entradas, salidas y edición de stock se gestionan desde administración.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
    }
}

@Composable
private fun CabeceraTituloInventario(cargando: Boolean, onRefrescar: () -> Unit) {
    val isDark = cobreIsDark()
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Inventario de Insumos",
                style = MaterialTheme.typography.headlineMedium,
                color = brandHeadingColor(),
                modifier = Modifier.semantics { heading() },
            )
            Text(
                "Stock disponible, mínimos de reposición y conteo físico por QR",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .pressableScale(interaction)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.7f))
                .semantics {
                    role = Role.Button
                    contentDescription = "Actualizar inventario"
                }
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = !cargando,
                    onClick = onRefrescar,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                CobreIcons.History,
                contentDescription = null,
                tint = if (isDark) Brand400 else Brand700,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun ResumenKpiInventario(inventario: InventarioState) {
    val total = inventario.insumos.size
    val alertas = inventario.insumos.count { it.estado != "OK" }
    val verificados = inventario.conteos.size
    val isDark = cobreIsDark()
    val alertaColor = if (alertas > 0) (if (isDark) StatusAmberDark else StatusAmber) else brandHeadingColor()
    val verificadoColor = if (verificados > 0) (if (isDark) StatusGreenDark else StatusGreen) else brandHeadingColor()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KpiInsumoPill("Total", "$total", brandHeadingColor(), Modifier.weight(1f))
        KpiInsumoPill("Stock bajo", "$alertas", alertaColor, Modifier.weight(1f))
        KpiInsumoPill("Verificados", "$verificados", verificadoColor, Modifier.weight(1f))
    }
}

@Composable
private fun KpiInsumoPill(titulo: String, valor: String, colorValor: Color, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.semantics(mergeDescendants = true) {},
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        elevation = 6.dp,
    ) {
        Column {
            Text(titulo, style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
            Spacer(Modifier.height(2.dp))
            Text(
                valor,
                style = MaterialTheme.typography.titleLarge,
                color = colorValor,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun BuscadorInsumos(buscar: String, onBuscar: (String) -> Unit) {
    OutlinedTextField(
        value = buscar,
        onValueChange = onBuscar,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        label = { Text("Buscar insumo o código QR") },
        leadingIcon = {
            Icon(CobreIcons.Search, contentDescription = null, tint = Brand500, modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (buscar.isNotEmpty()) {
            {
                IconButton(onClick = { onBuscar("") }) {
                    Icon(CobreIcons.Close, contentDescription = "Limpiar búsqueda", modifier = Modifier.size(18.dp))
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = cobreFieldColors(),
    )
}

@Composable
private fun BotonEscanearInsumo(habilitado: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        enabled = habilitado,
        interactionSource = interaction,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .pressableScale(interaction)
            .background(BrandCtaGradient, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
    ) {
        Icon(CobreIcons.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            "Escanear QR para conteo físico",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun cantidad(valor: Double): String = valor.toString().removeSuffix(".0").replace('.', ',')

@Composable
private fun InsumoCard(
    insumo: InsumoInventario,
    conteo: ConteoFisico? = null,
    onSeleccionar: () -> Unit = {},
) {
    val isDark = cobreIsDark()
    val colorEstado = colorParaEstadoInsumo(insumo.estado, isDark)
    val iconoEstado = if (insumo.estado == "OK") CobreIcons.CheckCircle else CobreIcons.Warning
    val proporcion = if (insumo.stockMinimo > 0.0) {
        (insumo.stockActual / (insumo.stockMinimo * 2.0)).toFloat().coerceIn(0.05f, 1f)
    } else 1f

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { role = Role.Button }
            .clickable(onClickLabel = "Ver conteo físico de ${insumo.nombre}", onClick = onSeleccionar),
        elevation = 8.dp,
        tint = colorEstado,
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
                    color = brandHeadingColor(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                StatusChip(
                    label = insumo.estado,
                    container = colorEstado.copy(alpha = if (isDark) 0.18f else 0.12f),
                    content = colorEstado,
                    icon = iconoEstado,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Disponible: ${cantidad(insumo.stockActual)} ${insumo.unidad}",
                    style = MaterialTheme.typography.titleLarge,
                    color = brandHeadingColor(),
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    "Mínimo: ${cantidad(insumo.stockMinimo)} ${insumo.unidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = brandMutedColor(),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorEstado.copy(alpha = 0.16f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(proporcion)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorEstado),
                )
            }
            conteo?.let { ResumenConteoEnTarjeta(it, insumo.unidad, isDark) }
            Text(
                "QR: ${insumo.codigoQr}",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
            )
        }
    }
}

@Composable
private fun colorParaEstadoInsumo(estado: String, isDark: Boolean): Color = when {
    estado.equals("OK", ignoreCase = true) -> if (isDark) StatusGreenDark else StatusGreen
    estado.contains("Crítico", ignoreCase = true) -> if (isDark) StatusRedDark else StatusRed
    else -> if (isDark) StatusAmberDark else StatusAmber
}

@Composable
private fun ResumenConteoEnTarjeta(conteo: ConteoFisico, unidad: String, isDark: Boolean) {
    val signo = if (conteo.diferencia > 0) "+" else ""
    val colorConteo = if (conteo.coincide) {
        if (isDark) StatusGreenDark else StatusGreen
    } else {
        if (isDark) StatusRedDark else StatusRed
    }
    val detalle = "${cantidad(conteo.cantidadContada)} $unidad (${conteo.estadoConteo}: $signo${cantidad(conteo.diferencia)})"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorConteo.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
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
        Text(
            "Conteo físico: $detalle",
            style = MaterialTheme.typography.bodySmall,
            color = colorConteo,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ConteoFisicoCard(
    insumo: InsumoInventario,
    conteoActual: ConteoFisico?,
    onRegistrar: (Double) -> Boolean,
) {
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

    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Conteo físico en bodega",
                style = MaterialTheme.typography.titleSmall,
                color = brandHeadingColor(),
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Ingresa la cantidad real contada físicamente para contrastarla con el stock del sistema.",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
            OutlinedTextField(
                value = textoCantidad,
                onValueChange = { textoCantidad = it; mensajeLocal = null },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                label = { Text("Cantidad contada (${insumo.unidad})") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { guardarConteo() }),
                colors = cobreFieldColors(),
            )
            Button(
                onClick = guardarConteo,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(CobreIcons.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Registrar conteo físico", fontWeight = FontWeight.Bold)
            }
            mensajeLocal?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (esErrorLocal) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EscanerInsumo(inventario: InventarioState, onCerrar: () -> Unit) {
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
    ModalBottomSheet(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Escanear insumo",
                style = MaterialTheme.typography.titleLarge,
                color = brandHeadingColor(),
                modifier = Modifier.semantics { heading() },
            )
            QrScannerView(
                modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(18.dp)),
                isActive = activo && !inventario.buscando,
                onQrCodeScanned = consultar,
            )
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it; error = null },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                label = { Text("Código del insumo") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { if (codigo.isNotBlank()) consultar(codigo) },
                ),
                colors = cobreFieldColors(),
            )
            Button(
                onClick = { consultar(codigo) },
                enabled = codigo.isNotBlank() && !inventario.buscando,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Buscar código")
            }
            if (inventario.buscando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            TextButton(onClick = onCerrar, modifier = Modifier.align(Alignment.End)) {
                Text("Cerrar")
            }
        }
    }
}

