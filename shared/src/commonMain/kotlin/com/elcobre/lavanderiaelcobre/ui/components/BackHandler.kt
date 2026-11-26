package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Intercepta el botón físico o gesto de volver atrás en plataformas que lo soportan (Android).
 * En iOS no realiza ninguna acción, ya que la navegación se controla desde la interfaz.
 *
 * @param enabled Activa o desactiva la intercepción sin desmontar el composable.
 * @param onBack Acción ejecutada al recibir el evento de retroceso.
 */
@Composable
expect fun CobreBackHandler(enabled: Boolean = true, onBack: () -> Unit)
