package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Impide capturas y grabaciones de pantalla (y oculta la vista previa en recientes)
 * mientras está en composición. Cada plataforma aplica su mecanismo nativo de protección
 * de ventana. Se usa en vistas con datos personales (Ley N° 21.719): clientes, comandas,
 * usuarios y entregas.
 */
@Composable
expect fun ProtegerCapturas()
