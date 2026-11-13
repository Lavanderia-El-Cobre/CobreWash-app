package com.elcobre.lavanderiaelcobre.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.minutosAtraso
import com.elcobre.lavanderiaelcobre.ui.components.ChipTono
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.EstadoCarga
import com.elcobre.lavanderiaelcobre.ui.components.EstadoVacio
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.KpiTile
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.Tono
import com.elcobre.lavanderiaelcobre.ui.components.TituloGrupo
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.components.tonoEstadoComanda
import com.elcobre.lavanderiaelcobre.ui.gestion.PanelViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.comandas.ProgresoEtapas
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.textoAtraso
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/**
 * Panel de inicio del administrador (`/intranet`): cuánta producción hay, cuánta va
 * atrasada y cuáles necesitan atención. Tocar una comanda abre su detalle en Comandas.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InicioAdminScreen(
    vm: PanelViewModel,
    nombre: String,
    ahoraMs: Long,
    onAbrirComanda: (String) -> Unit,
    applySystemBarsInsets: Boolean = true,
) {
    val resumen = vm.resumen
    val atrasadas = resumen?.recientes.orEmpty()
        .map { it to minutosAtraso(it, ahoraMs) }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
    LargeTitleScreen(
        title = "Hola, ${nombre.substringBefore(' ')}",
        subtitle = "Resumen de la producción de hoy",
        applySystemBarsInsets = applySystemBarsInsets,
        trailing = {
            GlassIconButton(CobreIcons.Refresh, "Actualizar panel", onClick = { vm.refrescar() }, size = 44.dp, enabled = !vm.cargando)
        },
    ) {
        item(key = "estado") { EstadoCarga(vm.cargando, vm.error, Modifier.readableWidth()) }
        item(key = "kpis") {
            FlowRow(
                modifier = Modifier.readableWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val total = (resumen?.pendientes ?: 0) + (resumen?.enProceso ?: 0) + (resumen?.listas ?: 0)
                KpiTile(total.toString(), "Producción activa", Tono.MARCA)
                KpiTile((resumen?.pendientes ?: 0).toString(), "Pendientes", Tono.ALERTA)
                KpiTile((resumen?.enProceso ?: 0).toString(), "En proceso", Tono.INFO)
                KpiTile((resumen?.listas ?: 0).toString(), "Listas", Tono.EXITO)
                KpiTile((resumen?.conAtraso ?: 0).toString(), "Con atraso", Tono.PELIGRO)
                KpiTile((resumen?.conIncidencias ?: 0).toString(), "Con incidencias", Tono.PELIGRO)
            }
        }
        item(key = "t-atraso") { TituloGrupo("Atrasadas", Modifier.readableWidth()) }
        if (atrasadas.isEmpty()) {
            item(key = "sin-atraso") {
                EstadoVacio(
                    CobreIcons.CheckCircle,
                    if (resumen == null) "Cargando…" else "Todo va a tiempo.",
                    modifier = Modifier.readableWidth(),
                )
            }
        }
        items(atrasadas, key = { "a-" + it.first.id }) { (comanda, minutos) ->
            FilaPanel(comanda, minutos, onClick = { onAbrirComanda(comanda.id) }, modifier = Modifier.readableWidth())
        }
        val recientes = resumen?.recientes.orEmpty().filter { c -> atrasadas.none { it.first.id == c.id } }.take(12)
        if (recientes.isNotEmpty()) {
            item(key = "t-recientes") { TituloGrupo("Movimiento reciente", Modifier.readableWidth()) }
            items(recientes, key = { "r-" + it.id }) { comanda ->
                FilaPanel(comanda, 0, onClick = { onAbrirComanda(comanda.id) }, modifier = Modifier.readableWidth())
            }
        }
    }
}

@Composable
private fun FilaPanel(comanda: ComandaGestion, atraso: Int, onClick: () -> Unit, modifier: Modifier) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier, onClick = onClick, stripe = if (atraso > 0) colors.danger else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    comanda.numero,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(comanda.cliente.nombre, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ChipTono(comanda.estado.etiqueta, tonoEstadoComanda(comanda.estado))
                if (atraso > 0) ChipTono("+${textoAtraso(atraso)}", Tono.PELIGRO)
                if (comanda.incidenciasAbiertas > 0) ChipTono("Incidencia", Tono.PELIGRO, icono = CobreIcons.Warning)
            }
        }
        if (comanda.etapas.isNotEmpty()) ProgresoEtapas(comanda)
    }
}
