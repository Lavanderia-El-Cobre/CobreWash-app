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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.DisponibilidadVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.VehiculoReparto
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
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.RecordatorioPrivacidadFotos
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.gestion.MovimientoInput
import com.elcobre.lavanderiaelcobre.ui.gestion.RepartoViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.salidaPendienteDesdeCola
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.SelectorCondicionVehiculo
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.SelectorFotos
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.components.rememberAccionesPendientes
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion

private fun tonoSalida(estado: EstadoSalida): Tono = when (estado) {
    EstadoSalida.PROGRAMADA -> Tono.MARCA
    EstadoSalida.EN_SERVICIO -> Tono.INFO
    EstadoSalida.FINALIZADA -> Tono.EXITO
    EstadoSalida.CANCELADA -> Tono.NEUTRO
}

private fun tonoDisponibilidad(d: DisponibilidadVehiculo): Tono = when (d) {
    DisponibilidadVehiculo.CONTIGO -> Tono.INFO
    DisponibilidadVehiculo.DISPONIBLE -> Tono.EXITO
    DisponibilidadVehiculo.EN_RUTA -> Tono.NEUTRO
}

/** Qué formulario está abierto: la salida de un vehículo, el regreso o una salida programada. */
private sealed interface Movimiento {
    data class Salida(val vehiculo: VehiculoReparto) : Movimiento
    data class Regreso(val salida: SalidaReparto) : Movimiento
    data class Programada(val salida: SalidaReparto) : Movimiento
}

/**
 * "Mi vehículo" del repartidor, como `/intranet/reparto` de la web: gestiona la salida en
 * curso y su regreso, las salidas programadas por administración, la flota disponible y el
 * historial. Observa la cola offline para impedir salidas o regresos duplicados sin red.
 */
@OptIn(ExperimentalLayoutApi::class)
@Suppress("LongMethod") // Una lista con sus secciones; cada tarjeta ya vive en su propia función.
@Composable
fun RepartoScreen(vm: RepartoViewModel, nombre: String, applySystemBarsInsets: Boolean = true) {
    var movimiento by remember { mutableStateOf<Movimiento?>(null) }
    val conectado by AccionesSinConexion.conectado.collectAsState()
    val pendientes = rememberAccionesPendientes()
    val regresosSinEnviar = pendientes.filter { it.tipo == TipoAccion.RETORNO_FLOTA }.map { it.entidad }.toSet()
    val salidaPendiente = if (vm.salidaEnCurso == null) salidaPendienteDesdeCola(pendientes, vm.panel) else null
    val salidaEnCurso = vm.salidaEnCurso ?: salidaPendiente
    val salidaSinEnviar = salidaPendiente != null && salidaPendiente.id !in regresosSinEnviar
    val salidaYRegresoSinEnviar = salidaPendiente != null && salidaPendiente.id in regresosSinEnviar
    LargeTitleScreen(
        title = "Mi vehículo",
        subtitle = "Salidas y regresos de ${nombre.substringBefore(' ')}",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(CobreIcons.Refresh, "Actualizar vehículos", { vm.refrescar() }, size = 44.dp, enabled = !vm.cargando)
        },
    ) {
        item(key = "kpis") {
            FlowRow(
                modifier = Modifier.readableWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val enRuta = salidaEnCurso != null && salidaEnCurso.id !in regresosSinEnviar
                KpiTile(if (enRuta) "1" else "0", "En ruta", Tono.INFO)
                val disponibles = vm.panel.vehiculos.count { it.disponibilidad == DisponibilidadVehiculo.DISPONIBLE }
                KpiTile(disponibles.toString(), "Disponibles", Tono.EXITO)
                KpiTile(vm.historial.size.toString(), "Finalizadas", Tono.MARCA)
                KpiTile("${vm.kilometrosRecorridos} km", "Recorridos", Tono.NEUTRO)
            }
        }
        item(key = "estado") {
            Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvisoExito(vm.mensaje, onCerrar = vm::limpiarMensajes)
                EstadoCarga(vm.cargando, vm.error)
            }
        }
        if (salidaYRegresoSinEnviar) {
            item(key = "salida-regreso-sin-enviar") {
                EstadoVacio(
                    CobreIcons.Schedule,
                    "Tu salida y regreso quedaron guardados en el teléfono y se enviarán en orden al recuperar la conexión.",
                    modifier = Modifier.readableWidth(),
                )
            }
        }
        seccionEnRuta(salidaEnCurso, salidaPendiente != null, regresosSinEnviar) { movimiento = Movimiento.Regreso(it) }
        seccionProgramadas(vm.programadas, conectado, salidaEnCurso == null) { movimiento = Movimiento.Programada(it) }
        listaVehiculos(vm, salidaSinEnviar) { movimiento = Movimiento.Salida(it) }
        if (vm.historial.isNotEmpty()) {
            item(key = "t-historial") { TituloGrupo("Historial", Modifier.readableWidth()) }
            items(vm.historial, key = { it.id }) { SalidaCard(it, Modifier.readableWidth()) }
        }
    }
    HojaMovimiento(vm, movimiento, onCerrar = { movimiento = null })
}

