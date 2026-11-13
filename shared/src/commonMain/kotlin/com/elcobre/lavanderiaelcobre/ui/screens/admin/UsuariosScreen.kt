package com.elcobre.lavanderiaelcobre.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.EdicionUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.RolRef
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.UsuarioGestion
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
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
import com.elcobre.lavanderiaelcobre.ui.components.FilaDato
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.Iniciales
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.SelectorConBusqueda
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.UsuariosViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private data class FiltroActivo(val etiqueta: String, val valor: Boolean?)

private val FiltrosActivo = listOf(FiltroActivo("Todos", null), FiltroActivo("Activos", true), FiltroActivo("Inactivos", false))

/** Etiqueta legible de un rol del catálogo. */
internal fun etiquetaRol(nombre: String): String = when (nombre) {
    "admin" -> "Administración"
    "recepcionista" -> "Recepción"
    "operario" -> "Operario"
    "repartidor" -> "Repartidor"
    "conductor" -> "Conductor"
    "cliente" -> "Cliente"
    else -> nombre.replaceFirstChar { it.uppercase() }
}

private fun tonoRol(nombre: String): Tono = when (nombre) {
    "admin" -> Tono.MARCA
    "recepcionista" -> Tono.INFO
    "repartidor", "conductor" -> Tono.ALERTA
    "cliente" -> Tono.NEUTRO
    else -> Tono.EXITO
}

private sealed interface HojaUsuario {
    data object Nuevo : HojaUsuario
    data class Editar(val usuario: UsuarioGestion) : HojaUsuario
}

/** Gestión de cuentas (`/intranet/usuarios`). */
@Composable
fun UsuariosScreen(vm: UsuariosViewModel, applySystemBarsInsets: Boolean = true) {
    var hoja by remember { mutableStateOf<HojaUsuario?>(null) }
    LargeTitleScreen(
        title = "Usuarios",
        subtitle = "${vm.usuarios.size} cuentas · ${vm.usuarios.count { it.activo }} activas",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = { CobreButton(text = "Nuevo", onClick = { hoja = HojaUsuario.Nuevo }, icon = CobreIcons.Add) },
    ) {
        item(key = "buscar") {
            GlassSearchField(vm.busqueda, vm::buscar, "Buscar por nombre o correo", modifier = Modifier.readableWidth())
        }
        item(key = "filtro") {
            ChipsFiltro(
                FiltrosActivo,
                FiltrosActivo.first { it.valor == vm.filtroActivo },
                { it.etiqueta },
                { vm.filtrar(it.valor) },
                Modifier.readableWidth(),
            )
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        if (vm.filtrados.isEmpty() && !vm.cargando) {
            item(key = "vacio") { EstadoVacio(CobreIcons.Group, "No hay cuentas que coincidan.", modifier = Modifier.readableWidth()) }
        }
        items(vm.filtrados, key = { it.id }) { usuario ->
            UsuarioCard(usuario, onClick = { hoja = HojaUsuario.Editar(usuario) }, modifier = Modifier.readableWidth())
        }
    }
    when (val h = hoja) {
        HojaUsuario.Nuevo -> NuevoUsuarioSheet(vm, onCerrar = { hoja = null })
        is HojaUsuario.Editar -> EditarUsuarioSheet(vm, h.usuario, onCerrar = { hoja = null })
        null -> Unit
    }
}

@Composable
private fun UsuarioCard(usuario: UsuarioGestion, onClick: () -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Iniciales(usuario.nombreCompleto)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombreCompleto, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(
                    usuario.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ChipTono(etiquetaRol(usuario.rol.nombre), tonoRol(usuario.rol.nombre))
                if (!usuario.activo) ChipTono("Inactivo", Tono.NEUTRO)
            }
        }
    }
}

