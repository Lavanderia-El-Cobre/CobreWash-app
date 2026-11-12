package com.elcobre.lavanderiaelcobre.data.model

import java.util.Calendar

internal actual fun obtenerHoraFormateadaActual(): String {
    val calendar = Calendar.getInstance()
    val hora = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val minuto = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
    return "$hora:$minuto"
}
