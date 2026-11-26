package com.elcobre.lavanderiaelcobre

import androidx.compose.ui.window.ComposeUIViewController

/**
 * Punto de entrada de la UI Compose Multiplatform para iOS, consumido desde SwiftUI
 * (`iosApp/ContentView.swift`) a través de `MainViewControllerKt.MainViewController()`.
 *
 * Mantiene `PascalCase` de forma intencional por convención de Compose/KMP para fábricas de
 * `UIViewController`.
 */
@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
