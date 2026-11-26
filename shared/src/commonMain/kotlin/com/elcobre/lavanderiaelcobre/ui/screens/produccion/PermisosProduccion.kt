package com.elcobre.lavanderiaelcobre.ui.screens.produccion

/**
 * Banderas de acciones habilitadas según el rol en la pantalla de producción, replicando
 * `app/intranet/seguimiento/page.tsx`:
 * - [completar]: avanzar la etapa activa (administración y operario).
 * - [reasignar]: cambiar el operario responsable de la etapa (administración).
 * - [asociarFlujo]: iniciar el flujo de etapas en comandas sin flujo (administración y recepción).
 * - [reportarIncidencia]: registrar incidencias desde planta (operario).
 */
data class PermisosProduccion(
    val completar: Boolean = false,
    val reasignar: Boolean = false,
    val asociarFlujo: Boolean = false,
    val reportarIncidencia: Boolean = false,
)
