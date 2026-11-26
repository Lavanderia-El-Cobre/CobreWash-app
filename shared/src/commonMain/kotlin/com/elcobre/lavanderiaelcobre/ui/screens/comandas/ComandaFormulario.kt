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
import com.elcobre.lavanderiaelcobre.data.model.Cobro
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.ModalidadCobro
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.data.model.prendasSugeridas
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CampoCantidad
import com.elcobre.lavanderiaelcobre.ui.components.CampoTelefono
import com.elcobre.lavanderiaelcobre.ui.components.CobreCheckbox
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
        SeccionCobro(vm, formulario)
        ConsumoEstimadoPanel(vm.estimacion, formulario.servicio)
        SectionLabel(if (formulario.porKg) "Prendas (opcional por kg)" else "Prendas")
        formulario.lineas.forEachIndexed { indice, linea ->
            LineaEditor(
                vm = vm,
                linea = linea,
                tipo = formulario.tipoCliente,
                porKg = formulario.porKg,
                puedeQuitar = formulario.lineas.size > 1 || formulario.porKg,
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
        CuentaClienteNuevo(formulario) { vm.actualizarFormulario(it) }
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
    CampoTelefono(
        valor = cliente.telefono,
        onCambiar = { onCambiar(cliente.copy(telefono = it)) },
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

/**
 * Registro opcional del cliente nuevo en la plataforma web, como la casilla de la web
 * (`67d38eb`): solo si se marca se crea la cuenta y se le envía la invitación.
 */
@Composable
private fun CuentaClienteNuevo(formulario: FormularioComanda, onCambiar: (FormularioComanda) -> Unit) {
    CobreCheckbox(
        checked = formulario.registrarCuenta,
        onCheckedChange = { onCambiar(formulario.copy(registrarCuenta = it, clienteInformado = false)) },
        label = "Registrar al cliente en la plataforma web",
    )
    if (formulario.registrarCuenta) {
        Text(
            "Se crea su cuenta con el correo indicado y recibe un enlace para definir su contraseña. " +
                "Podrá revisar sus comandas en la web.",
            style = MaterialTheme.typography.bodySmall,
            color = CobreTheme.colors.textSecondary,
        )
        CobreCheckbox(
            checked = formulario.clienteInformado,
            onCheckedChange = { onCambiar(formulario.copy(clienteInformado = it)) },
            label = "Informé al cliente sobre su cuenta, los Términos y Condiciones y el Aviso de Privacidad",
        )
    }
}

/** Modalidad de cobro, servicio de la comanda, peso total y precio por kg. */
@Composable
private fun SeccionCobro(vm: ComandasViewModel, formulario: FormularioComanda) {
    val cambiar = vm::actualizarFormulario
    SectionLabel("Cobro")
    GlassSegmentedControl(
        options = ModalidadCobro.entries,
        selected = formulario.modalidad,
        onSelect = { cambiar(formulario.copy(modalidad = it)) },
        label = { it.etiqueta },
    )
    SelectorConBusqueda(
        etiqueta = "Servicio",
        valorActual = formulario.servicio,
        opciones = vm.catalogos.servicios,
        texto = { it.nombre },
        detalle = {
            if (formulario.porKg && it.precioKg > 0) "Precio por kg ${clp(it.precioKg)}" else "Precio base ${clp(it.precioBase)}"
        },
        onSeleccionar = { vm.seleccionarServicio(it.nombre) },
        permitirTextoLibre = { vm.seleccionarServicio(it.trim()) },
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        val pesoInvalido = formulario.pesoKg.isNotBlank() && Cobro.leerPesoKg(formulario.pesoKg) == null
        CobreTextField(
            value = formulario.pesoKg,
            onValueChange = { texto ->
                val limpio = texto.filter { it.isDigit() || it == ',' || it == '.' }.take(9)
                cambiar(formulario.copy(pesoKg = limpio))
            },
            label = "Peso total (kg)",
            placeholder = "12,5",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = pesoInvalido,
            supportingText = if (pesoInvalido) "Mayor que 0 y hasta 10.000" else null,
            modifier = Modifier.weight(1f),
        )
        if (formulario.porKg) {
            val precioInvalido = formulario.precioKg.isNotBlank() && Cobro.leerPrecioKg(formulario.precioKg) == null
            CobreTextField(
                value = formulario.precioKg,
                onValueChange = { texto -> cambiar(formulario.copy(precioKg = texto.filter(Char::isDigit).take(7))) },
                label = "Precio por kg (CLP)",
                placeholder = "2500",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = precioInvalido,
                supportingText = if (precioInvalido) "Mayor que 0" else null,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LineaEditor(
    vm: ComandasViewModel,
    linea: LineaFormulario,
    tipo: TipoClienteFicha,
    porKg: Boolean,
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
                    // Las prendas nuevas se guardan en mayúsculas y con hasta 60 caracteres.
                    permitirTextoLibre = { onCambiar(linea.copy(prenda = Cobro.normalizarPrendaNueva(it))) },
                    modifier = Modifier.weight(1f),
                )
                if (puedeQuitar) GlassIconButton(
                    icon = CobreIcons.Delete,
                    contentDescription = "Quitar ${linea.prenda}",
                    onClick = onQuitar,
                    size = 44.dp,
                )
            }
            CantidadesYPrecio(linea, tipo, porKg, onCambiar)
            CobreTextField(
                value = linea.detalle,
                onValueChange = { if (it.length <= 150) onCambiar(linea.copy(detalle = it)) },
                label = "Detalle (opcional)",
                placeholder = "Color, marca o daño visible",
            )
            if (!porKg) {
                Text(
                    "Subtotal ${clp(linea.subtotal)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = CobreTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CantidadesYPrecio(linea: LineaFormulario, tipo: TipoClienteFicha, porKg: Boolean, onCambiar: (LineaFormulario) -> Unit) {
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
    // Por kg se cobra el peso: el precio de cada prenda no se usa.
    if (porKg) return
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
    val peso = Cobro.leerPesoKg(formulario.pesoKg)
    val precioKg = Cobro.leerPrecioKg(formulario.precioKg)
    if (formulario.porKg && peso != null && precioKg != null) {
        Text(
            "${Cobro.formatearPeso(peso)} kg × ${clp(precioKg)} por kg",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
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
