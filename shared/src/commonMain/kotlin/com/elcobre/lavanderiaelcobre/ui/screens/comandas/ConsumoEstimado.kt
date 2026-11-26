package com.elcobre.lavanderiaelcobre.ui.screens.comandas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.EstadoEstimacion
import com.elcobre.lavanderiaelcobre.data.model.InsumoEstimado
import com.elcobre.lavanderiaelcobre.data.model.NivelStock
import com.elcobre.lavanderiaelcobre.data.model.formatearCantidad
import com.elcobre.lavanderiaelcobre.data.model.rangoConsumo
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

private fun tonoNivel(nivel: NivelStock): Tono = when (nivel) {
    NivelStock.OK -> Tono.EXITO
    NivelStock.JUSTO -> Tono.ALERTA
    NivelStock.INSUFICIENTE -> Tono.PELIGRO
}

/**
 * Consumo de insumos estimado para la comanda, como `ConsumoEstimado` de la web. La
 * calcula el servidor; aquí solo se muestra. No impide guardar ni modifica el stock.
 */
@Composable
internal fun ConsumoEstimadoPanel(estado: EstadoEstimacion, servicio: String) {
    val colors = CobreTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.accentContainer.copy(alpha = 0.5f), CobreShapes.md)
            .padding(12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionLabel("Consumo estimado · Módulo IA")
        when (estado) {
            EstadoEstimacion.SinPeso -> Nota("Ingresa el peso total para estimar el consumo de insumos.")
            EstadoEstimacion.Cargando -> Nota("Calculando estimación…")
            EstadoEstimacion.NoDisponible -> Nota("No se pudo cargar la estimación. Puedes guardar la comanda igualmente.")
            is EstadoEstimacion.Lista -> {
                val e = estado.estimacion
                if (e.sinTasas) {
                    Nota("El servicio «${servicio.ifBlank { "sin servicio" }}» aún no tiene tasas de consumo en el Módulo IA.")
                } else {
                    e.insumos.forEach { FilaInsumo(it) }
                    if (e.faltantes.isNotEmpty()) {
                        Text(
                            "No hay stock suficiente de ${e.faltantes.joinToString(", ") { it.nombre }} para esta comanda. " +
                                "Avisa a administración antes de procesarla.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.dangerText,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaInsumo(insumo: InsumoEstimado) {
    val colors = CobreTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(insumo.nombre, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
            Text(
                listOfNotNull(
                    rangoConsumo(insumo.minimo, insumo.maximo, insumo.unidad),
                    insumo.stockActual?.let { "stock ${formatearCantidad(it)} ${insumo.unidad}" },
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        ChipTono(insumo.nivel.etiqueta, tonoNivel(insumo.nivel))
    }
}

@Composable
private fun Nota(texto: String) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = CobreTheme.colors.textSecondary)
}
