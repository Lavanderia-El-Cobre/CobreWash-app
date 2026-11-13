package com.elcobre.lavanderiaelcobre.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.data.model.ConteoGestion
import com.elcobre.lavanderiaelcobre.data.model.MovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.NuevoInsumoInput
import com.elcobre.lavanderiaelcobre.data.model.TipoMovimientoInventario
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CodigoQr
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreSwitch
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.FilaDato
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.StockViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerQrInsumo
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private enum class VistaStock(val etiqueta: String) { STOCK("Stock"), CONTEOS("Conteos"), MOVIMIENTOS("Movimientos") }

private fun cantidad(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toLong().toString() else ((valor * 100).toLong() / 100.0).toString()

private fun tonoInsumo(insumo: InsumoInventario): Tono = when (insumo.estado) {
    "Crítico" -> Tono.PELIGRO
    "Bajo" -> Tono.ALERTA
    "Inactivo" -> Tono.NEUTRO
    else -> Tono.EXITO
}

/** Hoja abierta en inventario: ficha de un insumo, alta, movimiento o resolución de conteo. */
private sealed interface HojaStock {
    data class Ficha(val insumo: InsumoInventario) : HojaStock
    data object Nuevo : HojaStock
    data class Movimiento(val insumo: InsumoInventario, val tipo: TipoMovimientoInventario) : HojaStock
    data class Resolver(val conteo: ConteoGestion, val aprobar: Boolean) : HojaStock
}

/** Inventario de administración: stock, aprobación de conteos físicos y movimientos. */
@Composable
fun StockScreen(vm: StockViewModel, applySystemBarsInsets: Boolean = true) {
    var vista by rememberSaveable { mutableStateOf(VistaStock.STOCK) }
    var hoja by remember { mutableStateOf<HojaStock?>(null) }
    LaunchedEffect(vista) { if (vista == VistaStock.MOVIMIENTOS) vm.refrescarMovimientos() }
    LargeTitleScreen(
        title = "Inventario",
        subtitle = "${vm.insumos.size} insumos · ${vm.bajoMinimo} bajo el mínimo · ${vm.conteosPendientes.size} conteos por revisar",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = { CobreButton(text = "Insumo", onClick = { hoja = HojaStock.Nuevo }, icon = CobreIcons.Add) },
    ) {
        item(key = "vista") { GlassSegmentedControl(VistaStock.entries, vista, { vista = it }, { it.etiqueta }, Modifier.readableWidth()) }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        when (vista) {
            VistaStock.STOCK -> listaStock(vm) { hoja = HojaStock.Ficha(it) }
            VistaStock.CONTEOS -> listaConteos(vm) { conteo, aprobar -> hoja = HojaStock.Resolver(conteo, aprobar) }
            VistaStock.MOVIMIENTOS -> listaMovimientos(vm)
        }
    }
    when (val h = hoja) {
        is HojaStock.Ficha -> FichaInsumoSheet(
            vm,
            h.insumo,
            onMovimiento = { hoja = HojaStock.Movimiento(h.insumo, it) },
            onCerrar = { hoja = null },
        )
        HojaStock.Nuevo -> NuevoInsumoSheet(vm, onCerrar = { hoja = null })
        is HojaStock.Movimiento -> MovimientoSheet(vm, h.insumo, h.tipo, onCerrar = { hoja = null })
        is HojaStock.Resolver -> ResolverConteoSheet(vm, h.conteo, h.aprobar, onCerrar = { hoja = null })
        null -> Unit
    }
}

private fun LazyListScope.listaStock(vm: StockViewModel, onAbrir: (InsumoInventario) -> Unit) {
    item(key = "buscar") { GlassSearchField(vm.busqueda, vm::buscar, "Buscar insumo", modifier = Modifier.readableWidth()) }
    if (vm.filtrados.isEmpty() && !vm.cargando) {
        item(key = "vacio") { EstadoVacio(CobreIcons.Inventory, "No hay insumos que coincidan.", modifier = Modifier.readableWidth()) }
    }
    items(vm.filtrados, key = { it.id }) { insumo ->
        val colors = CobreTheme.colors
        ContentCard(modifier = Modifier.readableWidth(), onClick = { onAbrir(insumo) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(insumo.nombre, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Text(
                        "Mínimo ${cantidad(insumo.stockMinimo)} ${insumo.unidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${cantidad(insumo.stockActual)} ${insumo.unidad}",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    ChipTono(insumo.estado, tonoInsumo(insumo))
                }
            }
        }
    }
}

private fun LazyListScope.listaConteos(vm: StockViewModel, onResolver: (ConteoGestion, Boolean) -> Unit) {
    item(key = "t-pend") { TituloGrupo("Por revisar", Modifier.readableWidth()) }
    if (vm.conteosPendientes.isEmpty()) {
        item(key = "sin-pend") { EstadoVacio(CobreIcons.CheckCircle, "No hay conteos pendientes.", modifier = Modifier.readableWidth()) }
    }
    items(vm.conteosPendientes, key = { "p-" + it.id }) { conteo -> ConteoCard(conteo, onResolver, Modifier.readableWidth()) }
    if (vm.conteosResueltos.isNotEmpty()) {
        item(key = "t-res") { TituloGrupo("Resueltos recientemente", Modifier.readableWidth()) }
        items(vm.conteosResueltos, key = { "r-" + it.id }) { conteo -> ConteoCard(conteo, null, Modifier.readableWidth()) }
    }
}

@Composable
private fun ConteoCard(conteo: ConteoGestion, onResolver: ((ConteoGestion, Boolean) -> Unit)?, modifier: Modifier) {
    val colors = CobreTheme.colors
    val dif = conteo.diferencia
    ContentCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(conteo.insumo, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(
                    "${conteo.registradoPor} · ${conteo.creadoEn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            val (texto, tono) = when {
                kotlin.math.abs(dif) < 0.001 -> "Cuadrado" to Tono.EXITO
                dif < 0 -> "Faltan ${cantidad(-dif)}" to Tono.PELIGRO
                else -> "Sobran ${cantidad(dif)}" to Tono.ALERTA
            }
            ChipTono(texto, tono)
        }
        Spacer(Modifier.height(6.dp))
        FilaDato("Contado", "${cantidad(conteo.cantidadContada)} ${conteo.unidad}")
        FilaDato("En sistema", "${cantidad(conteo.stockSistema)} ${conteo.unidad} (ahora ${cantidad(conteo.stockActual)})")
        FilaDato(
            "Resolución",
            listOfNotNull(
                conteo.estado.lowercase().replaceFirstChar { it.uppercase() }.takeIf { onResolver == null },
                conteo.resueltoPor,
                conteo.motivoResolucion,
            ).joinToString(" · "),
        )
        if (onResolver != null) {
            if (conteo.desactualizado) {
                Text(
                    "El stock cambió después de este conteo: solo puede rechazarse.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.warningText,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CobreButton(
                    "Aprobar",
                    { onResolver(conteo, true) },
                    Modifier.weight(1f),
                    icon = CobreIcons.Check,
                    enabled = !conteo.desactualizado,
                )
                CobreButton(
                    "Rechazar",
                    { onResolver(conteo, false) },
                    Modifier.weight(1f),
                    variant = ButtonVariant.Danger,
                    icon = CobreIcons.Close,
                )
            }
        }
    }
}

private fun LazyListScope.listaMovimientos(vm: StockViewModel) {
    if (vm.movimientos.isEmpty() && !vm.cargando) {
        item(key = "sin-mov") {
            EstadoVacio(CobreIcons.History, "Aún no hay movimientos registrados.", modifier = Modifier.readableWidth())
        }
    }
    items(vm.movimientos, key = { "m-" + it.id }) { mov -> MovimientoFila(mov, Modifier.readableWidth()) }
    if (vm.hayMasMovimientos) {
        item(key = "mas-mov") {
            CobreButton(
                "Cargar más",
                { vm.refrescarMovimientos(mas = true) },
                Modifier.readableWidth(),
                variant = ButtonVariant.Tonal,
                fillWidth = true,
            )
        }
    }
}

@Composable
private fun MovimientoFila(mov: MovimientoInventario, modifier: Modifier) {
    val colors = CobreTheme.colors
    val entrada = mov.tipo == TipoMovimientoInventario.ENTRADA
    ContentCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(mov.insumo, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(
                    listOfNotNull(mov.motivo, mov.usuario, mov.fecha).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            ChipTono("${if (entrada) "+" else "−"}${cantidad(mov.cantidad)} ${mov.unidad}", if (entrada) Tono.EXITO else Tono.ALERTA)
        }
    }
}

@Composable
private fun FichaInsumoSheet(
    vm: StockViewModel,
    insumo: InsumoInventario,
    onMovimiento: (TipoMovimientoInventario) -> Unit,
    onCerrar: () -> Unit,
) {
    var nombre by remember { mutableStateOf(insumo.nombre) }
    var unidad by remember { mutableStateOf(insumo.unidad) }
    var minimo by remember { mutableStateOf(cantidad(insumo.stockMinimo)) }
    var activo by remember { mutableStateOf(insumo.activo) }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = insumo.nombre,
        subtitle = "Stock actual: ${cantidad(insumo.stockActual)} ${insumo.unidad}",
    ) {
        EtiquetaQrInsumo(insumo)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CobreButton(
                "Entrada",
                { onMovimiento(TipoMovimientoInventario.ENTRADA) },
                Modifier.weight(1f),
                icon = CobreIcons.Add,
                variant = ButtonVariant.Tonal,
            )
            CobreButton(
                "Salida",
                { onMovimiento(TipoMovimientoInventario.SALIDA) },
                Modifier.weight(1f),
                icon = CobreIcons.Remove,
                variant = ButtonVariant.Tonal,
            )
        }
        CobreTextField(nombre, { if (it.length <= 80) nombre = it }, label = "Nombre")
        CobreTextField(unidad, { if (it.length <= 20) unidad = it }, label = "Unidad de medida")
        CobreTextField(
            minimo,
            { minimo = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Stock mínimo",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        FilaActivo(activo) { activo = it }
        ErrorHoja(error)
        CobreButton(
            text = "Guardar cambios",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                val min = minimo.toDoubleOrNull()
                if (min == null) {
                    error = "Ingresa un stock mínimo válido."
                } else {
                    vm.actualizarInsumo(insumo.copy(nombre = nombre, unidad = unidad, stockMinimo = min, activo = activo)) { p ->
                        if (p == null) onCerrar() else error = p
                    }
                }
            },
        )
    }
}

