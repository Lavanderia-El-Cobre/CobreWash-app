package com.elcobre.lavanderiaelcobre.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Sincroniza el contraste de los íconos de las barras de estado y navegación de Android
 * según [temaOscuro]. Se omite en modo edición para no fallar en vistas previas de IDE.
 */
@Composable
actual fun BarrasDelSistema(temaOscuro: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.buscarActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !temaOscuro
            isAppearanceLightNavigationBars = !temaOscuro
        }
    }
}

/** Desenvuelve recursivamente los [ContextWrapper] hasta encontrar la [Activity] anfitriona. */
private tailrec fun Context.buscarActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActivity()
    else -> null
}
