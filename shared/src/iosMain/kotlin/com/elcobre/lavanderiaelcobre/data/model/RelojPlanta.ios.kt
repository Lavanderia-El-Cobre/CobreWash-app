package com.elcobre.lavanderiaelcobre.data.model

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDate

internal actual fun obtenerHoraFormateadaActual(): String {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    val hora = calendar.component(NSCalendarUnitHour, fromDate = date).toInt().toString().padStart(2, '0')
    val minuto = calendar.component(NSCalendarUnitMinute, fromDate = date).toInt().toString().padStart(2, '0')
    return "$hora:$minuto"
}

actual fun fechaHoraLocal(ms: Long): String {
    val formato = platform.Foundation.NSDateFormatter()
    formato.dateFormat = "dd/MM HH:mm"
    return formato.stringFromDate(NSDate(timeIntervalSinceReferenceDate = ms / 1000.0 - 978_307_200.0))
}