@Composable
private fun FilaActivo(activo: Boolean, onCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().toggleable(value = activo, role = Role.Switch, onValueChange = onCambiar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Activo", style = MaterialTheme.typography.titleSmall, color = CobreTheme.colors.textPrimary)
            Text(
                "Los insumos inactivos no aparecen para el equipo de planta.",
                style = MaterialTheme.typography.bodySmall,
                color = CobreTheme.colors.textSecondary,
            )
        }
        Spacer(Modifier.width(12.dp))
        CobreSwitch(checked = activo)
    }
}

@Composable
private fun NuevoInsumoSheet(vm: StockViewModel, onCerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var inicial by remember { mutableStateOf("0") }
    var minimo by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf<String?>(null) }
    val decimal = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Nuevo insumo",
        subtitle = "Se genera su código QR automáticamente.",
    ) {
        CobreTextField(nombre, { if (it.length <= 80) nombre = it }, label = "Nombre", placeholder = "Detergente industrial")
        CobreTextField(unidad, { if (it.length <= 20) unidad = it }, label = "Unidad de medida", placeholder = "L, kg, un")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CobreTextField(
                inicial,
                { inicial = it.filter { c -> c.isDigit() || c == '.' } },
                Modifier.weight(1f),
                label = "Stock inicial",
                keyboardOptions = decimal,
            )
            CobreTextField(
                minimo,
                { minimo = it.filter { c -> c.isDigit() || c == '.' } },
                Modifier.weight(1f),
                label = "Stock mínimo",
                keyboardOptions = decimal,
            )
        }
        ErrorHoja(error)
        CobreButton(
            text = "Registrar insumo",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                val ini = inicial.toDoubleOrNull()
                val min = minimo.toDoubleOrNull()
                if (ini == null || min == null) {
                    error = "Revisa las cantidades."
                } else {
                    vm.crearInsumo(NuevoInsumoInput(nombre, unidad, ini, min)) { p -> if (p == null) onCerrar() else error = p }
                }
            },
        )
    }
}

