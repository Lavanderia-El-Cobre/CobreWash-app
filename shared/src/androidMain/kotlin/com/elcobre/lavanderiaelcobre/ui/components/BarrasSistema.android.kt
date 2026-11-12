package com.elcobre.lavanderiaelcobre.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

private tailrec fun Context.buscarActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActivity()
    else -> null
}
