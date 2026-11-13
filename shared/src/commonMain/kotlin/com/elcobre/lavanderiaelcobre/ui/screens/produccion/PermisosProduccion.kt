package com.elcobre.lavanderiaelcobre.ui.screens.produccion

/** Qué puede hacer cada rol en el seguimiento, igual que `app/intranet/seguimiento/page.tsx`. */
data class PermisosProduccion(
    val completar: Boolean = false,
    val reasignar: Boolean = false,
    val asociarFlujo: Boolean = false,
    val reportarIncidencia: Boolean = false,
)
