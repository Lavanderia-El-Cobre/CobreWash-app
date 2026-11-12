package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Cronología de eventos del pedido (avances, comentarios, alertas), como lista agrupada:
 * encabezado en `label-caps` y una tarjeta sólida con la línea de tiempo.
 * Compartida entre DetalleScreen y el panel derecho de la tablet en DashboardScreen.
 */
@Composable
fun HistorialCard(pedido: Pedido, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Historial", modifier = Modifier.padding(start = 4.dp))
        ContentCard {
            if (pedido.historial.isEmpty()) {
                Text("Sin eventos registrados todavía.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }
            pedido.historial.forEachIndexed { index, evento ->
                EventoFila(
                    tipo = evento.tipo,
                    titulo = evento.titulo,
                    detalle = evento.detalle,
                    hora = evento.hora,
                    autor = evento.autor,
                    esUltimo = index == pedido.historial.size - 1,
                )
            }
        }
    }
}

@Composable
fun EventoFila(
    tipo: TipoEvento,
    titulo: String,
    detalle: String,
    hora: String,
    autor: String,
    esUltimo: Boolean,
) {
    val colors = CobreTheme.colors
    val color = eventoColor(tipo)
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CobreShapes.full)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(eventoIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            if (!esUltimo) {
                Box(modifier = Modifier.width(1.dp).height(26.dp).background(colors.hairline))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f).padding(bottom = if (esUltimo) 0.dp else 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(titulo, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, modifier = Modifier.weight(1f))
                Text(hora, style = CobreTheme.text.label, color = colors.textSecondary)
            }
            Spacer(Modifier.height(2.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
            Text(autor, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
        }
    }
}