@Composable
private fun MovimientoSheet(vm: StockViewModel, insumo: InsumoInventario, tipo: TipoMovimientoInventario, onCerrar: () -> Unit) {
    var cantidadTexto by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "${tipo.etiqueta} de ${insumo.nombre}",
        subtitle = "Stock actual: ${cantidad(insumo.stockActual)} ${insumo.unidad}",
    ) {
        CobreTextField(
            cantidadTexto,
            { cantidadTexto = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Cantidad (${insumo.unidad})",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        CobreTextField(
            motivo,
            { if (it.length <= 200) motivo = it },
            label = "Motivo (opcional)",
            placeholder = if (tipo == TipoMovimientoInventario.ENTRADA) "Compra a proveedor" else "Consumo de planta",
        )
        ErrorHoja(error)
        CobreButton(
            text = "Registrar ${tipo.etiqueta.lowercase()}",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = { vm.registrarMovimiento(
                insumo,
                tipo,
                cantidadTexto.toDoubleOrNull(),
                motivo,
            ) { p -> if (p == null) onCerrar() else error = p } },
        )
    }
}

@Composable
private fun ResolverConteoSheet(vm: StockViewModel, conteo: ConteoGestion, aprobar: Boolean, onCerrar: () -> Unit) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = if (aprobar) "Aprobar conteo" else "Rechazar conteo",
        subtitle = if (aprobar) {
            "El stock de ${conteo.insumo} quedará en ${cantidad(conteo.cantidadContada)} ${conteo.unidad}."
        } else {
            "El stock de ${conteo.insumo} no cambia."
        },
    ) {
        CobreTextField(motivo, { if (it.length <= 200) motivo = it }, label = "Motivo", singleLine = false, minLines = 2)
        ErrorHoja(error)
        CobreButton(
            text = if (aprobar) "Aprobar y ajustar stock" else "Rechazar conteo",
            variant = if (aprobar) ButtonVariant.Accent else ButtonVariant.Danger,
            loading = vm.guardando,
            fillWidth = true,
            onClick = { vm.resolverConteo(conteo, aprobar, motivo) { p -> if (p == null) onCerrar() else error = p } },
        )
    }
}

/** Etiqueta de inventario (`QrInsumo.tsx`): el QR que se escanea desde Inventario en la app. */
@Composable
private fun EtiquetaQrInsumo(insumo: InsumoInventario) {
    val codigo = remember(insumo.codigoQr) { extraerQrInsumo(insumo.codigoQr) } ?: return
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CodigoQr(valor = codigo, descripcion = "Código QR del insumo ${insumo.nombre}", tamano = 200.dp)
        Text(
            "Escanéalo desde Inventario en la app para registrar un conteo.",
            style = MaterialTheme.typography.bodySmall,
            color = CobreTheme.colors.textSecondary,
        )
        Text(codigo, style = CobreTheme.text.label, color = CobreTheme.colors.textSecondary)
    }
}
