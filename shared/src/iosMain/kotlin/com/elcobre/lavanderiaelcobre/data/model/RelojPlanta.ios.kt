package com.elcobre.lavanderiaelcobre.data.model

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDate

/**
 * Obtiene la hora actual del dispositivo en formato `HH:mm` usando [NSCalendar.currentCalendar]
 * y la zona horaria local de iOS.
 */
internal actual fun obtenerHoraFormateadaActual(): String {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    val hora = calendar.component(NSCalendarUnitHour, fromDate = date).toInt().toString().padStart(2, '0')
    val minuto = calendar.component(NSCalendarUnitMinute, fromDate = date).toInt().toString().padStart(2, '0')
    return "$hora:$minuto"
}

/**
 * Formatea un instante Unix en milisegundos (`ms`) como `dd/MM HH:mm` en la zona horaria local
 * de iOS mediante [platform.Foundation.NSDateFormatter].
 */
actual fun fechaHoraLocal(ms: Long): String {
    val formato = platform.Foundation.NSDateFormatter()
    formato.dateFormat = "dd/MM HH:mm"
    // Foundation usa el 1 de enero de 2001 como época de referencia (978 307 200 s tras 1970).
    return formato.stringFromDate(NSDate(timeIntervalSinceReferenceDate = ms / 1000.0 - 978_307_200.0))
}
