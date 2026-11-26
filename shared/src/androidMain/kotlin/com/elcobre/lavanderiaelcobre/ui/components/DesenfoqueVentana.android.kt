package com.elcobre.lavanderiaelcobre.ui.components

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogWindowProvider

/**
 * Activa el desenfoque real detrás de ventanas de diálogo en Android 12+ (API 31+) cuando
 * el dispositivo soporta cross-window blur. Devuelve `true` si se aplicó para que la UI
 * reduzca la opacidad del fondo, o `false` para recurrir al velo sólido estándar.
 */
@Composable
internal actual fun desenfocarDetrasDeVentana(radio: Dp): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
    val view = LocalView.current
    val radioPx = with(LocalDensity.current) { radio.roundToPx() }
    var activo by remember { mutableStateOf(false) }
    DisposableEffect(view, radioPx) {
        val window = generateSequence(view as View?) { it.parent as? View }
            .firstNotNullOfOrNull { it as? DialogWindowProvider }
            ?.window
        val windowManager = view.context.getSystemService(WindowManager::class.java)
        if (window != null && windowManager?.isCrossWindowBlurEnabled == true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes = window.attributes.apply { blurBehindRadius = radioPx }
            // Con desenfoque el velo puede ser más suave: el vidrio ya separa la ventana del fondo.
            window.setDimAmount(0.2f)
            activo = true
        }
        onDispose { }
    }
    return activo
}
