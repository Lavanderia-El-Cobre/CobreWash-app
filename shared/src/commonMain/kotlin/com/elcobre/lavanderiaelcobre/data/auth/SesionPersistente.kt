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

/**
 * Almacén multiplataforma de sesión para la funcionalidad "Recordarme".
 * En Android persiste en SharedPreferences; en iOS en NSUserDefaults.
 */
expect object SesionPersistente {
    fun guardar(datos: DatosSesionGuardada)
    fun obtener(): DatosSesionGuardada?
    fun limpiar()
    fun estaRecordado(): Boolean
    fun setRecordarme(recordar: Boolean)
    fun getRecordarme(): Boolean
}
