package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Barra de progreso y error de carga en la cabecera de una lista. */
@Composable
fun EstadoCarga(cargando: Boolean, error: String?, modifier: Modifier = Modifier) {
    val colors = CobreTheme.colors
    Column(modifier = modifier) {
        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().clip(CobreShapes.full).semantics { contentDescription = "Cargando" },
                color = colors.accent,
                trackColor = colors.surfaceMuted,
            )
        }
        error?.let {
            Text(
                it,
                color = colors.dangerText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp).semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

/** Mensaje de confirmación de una operación (p. ej. "Comanda creada"), descartable. */
@Composable
fun AvisoExito(mensaje: String?, onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    if (mensaje == null) return
    val colors = CobreTheme.colors
    val tono = tonoColors(Tono.EXITO)
    Row(
        modifier = modifier
            .clip(CobreShapes.md)
            .background(tono.container)
            .clickable(role = Role.Button, onClickLabel = "Descartar", onClick = onCerrar)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(CobreIcons.CheckCircle, contentDescription = null, tint = tono.accent, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(mensaje, style = MaterialTheme.typography.bodySmall, color = tono.content, modifier = Modifier.weight(1f))
        Icon(CobreIcons.Close, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
    }
}

/** Estado vacío de una lista con ícono, texto y una acción opcional. */
@Composable
fun EstadoVacio(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    accion: String? = null,
    onAccion: () -> Unit = {},
) {
    val colors = CobreTheme.colors
    ContentCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icono, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(32.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary, textAlign = TextAlign.Center)
            if (accion != null) CobreButton(text = accion, onClick = onAccion, variant = ButtonVariant.Tonal)
        }
    }
}

/** Chip de estado con un [Tono] semántico. */
@Composable
fun ChipTono(texto: String, tono: Tono, modifier: Modifier = Modifier, icono: ImageVector? = null) {
    val c = tonoColors(tono)
    StatusChip(label = texto, container = c.container, content = c.content, modifier = modifier, icon = icono)
}

/** Etiqueta y valor en una fila, para fichas de detalle. */
@Composable
fun FilaDato(etiqueta: String, valor: String?, modifier: Modifier = Modifier) {
    if (valor.isNullOrBlank()) return
    val colors = CobreTheme.colors
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, modifier = Modifier.widthIn(min = 96.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, modifier = Modifier.weight(1f))
    }
}

/**
 * Indicador numérico que también filtra (como las tarjetas de estado de la web). Con
 * [onClick] nulo es solo informativo.
 */
@Composable
fun KpiTile(
    valor: String,
    etiqueta: String,
    tono: Tono,
    modifier: Modifier = Modifier,
    seleccionado: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = CobreTheme.colors
    val c = tonoColors(tono)
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .widthIn(min = 104.dp)
            .then(if (onClick != null) Modifier.pressableScale(interaction) else Modifier)
            .clip(CobreShapes.lg)
            .background(if (seleccionado) c.container else colors.surface)
            .border(if (seleccionado) 1.5.dp else 0.5.dp, if (seleccionado) c.accent else colors.hairline, CobreShapes.lg)
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interaction, indication = null, role = Role.Checkbox, onClick = onClick)
                        .semantics { selected = seleccionado }
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(valor, style = MaterialTheme.typography.headlineSmall, color = c.content, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionado) c.content else colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Fila desplazable de chips de filtro de selección única. */
@Composable
fun <T> ChipsFiltro(
    opciones: List<T>,
    seleccionado: T,
    etiqueta: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
        items(opciones, key = { etiqueta(it) }) { opcion ->
            SelectableChip(label = etiqueta(opcion), seleccionado = opcion == seleccionado, onClick = { onSeleccionar(opcion) })
        }
    }
}

/** Campo numérico con botones − y + (cantidades de prendas), cómodo con guantes. */
@Composable
fun CampoCantidad(
    valor: Int,
    onCambiar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Cantidad",
    minimo: Int = 1,
    enabled: Boolean = true,
) {
    val colors = CobreTheme.colors
    Column(modifier = modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        Spacer(Modifier.padding(top = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassIconButton(
                icon = CobreIcons.Remove,
                contentDescription = "Restar uno a $etiqueta",
                onClick = { onCambiar((valor - 1).coerceAtLeast(minimo)) },
                size = 40.dp,
                enabled = enabled && valor > minimo,
            )
            Text(
                valor.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 36.dp).semantics { contentDescription = "$etiqueta: $valor" },
            )
            GlassIconButton(
                icon = CobreIcons.Add,
                contentDescription = "Sumar uno a $etiqueta",
                onClick = { onCambiar(valor + 1) },
                size = 40.dp,
                enabled = enabled,
            )
        }
    }
}