@Composable
private fun NuevoUsuarioSheet(vm: UsuariosViewModel, onCerrar: () -> Unit) {
    var datos by remember { mutableStateOf(NuevoUsuarioInput("", "", "", "", "", "", "", "")) }
    var error by remember { mutableStateOf<String?>(null) }
    val cambiar = { nuevo: NuevoUsuarioInput ->
        datos = nuevo
        error = null
    }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Nueva cuenta",
        subtitle = "La persona entra con su correo y esta contraseña.",
    ) {
        SelectorRol(vm.roles, datos.rolId) { cambiar(datos.copy(rolId = it.id, rolNombre = it.nombre)) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CobreTextField(datos.nombre, { cambiar(datos.copy(nombre = it.take(80))) }, Modifier.weight(1f), label = "Nombre")
            CobreTextField(datos.apellido, { cambiar(datos.copy(apellido = it.take(80))) }, Modifier.weight(1f), label = "Apellido")
        }
        CobreTextField(datos.rut, { cambiar(datos.copy(rut = Validaciones.formatearRut(it))) }, label = "RUT", placeholder = "12.345.678-5")
        CobreTextField(
            datos.telefono,
            { cambiar(datos.copy(telefono = Validaciones.formatearTelefono(it))) },
            label = "Teléfono (opcional)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )
        if (datos.rolNombre == "cliente") {
            GlassSegmentedControl(
                TipoClienteFicha.entries,
                datos.tipoCliente ?: TipoClienteFicha.PARTICULAR,
                { cambiar(datos.copy(tipoCliente = it)) },
                { it.etiqueta },
            )
            CobreTextField(datos.direccion, { cambiar(datos.copy(direccion = it.take(200))) }, label = "Dirección (opcional)")
        }
        CobreTextField(
            datos.email,
            { cambiar(datos.copy(email = it.trim().take(120))) },
            label = "Correo",
            leadingIcon = CobreIcons.Mail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        CobreTextField(
            datos.contrasena,
            { cambiar(datos.copy(contrasena = it)) },
            label = "Contraseña inicial",
            leadingIcon = CobreIcons.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            supportingText = datos.contrasena.takeIf { it.isNotEmpty() }
                ?.let { Validaciones.problemaContrasena(it) ?: "Contraseña válida" },
        )
        ErrorHoja(error)
        CobreButton(
            text = "Crear cuenta",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                val tipo = if (datos.rolNombre == "cliente") datos.tipoCliente ?: TipoClienteFicha.PARTICULAR else null
                vm.crear(datos.copy(tipoCliente = tipo)) { p -> if (p == null) onCerrar() else error = p }
            },
        )
    }
}

@Composable
private fun SelectorRol(roles: List<RolRef>, rolId: String, onSeleccionar: (RolRef) -> Unit) {
    SelectorConBusqueda(
        etiqueta = "Rol",
        valorActual = roles.firstOrNull { it.id == rolId }?.let { etiquetaRol(it.nombre) }.orEmpty(),
        opciones = roles,
        texto = { etiquetaRol(it.nombre) },
        detalle = { it.descripcion },
        onSeleccionar = onSeleccionar,
        placeholder = "Elegir rol",
    )
}

@Composable
private fun EditarUsuarioSheet(vm: UsuariosViewModel, usuario: UsuarioGestion, onCerrar: () -> Unit) {
    var rolId by remember { mutableStateOf(usuario.rol.id) }
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var apellido by remember { mutableStateOf(usuario.apellido.orEmpty()) }
    var telefono by remember { mutableStateOf(usuario.telefono.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmarEstado by remember { mutableStateOf(false) }
    GlassSheet(onDismiss = { if (!vm.guardando) onCerrar() }, title = usuario.nombreCompleto, subtitle = usuario.email) {
        FilaDato("RUT", usuario.rut)
        FilaDato("Creada", usuario.creadoEn)
        SelectorRol(vm.roles, rolId) { rolId = it.id }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CobreTextField(nombre, { nombre = it.take(80) }, Modifier.weight(1f), label = "Nombre")
            CobreTextField(apellido, { apellido = it.take(80) }, Modifier.weight(1f), label = "Apellido")
        }
        CobreTextField(
            telefono,
            { telefono = Validaciones.formatearTelefono(it) },
            label = "Teléfono",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )
        ErrorHoja(error)
        CobreButton(
            text = "Guardar cambios",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                vm.editar(EdicionUsuarioInput(usuario.id, rolId, nombre, apellido, telefono, usuario.activo)) { p ->
                    if (p == null) onCerrar() else error = p
                }
            },
        )
        if (confirmarEstado) {
            Text(
                if (usuario.activo) "La cuenta no podrá entrar a la app ni a la intranet." else "La cuenta podrá volver a entrar.",
                style = MaterialTheme.typography.bodySmall,
                color = CobreTheme.colors.textSecondary,
            )
        }
        CobreButton(
            text = when {
                confirmarEstado && usuario.activo -> "Confirmar desactivación"
                confirmarEstado -> "Confirmar activación"
                usuario.activo -> "Desactivar cuenta"
                else -> "Activar cuenta"
            },
            variant = if (usuario.activo) ButtonVariant.Danger else ButtonVariant.Tonal,
            enabled = !vm.guardando,
            fillWidth = true,
            onClick = {
                if (!confirmarEstado) {
                    confirmarEstado = true
                } else {
                    vm.alternarActivo(usuario)
                    onCerrar()
                }
            },
        )
    }
}
