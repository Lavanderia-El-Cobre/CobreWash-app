package com.elcobre.lavanderiaelcobre.data.auth

import com.elcobre.lavanderiaelcobre.data.model.Sesion

/**
 * Cierre de sesión cuando la cuenta deja de poder usar la app mientras está abierta:
 * administración la desactivó, le quitó el perfil o le cambió el rol, o Firebase la
 * deshabilitó. El servidor ya rechaza cada operación; esto evita que la app siga
 * mostrando una sesión que no sirve.
 */
object VigilanciaCuenta {
    /** Mensajes de `@check` del backend que indican que la cuenta ya no sirve para operar. */
    private val RECHAZOS_CUENTA = listOf("La cuenta está inactiva", "Perfil no encontrado", "Tu cuenta está inactiva")

    private var oyente: ((String) -> Unit)? = null

    /** Lo registra [com.elcobre.lavanderiaelcobre.ui.CobreViewModel]: cierra la sesión con el mensaje. */
    fun alRechazar(accion: ((String) -> Unit)?) {
        oyente = accion
    }

    /** Revisa un error del servidor; si la cuenta quedó inactiva o sin perfil, avisa para cerrar la sesión. */
    fun revisar(mensajeServidor: String?) {
        if (mensajeServidor != null && RECHAZOS_CUENTA.any { mensajeServidor.contains(it, ignoreCase = true) }) {
            oyente?.invoke("Tu cuenta fue desactivada o ya no tiene acceso. Contacta a administración.")
        }
    }

    /**
     * Motivo para cerrar la sesión según el perfil vigente en el servidor, o `null` si la
     * sesión sigue siendo válida. Un error de red no cierra la sesión.
     */
    fun motivoCierre(actual: Sesion, perfil: PerfilResultado?, sesionDelPerfil: (PerfilResultado.Exito) -> Sesion?): String? =
        when (perfil) {
            null -> "Tu sesión terminó. Inicia sesión nuevamente."
            is PerfilResultado.Error -> perfil.mensaje.takeIf { perfil.definitivo }
            is PerfilResultado.Exito -> {
                val vigente = sesionDelPerfil(perfil)
                when {
                    vigente == null -> "Tu cuenta ya no tiene acceso a la app móvil."
                    // Repartidor y conductor comparten la misma sesión y permisos en la app.
                    vigente::class != actual::class -> "Tus permisos cambiaron. Vuelve a iniciar sesión."
                    else -> null
                }
            }
        }
}
