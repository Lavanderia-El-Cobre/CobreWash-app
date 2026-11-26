package com.elcobre.lavanderiaelcobre.data.auth

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "cobre_sesion_prefs"
private const val KEY_RECORDARME = "recordarme"
private const val KEY_EMAIL = "email"
private const val KEY_NOMBRE = "nombre"
private const val KEY_ROL = "rol"
private const val KEY_HASTA = "recordada_hasta"
private const val KEY_INTENTOS = "intentos_ingreso"
// Clave que guardaban versiones anteriores (turnos de planta); se limpia al guardar o cerrar sesión.
private const val KEY_TURNO_LEGADO = "turno"

private var prefs: SharedPreferences? = null
private var inMemoryFallback: DatosSesionGuardada? = null
private var inMemoryRecordarme: Boolean = false
private var inMemoryHasta: Long = 0
private var inMemoryIntentos: String? = null

/**
 * Inicializa las preferencias privadas (`MODE_PRIVATE`) donde se persiste la sesión recordada
 * y el contador de intentos fallidos de inicio de sesión.
 */
fun inicializarSesionPersistente(context: Context) {
    if (prefs == null) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

/**
 * Implementación Android de [SesionPersistente] sobre `SharedPreferences` privadas.
 * Incluye respaldo en memoria para entornos sin contexto inicializado, control de vigencia
 * por [DURACION_SESION_RECORDADA_MS] y limpieza de claves legadas de versiones anteriores.
 */
actual object SesionPersistente {
    actual fun guardar(datos: DatosSesionGuardada, ahoraMs: Long) {
        val hasta = ahoraMs + DURACION_SESION_RECORDADA_MS
        val p = prefs
        if (p != null) {
            p.edit()
                .putBoolean(KEY_RECORDARME, true)
                .putString(KEY_EMAIL, datos.email)
                .putString(KEY_NOMBRE, datos.nombre)
                .putString(KEY_ROL, datos.rol)
                .putLong(KEY_HASTA, hasta)
                .remove(KEY_TURNO_LEGADO)
                .apply()
        } else {
            inMemoryFallback = datos
            inMemoryRecordarme = true
            inMemoryHasta = hasta
        }
    }

    actual fun obtener(ahoraMs: Long): DatosSesionGuardada? {
        val p = prefs
        if (p == null) {
            if (inMemoryRecordarme && ahoraMs >= inMemoryHasta) limpiar()
            return if (inMemoryRecordarme) inMemoryFallback else null
        }
        return leerDePrefs(p, ahoraMs)
    }

    private fun leerDePrefs(p: SharedPreferences, ahoraMs: Long): DatosSesionGuardada? {
        if (!p.getBoolean(KEY_RECORDARME, false)) return null
        val hasta = p.getLong(KEY_HASTA, 0L)
        if (hasta == 0L) {
            // Sesión guardada por una versión anterior, sin vencimiento: rige desde hoy.
            p.edit().putLong(KEY_HASTA, ahoraMs + DURACION_SESION_RECORDADA_MS).apply()
        } else if (ahoraMs >= hasta) {
            limpiar()
            return null
        }
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
                .remove(KEY_HASTA)
                .remove(KEY_TURNO_LEGADO)
                .apply()
        } else {
            inMemoryFallback = null
            inMemoryRecordarme = false
            inMemoryHasta = 0
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
        // Desmarcado por defecto, como "Mantener la sesión iniciada" de la web.
        return p?.getBoolean(KEY_RECORDARME, false) ?: inMemoryRecordarme
    }

    actual fun leerIntentos(): String? = prefs?.getString(KEY_INTENTOS, null) ?: inMemoryIntentos

    actual fun guardarIntentos(valor: String?) {
        val p = prefs
        if (p == null) {
            inMemoryIntentos = valor
            return
        }
        if (valor == null) p.edit().remove(KEY_INTENTOS).apply() else p.edit().putString(KEY_INTENTOS, valor).apply()
    }
}
