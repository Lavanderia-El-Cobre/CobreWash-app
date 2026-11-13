package com.elcobre.lavanderiaelcobre.ui.screens.vehiculos

import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.rememberShellContentPadding
import com.elcobre.lavanderiaelcobre.ui.components.rememberTitleCollapsed
import com.elcobre.lavanderiaelcobre.ui.components.CompactTitleBar
import com.elcobre.lavanderiaelcobre.ui.components.GlassStack
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FiltroFlota
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.KpisFlota
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.calcularKpisFlota
import com.elcobre.lavanderiaelcobre.data.model.esPatenteChilenaValida
import com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackHandler
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.GlassSearchField
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitle
import com.elcobre.lavanderiaelcobre.ui.components.MiniaturaFotoChequeo
import com.elcobre.lavanderiaelcobre.ui.components.RecordatorioPrivacidadFotos
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.rememberCapturadorFoto
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay

private val ETIQUETAS_FOTOS = listOf("Frente", "Lateral derecho", "Lateral izquierdo", "Posterior", "Tablero / Odómetro")

/**
 * Vista del rol Administrador conectada a Firebase Data Connect (`RF-VE01` a `RF-VE04`):
 * administra el catálogo de flota, registra salidas e inspecciones previas, recibe retornos
 * con validación de odómetro y consulta la bitácora histórica con evidencia fotográfica.
 */
