package com.elcobre.lavanderiaelcobre

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.elcobre.lavanderiaelcobre.ui.CobreApp

/**
 * Punto de entrada `@Composable` compartido entre Android (`MainActivity`) e iOS
 * (`MainViewController`). Delega tema, estado global y navegación por rol a [CobreApp].
 */
@Composable
@Preview
fun App() {
    CobreApp()
}
