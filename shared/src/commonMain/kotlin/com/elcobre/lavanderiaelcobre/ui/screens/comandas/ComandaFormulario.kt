package com.elcobre.lavanderiaelcobre.ui.screens.comandas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.data.model.prendasSugeridas
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CampoCantidad
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.SelectorConBusqueda
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private enum class OrigenCliente(val etiqueta: String) { EXISTENTE("Cliente registrado"), NUEVO("Cliente nuevo") }

/** Formulario de vidrio para crear una comanda o editar una pendiente. */
@Composable
internal fun ComandaFormularioSheet(vm: ComandasViewModel, formulario: FormularioComanda) {
    val edicion = vm.edicion
    val cambiar = vm::actualizarFormulario
    GlassSheet(
        onDismiss = vm::cerrarFormulario,
        title = edicion?.let { "Editar ${it.numero}" } ?: "Nueva comanda",
        subtitle = if (edicion != null) "Solo las comandas pendientes pueden editarse." else "Registra al cliente y las prendas recibidas.",
    ) {
        SectionLabel("Cliente")
        if (edicion != null) {
            Text(
                "${formulario.cliente.nombre} · ${formulario.tipoCliente.etiqueta}",
                style = MaterialTheme.typography.bodyLarge,
                color = CobreTheme.colors.textPrimary,
            )
        } else {
            SeccionCliente(vm, formulario)
        }
        CobreTextField(
            value = formulario.empresa,
            onValueChange = { if (it.length <= 120) cambiar(formulario.copy(empresa = it)) },
            label = "Empresa (opcional)",
        )
        CobreTextField(
            value = formulario.proyecto,
            onValueChange = { if (it.length <= 120) cambiar(formulario.copy(proyecto = it)) },
            label = "Proyecto (opcional)",
        )
        SectionLabel("Prendas")
        formulario.lineas.forEachIndexed { indice, linea ->
            LineaEditor(
                vm = vm,
                linea = linea,
                tipo = formulario.tipoCliente,
                puedeQuitar = formulario.lineas.size > 1,
                onCambiar = { nueva -> cambiar(formulario.copy(lineas = formulario.lineas.toMutableList().also { it[indice] = nueva })) },
                onQuitar = { cambiar(formulario.copy(lineas = formulario.lineas.filterIndexed { i, _ -> i != indice })) },
            )
        }
        CobreButton(
            text = "Agregar prenda",
            onClick = vm::agregarLinea,
            variant = ButtonVariant.Tonal,
            icon = CobreIcons.Add,
            fillWidth = true,
        )
        CobreTextField(
            value = formulario.observaciones,
            onValueChange = { if (it.length <= 300) cambiar(formulario.copy(observaciones = it)) },
            label = "Observaciones (opcional)",
            singleLine = false,
            minLines = 2,
            supportingText = "${formulario.observaciones.length}/300",
        )
        PieFormulario(vm, formulario)
    }
}

@Composable
private fun SeccionCliente(vm: ComandasViewModel, formulario: FormularioComanda) {
    val origen = if (formulario.nuevoCliente) OrigenCliente.NUEVO else OrigenCliente.EXISTENTE
    GlassSegmentedControl(
        options = OrigenCliente.entries,
        selected = origen,
        onSelect = {
            vm.actualizarFormulario(
                formulario.copy(
                    nuevoCliente = it == OrigenCliente.NUEVO,
                    clienteId = if (it == OrigenCliente.NUEVO) "" else formulario.clienteId,
                    cliente = if (it == OrigenCliente.NUEVO) NuevoClienteInput("", TipoClienteFicha.PARTICULAR, "") else formulario.cliente,
                ),
            )
        },
        label = { it.etiqueta },
    )
    if (formulario.nuevoCliente) {
        CamposCliente(formulario.cliente) { vm.actualizarFormulario(formulario.copy(cliente = it)) }
    } else {
        SelectorConBusqueda(
            etiqueta = "Cliente",
            valorActual = if (formulario.clienteId.isBlank()) "" else formulario.cliente.nombre,
            opciones = vm.catalogos.clientes,
            texto = { it.nombre },
            detalle = { listOfNotNull(it.tipo.etiqueta, it.rut, it.telefono).joinToString(" · ") },
            onSeleccionar = { vm.seleccionarCliente(it.id) },
            placeholder = if (vm.cargando && vm.catalogos.clientes.isEmpty()) "Cargando clientes…" else "Buscar cliente",
        )
    }
}