/**
 * Campo que abre una hoja de vidrio con búsqueda para elegir una opción (cliente,
 * prenda, servicio, rol). Si [permitirTextoLibre], el texto buscado puede usarse tal cual.
 */
@Composable
fun <T> SelectorConBusqueda(
    etiqueta: String,
    valorActual: String,
    opciones: List<T>,
    texto: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    detalle: (T) -> String? = { null },
    placeholder: String = "Seleccionar",
    permitirTextoLibre: ((String) -> Unit)? = null,
    enabled: Boolean = true,
) {
    var abierto by remember { mutableStateOf(false) }
    val colors = CobreTheme.colors
    Column(modifier = modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        Spacer(Modifier.padding(top = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CobreShapes.md)
                .background(colors.surface)
                .border(1.dp, colors.fieldBorder, CobreShapes.md)
                .clickable(enabled = enabled, role = Role.Button, onClickLabel = "Elegir $etiqueta") { abierto = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                valorActual.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyLarge,
                color = if (valorActual.isBlank()) colors.textSecondary else colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(CobreIcons.ChevronRight, contentDescription = null, tint = colors.textSecondary)
        }
    }
    if (abierto) {
        HojaSeleccion(
            titulo = etiqueta,
            opciones = opciones,
            texto = texto,
            detalle = detalle,
            onSeleccionar = {
                onSeleccionar(it)
                abierto = false
            },
            permitirTextoLibre = permitirTextoLibre?.let { libre ->
                { valor: String ->
                    libre(valor)
                    abierto = false
                }
            },
            onCerrar = { abierto = false },
        )
    }
}

@Composable
private fun <T> HojaSeleccion(
    titulo: String,
    opciones: List<T>,
    texto: (T) -> String,
    detalle: (T) -> String?,
    onSeleccionar: (T) -> Unit,
    permitirTextoLibre: ((String) -> Unit)?,
    onCerrar: () -> Unit,
) {
    var busqueda by remember { mutableStateOf("") }
    val colors = CobreTheme.colors
    val filtradas = remember(opciones, busqueda) {
        val q = busqueda.trim().lowercase()
        if (q.isEmpty()) {
            opciones
        } else {
            opciones.filter { texto(it).lowercase().contains(q) || detalle(it)?.lowercase()?.contains(q) == true }
        }
    }
    GlassSheet(onDismiss = onCerrar, title = titulo) {
        GlassSearchField(query = busqueda, onQueryChange = { busqueda = it }, placeholder = "Buscar")
        val textoNuevo = busqueda.isNotBlank() && filtradas.none { texto(it).equals(busqueda.trim(), ignoreCase = true) }
        if (permitirTextoLibre != null && textoNuevo) {
            CobreButton(
                text = "Usar \"${busqueda.trim()}\"",
                onClick = { permitirTextoLibre(busqueda.trim()) },
                variant = ButtonVariant.Tonal,
                icon = CobreIcons.Add,
                fillWidth = true,
            )
        }
        if (filtradas.isEmpty()) {
            Text("Sin resultados.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
        filtradas.take(MAX_OPCIONES_VISIBLES).forEach { opcion ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.md)
                    .clickable(role = Role.Button) { onSeleccionar(opcion) }
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            ) {
                Text(texto(opcion), style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
                detalle(opcion)?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }
        }
        if (filtradas.size > MAX_OPCIONES_VISIBLES) {
            Text(
                "Mostrando $MAX_OPCIONES_VISIBLES de ${filtradas.size}. Escribe para acotar la búsqueda.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

private const val MAX_OPCIONES_VISIBLES = 60

/** Pastilla redonda con la inicial de un nombre (usuarios, clientes). */
@Composable
fun Iniciales(nombre: String, modifier: Modifier = Modifier, fondo: Color = CobreTheme.colors.accentContainer) {
    Box(modifier = modifier.size(40.dp).clip(CobreShapes.full).background(fondo), contentAlignment = Alignment.Center) {
        Text(
            nombre.trim().take(1).uppercase().ifEmpty { "?" },
            style = MaterialTheme.typography.titleMedium,
            color = CobreTheme.colors.accentText,
        )
    }
}

/** Encabezado de un grupo dentro de una lista (sin mayúsculas forzadas). */
@Composable
fun TituloGrupo(texto: String, modifier: Modifier = Modifier, trailing: (@Composable () -> Unit)? = null) {
    Row(modifier = modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        SectionLabel(texto, modifier = Modifier.weight(1f).padding(start = 4.dp))
        trailing?.invoke()
    }
}