@Composable
fun VehiculosScreen(
    administrador: Administrador,
    vehiculos: List<Vehiculo>,
    chequeoEnRuta: (String) -> ChequeoVehiculo?,
    historialVehiculo: (String) -> List<ChequeoVehiculo>,
    onRegistrarSalida: (vehiculoId: String, km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
    onRegistrarEntrada: (vehiculoId: String, km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
    onAgregarVehiculo: (patente: String, modelo: String, kilometrajeInicial: Int) -> Unit,
    conductores: List<ConductorFlota> = emptyList(),
    kpis: KpisFlota = calcularKpisFlota(vehiculos, emptyList()),
    filtroActual: FiltroFlota = FiltroFlota.TODOS,
    busqueda: String = "",
    cargando: Boolean = false,
    operacionEnCurso: Boolean = false,
    error: String? = null,
    mensajeExito: String? = null,
    tieneFotosPendientes: (String) -> Boolean = { false },
    onDescartarReintentos: () -> Unit = {},
    onCambiarBusqueda: (String) -> Unit = {},
    onSeleccionarFiltro: (FiltroFlota) -> Unit = {},
    onRefrescar: () -> Unit = {},
    onLimpiarMensajes: () -> Unit = {},
    onCambiarEstadoVehiculo: (vehiculoId: String, activo: Boolean) -> Unit = { _, _ -> },
    onRegistrarVehiculoDetallado: (patente: String, marca: String, modelo: String, anio: Int?, km: Int) -> Unit =
        { patente, _, modelo, _, km -> onAgregarVehiculo(patente, modelo, km) },
    onRegistrarSalidaDetallada: (
        vehiculoId: String,
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit = { id, _, _, km, _, fotos, obs -> onRegistrarSalida(id, km, fotos, obs) },
    onRegistrarEntradaDetallada: (
        vehiculoId: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit = { id, km, _, fotos, obs -> onRegistrarEntrada(id, km, fotos, obs) },
    onProgramarSalida: ((vehiculoId: String, repartidorId: String, observaciones: String?) -> Unit)? = null,
) {
    var vehiculoChequeoModal by remember { mutableStateOf<Vehiculo?>(null) }
    var vehiculoHistorialModal by remember { mutableStateOf<Vehiculo?>(null) }
    var mostrandoAltaModal by remember { mutableStateOf(false) }
    var programando by remember { mutableStateOf(false) }

    val algunModalAbierto = vehiculoChequeoModal != null || vehiculoHistorialModal != null || mostrandoAltaModal
    CobreBackHandler(enabled = algunModalAbierto && !operacionEnCurso) {
        when {
            vehiculoChequeoModal != null -> { onDescartarReintentos(); vehiculoChequeoModal = null }
            vehiculoHistorialModal != null -> vehiculoHistorialModal = null
            mostrandoAltaModal -> mostrandoAltaModal = false
        }
    }

    CerrarChequeoAlConfirmar(mensajeExito, onLimpiarMensajes) { vehiculoChequeoModal = null }

    val gridState = rememberLazyGridState()
    val colapsado by rememberTitleCollapsed(gridState)
    GlassStack(
        modifier = Modifier.fillMaxSize(),
        background = { fuente ->
            ContenidoFlotaGrid(
                modifier = fuente,
                gridState = gridState,
                vehiculos = vehiculos,
                kpis = kpis,
                filtroActual = filtroActual,
                busqueda = busqueda,
                cargando = cargando,
                operacionEnCurso = operacionEnCurso,
                error = error,
                mensajeExito = mensajeExito,
                chequeoEnRuta = chequeoEnRuta,
                historialVehiculo = historialVehiculo,
                onRefrescar = onRefrescar,
                onAbrirAlta = { mostrandoAltaModal = true },
                onAbrirProgramar = { programando = true }.takeIf { onProgramarSalida != null },
                onLimpiarMensajes = onLimpiarMensajes,
                onCambiarBusqueda = onCambiarBusqueda,
                onSeleccionarFiltro = onSeleccionarFiltro,
                onAbrirChequeo = { vehiculoChequeoModal = it },
                onAbrirHistorial = { vehiculoHistorialModal = it },
                onCambiarEstadoVehiculo = onCambiarEstadoVehiculo,
            )
        },
        overlay = {
            CompactTitleBar(title = "Flota de reparto", visible = colapsado, modifier = Modifier.align(Alignment.TopCenter))
        },
    )

    if (mostrandoAltaModal) {
        ModalAgregarVehiculo(
            patentesExistentes = vehiculos.map { it.patente },
            onDismiss = { mostrandoAltaModal = false },
            onAgregar = { patente, marca, modelo, anio, km ->
                onRegistrarVehiculoDetallado(patente, marca, modelo, anio, km)
                mostrandoAltaModal = false
            },
        )
    }

    vehiculoChequeoModal?.let { vehiculo ->
        // Mantener el tipo de operación aunque el sondeo ya vea el movimiento guardado.
        val chequeoActivo = remember(vehiculo.id) { chequeoEnRuta(vehiculo.id) }
        ModalChequeoVehiculo(
            vehiculo = vehiculo,
            chequeo = chequeoActivo,
            conductores = conductores,
            administradorNombre = administrador.nombre,
            guardando = operacionEnCurso,
            movimientoConfirmado = tieneFotosPendientes(vehiculo.id),
            errorServidor = error,
            onDismiss = { onDescartarReintentos(); vehiculoChequeoModal = null },
            onConfirmar = { repartidorId, responsable, km, estado, fotos, obs ->
                if (chequeoActivo != null && chequeoActivo.estaEnRuta) {
                    onRegistrarEntradaDetallada(vehiculo.id, km, estado, fotos, obs)
                } else {
                    onRegistrarSalidaDetallada(vehiculo.id, repartidorId, responsable, km, estado, fotos, obs)
                }
            },
        )
    }

    vehiculoHistorialModal?.let { vehiculo ->
        ModalHistorialVehiculo(
            vehiculo = vehiculo,
            historial = historialVehiculo(vehiculo.id),
            onDismiss = { vehiculoHistorialModal = null },
        )
    }

    HojaProgramarSalida(
        abierta = programando,
        vehiculos = vehiculos,
        conductores = conductores,
        chequeoEnRuta = chequeoEnRuta,
        guardando = operacionEnCurso,
        onProgramar = onProgramarSalida,
        onCerrar = { programando = false },
    )
}

@Composable
private fun CerrarChequeoAlConfirmar(mensaje: String?, limpiar: () -> Unit, cerrar: () -> Unit) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            cerrar()
            delay(3500)
            limpiar()
        }
    }
}

@Composable
private fun ContenidoFlotaGrid(
    modifier: Modifier,
    gridState: LazyGridState,
    vehiculos: List<Vehiculo>,
    kpis: KpisFlota,
    filtroActual: FiltroFlota,
    busqueda: String,
    cargando: Boolean,
    operacionEnCurso: Boolean,
    error: String?,
    mensajeExito: String?,
    chequeoEnRuta: (String) -> ChequeoVehiculo?,
    historialVehiculo: (String) -> List<ChequeoVehiculo>,
    onRefrescar: () -> Unit,
    onAbrirAlta: () -> Unit,
    onAbrirProgramar: (() -> Unit)? = null,
    onLimpiarMensajes: () -> Unit,
    onCambiarBusqueda: (String) -> Unit,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
    onAbrirChequeo: (Vehiculo) -> Unit,
    onAbrirHistorial: (Vehiculo) -> Unit,
    onCambiarEstadoVehiculo: (String, Boolean) -> Unit,
) {
    val window = CobreTheme.window
    BoxWithConstraints(modifier = modifier) {
        val esAncho = maxWidth >= 700.dp
        val columnas = if (esAncho) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)
        LazyVerticalGrid(
            columns = columnas,
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            // Barras del sistema y navegación de vidrio como contentPadding: las tarjetas pasan por debajo.
            contentPadding = rememberShellContentPadding(
                PaddingValues(start = window.gutter, end = window.gutter, top = 8.dp, bottom = 24.dp),
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CabeceraFlota(
                    cargando = cargando || operacionEnCurso,
                    onRefrescar = onRefrescar,
                    onAbrirAlta = onAbrirAlta,
                    onAbrirProgramar = onAbrirProgramar,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ResumenKpiFlota(
                    kpis = kpis,
                    filtroActual = filtroActual,
                    esAncho = esAncho,
                    onSeleccionarFiltro = onSeleccionarFiltro,
                )
            }
            if (error != null || mensajeExito != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BannersEstadoFlota(
                        error = error,
                        mensajeExito = mensajeExito,
                        onLimpiar = onLimpiarMensajes,
                    )
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                BarraFiltroFlota(
                    busqueda = busqueda,
                    filtroActual = filtroActual,
                    onCambiarBusqueda = onCambiarBusqueda,
                    onSeleccionarFiltro = onSeleccionarFiltro,
                )
            }
            if (vehiculos.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EstadoVacioFlota(
                        cargando = cargando,
                        busqueda = busqueda,
                        filtroActual = filtroActual,
                        onLimpiarFiltros = {
                            onCambiarBusqueda("")
                            onSeleccionarFiltro(FiltroFlota.TODOS)
                        },
                        onRefrescar = onRefrescar,
                    )
                }
            } else {
                items(vehiculos, key = { it.id }) { vehiculo ->
                    val chequeo = chequeoEnRuta(vehiculo.id)
                    VehiculoCard(
                        vehiculo = vehiculo,
                        chequeo = chequeo,
                        totalHistorial = historialVehiculo(vehiculo.id).size,
                        onAbrirChequeo = { onAbrirChequeo(vehiculo) },
                        onAbrirHistorial = { onAbrirHistorial(vehiculo) },
                        onToggleActivo = { onCambiarEstadoVehiculo(vehiculo.id, !vehiculo.activo) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CabeceraFlota(
    cargando: Boolean,
    onRefrescar: () -> Unit,
    onAbrirAlta: () -> Unit,
    onAbrirProgramar: (() -> Unit)? = null,
) {
    LargeTitle(
        title = "Flota de reparto",
        subtitle = "Control de unidades, despacho a ruta e inspección técnica",
        trailing = {
            GlassIconButton(
                icon = CobreIcons.History,
                contentDescription = "Actualizar flota",
                onClick = onRefrescar,
                size = 44.dp,
                enabled = !cargando,
            )
            if (onAbrirProgramar != null) {
                GlassIconButton(
                    icon = CobreIcons.Schedule,
                    contentDescription = "Programar salida para un repartidor",
                    onClick = onAbrirProgramar,
                    size = 44.dp,
                    enabled = !cargando,
                )
            }
            CobreButton(text = "Nuevo", onClick = onAbrirAlta, icon = CobreIcons.Add)
        },
    )
}

private data class KpiDescriptor(
    val titulo: String,
    val subtitulo: String,
    val valor: String,
    val icono: ImageVector,
    val accent: Color,
    val filtro: FiltroFlota,
)

@Composable
private fun ResumenKpiFlota(
    kpis: KpisFlota,
    filtroActual: FiltroFlota,
    esAncho: Boolean,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
) {
    val colors = CobreTheme.colors
    val items = listOf(
        KpiDescriptor(
            "Total flota",
            "Unidades registradas",
            "${kpis.total}",
            CobreIcons.LocalShipping,
            colors.textPrimary,
            FiltroFlota.TODOS,
        ),
        KpiDescriptor(
            "Disponibles",
            "Listos en planta",
            "${kpis.disponibles}",
            CobreIcons.CheckCircle,
            colors.successText,
            FiltroFlota.DISPONIBLES,
        ),
        KpiDescriptor("En ruta", "Despachos activos", "${kpis.enRuta}", CobreIcons.Speed, colors.warningText, FiltroFlota.EN_RUTA),
        KpiDescriptor(
            "Mantención",
            "Fuera de servicio",
            "${kpis.inactivos}",
            CobreIcons.Warning,
            if (kpis.inactivos > 0) colors.dangerText else colors.textSecondary,
            FiltroFlota.INACTIVOS,
        ),
    )
    val filas = if (esAncho) listOf(items) else items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        filas.forEach { fila ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { item ->
                    KpiFlotaCard(
                        titulo = item.titulo,
                        subtitulo = item.subtitulo,
                        valor = item.valor,
                        icono = item.icono,
                        accent = item.accent,
                        seleccionado = filtroActual == item.filtro,
                        onClick = { onSeleccionarFiltro(item.filtro) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiFlotaCard(
    titulo: String,
    subtitulo: String,
    valor: String,
    icono: ImageVector,
    accent: Color,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CobreTheme.colors
    ContentCard(
        modifier = modifier
            .then(if (seleccionado) Modifier.border(2.dp, colors.accent, CobreShapes.lg) else Modifier)
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                selected = seleccionado
            },
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    style = CobreTheme.text.label,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(valor, style = MaterialTheme.typography.headlineSmall, color = accent)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(icono, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun BannersEstadoFlota(error: String?, mensajeExito: String?, onLimpiar: () -> Unit) {
    val colors = CobreTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AnimatedVisibility(visible = error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.md)
                    .background(colors.danger.copy(alpha = 0.10f))
                    .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                GlassIconButton(icon = CobreIcons.Close, contentDescription = "Cerrar aviso", onClick = onLimpiar, size = 36.dp)
            }
        }
        AnimatedVisibility(visible = mensajeExito != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CobreShapes.md)
                    .background(colors.success.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(CobreIcons.CheckCircle, contentDescription = null, tint = colors.successText, modifier = Modifier.size(18.dp))
                Text(mensajeExito.orEmpty(), style = MaterialTheme.typography.labelMedium, color = colors.successText)
            }
        }
    }
}

@Composable
private fun BarraFiltroFlota(
    busqueda: String,
    filtroActual: FiltroFlota,
    onCambiarBusqueda: (String) -> Unit,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlassSearchField(
            query = busqueda,
            onQueryChange = onCambiarBusqueda,
            placeholder = "Buscar por patente, marca, modelo o conductor",
        )
        GlassSegmentedControl(
            options = FiltroFlota.entries,
            selected = filtroActual,
            onSelect = onSeleccionarFiltro,
            label = { it.etiqueta },
        )
    }
}

@Composable
private fun EstadoVacioFlota(
    cargando: Boolean,
    busqueda: String,
    filtroActual: FiltroFlota,
    onLimpiarFiltros: () -> Unit,
    onRefrescar: () -> Unit,
) {
    val colors = CobreTheme.colors
    val hayFiltrosActivos = busqueda.isNotBlank() || filtroActual != FiltroFlota.TODOS
    ContentCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (cargando) {
                CircularProgressIndicator(color = colors.accent)
                Text(
                    "Sincronizando flota de vehículos…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            } else {
                Icon(CobreIcons.LocalShipping, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(32.dp))
                Text(
                    text = if (hayFiltrosActivos) {
                        "No hay vehículos que coincidan con el filtro seleccionado."
                    } else {
                        "No hay vehículos registrados en la flota."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                CobreButton(
                    text = if (hayFiltrosActivos) "Limpiar filtros" else "Actualizar flota",
                    onClick = if (hayFiltrosActivos) onLimpiarFiltros else onRefrescar,
                    variant = ButtonVariant.Primary,
                )
            }
        }
    }
}

/** Estado de la unidad: color de relleno (franja, íconos) y de texto legible. */
private data class EstadoVehiculoUi(val relleno: Color, val texto: Color, val etiqueta: String)

@Composable
private fun VehiculoCard(
    vehiculo: Vehiculo,
    chequeo: ChequeoVehiculo?,
    totalHistorial: Int,
    onAbrirChequeo: () -> Unit,
    onAbrirHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
) {
    val colors = CobreTheme.colors
    val enRuta = chequeo != null && chequeo.estaEnRuta
    val estado = when {
        enRuta -> EstadoVehiculoUi(colors.warning, colors.warningText, "En ruta")
        vehiculo.activo -> EstadoVehiculoUi(colors.success, colors.successText, "Disponible")
        else -> EstadoVehiculoUi(colors.danger, colors.dangerText, "Mantención")
    }

    ContentCard(stripe = estado.relleno) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CabeceraTarjetaVehiculo(vehiculo = vehiculo, estado = estado)
            MetadatosTarjetaVehiculo(vehiculo = vehiculo, chequeo = chequeo, estado = estado)
            AccionesTarjetaVehiculo(
                activo = vehiculo.activo,
                enRuta = enRuta,
                totalHistorial = totalHistorial,
                onAbrirChequeo = onAbrirChequeo,
                onAbrirHistorial = onAbrirHistorial,
                onToggleActivo = onToggleActivo,
            )
        }
    }
}

@Composable
private fun CabeceraTarjetaVehiculo(vehiculo: Vehiculo, estado: EstadoVehiculoUi) {
    val colors = CobreTheme.colors
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CobreShapes.md).background(estado.relleno.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.LocalShipping, contentDescription = null, tint = estado.texto, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(vehiculo.patente.uppercase(), style = CobreTheme.text.label, color = colors.textSecondary)
            Text(
                vehiculo.nombreCompleto,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        StatusChip(label = estado.etiqueta, container = estado.relleno.copy(alpha = 0.14f), content = estado.texto)
    }
}

@Composable
private fun MetadatosTarjetaVehiculo(vehiculo: Vehiculo, chequeo: ChequeoVehiculo?, estado: EstadoVehiculoUi) {
    val colors = CobreTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CobreShapes.md)
            .background(colors.surfaceMuted)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(CobreIcons.Speed, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                Text("${vehiculo.kilometrajeActual} km", style = CobreTheme.text.label, color = colors.textPrimary)
            }
            vehiculo.anio?.let { anio ->
                Text("Año $anio", style = CobreTheme.text.label, color = colors.textSecondary)
            }
        }
        if (chequeo != null && chequeo.estaEnRuta) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(CobreIcons.Person, contentDescription = null, tint = estado.texto, modifier = Modifier.size(15.dp))
                Text(
                    "En ruta: ${chequeo.responsable} · Salida ${chequeo.horaSalida ?: "En curso"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = estado.texto,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Text(
                text = if (vehiculo.activo) {
                    vehiculo.descripcion?.takeIf { it.isNotBlank() } ?: "Unidad operativa disponible en planta"
                } else {
                    "Unidad fuera de servicio por mantención preventiva"
                },
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccionesTarjetaVehiculo(
    activo: Boolean,
    enRuta: Boolean,
    totalHistorial: Int,
    onAbrirChequeo: () -> Unit,
    onAbrirHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
) {
    // FlowRow: en tarjetas angostas los botones secundarios bajan de línea en vez de cortarse.
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (activo || enRuta) {
            CobreButton(
                text = if (enRuta) "Registrar retorno" else "Dar salida",
                onClick = onAbrirChequeo,
                variant = if (enRuta) ButtonVariant.Primary else ButtonVariant.Accent,
                icon = if (enRuta) CobreIcons.CheckCircle else CobreIcons.LocalShipping,
            )
        }
        CobreButton(
            text = if (totalHistorial > 0) "Bitácora ($totalHistorial)" else "Bitácora",
            onClick = onAbrirHistorial,
            variant = ButtonVariant.Tonal,
            icon = CobreIcons.History,
        )
        if (!enRuta) {
            CobreButton(
                text = if (activo) "Mantención" else "Habilitar",
                onClick = onToggleActivo,
                variant = if (activo) ButtonVariant.Danger else ButtonVariant.Tonal,
            )
        }
    }
}

private fun diagnosticarPatenteAlta(patente: String, patentesExistentes: List<String>): String? {
    if (patente.isBlank()) return null
    val normalizada = normalizarPatenteChilena(patente)
    val duplicada = patentesExistentes.any { normalizarPatenteChilena(it) == normalizada }
    return when {
        duplicada -> "Ya existe un vehículo con esa patente ($normalizada)."
        !esPatenteChilenaValida(normalizada) -> "Ingresa una patente chilena válida (ej. HLRT-24 o AB-1234)."
        else -> null
    }
}

/** RF-VE01: modal de alta de un vehículo nuevo a la flota con validación de patente chilena. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalAgregarVehiculo(
    patentesExistentes: List<String>,
    onDismiss: () -> Unit,
    onAgregar: (patente: String, marca: String, modelo: String, anio: Int?, km: Int) -> Unit,
) {
    var patente by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anioTexto by remember { mutableStateOf("") }
    var kmTexto by remember { mutableStateOf("0") }
    val haptic = LocalHapticFeedback.current
    val isDark = cobreIsDark()

    val km = kmTexto.toIntOrNull()
    val anio = anioTexto.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    val anioInvalido = anioTexto.isNotBlank() && (anio == null || anio !in 1980..2100)
    val errorPatente = diagnosticarPatenteAlta(patente, patentesExistentes)
    val habilitado = patente.isNotBlank() && modelo.isNotBlank() &&
        km != null && km >= 0 && errorPatente == null && !anioInvalido

    CobreBackHandler(enabled = true, onBack = onDismiss)

    GlassSheet(
        onDismiss = onDismiss,
        title = "Registrar vehículo",
        subtitle = "Datos técnicos y odómetro inicial de la unidad.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CamposAgregarVehiculo(
                patente = patente,
                marca = marca,
                modelo = modelo,
                anioTexto = anioTexto,
                kmTexto = kmTexto,
                errorPatente = errorPatente,
                anioInvalido = anioInvalido,
                onPatenteChange = { patente = it },
                onMarcaChange = { marca = it },
                onModeloChange = { modelo = it },
                onAnioChange = { anioTexto = it.filter(Char::isDigit).take(4) },
                onKmChange = { kmTexto = it.filter(Char::isDigit) },
            )
            Spacer(Modifier.height(4.dp))
            CobreButton(
                text = "Agregar a la flota",
                onClick = {
                    val kmValido = km ?: return@CobreButton
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAgregar(normalizarPatenteChilena(patente), marca, modelo, anio, kmValido)
                },
                icon = CobreIcons.Add,
                enabled = habilitado,
                fillWidth = true,
            )
        }
    }
}

@Composable
private fun CamposAgregarVehiculo(
    patente: String,
    marca: String,
    modelo: String,
    anioTexto: String,
    kmTexto: String,
    errorPatente: String?,
    anioInvalido: Boolean,
    onPatenteChange: (String) -> Unit,
    onMarcaChange: (String) -> Unit,
    onModeloChange: (String) -> Unit,
    onAnioChange: (String) -> Unit,
    onKmChange: (String) -> Unit,
) {
    CobreTextField(
        value = patente,
        onValueChange = onPatenteChange,
        label = "Patente",
        placeholder = "Ej. HLRT-24",
        isError = errorPatente != null,
        supportingText = errorPatente,
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CobreTextField(
            value = marca,
            onValueChange = onMarcaChange,
            label = "Marca",
            placeholder = "Ej. Suzuki",
            modifier = Modifier.weight(1f),
        )
        CobreTextField(
            value = modelo,
            onValueChange = onModeloChange,
            label = "Modelo",
            placeholder = "Ej. APV Cargo",
            modifier = Modifier.weight(1f),
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CobreTextField(
            value = anioTexto,
            onValueChange = onAnioChange,
            label = "Año (opcional)",
            placeholder = "2022",
            isError = anioInvalido,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
        )
        CobreTextField(
            value = kmTexto,
            onValueChange = onKmChange,
            label = "Kilometraje inicial",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = CobreIcons.Speed,
            modifier = Modifier.weight(1f),
        )
    }
}

/** RF-VE04: modal con historial de salidas/regresos con kilometraje, fotos y comentarios. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalHistorialVehiculo(
    vehiculo: Vehiculo,
    historial: List<ChequeoVehiculo>,
    onDismiss: () -> Unit,
) {
    val isDark = cobreIsDark()
    CobreBackHandler(enabled = true, onBack = onDismiss)
    GlassSheet(
        onDismiss = onDismiss,
        title = "Bitácora · ${vehiculo.patente}",
        subtitle = "${vehiculo.nombreCompleto} · Odómetro actual: ${vehiculo.kilometrajeActual} km",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (historial.isEmpty()) {
                ContentCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sin registros de salida o retorno todavía para esta unidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandMutedColor(),
                    )
                }
            } else {
                historial.forEach { chequeo ->
                    ContentCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        HistorialFila(chequeo)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialFila(chequeo: ChequeoVehiculo) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                chequeo.fecha,
                style = MaterialTheme.typography.labelLarge,
                color = brandHeadingColor(),
                fontWeight = FontWeight.Bold,
            )
            val isDark = cobreIsDark()
            val estadoColor = if (chequeo.estaCerrado) {
                if (isDark) StatusGreenDark else StatusGreen
            } else {
                if (isDark) StatusAmberDark else StatusAmber
            }
            StatusChip(
                label = if (chequeo.estaCerrado) "Cerrado" else "En ruta",
                container = estadoColor.copy(alpha = 0.16f),
                content = estadoColor,
            )
        }
        Text(
            "Salida: ${chequeo.kmSalida ?: "—"} km · ${chequeo.horaSalida ?: "—"} · ${chequeo.estadoSalida.etiqueta}",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
        if (chequeo.estaCerrado) {
            val condicionRetorno = chequeo.estadoEntrada?.etiqueta ?: "Apto"
            Text(
                "Retorno: ${chequeo.kmEntrada} km · ${chequeo.horaEntrada ?: "—"} · " +
                    "${chequeo.kilometrosRecorridos} km recorridos · $condicionRetorno",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
        }
        val totalFotos = chequeo.fotosSalida.size + chequeo.fotosEntrada.size
        if (totalFotos > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(CobreIcons.PhotoCamera, contentDescription = null, tint = CobreTheme.colors.accent, modifier = Modifier.size(14.dp))
                Text(
                    "$totalFotos foto${if (totalFotos == 1) "" else "s"} de inspección " +
                        "(${chequeo.fotosSalida.size} salida / ${chequeo.fotosEntrada.size} retorno)",
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor(),
                )
            }
        }
        chequeo.observacionesSalida?.takeIf { it.isNotBlank() }?.let {
            Text(
                "Inspección salida: $it",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor().copy(alpha = 0.85f),
            )
        }
        chequeo.observacionesEntrada?.takeIf { it.isNotBlank() }?.let {
            Text(
                "Inspección retorno: $it",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor().copy(alpha = 0.85f),
            )
        }
        Text(
            "Responsable: ${chequeo.responsable}",
            style = MaterialTheme.typography.labelSmall,
            color = brandHeadingColor().copy(alpha = 0.8f),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun diagnosticarKilometrajeChequeo(kmTexto: String, kmMinimo: Int): String? {
    if (kmTexto.isBlank()) return null
    val km = kmTexto.toIntOrNull() ?: return "Ingresa un kilometraje válido."
    return if (km < kmMinimo) "No puede ser menor al kilometraje anterior registrado ($kmMinimo km)." else null
}

/** RF-VE02 / RF-VE03: modal de registro de salida o retorno con inspección técnica y evidencia fotográfica. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalChequeoVehiculo(
    vehiculo: Vehiculo,
    chequeo: ChequeoVehiculo?,
    conductores: List<ConductorFlota>,
    administradorNombre: String,
    guardando: Boolean,
    movimientoConfirmado: Boolean,
    errorServidor: String?,
    onDismiss: () -> Unit,
    onConfirmar: (
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit,
) {
    val esEntrada = chequeo != null && chequeo.estaEnRuta
    val isDark = cobreIsDark()
    val accent = if (esEntrada) {
        if (isDark) StatusAmberDark else StatusAmber
    } else {
        if (isDark) StatusGreenDark else StatusGreen
    }
    val kmMinimo = if (esEntrada) chequeo.kmSalida ?: vehiculo.kilometrajeActual else vehiculo.kilometrajeActual

    val cerrar = { if (!guardando) onDismiss() }
    CobreBackHandler(enabled = true, onBack = cerrar)

    GlassSheet(
        onDismiss = cerrar,
        title = if (esEntrada) "Registrar retorno · ${vehiculo.patente}" else "Dar salida · ${vehiculo.patente}",
        subtitle = "${vehiculo.nombreCompleto} · Odómetro mínimo: $kmMinimo km",
    ) {
        Column {
            ChequeoForm(
                accent = accent,
                esEntrada = esEntrada,
                kmMinimo = kmMinimo,
                conductores = conductores,
                responsablePorDefecto = chequeo?.responsable ?: administradorNombre,
                guardando = guardando,
                movimientoConfirmado = movimientoConfirmado,
                errorServidor = errorServidor,
                onDismiss = cerrar,
                onConfirmar = onConfirmar,
            )
        }
    }
}

/** Formulario de salida o entrada con conductor, condición técnica, odómetro y evidencia fotográfica. */
@Composable
private fun ChequeoForm(
    accent: Color,
    esEntrada: Boolean,
    kmMinimo: Int,
    conductores: List<ConductorFlota>,
    responsablePorDefecto: String,
    guardando: Boolean,
    movimientoConfirmado: Boolean,
    errorServidor: String?,
    onDismiss: () -> Unit,
    onConfirmar: (
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit,
) {
    CobreBackHandler(enabled = true, onBack = onDismiss)
    val conductoresHabilitados = remember(conductores) {
        conductores.filter { esRolConductorORepartidor(it.rol) }
    }
    var kmTexto by remember { mutableStateOf(kmMinimo.toString()) }
    var observaciones by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf(EstadoCondicionVehiculo.APTO) }
    var conductorSeleccionado by remember(conductoresHabilitados) {
        mutableStateOf(conductoresHabilitados.firstOrNull())
    }
    val fotos = remember { mutableStateOf(listOf<FotoChequeo>()) }
    val haptic = LocalHapticFeedback.current

    val km = kmTexto.toIntOrNull()
    val errorKm = diagnosticarKilometrajeChequeo(kmTexto, kmMinimo)
    val bloqueadoPorNoApto = !esEntrada && estadoSeleccionado == EstadoCondicionVehiculo.NO_APTO
    val habilitado = km != null && km >= kmMinimo && !bloqueadoPorNoApto && !guardando
    val editarDatos = !guardando && !movimientoConfirmado

    Column(modifier = Modifier.padding(top = 12.dp)) {
        EstadoGuardadoChequeo(guardando, movimientoConfirmado, errorServidor, accent)
        if (!esEntrada) {
            SelectorConductorFlota(
                conductores = conductoresHabilitados,
                seleccionado = conductorSeleccionado,
                onSeleccionar = { if (editarDatos) conductorSeleccionado = it },
            )
            Spacer(Modifier.height(12.dp))
        }

        SelectorCondicionVehiculo(
            esEntrada = esEntrada,
            seleccionado = estadoSeleccionado,
            onSeleccionar = { if (editarDatos) estadoSeleccionado = it },
        )
        Spacer(Modifier.height(12.dp))

        CobreTextField(
            value = kmTexto,
            enabled = editarDatos,
            onValueChange = { kmTexto = it.filter(Char::isDigit) },
            label = if (esEntrada) "Kilometraje de retorno" else "Kilometraje de salida",
            placeholder = "Mínimo $kmMinimo",
            isError = errorKm != null,
            supportingText = errorKm,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = CobreIcons.Speed,
        )

        Spacer(Modifier.height(12.dp))
        SelectorFotos(accent, fotos, haptic, enabled = !guardando)
        Spacer(Modifier.height(8.dp))
        RecordatorioPrivacidadFotos()

        Spacer(Modifier.height(12.dp))
        ObservacionesChequeo(observaciones, editarDatos) { observaciones = it }

        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = CobreShapes.md,
                border = BorderStroke(1.dp, brandMutedColor().copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = brandHeadingColor()),
            ) {
                Text("Regresar", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            BotonConfirmarChequeo(
                modifier = Modifier.weight(1.5f),
                esEntrada = esEntrada,
                habilitado = habilitado,
                onClick = {
                    val kmValido = km ?: return@BotonConfirmarChequeo
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val nombreResponsable = conductorSeleccionado?.nombreCompleto ?: responsablePorDefecto
                    onConfirmar(
                        conductorSeleccionado?.id,
                        nombreResponsable,
                        kmValido,
                        estadoSeleccionado,
                        fotos.value,
                        observaciones.trim().takeIf { it.isNotEmpty() },
                    )
                },
            )
        }
    }
}

@Composable
private fun EstadoGuardadoChequeo(guardando: Boolean, confirmado: Boolean, error: String?, accent: Color) {
    if (guardando) Text("Guardando movimiento y fotografías…", color = brandMutedColor())
    if (confirmado) Text("El movimiento ya está guardado. Reintenta las fotografías pendientes.", color = accent)
    if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
}

@Composable
private fun ObservacionesChequeo(valor: String, enabled: Boolean, cambiar: (String) -> Unit) {
    CobreTextField(
        value = valor,
        enabled = enabled,
        onValueChange = cambiar,
        label = "Observaciones (opcional)",
        placeholder = "Ej. Rayón leve en parachoques trasero",
        singleLine = false,
        minLines = 2,
    )
}

@Composable
private fun BotonConfirmarChequeo(
    esEntrada: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    CobreButton(
        text = if (esEntrada) "Confirmar retorno" else "Confirmar salida",
        onClick = onClick,
        icon = CobreIcons.Check,
        enabled = habilitado,
        fillWidth = true,
        modifier = modifier,
    )
}

@Composable
private fun SelectorConductorFlota(
    conductores: List<ConductorFlota>,
    seleccionado: ConductorFlota?,
    onSeleccionar: (ConductorFlota) -> Unit,
) {
    Text("Conductor o repartidor responsable", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
    Spacer(Modifier.height(6.dp))
    if (conductores.isEmpty()) {
        Text(
            "No hay usuarios con rol conductor o repartidor disponibles.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(conductores, key = { it.id }) { conductor ->
                SelectableChip(
                    label = "${conductor.nombreCompleto} · ${conductor.rol}",
                    seleccionado = seleccionado?.id == conductor.id,
                    onClick = { onSeleccionar(conductor) },
                )
            }
        }
    }
}

@Composable
internal fun SelectorCondicionVehiculo(
    esEntrada: Boolean,
    seleccionado: EstadoCondicionVehiculo,
    onSeleccionar: (EstadoCondicionVehiculo) -> Unit,
) {
    val isDark = cobreIsDark()
    Text(
        if (esEntrada) "Estado técnico al retorno" else "Estado técnico de salida",
        style = MaterialTheme.typography.titleSmall,
        color = brandHeadingColor(),
    )
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoCondicionVehiculo.entries.forEach { opcion ->
            val activo = seleccionado == opcion
            val colorOpcion = when (opcion) {
                EstadoCondicionVehiculo.APTO -> if (isDark) StatusGreenDark else StatusGreen
                EstadoCondicionVehiculo.CON_OBSERVACIONES -> if (isDark) StatusAmberDark else StatusAmber
                EstadoCondicionVehiculo.NO_APTO -> if (isDark) StatusRedDark else StatusRed
            }
            Box(
                modifier = Modifier
                    .clip(CobreShapes.md)
                    .background(if (activo) colorOpcion else colorOpcion.copy(alpha = 0.12f))
                    .clickable { onSeleccionar(opcion) }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) {
                Text(
                    opcion.etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (activo) Color.White else colorOpcion,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
    if (!esEntrada && seleccionado == EstadoCondicionVehiculo.NO_APTO) {
        val rojo = if (isDark) StatusRedDark else StatusRed
        Spacer(Modifier.height(6.dp))
        Text(
            "Un vehículo calificado como NO APTO no puede autorizarse para salir a ruta.",
            style = MaterialTheme.typography.labelSmall,
            color = rojo,
        )
    }
}

@Composable
internal fun SelectorFotos(accent: Color, fotos: MutableState<List<FotoChequeo>>, haptic: HapticFeedback, enabled: Boolean) {
    val abrirCamara = rememberCapturadorFoto { uriCapturada ->
        val orden = fotos.value.size + 1
        val etiqueta = ETIQUETAS_FOTOS.getOrElse(orden - 1) { "Evidencia $orden" }
        fotos.value = fotos.value + FotoChequeo(
            id = "F-$orden-${uriCapturada.hashCode()}",
            uri = uriCapturada,
            descripcion = etiqueta,
            orden = orden,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Evidencia fotográfica (${fotos.value.size})",
            style = MaterialTheme.typography.titleSmall,
            color = brandHeadingColor(),
        )
        CobreButton(
            text = "Tomar foto",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                abrirCamara()
            },
            variant = ButtonVariant.Tonal,
            icon = CobreIcons.PhotoCamera,
            enabled = enabled && fotos.value.size < 20,
        )
    }
    Spacer(Modifier.height(6.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(fotos.value, key = { it.id }) { foto ->
            Column(
                modifier = Modifier
                    .clip(CobreShapes.md)
                    .background(accent.copy(alpha = 0.14f))
                    .clickable(enabled = enabled) { fotos.value = fotos.value.filterNot { it.id == foto.id } }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MiniaturaFotoChequeo(
                    uri = foto.uri,
                    descripcion = foto.descripcion,
                    tint = accent,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CobreShapes.md),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    foto.descripcion ?: "Foto ${foto.orden}",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
            }
        }
        item {
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .pressableScale(interaction)
                    .clip(CobreShapes.md)
                    .background(CobreTheme.colors.accentContainer)
                    .clickable(enabled = enabled && fotos.value.size < 20, interactionSource = interaction, indication = null) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        abrirCamara()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        CobreIcons.PhotoCamera,
                        contentDescription = "Tomar foto",
                        tint = CobreTheme.colors.accent,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Cámara",
                        style = MaterialTheme.typography.labelSmall,
                        color = CobreTheme.colors.accentText,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
