package com.elcobre.lavanderiaelcobre.data.auth

/**
 * Datos mínimos de la sesión que se persisten localmente cuando el usuario
 * activa la opción "Recordarme" en la pantalla de inicio de sesión.
 */
data class DatosSesionGuardada(
    val email: String,
    val nombre: String,
    val rol: String,
)

/** Una sesión recordada dura como máximo 30 días; luego se vuelve a pedir la contraseña (como la web). */
const val DURACION_SESION_RECORDADA_MS = 30L * 24 * 60 * 60 * 1000

/**
 * Almacén multiplataforma de sesión para la funcionalidad "Recordarme".
 * En Android persiste en SharedPreferences; en iOS en NSUserDefaults.
 * Nunca guarda la contraseña: solo correo, nombre y rol para la interfaz (el rol se
 * vuelve a validar en el servidor al restaurar) y la fecha en que vence.
 */
expect object SesionPersistente {
    /** Guarda la sesión recordada con vencimiento a [DURACION_SESION_RECORDADA_MS] desde [ahoraMs]. */
    fun guardar(datos: DatosSesionGuardada, ahoraMs: Long)

    /** La sesión recordada vigente en [ahoraMs], o `null` si no hay o ya venció (y la borra). */
    fun obtener(ahoraMs: Long): DatosSesionGuardada?
    fun limpiar()
    fun estaRecordado(): Boolean
    fun setRecordarme(recordar: Boolean)
    fun getRecordarme(): Boolean

    /** Registro del bloqueo de intentos de ingreso ([IntentosIngreso]); `null` lo borra. */
    fun leerIntentos(): String?
    fun guardarIntentos(valor: String?)
}
