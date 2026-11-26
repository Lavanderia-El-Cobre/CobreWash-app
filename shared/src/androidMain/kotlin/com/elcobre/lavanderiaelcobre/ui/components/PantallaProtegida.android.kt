package com.elcobre.lavanderiaelcobre.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/** Cuántas vistas protegidas hay abiertas: el flag se quita solo cuando se cierra la última. */
private var protegidas = 0

/** Desenvuelve los [ContextWrapper] hasta obtener la [Activity] dueña de la ventana. */
private tailrec fun Context.actividad(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.actividad()
    else -> null
}

/**
 * Aplica `FLAG_SECURE` sobre la ventana de la [Activity] mientras el composable esté montado
 * para bloquear capturas, grabaciones de pantalla y previsualizaciones en apps recientes.
 * Usa conteo de referencias ([protegidas]) para soportar pantallas protegidas superpuestas.
 */
@Composable
actual fun ProtegerCapturas() {
    val actividad = LocalContext.current.actividad() ?: return
    DisposableEffect(actividad) {
        protegidas++
        actividad.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            protegidas = (protegidas - 1).coerceAtLeast(0)
            if (protegidas == 0) actividad.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
