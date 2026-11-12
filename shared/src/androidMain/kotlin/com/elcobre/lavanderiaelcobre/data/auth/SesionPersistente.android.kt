package com.elcobre.lavanderiaelcobre.data.auth

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "cobre_sesion_prefs"
private const val KEY_RECORDARME = "recordarme"
private const val KEY_EMAIL = "email"
private const val KEY_NOMBRE = "nombre"
private const val KEY_ROL = "rol"
// Clave que guardaban versiones anteriores (turnos de planta); se limpia al guardar o cerrar sesión.
private const val KEY_TURNO_LEGADO = "turno"

private var prefs: SharedPreferences? = null
private var inMemoryFallback: DatosSesionGuardada? = null
private var inMemoryRecordarme: Boolean = true

fun inicializarSesionPersistente(context: Context) {
    if (prefs == null) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

actual object SesionPersistente {
    actual fun guardar(datos: DatosSesionGuardada) {
        val p = prefs
        if (p != null) {
            p.edit()
                .putBoolean(KEY_RECORDARME, true)
                .putString(KEY_EMAIL, datos.email)
                .putString(KEY_NOMBRE, datos.nombre)
                .putString(KEY_ROL, datos.rol)
                .remove(KEY_TURNO_LEGADO)
                .apply()
        } else {
            inMemoryFallback = datos
            inMemoryRecordarme = true
        }
    }

    actual fun obtener(): DatosSesionGuardada? {
        val p = prefs
        if (p == null) {
            return if (inMemoryRecordarme) inMemoryFallback else null
        }
        return leerDePrefs(p)
    }

    private fun leerDePrefs(p: SharedPreferences): DatosSesionGuardada? {
        if (!p.getBoolean(KEY_RECORDARME, false)) return null
        val email = p.getString(KEY_EMAIL, null)
        val nombre = p.getString(KEY_NOMBRE, null)
        val rol = p.getString(KEY_ROL, null)
        return if (email != null && nombre != null && rol != null) {
            DatosSesionGuardada(email = email, nombre = nombre, rol = rol)
        } else {
            null
        }
    }

    actual fun limpiar() {
        val p = prefs
        if (p != null) {
            p.edit()
                .putBoolean(KEY_RECORDARME, false)
                .remove(KEY_EMAIL)
                .remove(KEY_NOMBRE)
                .remove(KEY_ROL)
                .remove(KEY_TURNO_LEGADO)
                .apply()
        } else {
            inMemoryFallback = null
            inMemoryRecordarme = false
        }
    }

    actual fun estaRecordado(): Boolean {
        val p = prefs
        return p?.getBoolean(KEY_RECORDARME, false) ?: inMemoryRecordarme
    }

    actual fun setRecordarme(recordar: Boolean) {
        val p = prefs
        if (p != null) {
            p.edit().putBoolean(KEY_RECORDARME, recordar).apply()
        } else {
            inMemoryRecordarme = recordar
        }
    }

    actual fun getRecordarme(): Boolean {
        val p = prefs
        return p?.getBoolean(KEY_RECORDARME, true) ?: inMemoryRecordarme
    }
}