/** Campos de una ficha de cliente; también los usa la pantalla Clientes. */
@Composable
internal fun CamposCliente(cliente: NuevoClienteInput, enabled: Boolean = true, onCambiar: (NuevoClienteInput) -> Unit) {
    CobreTextField(
        value = cliente.nombre,
        onValueChange = { if (it.length <= 120) onCambiar(cliente.copy(nombre = it)) },
        label = "Nombre o razón social",
        leadingIcon = CobreIcons.Person,
        enabled = enabled,
    )
    GlassSegmentedControl(
        options = TipoClienteFicha.entries,
        selected = cliente.tipo,
        onSelect = { if (enabled) onCambiar(cliente.copy(tipo = it)) },
        label = { it.etiqueta },
    )
    val rutInvalido = cliente.rut.length >= 3 && !Validaciones.rutValido(cliente.rut)
    CobreTextField(
        value = cliente.rut,
        onValueChange = { onCambiar(cliente.copy(rut = Validaciones.formatearRut(it))) },
        label = "RUT",
        placeholder = "12.345.678-5",
        isError = rutInvalido,
        supportingText = if (rutInvalido) "RUT no válido" else null,
        enabled = enabled,
    )
    val tipoFono = Validaciones.tipoTelefono(cliente.telefono)
    CobreTextField(
        value = cliente.telefono,
        onValueChange = { onCambiar(cliente.copy(telefono = Validaciones.formatearTelefono(it))) },
        label = "Teléfono (opcional)",
        placeholder = "+56 9 1234 5678",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        supportingText = if (cliente.telefono.isNotBlank()) tipoFono ?: "Número chileno no válido" else null,
        isError = cliente.telefono.length > 8 && tipoFono == null,
        enabled = enabled,
    )
    CobreTextField(
        value = cliente.email,
        onValueChange = { if (it.length <= 120) onCambiar(cliente.copy(email = it.trim())) },
        label = "Correo (opcional)",
        leadingIcon = CobreIcons.Mail,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = enabled,
    )
    CobreTextField(
        value = cliente.direccion,
        onValueChange = { if (it.length <= 200) onCambiar(cliente.copy(direccion = it)) },
        label = "Dirección (opcional)",
        enabled = enabled,
    )
}

@Composable
private fun LineaEditor(
    vm: ComandasViewModel,
    linea: LineaFormulario,
    tipo: TipoClienteFicha,
    puedeQuitar: Boolean,
    onCambiar: (LineaFormulario) -> Unit,
    onQuitar: () -> Unit,
) {
    val prendas = remember(tipo, vm.catalogos.prendas) {
        (prendasSugeridas(tipo) + vm.catalogos.prendas.map { it.nombre })
            .distinctBy { Validaciones.normalizarCatalogo(it) }
    }
    ContentCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorConBusqueda(
                    etiqueta = "Prenda",
                    valorActual = linea.prenda,
                    opciones = prendas,
                    texto = { it },
                    onSeleccionar = { onCambiar(linea.copy(prenda = it)) },
                    permitirTextoLibre = { onCambiar(linea.copy(prenda = it.uppercase())) },
                    modifier = Modifier.weight(1f),
                )
                if (puedeQuitar) GlassIconButton(
                    icon = CobreIcons.Delete,
                    contentDescription = "Quitar ${linea.prenda}",
                    onClick = onQuitar,
                    size = 44.dp,
                )
            }
            SelectorConBusqueda(
                etiqueta = "Servicio",
                valorActual = linea.servicio,
                opciones = vm.catalogos.servicios,
                texto = { it.nombre },
                detalle = { "Precio base ${clp(it.precioBase)}" },
                onSeleccionar = { onCambiar(linea.copy(servicio = it.nombre, precioUnitario = it.precioBase)) },
                permitirTextoLibre = { onCambiar(linea.copy(servicio = it)) },
            )
            CantidadesYPrecio(linea, tipo, onCambiar)
            CobreTextField(
                value = linea.detalle,
                onValueChange = { if (it.length <= 150) onCambiar(linea.copy(detalle = it)) },
                label = "Detalle (opcional)",
                placeholder = "Color, marca o daño visible",
            )
            Text(
                "Subtotal ${clp(linea.subtotal)}",
                style = MaterialTheme.typography.labelLarge,
                color = CobreTheme.colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CantidadesYPrecio(linea: LineaFormulario, tipo: TipoClienteFicha, onCambiar: (LineaFormulario) -> Unit) {
    var precioTexto by remember(linea.servicio) { mutableStateOf(linea.precioUnitario.toLong().toString()) }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
        CampoCantidad(
            valor = linea.cantidad,
            onCambiar = { onCambiar(linea.copy(cantidad = it, recibido = linea.recibido.coerceAtMost(it))) },
            etiqueta = if (tipo == TipoClienteFicha.HOTEL) "Entregado" else "Cantidad",
        )
        if (tipo == TipoClienteFicha.HOTEL) {
            CampoCantidad(
                valor = linea.recibido,
                onCambiar = { onCambiar(linea.copy(recibido = it.coerceAtMost(linea.cantidad))) },
                etiqueta = "Recibido",
                minimo = 0,
            )
        }
    }
    CobreTextField(
        value = precioTexto,
        onValueChange = { texto ->
            val limpio = texto.filter(Char::isDigit).take(9)
            precioTexto = limpio
            onCambiar(linea.copy(precioUnitario = limpio.toDoubleOrNull() ?: 0.0))
        },
        label = "Precio unitario (CLP)",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
private fun PieFormulario(vm: ComandasViewModel, formulario: FormularioComanda) {
    val colors = CobreTheme.colors
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Total", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, modifier = Modifier.weight(1f))
        Text(clp(formulario.total), style = MaterialTheme.typography.titleLarge, color = colors.accentText, fontWeight = FontWeight.Bold)
    }
    vm.errorFormulario?.let {
        Text(
            it,
            color = colors.dangerText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
    CobreButton(
        text = if (vm.edicion != null) "Guardar cambios" else "Crear comanda",
        onClick = vm::guardar,
        icon = CobreIcons.Check,
        loading = vm.guardando,
        fillWidth = true,
    )
}
