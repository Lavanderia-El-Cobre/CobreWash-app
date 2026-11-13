package com.elcobre.lavanderiaelcobre.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.FilaDato
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.Iniciales
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.ClientesViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.comandas.CamposCliente
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Hoja abierta: ficha nueva ([cliente] nulo) o edición de una existente. */
private data class EdicionFicha(val cliente: ClienteFicha?)

/** Fichas de clientes con historial resumido, alta y edición (`/intranet/clientes`). */
@Composable
fun ClientesScreen(vm: ClientesViewModel, applySystemBarsInsets: Boolean = true) {
    var edicion by remember { mutableStateOf<EdicionFicha?>(null) }
    LargeTitleScreen(
        title = "Clientes",
        subtitle = "${vm.clientes.size} fichas registradas",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = { CobreButton(text = "Nuevo", onClick = { edicion = EdicionFicha(null) }, icon = CobreIcons.Add) },
    ) {
        item(key = "buscar") {
            GlassSearchField(vm.busqueda, vm::buscar, "Buscar por nombre, RUT o correo", modifier = Modifier.readableWidth())
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        val lista = vm.filtrados
        if (lista.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(CobreIcons.Contacts, "No hay clientes que coincidan.", modifier = Modifier.readableWidth())
            }
        }
        items(lista, key = { it.id }) { cliente ->
            ClienteCard(cliente, onClick = { edicion = EdicionFicha(cliente) }, modifier = Modifier.readableWidth())
        }
    }
    edicion?.let { e -> FichaClienteSheet(vm, e.cliente, onCerrar = { edicion = null }) }
}

@Composable
private fun ClienteCard(cliente: ClienteFicha, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Iniciales(cliente.nombre)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cliente.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    listOfNotNull(cliente.rut, cliente.telefono, cliente.email).joinToString(" · ").ifEmpty { "Sin datos de contacto" },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChipTono(cliente.tipo.etiqueta, if (cliente.tipo == TipoClienteFicha.HOTEL) Tono.INFO else Tono.NEUTRO)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Resumen("Comandas", cliente.comandas.toString())
            Resumen("Activas", cliente.comandasActivas.toString())
            Resumen("Prendas", cliente.prendas.toString())
            Resumen("Facturado", clp(cliente.totalFacturado))
        }
    }
}

@Composable
private fun Resumen(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = CobreTheme.colors.textSecondary)
        Text(valor, style = MaterialTheme.typography.labelLarge, color = CobreTheme.colors.textPrimary)
    }
}

@Composable
private fun FichaClienteSheet(vm: ClientesViewModel, cliente: ClienteFicha?, onCerrar: () -> Unit) {
    var datos by remember {
        mutableStateOf(
            NuevoClienteInput(
                nombre = cliente?.nombre.orEmpty(),
                tipo = cliente?.tipo ?: TipoClienteFicha.PARTICULAR,
                rut = cliente?.rut.orEmpty(),
                telefono = cliente?.telefono.orEmpty(),
                email = cliente?.email.orEmpty(),
                direccion = cliente?.direccion.orEmpty(),
            ),
        )
    }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = if (cliente == null) "Nuevo cliente" else "Ficha de cliente",
        subtitle = cliente?.let { "Registrado el ${it.creadoEn}" } ?: "El RUT es opcional, pero no se puede repetir.",
    ) {
        if (cliente != null) {
            FilaDato("Comandas", "${cliente.comandas} (${cliente.comandasActivas} activas)")
            FilaDato("Última", cliente.ultimaComanda)
            FilaDato("Facturado", clp(cliente.totalFacturado))
        }
        CamposCliente(datos, enabled = !vm.guardando) {
            datos = it
            error = null
        }
        ErrorHoja(error)
        CobreButton(
            text = if (cliente == null) "Registrar cliente" else "Guardar cambios",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = { vm.guardar(cliente?.id, datos) { problema -> if (problema == null) onCerrar() else error = problema } },
        )
    }
}
