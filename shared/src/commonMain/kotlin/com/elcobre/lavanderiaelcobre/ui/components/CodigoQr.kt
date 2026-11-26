package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

/**
 * Código QR dibujado en la app. Siempre negro sobre blanco y con margen claro (quiet zone
 * de 12 dp), también en tema oscuro, para que lo lean los escáneres y las cámaras.
 *
 * @param valor Contenido codificado en el QR.
 * @param descripcion Texto accesible (`contentDescription`) para lectores de pantalla.
 * @param tamano Lado del código QR en dp, sin incluir el margen blanco exterior.
 */
@Composable
fun CodigoQr(valor: String, descripcion: String, modifier: Modifier = Modifier, tamano: Dp = 160.dp) {
    val pintor = rememberQrCodePainter(valor)
    Box(
        modifier = modifier
            .clip(CobreShapes.lg)
            .background(Color.White)
            .border(0.5.dp, CobreTheme.colors.hairline, CobreShapes.lg)
            .padding(12.dp),
    ) {
        Image(painter = pintor, contentDescription = descripcion, modifier = Modifier.size(tamano))
    }
}
