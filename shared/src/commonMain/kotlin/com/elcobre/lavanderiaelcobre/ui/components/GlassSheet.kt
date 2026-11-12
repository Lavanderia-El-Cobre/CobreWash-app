package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Ancho máximo de la hoja: en tablet y en horizontal queda centrada en vez de cubrir todo el ancho. */
private val AnchoMaxHoja = 640.dp

/** Ancho máximo de un diálogo de confirmación. */
private val AnchoMaxDialogo = 400.dp

/** Radio de las hojas y diálogos flotantes, como las hojas de vidrio de iOS 26. */
private val RadioFlotante = 28.dp

/** Desenfoque del contenido de la app detrás de una hoja o diálogo. */
private val DesenfoqueFondo = 28.dp

/**
 * Hoja de vidrio flotante para formularios y lecturas (alta y bitácora de vehículos,
 * privacidad, escaneo de insumos). No es la hoja de Material pegada al borde: es una
 * tarjeta separada de los bordes, con esquinas amplias, asa, botón de cerrar circular
 * y el contenido de la app desenfocado detrás. Se muestra mientras esté en composición:
 * el llamador la quita en [onDismiss]. El contenido hace scroll para que entre en un
 * teléfono horizontal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSheet(
    onDismiss: () -> Unit,
    title: String? = null,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CobreTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetMaxWidth = AnchoMaxHoja,
        shape = RoundedCornerShape(RadioFlotante),
        containerColor = Color.Transparent,
        contentColor = colors.textPrimary,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = if (colors.isDark) 0.40f else 0.18f),
        dragHandle = null,
    ) {
        val forma = RoundedCornerShape(RadioFlotante)
        TarjetaFlotante(
            forma = forma,
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        ) {
            AsaHoja()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (title != null) CabeceraHoja(title = title, subtitle = subtitle, leading = leading, onCerrar = onDismiss)
                content()
            }
        }
    }
}

/**
 * Confirmación de vidrio centrada (p. ej. cerrar sesión), en lugar del `AlertDialog` de
 * Material: tarjeta translúcida con esquinas amplias, fondo desenfocado y botones de la app.
 */
@Composable
fun GlassAlert(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "Cancelar",
    destructive: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        TarjetaFlotante(
            forma = RoundedCornerShape(RadioFlotante),
            modifier = Modifier.widthIn(max = AnchoMaxDialogo).fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CobreTheme.colors.textPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(message, style = MaterialTheme.typography.bodyMedium, color = CobreTheme.colors.textSecondary)
                Spacer(Modifier.height(12.dp))
                CobreButton(
                    text = confirmText,
                    onClick = onConfirm,
                    variant = if (destructive) ButtonVariant.Danger else ButtonVariant.Accent,
                    fillWidth = true,
                )
                CobreButton(text = dismissText, onClick = onDismiss, variant = ButtonVariant.Glass, fillWidth = true)
            }
        }
    }
}

/**
 * Superficie de vidrio de una ventana aparte. El desenfoque de Haze no cruza ventanas,
 * así que se pide al sistema el desenfoque de fondo; si no está disponible, o con
 * "Reducir transparencia", la tarjeta pasa a ser casi opaca.
 */
@Composable
private fun TarjetaFlotante(
    forma: Shape,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CobreTheme.colors
    val desenfoque = desenfocarDetrasDeVentana(DesenfoqueFondo) && !CobreTheme.reduceTransparency
    val opacidad = when {
        !desenfoque -> 0.98f
        colors.isDark -> 0.78f
        else -> 0.74f
    }
    val brillo = Brush.verticalGradient(
        listOf(Color.White.copy(alpha = if (colors.isDark) 0.06f else 0.35f), Color.Transparent),
    )
    // Sin fondo propio para el vidrio interno: en otra ventana el desenfoque de la app no llega.
    CompositionLocalProvider(LocalGlassBackdrop provides null) {
        Column(
            modifier = modifier
                .clip(forma)
                .background(colors.surface.copy(alpha = opacidad))
                .background(brillo)
                .border(width = 1.dp, color = colors.hairline, shape = forma),
            content = content,
        )
    }
}

@Composable
private fun CabeceraHoja(
    title: String,
    subtitle: String?,
    leading: (@Composable () -> Unit)?,
    onCerrar: () -> Unit,
) {
    val colors = CobreTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        leading?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
        }
        GlassIconButton(icon = CobreIcons.Close, contentDescription = "Cerrar", onClick = onCerrar, size = 36.dp)
    }
}

@Composable
private fun AsaHoja() {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 5.dp)
                .clip(CobreShapes.full)
                .background(CobreTheme.colors.textSecondary.copy(alpha = 0.35f)),
        )
    }
}
