package com.elcobre.lavanderiaelcobre.ui.screens.escaner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.components.QrScannerView
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.Ink

/** Lado máximo del visor: en tablet no tiene sentido un cuadrado de 700dp. */
private val LadoMaxVisor = 380.dp

/**
 * Escáner real de comandas e insumos con búsqueda manual de respaldo.
 *
 * El visor se limita por el alto disponible y, en horizontal, va a la izquierda con las
 * acciones a la derecha, para que todo entre sin scroll en un teléfono acostado.
 */
@Composable
fun EscanerScreen(
    onVolver: () -> Unit,
    buscarComanda: (String) -> Pedido?,
    buscarRemota: (String, (Pedido) -> Unit, () -> Unit) -> Unit,
    onEscanearQr: (String, onResult: (errorMsg: String?) -> Unit) -> Unit,
    onEscanearInsumo: (String, onResult: (errorMsg: String?) -> Unit) -> Unit,
    onAbrir: (Pedido) -> Unit,
) {
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val window = CobreTheme.window

    // Cooldown para evitar que, al volver de Detalle, la cámara vuelva a atrapar el QR
    // de inmediato y te mande de regreso sin dejarte respirar.
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        isScanning = true
    }

    val alLeerQr: (String) -> Unit = { rawUrl ->
        val uuid = extraerCodigoQr(rawUrl)
        if (uuid != null) {
            isScanning = false // Pausar escáner
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            error = "Buscando comanda en la base de datos..."
            onEscanearQr(uuid) { errorMsg ->
                if (errorMsg != null) {
                    error = errorMsg
                    isScanning = true // Reanudar escaneo si falló
                }
            }
        } else if (extraerQrInsumo(rawUrl) != null) {
            isScanning = false
            error = "Buscando insumo…"
            onEscanearInsumo(rawUrl) { errorMsg ->
                if (errorMsg != null) { error = errorMsg; isScanning = true }
            }
        } else {
            error = "Código QR inválido. Escanea una comanda oficial o un QR de insumo."
        }
    }
    val abrirManual = {
        val pedido = buscarComanda(codigo)
        if (pedido != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            codigo = ""
            onAbrir(pedido)
        } else {
            buscarRemota(codigo, onAbrir) { error = "No se encontró la comanda o no se pudo consultar." }
        }
    }
    val acciones: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EntradaManual(codigo = codigo, onCodigoChange = { codigo = it; error = null }, onAbrir = abrirManual)
            Mensaje(error)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        val horizontal = window.isLandscape && maxWidth >= 600.dp
        val altoUtil = maxHeight - 96.dp
        val anchoUtil = maxWidth - window.gutter * 2
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = window.gutter, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            EncabezadoSimple(
                titulo = "Escanear QR",
                subtitulo = "Apunta al QR de una comanda o un insumo",
                onVolver = onVolver,
            )
            if (horizontal) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Visor(lado = minOf(altoUtil, LadoMaxVisor), activo = isScanning, onLeido = alLeerQr)
                    }
                    Box(modifier = Modifier.weight(1f).widthIn(max = 460.dp)) { acciones() }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Visor(lado = minOf(anchoUtil, altoUtil * 0.55f, LadoMaxVisor), activo = isScanning, onLeido = alLeerQr)
                }
                Box(modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp).align(Alignment.CenterHorizontally)) { acciones() }
            }
        }
    }
}

/**
 * Visor de cámara con esquinas de mira cobre dibujadas encima. El fondo es siempre
 * oscuro (como el visor real) mientras la cámara arranca.
 */
@Composable
private fun Visor(lado: Dp, activo: Boolean, onLeido: (String) -> Unit) {
    val colors = CobreTheme.colors
    val acento = colors.accent
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(lado)
                .clip(CobreShapes.lg)
                .background(Ink)
                .drawWithContent {
                    drawContent()
                    val largo = size.minDimension * 0.16f
                    val margen = size.minDimension * 0.10f
                    val grosor = 4.dp.toPx()
                    val (w, h) = size.width to size.height
                    fun esquina(origen: Offset, dx: Float, dy: Float) {
                        drawLine(acento, origen, origen + Offset(dx * largo, 0f), grosor, StrokeCap.Round)
                        drawLine(acento, origen, origen + Offset(0f, dy * largo), grosor, StrokeCap.Round)
                    }
                    esquina(Offset(margen, margen), 1f, 1f)
                    esquina(Offset(w - margen, margen), -1f, 1f)
                    esquina(Offset(margen, h - margen), 1f, -1f)
                    esquina(Offset(w - margen, h - margen), -1f, -1f)
                },
        ) {
            QrScannerView(modifier = Modifier.fillMaxSize(), isActive = activo, onQrCodeScanned = onLeido)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (activo) "Buscando código QR…" else "Preparando cámara…",
            style = CobreTheme.text.label,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun EntradaManual(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    onAbrir: () -> Unit,
) {
    val colors = CobreTheme.colors
    val habilitado = codigo.isNotBlank()
    ContentCard {
        Text(
            "¿No escanea?",
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(2.dp))
        Text("Ingresa el código de comanda manualmente.", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        Spacer(Modifier.height(12.dp))
        CobreTextField(
            value = codigo,
            onValueChange = onCodigoChange,
            label = "Código de comanda",
            placeholder = "Ej. ELCOBRE-14r3",
            leadingIcon = CobreIcons.Inventory,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { if (habilitado) onAbrir() }),
        )
        Spacer(Modifier.height(12.dp))
        CobreButton(
            text = "Abrir comanda",
            onClick = onAbrir,
            variant = ButtonVariant.Tonal,
            icon = CobreIcons.ArrowForward,
            enabled = habilitado,
            fillWidth = true,
        )
    }
}

/** Estado del escaneo o error de búsqueda, anunciado a lectores de pantalla. */
@Composable
private fun Mensaje(texto: String?) {
    val colors = CobreTheme.colors
    AnimatedVisibility(visible = texto != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CobreShapes.md)
                .background(colors.danger.copy(alpha = 0.10f))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(18.dp))
            Text(texto.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
        }
    }
}

/** Encabezado con botón de volver de vidrio, reutilizado por las pantallas secundarias. */
@Composable
internal fun EncabezadoSimple(
    titulo: String,
    subtitulo: String,
    onVolver: () -> Unit,
) {
    val colors = CobreTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        GlassIconButton(icon = CobreIcons.ArrowBack, contentDescription = "Volver", onClick = onVolver, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
    }
}
