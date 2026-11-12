package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.ui.theme.CobreColors
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Componente interactivo que reproduce `FlujoProduccion.tsx` de la versión web:
 * porcentaje global de avance, etapas segmentadas con números o checks y acciones
 * directas (avanzar en cobre, resolver o reportar incidencia).
 */
@Composable
fun FlujoProduccionCard(
    pedido: Pedido,
    onAvanzar: () -> Unit,
    onResolverAlerta: () -> Unit,
    onReportarIncidencia: () -> Unit,
    habilitado: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    val porcentaje = ((pedido.indiceEtapa + (if (pedido.esFinal) 1 else 0)).toFloat() / pedido.totalEtapas * 100).toInt()
        .coerceIn(0, 100)

    ContentCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ESTADO GLOBAL DE PRODUCCIÓN", style = CobreTheme.text.labelCaps, color = colors.textSecondary)
                Text("Flujo de lavado y acondicionamiento", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            Text("$porcentaje%", style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary)
        }

        Spacer(Modifier.height(16.dp))

        if (pedido.estaBloqueado) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.md)
                    .background(colors.danger.copy(alpha = 0.10f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(CobreIcons.Warning, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(22.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Incidencia activa: ${pedido.alertaActiva?.displayName}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.dangerText,
                    )
                    Text(
                        pedido.alertaActiva?.descripcionCorta ?: "El avance está detenido hasta resolver.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.dangerText,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        EtapasCadenaRow(pedido = pedido)

        Spacer(Modifier.height(20.dp))

        FlujoAccionesRow(
            pedido = pedido,
            habilitado = habilitado,
            onAvanzar = onAvanzar,
            onResolverAlerta = onResolverAlerta,
            onReportarIncidencia = onReportarIncidencia,
        )
    }
}

private enum class EstadoChipEtapa { COMPLETADA, ACTIVA, PENDIENTE }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EtapasCadenaRow(pedido: Pedido) {
    val indiceActual = pedido.indiceEtapa
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        pedido.flujo.forEachIndexed { index, etapa ->
            val estado = when {
                index < indiceActual || (pedido.esFinal && index == indiceActual) -> EstadoChipEtapa.COMPLETADA
                index == indiceActual && !pedido.esFinal -> EstadoChipEtapa.ACTIVA
                else -> EstadoChipEtapa.PENDIENTE
            }
            EtapaCadenaChip(etapaNombre = etapa.corto, numero = index + 1, estado = estado)
        }
    }
}

/** Colores de un chip de etapa: completada = verde, en curso = índigo (proceso), pendiente = neutro. */
private data class ColoresEtapa(val fondo: Color, val texto: Color, val insignia: Color, val subtitulo: String)

private fun coloresEtapa(estado: EstadoChipEtapa, colors: CobreColors): ColoresEtapa = when (estado) {
    EstadoChipEtapa.COMPLETADA -> ColoresEtapa(colors.success.copy(alpha = 0.10f), colors.successText, colors.success, "Completada")
    EstadoChipEtapa.ACTIVA -> ColoresEtapa(colors.infoContainer, colors.info, colors.info, "En curso")
    EstadoChipEtapa.PENDIENTE -> ColoresEtapa(colors.surfaceMuted, colors.textSecondary, colors.hairline, "Pendiente")
}

@Composable
private fun EtapaCadenaChip(etapaNombre: String, numero: Int, estado: EstadoChipEtapa) {
    val colors = CobreTheme.colors
    val c = coloresEtapa(estado, colors)
    Row(
        modifier = Modifier
            .clip(CobreShapes.md)
            .background(c.fondo)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CobreShapes.full).background(c.insignia),
            contentAlignment = Alignment.Center,
        ) {
            if (estado == EstadoChipEtapa.COMPLETADA) {
                Icon(CobreIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            } else {
                Text(
                    "$numero",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (estado == EstadoChipEtapa.ACTIVA) Color.White else colors.textSecondary,
                )
            }
        }
        Column {
            Text(etapaNombre, style = MaterialTheme.typography.labelMedium, color = c.texto, fontWeight = FontWeight.Bold)
            Text(c.subtitulo.uppercase(), style = CobreTheme.text.labelCaps, color = colors.textSecondary)
        }
    }
}

@Composable
private fun FlujoAccionesRow(
    pedido: Pedido,
    habilitado: Boolean,
    onAvanzar: () -> Unit,
    onResolverAlerta: () -> Unit,
    onReportarIncidencia: () -> Unit,
) {
    val siguiente = pedido.siguienteEtapa
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (pedido.estaBloqueado) {
            CobreButton(
                text = "Resolver incidencia",
                onClick = onResolverAlerta,
                icon = CobreIcons.CheckCircle,
                enabled = habilitado,
                modifier = Modifier.weight(1f),
            )
        } else {
            CobreButton(
                text = if (siguiente != null) "Avanzar a ${siguiente.corto}" else "Proceso finalizado",
                onClick = onAvanzar,
                icon = if (siguiente != null) CobreIcons.ArrowForward else CobreIcons.DoneAll,
                enabled = siguiente != null && habilitado,
                modifier = Modifier.weight(1f),
            )
        }
        if (!pedido.estaBloqueado && !pedido.esFinal) {
            CobreButton(
                text = "Incidencia",
                onClick = onReportarIncidencia,
                variant = ButtonVariant.Danger,
                icon = CobreIcons.Warning,
            )
        }
    }
}
