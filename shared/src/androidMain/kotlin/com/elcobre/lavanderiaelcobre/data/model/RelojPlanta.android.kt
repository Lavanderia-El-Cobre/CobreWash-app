package com.elcobre.lavanderiaelcobre.data.model

import java.util.Calendar

/** Devuelve la hora local actual del dispositivo en formato `HH:mm` (24 horas). */
internal actual fun obtenerHoraFormateadaActual(): String {
    val calendar = Calendar.getInstance()
    val hora = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val minuto = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
    return "$hora:$minuto"
}

/** Formatea una marca de tiempo en milisegundos como `dd/MM HH:mm` en configuración `es-CL`. */
actual fun fechaHoraLocal(ms: Long): String =
    java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.forLanguageTag("es-CL")).format(java.util.Date(ms))
