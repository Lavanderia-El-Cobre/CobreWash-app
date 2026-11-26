package com.elcobre.lavanderiaelcobre.data.model

/**
 * Proveedor multiplataforma de la hora actual en formato "HH:mm" usando la zona horaria local.
 */
internal expect fun obtenerHoraFormateadaActual(): String

/** Formatea un timestamp en milisegundos como "dd/MM HH:mm" en la zona horaria del dispositivo. */
expect fun fechaHoraLocal(ms: Long): String
