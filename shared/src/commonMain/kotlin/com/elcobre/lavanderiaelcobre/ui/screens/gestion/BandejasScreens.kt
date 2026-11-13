package com.elcobre.lavanderiaelcobre.ui.screens.gestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EstadoSolicitud
import com.elcobre.lavanderiaelcobre.data.model.IncidenciaGestion
import com.elcobre.lavanderiaelcobre.data.model.SolicitudContacto
import com.elcobre.lavanderiaelcobre.ui.components.AvisoExito
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.ChipsFiltro
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.CotizacionesViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.IncidenciasViewModel
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private fun tonoIncidencia(estado: EstadoIncidencia): Tono = when (estado) {
    EstadoIncidencia.ABIERTA -> Tono.PELIGRO
    EstadoIncidencia.EN_REVISION -> Tono.ALERTA
    EstadoIncidencia.RESUELTA -> Tono.EXITO
}

private fun tonoSolicitud(estado: EstadoSolicitud): Tono = when (estado) {
    EstadoSolicitud.RECIBIDA -> Tono.MARCA
    EstadoSolicitud.EN_REVISION -> Tono.ALERTA
    EstadoSolicitud.ATENDIDA -> Tono.EXITO
}

/** Filtro "Todas" + cada estado, para los chips de las bandejas. */
private data class OpcionFiltro<T>(val etiqueta: String, val valor: T?)

/** Incidencias de producción con su gestión de estado (admin y recepción). */
@Composable
fun IncidenciasScreen(vm: IncidenciasViewModel, applySystemBarsInsets: Boolean = true) {
    val opciones = listOf(OpcionFiltro<EstadoIncidencia>("Todas", null)) + EstadoIncidencia.entries.map { OpcionFiltro(it.etiqueta, it) }
    LargeTitleScreen(
        title = "Incidencias",
        subtitle = "${vm.abiertas} sin resolver de ${vm.incidencias.size}",
        applySystemBarsInsets = applySystemBarsInsets,
    ) {
        item(key = "filtro") {
            ChipsFiltro(
                opciones,
                opciones.first { it.valor == vm.filtro },
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
        if (vm.filtradas.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(CobreIcons.CheckCircle, "No hay incidencias en este estado.", modifier = Modifier.readableWidth())
            }
        }
        items(vm.filtradas, key = { it.id }) { incidencia ->
            IncidenciaCard(incidencia, vm.guardando, onCambiar = { vm.cambiarEstado(incidencia, it) }, modifier = Modifier.readableWidth())
        }
    }
}

@Composable
private fun IncidenciaCard(incidencia: IncidenciaGestion, guardando: Boolean, onCambiar: (EstadoIncidencia) -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, stripe = if (incidencia.estado == EstadoIncidencia.ABIERTA) colors.danger else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(incidencia.motivo, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(
                    "${incidencia.numeroComanda} · ${incidencia.cliente}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            ChipTono(incidencia.estado.etiqueta, tonoIncidencia(incidencia.estado))
        }
        if (incidencia.descripcion.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(incidencia.descripcion, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
        }
        Spacer(Modifier.height(6.dp))
        Text("Reportada por ${incidencia.reportadaPor} · ${incidencia.fecha}", style = CobreTheme.text.label, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        GlassSegmentedControl(
            options = EstadoIncidencia.entries,
            selected = incidencia.estado,
            onSelect = { if (!guardando) onCambiar(it) },
            label = { it.etiqueta },
        )
    }
}

/** Bandeja de cotizaciones del sitio público, con su estado de atención. */
@Composable
fun CotizacionesScreen(vm: CotizacionesViewModel, applySystemBarsInsets: Boolean = true) {
    val opciones = listOf(OpcionFiltro<EstadoSolicitud>("Todas", null)) + EstadoSolicitud.entries.map { OpcionFiltro(it.etiqueta, it) }
    LargeTitleScreen(
        title = "Cotizaciones",
        subtitle = "${vm.total} solicitudes del formulario de contacto",
        applySystemBarsInsets = applySystemBarsInsets,
    ) {
        item(key = "filtro") {
            ChipsFiltro(
                opciones,
                opciones.first { it.valor == vm.filtro },
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
        if (vm.solicitudes.isEmpty() && !vm.cargando) {
            item(key = "vacio") {
                EstadoVacio(CobreIcons.RequestQuote, "No hay solicitudes en este estado.", modifier = Modifier.readableWidth())
            }
        }
        items(vm.solicitudes, key = { it.id }) { solicitud ->
            SolicitudCard(solicitud, vm.guardando, onCambiar = { vm.cambiarEstado(solicitud, it) }, modifier = Modifier.readableWidth())
        }
        if (vm.hayMas) {
            item(key = "mas") {
                CobreButton(
                    text = "Cargar más",
                    onClick = { vm.refrescar(mas = true) },
                    variant = ButtonVariant.Tonal,
                    fillWidth = true,
                    modifier = Modifier.readableWidth(),
                )
            }
        }
    }
}

@Composable
private fun SolicitudCard(solicitud: SolicitudContacto, guardando: Boolean, onCambiar: (EstadoSolicitud) -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(solicitud.nombre, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(solicitud.servicio, style = MaterialTheme.typography.bodySmall, color = colors.accentText)
            }
            ChipTono(solicitud.estado.etiqueta, tonoSolicitud(solicitud.estado))
        }
        Spacer(Modifier.height(6.dp))
        Text(solicitud.mensaje, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
        Spacer(Modifier.height(6.dp))
        Text("${solicitud.email} · ${solicitud.telefono}", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        Text("Recibida el ${solicitud.creadoEn}", style = CobreTheme.text.label, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        GlassSegmentedControl(
            options = EstadoSolicitud.entries,
            selected = solicitud.estado,
            onSelect = { if (!guardando) onCambiar(it) },
            label = { it.etiqueta },
        )
    }
}
