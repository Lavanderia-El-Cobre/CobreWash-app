package com.elcobre.lavanderiaelcobre.data.model

/**
 * Proveedor multiplataforma de la hora actual en formato "HH:mm".
 */
internal expect fun obtenerHoraFormateadaActual(): String

/** Fecha y hora "dd/MM HH:mm" en la zona horaria del teléfono. */
expect fun fechaHoraLocal(ms: Long): String
