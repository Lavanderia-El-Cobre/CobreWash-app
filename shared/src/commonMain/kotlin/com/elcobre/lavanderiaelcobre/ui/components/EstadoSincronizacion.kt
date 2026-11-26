package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.fechaHoraLocal
import com.elcobre.lavanderiaelcobre.data.offline.AccionPendiente
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.EstadoAccion
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private val SIN_COLA = MutableStateFlow(emptyList<AccionPendiente>())

/**
 * Estado del modo sin conexión: "Sin conexión", acciones por enviar o que requieren
 * atención. Al tocarlo se abre la bandeja para enviarlas ahora, reintentar o descartar.
 * No se muestra cuando todo está enviado y hay conexión.
 */
@Composable
fun EstadoSincronizacion(modifier: Modifier = Modifier) {
    val cola = AccionesSinConexion.cola
    val acciones by (cola?.acciones ?: SIN_COLA).collectAsState()
    val conectado by AccionesSinConexion.conectado.collectAsState()
    val uid = AccionesSinConexion.uid()
    val propias = acciones.filter { it.uid == uid }
    val atencion = propias.count { it.estado == EstadoAccion.REQUIERE_ATENCION }
    val pendientes = propias.size - atencion
    var abierta by remember { mutableStateOf(false) }
    if (conectado && propias.isEmpty()) return
    val colors = CobreTheme.colors
    val texto = listOfNotNull(
        "Sin conexión".takeIf { !conectado },
        "$pendientes por enviar".takeIf { pendientes > 0 },
        "$atencion requieren atención".takeIf { atencion > 0 },
    ).joinToString(" · ")
    Row(
        modifier = modifier
            .background(if (atencion > 0) colors.danger.copy(alpha = 0.92f) else colors.surface, CobreShapes.full)
            .clickable(role = Role.Button, onClickLabel = "Ver acciones pendientes", enabled = propias.isNotEmpty()) { abierta = true }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            if (atencion > 0) CobreIcons.Warning else CobreIcons.Schedule,
            contentDescription = null,
            tint = if (atencion > 0) Color.White else colors.textSecondary,
        )
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            color = if (atencion > 0) Color.White else colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
        )
    }
    if (abierta) BandejaPendientes(propias, conectado, onCerrar = { abierta = false })
}

@Composable
private fun BandejaPendientes(acciones: List<AccionPendiente>, conectado: Boolean, onCerrar: () -> Unit) {
    val scope = rememberCoroutineScope()
    var descartando by remember { mutableStateOf<AccionPendiente?>(null) }
    var enviando by remember { mutableStateOf(false) }
    GlassSheet(
        onDismiss = onCerrar,
        title = "Acciones sin enviar",
        subtitle = "Se guardaron en el teléfono con la hora en que ocurrieron y se envían solas al recuperar la conexión.",
    ) {
        if (acciones.isEmpty()) {
            Text("No quedan acciones pendientes.", color = CobreTheme.colors.textSecondary)
        }
        acciones.forEach { accion ->
            FilaAccion(
                accion,
                onReintentar = { scope.launch { AccionesSinConexion.sincronizador?.reintentar(accion.id) } },
                onDescartar = { descartando = accion },
            )
        }
        CobreButton(
            text = if (conectado) "Enviar ahora" else "Sin conexión",
            onClick = {
                enviando = true
                scope.launch {
                    AccionesSinConexion.sincronizar(forzar = true)
                    enviando = false
                }
            },
            icon = CobreIcons.Send,
            loading = enviando,
            enabled = conectado && acciones.any { it.estado == EstadoAccion.PENDIENTE },
            fillWidth = true,
        )
    }
    descartando?.let { accion ->
        GlassAlert(
            title = "¿Descartar «${accion.descripcion}»?",
            message = "La acción no se enviará al servidor y sus fotografías se borrarán del teléfono. No se puede deshacer.",
            confirmText = "Descartar",
            destructive = true,
            onConfirm = {
                descartando = null
                scope.launch { AccionesSinConexion.sincronizador?.descartar(accion.id) }
            },
            onDismiss = { descartando = null },
        )
    }
}

@Composable
private fun FilaAccion(accion: AccionPendiente, onReintentar: () -> Unit, onDescartar: () -> Unit) {
    val colors = CobreTheme.colors
    val atencion = accion.estado == EstadoAccion.REQUIERE_ATENCION
    ContentCard(stripe = if (atencion) colors.danger else null) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(accion.descripcion, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
            Text(
                listOfNotNull(
                    "Hecha ${fechaHoraLocal(accion.ocurridoEnMs)}",
                    accion.fotos.size.takeIf { it > 0 }?.let { "$it foto(s)" },
                    "Fotos pendientes".takeIf { accion.aplicada },
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            if (atencion) {
                Text(
                    "Requiere atención: ${accion.ultimoError ?: "el servidor la rechazó."}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CobreButton(text = "Reintentar", onClick = onReintentar, variant = ButtonVariant.Tonal, modifier = Modifier.weight(1f))
                    CobreButton(text = "Descartar", onClick = onDescartar, variant = ButtonVariant.Danger, modifier = Modifier.weight(1f))
                }
            } else {
                ChipTono(if (accion.intentos > 0) "Reintentando" else "En cola", Tono.INFO)
            }
        }
    }
}

/** Acciones de la sesión actual que aún no se envían (para marcar comandas o salidas en las listas). */
@Composable
fun rememberAccionesPendientes(): List<AccionPendiente> {
    val acciones by (AccionesSinConexion.cola?.acciones ?: SIN_COLA).collectAsState()
    val uid = AccionesSinConexion.uid()
    return acciones.filter { it.uid == uid }
}