@Composable
private fun HojaMovimiento(vm: RepartoViewModel, movimiento: Movimiento?, onCerrar: () -> Unit) {
    when (val m = movimiento) {
        is Movimiento.Salida -> MovimientoSheet(
            vm, titulo = "Salida · ${m.vehiculo.patente}", detalle = m.vehiculo.vehiculo, esSalida = true,
            minimo = m.vehiculo.kilometrajeActual, onCerrar = onCerrar,
        ) { input, fin -> vm.registrarSalida(m.vehiculo, input, fin) }
        is Movimiento.Regreso -> MovimientoSheet(
            vm, titulo = "Regreso · ${m.salida.patente}", detalle = m.salida.vehiculo, esSalida = false,
            minimo = m.salida.antes?.kilometraje ?: 0, onCerrar = onCerrar,
        ) { input, fin -> vm.registrarRetorno(m.salida, input, fin) }
        is Movimiento.Programada -> MovimientoSheet(
            vm, titulo = "Iniciar · ${m.salida.patente}", detalle = m.salida.vehiculo, esSalida = true,
            minimo = vm.kilometrajeMinimo(m.salida), onCerrar = onCerrar, yaInspeccionada = m.salida.antes,
        ) { input, fin ->
            vm.iniciarProgramada(
                m.salida,
                InspeccionInput(
                    m.salida.id, input.kilometraje ?: -1, input.estado, input.observaciones.trim().ifEmpty { null }, input.fotos,
                ),
                fin,
            )
        }
        null -> Unit
    }
}

private fun LazyListScope.seccionEnRuta(
    salidaEnCurso: SalidaReparto?,
    esPendiente: Boolean,
    regresosSinEnviar: Set<String>,
    onRegreso: (SalidaReparto) -> Unit,
) {
    val salida = salidaEnCurso ?: return
    val tituloEnRuta = if (esPendiente) "En ruta (sin enviar)" else "En ruta"
    item(key = "t-en-curso") { TituloGrupo(tituloEnRuta, Modifier.readableWidth()) }
    item(key = salida.id) {
        SalidaCard(salida, Modifier.readableWidth()) {
            CobreButton(
                text = if (salida.id in regresosSinEnviar) "Regreso sin enviar" else "Registrar regreso",
                onClick = { onRegreso(salida) },
                icon = CobreIcons.CheckCircle,
                fillWidth = true,
                enabled = salida.id !in regresosSinEnviar,
            )
        }
    }
}

private fun LazyListScope.seccionProgramadas(
    programadas: List<SalidaReparto>,
    conectado: Boolean,
    sinSalidaEnCurso: Boolean,
    onIniciar: (SalidaReparto) -> Unit,
) {
    if (programadas.isEmpty()) return
    item(key = "t-programadas") { TituloGrupo("Programadas por administración", Modifier.readableWidth()) }
    items(programadas, key = { it.id }) { salida ->
        SalidaCard(salida, Modifier.readableWidth()) {
            CobreButton(
                text = when {
                    !conectado -> "Requiere conexión"
                    salida.antes == null -> "Inspeccionar e iniciar"
                    else -> "Iniciar recorrido"
                },
                onClick = { onIniciar(salida) },
                icon = CobreIcons.LocalShipping,
                variant = ButtonVariant.Tonal,
                fillWidth = true,
                enabled = conectado && sinSalidaEnCurso,
            )
        }
    }
}

