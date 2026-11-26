package com.elcobre.lavanderiaelcobre.data.auth

import platform.Foundation.NSUserDefaults

private const val KEY_RECORDARME = "cobre_recordarme"
private const val KEY_EMAIL = "cobre_email"
private const val KEY_NOMBRE = "cobre_nombre"
private const val KEY_ROL = "cobre_rol"
private const val KEY_HASTA = "cobre_recordada_hasta"
private const val KEY_INTENTOS = "cobre_intentos_ingreso"
// Clave que guardaban versiones anteriores (turnos de planta); se limpia al guardar o cerrar sesión.
private const val KEY_TURNO_LEGADO = "cobre_turno"

actual object SesionPersistente {
    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun guardar(datos: DatosSesionGuardada, ahoraMs: Long) {
        defaults.setDouble((ahoraMs + DURACION_SESION_RECORDADA_MS).toDouble(), forKey = KEY_HASTA)
        defaults.setBool(true, forKey = KEY_RECORDARME)
        defaults.setObject(datos.email, forKey = KEY_EMAIL)
        defaults.setObject(datos.nombre, forKey = KEY_NOMBRE)
        defaults.setObject(datos.rol, forKey = KEY_ROL)
        defaults.removeObjectForKey(KEY_TURNO_LEGADO)
    }

    actual fun obtener(ahoraMs: Long): DatosSesionGuardada? {
        if (!defaults.boolForKey(KEY_RECORDARME)) return null
        val hasta = defaults.doubleForKey(KEY_HASTA).toLong()
        if (hasta == 0L) {
            defaults.setDouble((ahoraMs + DURACION_SESION_RECORDADA_MS).toDouble(), forKey = KEY_HASTA)
        } else if (ahoraMs >= hasta) {
            limpiar()
            return null
        }
        val email = defaults.stringForKey(KEY_EMAIL)
        val nombre = defaults.stringForKey(KEY_NOMBRE)
        val rol = defaults.stringForKey(KEY_ROL)
        return if (email != null && nombre != null && rol != null) {
            DatosSesionGuardada(email = email, nombre = nombre, rol = rol)
        } else {
            null
        }
    }

    actual fun limpiar() {
        defaults.setBool(false, forKey = KEY_RECORDARME)
        defaults.removeObjectForKey(KEY_EMAIL)
        defaults.removeObjectForKey(KEY_NOMBRE)
        defaults.removeObjectForKey(KEY_ROL)
        defaults.removeObjectForKey(KEY_HASTA)
        defaults.removeObjectForKey(KEY_TURNO_LEGADO)
    }

    actual fun estaRecordado(): Boolean {
        return defaults.boolForKey(KEY_RECORDARME)
    }

    actual fun setRecordarme(recordar: Boolean) {
        defaults.setBool(recordar, forKey = KEY_RECORDARME)
    }

    actual fun getRecordarme(): Boolean {
        if (defaults.objectForKey(KEY_RECORDARME) == null) return false
        return defaults.boolForKey(KEY_RECORDARME)
    }

    actual fun leerIntentos(): String? = defaults.stringForKey(KEY_INTENTOS)

    actual fun guardarIntentos(valor: String?) {
        if (valor == null) defaults.removeObjectForKey(KEY_INTENTOS) else defaults.setObject(valor, forKey = KEY_INTENTOS)
    }
}
