package com.elcobre.lavanderiaelcobre.ui.screens.vehiculos

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.SelectorConBusqueda
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Programa una salida para un repartidor (`CrearSalidaVehiculo`): queda PROGRAMADA y el
 * repartidor la inspecciona con fotos e inicia desde su app, en vez de despacharla aquí.
 */
@Composable
internal fun ProgramarSalidaSheet(
    vehiculos: List<Vehiculo>,
    conductores: List<ConductorFlota>,
    guardando: Boolean,
    onProgramar: (vehiculoId: String, repartidorId: String, observaciones: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var vehiculo by remember { mutableStateOf<Vehiculo?>(null) }
    var conductor by remember { mutableStateOf(conductores.singleOrNull()) }
    var observaciones by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!guardando) onDismiss() },
        title = "Programar salida",
        subtitle = "El repartidor hará la inspección previa e iniciará el recorrido desde su app.",
    ) {
        SelectorConBusqueda(
            etiqueta = "Vehículo",
            valorActual = vehiculo?.let { "${it.patente} · ${it.nombreCompleto}" }.orEmpty(),
            opciones = vehiculos,
            texto = { it.patente },
            detalle = { it.nombreCompleto },
            onSeleccionar = { vehiculo = it },
            placeholder = if (vehiculos.isEmpty()) "No hay vehículos disponibles" else "Elegir vehículo",
        )
        SelectorConBusqueda(
            etiqueta = "Repartidor",
            valorActual = conductor?.nombreCompleto.orEmpty(),
            opciones = conductores,
            texto = { it.nombreCompleto },
            detalle = { it.rut },
            onSeleccionar = { conductor = it },
            placeholder = if (conductores.isEmpty()) "No hay repartidores activos" else "Elegir repartidor",
        )
        CobreTextField(
            value = observaciones,
            onValueChange = { if (it.length <= 300) observaciones = it },
            label = "Indicaciones (opcional)",
            placeholder = "Ruta, horario o carga a retirar",
            singleLine = false,
            minLines = 2,
        )
        if (conductores.isEmpty()) {
            Text(
                "Crea una cuenta con rol repartidor en Más › Usuarios para poder asignarle salidas.",
                style = MaterialTheme.typography.bodySmall,
                color = CobreTheme.colors.textSecondary,
            )
        }
        ErrorHoja(error)
        CobreButton(
            text = "Programar salida",
            icon = CobreIcons.Schedule,
            loading = guardando,
            fillWidth = true,
            onClick = {
                val v = vehiculo
                val c = conductor
                if (v == null || c == null) {
                    error = "Elige el vehículo y el repartidor."
                } else {
                    onProgramar(v.id, c.id, observaciones.trim().ifEmpty { null })
                }
            },
        )
    }
}

/** Muestra [ProgramarSalidaSheet] con los vehículos disponibles y los repartidores activos. */
@Composable
internal fun HojaProgramarSalida(
    abierta: Boolean,
    vehiculos: List<Vehiculo>,
    conductores: List<ConductorFlota>,
    chequeoEnRuta: (String) -> com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo?,
    guardando: Boolean,
    onProgramar: ((vehiculoId: String, repartidorId: String, observaciones: String?) -> Unit)?,
    onCerrar: () -> Unit,
) {
    if (!abierta || onProgramar == null) return
    ProgramarSalidaSheet(
        vehiculos = vehiculos.filter { it.activo && chequeoEnRuta(it.id)?.estaEnRuta != true },
        conductores = conductores.filter { com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor(it.rol) },
        guardando = guardando,
        onProgramar = { vehiculoId, repartidorId, obs ->
            onProgramar(vehiculoId, repartidorId, obs)
            onCerrar()
        },
        onDismiss = onCerrar,
    )
}
