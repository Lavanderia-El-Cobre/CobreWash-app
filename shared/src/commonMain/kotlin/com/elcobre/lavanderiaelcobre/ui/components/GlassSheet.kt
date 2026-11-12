package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Ancho máximo de la hoja: en tablet y en horizontal queda centrada en vez de cubrir todo el ancho. */
private val AnchoMaxHoja = 640.dp

/**
 * Hoja inferior para formularios (alta de vehículo, reporte de insumo, chequeo) que hoy
 * se abren dentro de la lista y la empujan. Se muestra mientras esté en composición:
 * el llamador la quita en [onDismiss].
 *
 * La hoja vive en su propia ventana, donde el desenfoque de fondo no llega; por eso usa
 * la superficie sólida del tema y un velo, como las hojas de iOS sobre contenido denso.
 * El contenido hace scroll para que entre en un teléfono horizontal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassSheet(
    onDismiss: () -> Unit,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CobreTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetMaxWidth = AnchoMaxHoja,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = colors.surface,
        contentColor = colors.textPrimary,
        scrimColor = Color.Black.copy(alpha = if (colors.isDark) 0.5f else 0.28f),
        dragHandle = { AsaHoja() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        ) {
            if (title != null) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary)
                Spacer(Modifier.height(16.dp))
            }
            content()
        }
    }
}

@Composable
private fun AsaHoja() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 5.dp)
                .clip(CobreShapes.full)
                .background(CobreTheme.colors.textSecondary.copy(alpha = 0.35f)),
        )
    }
}
