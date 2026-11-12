package com.elcobre.lavanderiaelcobre.data.auth

import platform.Foundation.NSUserDefaults

private const val KEY_RECORDARME = "cobre_recordarme"
private const val KEY_EMAIL = "cobre_email"
private const val KEY_NOMBRE = "cobre_nombre"
private const val KEY_ROL = "cobre_rol"
// Clave que guardaban versiones anteriores (turnos de planta); se limpia al guardar o cerrar sesión.
private const val KEY_TURNO_LEGADO = "cobre_turno"

actual object SesionPersistente {
    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun guardar(datos: DatosSesionGuardada) {
        defaults.setBool(true, forKey = KEY_RECORDARME)
        defaults.setObject(datos.email, forKey = KEY_EMAIL)
        defaults.setObject(datos.nombre, forKey = KEY_NOMBRE)
        defaults.setObject(datos.rol, forKey = KEY_ROL)
        defaults.removeObjectForKey(KEY_TURNO_LEGADO)
    }

    actual fun obtener(): DatosSesionGuardada? {
        if (!defaults.boolForKey(KEY_RECORDARME)) return null
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
        defaults.removeObjectForKey(KEY_TURNO_LEGADO)
    }

    actual fun estaRecordado(): Boolean {
        return defaults.boolForKey(KEY_RECORDARME)
    }

    actual fun setRecordarme(recordar: Boolean) {
        defaults.setBool(recordar, forKey = KEY_RECORDARME)
    }

    actual fun getRecordarme(): Boolean {
        if (defaults.objectForKey(KEY_RECORDARME) == null) return true
        return defaults.boolForKey(KEY_RECORDARME)
    }
}