private fun LazyListScope.listaVehiculos(vm: RepartoViewModel, salidaSinEnviar: Boolean, onSalida: (VehiculoReparto) -> Unit) {
    item(key = "t-vehiculos") { TituloGrupo("Vehículos", Modifier.readableWidth()) }
    if (vm.panel.vehiculos.isEmpty() && !vm.cargando) {
        item(key = "sin-vehiculos") {
            EstadoVacio(CobreIcons.LocalShipping, "No hay vehículos activos. Avisa a administración.", modifier = Modifier.readableWidth())
        }
    }
    items(vm.panel.vehiculos, key = { "v-${it.id}" }) { vehiculo ->
        val colors = CobreTheme.colors
        ContentCard(modifier = Modifier.readableWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(vehiculo.patente, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                    Text(vehiculo.vehiculo, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    vehiculo.descripcion?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary) }
                }
                ChipTono(vehiculo.disponibilidad.etiqueta, tonoDisponibilidad(vehiculo.disponibilidad))
            }
            FilaDato("Kilometraje", "${vehiculo.kilometrajeActual} km")
            if (vehiculo.disponibilidad == DisponibilidadVehiculo.EN_RUTA) FilaDato("Lo tiene", vehiculo.conductorActual)
            if (vehiculo.disponibilidad == DisponibilidadVehiculo.DISPONIBLE) {
                Spacer(Modifier.height(8.dp))
                CobreButton(
                    text = "Registrar salida",
                    onClick = { onSalida(vehiculo) },
                    icon = CobreIcons.LocalShipping,
                    variant = ButtonVariant.Tonal,
                    fillWidth = true,
                    enabled = vm.salidaEnCurso == null && !salidaSinEnviar && !vm.guardando,
                )
            }
        }
    }
}

@Composable
private fun SalidaCard(salida: SalidaReparto, modifier: Modifier, accion: (@Composable () -> Unit)? = null) {
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
        FilaDato("Salida", salida.fechaSalida)
        FilaDato("Regreso", salida.fechaRetorno)
        salida.antes?.let { FilaDato("Al salir", resumenInspeccion(it)) }
        salida.despues?.let { FilaDato("Al volver", resumenInspeccion(it)) }
        salida.kilometrosRecorridos?.let { FilaDato("Recorrido", "$it km") }
        FilaDato("Notas", salida.observaciones)
        if (accion != null) {
            Spacer(Modifier.height(8.dp))
            accion()
        }
    }
}

private fun resumenInspeccion(i: InspeccionReparto): String =
    listOfNotNull("${i.kilometraje} km", i.estado.etiqueta, if (i.fotos > 0) "${i.fotos} foto(s)" else null, i.observaciones)
        .joinToString(" · ")

/**
 * Formulario de salida o regreso: odómetro, condición del vehículo, fotos y observaciones.
 * Si [yaInspeccionada] no es nulo, omite los campos de chequeo y solo pide confirmar el inicio.
 */
@Suppress("LongParameterList")
@Composable
private fun MovimientoSheet(
    vm: RepartoViewModel,
    titulo: String,
    detalle: String,
    esSalida: Boolean,
    minimo: Int,
    onCerrar: () -> Unit,
    yaInspeccionada: InspeccionReparto? = null,
    onConfirmar: (MovimientoInput, (String?) -> Unit) -> Unit,
) {
    var km by remember { mutableStateOf((yaInspeccionada?.kilometraje ?: minimo).toString()) }
    var estado by remember { mutableStateOf(yaInspeccionada?.estado ?: EstadoCondicionVehiculo.APTO) }
    var observaciones by remember { mutableStateOf("") }
    val fotos = remember { mutableStateOf(emptyList<FotoChequeo>()) }
    var error by remember { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current
    val accent = CobreTheme.colors.accent
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = titulo,
        subtitle = "$detalle · Odómetro mínimo: $minimo km",
    ) {
        if (yaInspeccionada != null) {
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
                label = if (esSalida) "Kilometraje de salida" else "Kilometraje de regreso",
                leadingIcon = CobreIcons.Speed,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            SelectorFotos(accent, fotos, haptic, enabled = !vm.guardando)
            RecordatorioPrivacidadFotos()
            CobreTextField(
                observaciones,
                { if (it.length <= 500) observaciones = it },
                label = if (estado == EstadoCondicionVehiculo.APTO) "Observaciones (opcional)" else "Describe lo que observaste",
                placeholder = "Ej. Rayón leve en parachoques trasero",
                singleLine = false,
                minLines = 2,
            )
        }
        ErrorHoja(error)
        CobreButton(
            text = if (esSalida) "Confirmar salida" else "Confirmar regreso",
            icon = CobreIcons.Check,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                onConfirmar(MovimientoInput(km.toIntOrNull(), estado, observaciones, fotos.value)) { p ->
                    if (p == null) onCerrar() else error = p
                }
            },
        )
    }
}
