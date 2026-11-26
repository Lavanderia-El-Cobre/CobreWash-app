package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Impide capturas y grabaciones de pantalla (y oculta la vista previa en recientes)
 * mientras está en composición. Se usa en las vistas con datos personales de clientes
 * o del personal (Ley 21.719): clientes, comandas, usuarios y entregas.
 */
@Composable
expect fun ProtegerCapturas()
