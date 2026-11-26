package com.elcobre.lavanderiaelcobre.ui

/**
 * Canal multiplataforma de notificaciones del sistema para avisos de administración y
 * nuevas comandas asignadas al operario en planta (RF-AN02 / RF-AN04).
 *
 * Cada plataforma implementa el envío mediante su servicio nativo de notificaciones.
 */
internal expect fun notificarEventoPlanta(titulo: String, mensaje: String)
