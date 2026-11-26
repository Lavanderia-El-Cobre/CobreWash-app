package com.elcobre.lavanderiaelcobre.data.auth

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.elcobre.lavanderiaelcobre.data.ORIGEN_WEB
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

/** Códigos de Firebase que corresponden a credenciales incorrectas (cuentan para el bloqueo). */
private val CREDENCIALES_INVALIDAS = setOf(
    "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD", "ERROR_INVALID_LOGIN_CREDENTIALS", "ERROR_INVALID_CREDENTIAL",
)

private fun falloDe(error: Throwable): FalloIngreso = when {
    error is FirebaseTooManyRequestsException -> FalloIngreso.BLOQUEO_SERVIDOR
    error is FirebaseAuthException && error.errorCode == "ERROR_TOO_MANY_REQUESTS" -> FalloIngreso.BLOQUEO_SERVIDOR
    error is FirebaseAuthException && error.errorCode in CREDENCIALES_INVALIDAS -> FalloIngreso.CREDENCIALES
    else -> FalloIngreso.OTRO
}

/** Enlace de retorno al inicio de sesión de la web, como `conRetorno` de `lib/firebase/verificacion.ts`. */
private fun ajustesRetorno(): ActionCodeSettings =
    ActionCodeSettings.newBuilder().setUrl("$ORIGEN_WEB/?login=1").setHandleCodeInApp(false).build()

private val CODIGOS_RETORNO = setOf(
    "ERROR_UNAUTHORIZED_CONTINUE_URI", "ERROR_INVALID_CONTINUE_URI", "ERROR_MISSING_CONTINUE_URI", "ERROR_UNAUTHORIZED_DOMAIN",
)

/** Envía con enlace de retorno y, si el dominio no está autorizado, reintenta sin él. */
private suspend fun conRetorno(enviar: suspend (ActionCodeSettings?) -> Unit) {
    try {
        enviar(ajustesRetorno())
    } catch (error: FirebaseAuthException) {
        if (error.errorCode !in CODIGOS_RETORNO) throw error
        try {
            enviar(null)
        } catch (reintento: FirebaseAuthException) {
            // Firebase acepta un solo envío por minuto: si choca con ese límite, el primero ya salió.
            if (reintento.errorCode != "ERROR_TOO_MANY_REQUESTS") throw reintento
        }
    }
}

private const val AUTH_TAG = "AuthRepository"

/** Igual que lib/firebase/errors.ts en la web: mensajes en español por código de error. */
private fun mapAuthError(error: Throwable): String = when (error) {
    is FirebaseNetworkException -> "No hay conexión a internet. Verifica tu red e intenta de nuevo."
    is FirebaseTooManyRequestsException -> "Demasiados intentos. Intenta nuevamente más tarde."
    is FirebaseAuthException -> when (error.errorCode) {
        "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
        "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada."
        "ERROR_USER_NOT_FOUND",
        "ERROR_WRONG_PASSWORD",
        "ERROR_INVALID_LOGIN_CREDENTIALS",
        "ERROR_INVALID_CREDENTIAL" -> "Correo o contraseña incorrectos."
        "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta nuevamente más tarde."
        "ERROR_INVALID_API_KEY",
        "ERROR_APP_NOT_AUTHORIZED" -> "La app no está configurada correctamente para iniciar sesión. Contacta al administrador."
        "ERROR_OPERATION_NOT_ALLOWED" -> "El inicio de sesión con correo no está habilitado. Contacta al administrador."
        else -> "Ocurrió un error inesperado. Intenta nuevamente."
    }
    else -> "Ocurrió un error inesperado. Intenta nuevamente."
}

actual class AuthRepository actual constructor() {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    actual suspend fun iniciarSesion(correo: String, password: String): AuthResultado = try {
        val credential = auth.signInWithEmailAndPassword(correo, password).await()
        val user = credential.user
        if (user == null) {
            AuthResultado.Error("Ocurrió un error inesperado. Intenta nuevamente.")
        } else {
            AuthResultado.Exito(uid = user.uid, email = user.email ?: correo)
        }
    } catch (error: FirebaseException) {
        val codigo = (error as? FirebaseAuthException)?.errorCode ?: error::class.simpleName
        Log.w(AUTH_TAG, "Firebase Auth rechazó el inicio de sesión: $codigo")
        AuthResultado.Error(mapAuthError(error), falloDe(error))
    } catch (error: IllegalArgumentException) {
        Log.w(AUTH_TAG, "Las credenciales no tienen un formato válido: ${error::class.simpleName}")
        AuthResultado.Error("El correo electrónico no es válido.")
    } catch (error: IllegalStateException) {
        Log.e(AUTH_TAG, "Firebase Auth no está inicializado correctamente: ${error::class.simpleName}")
        AuthResultado.Error(mapAuthError(error))
    }

    actual fun cerrarSesion() {
        auth.signOut()
        com.elcobre.lavanderiaelcobre.data.eliminarCapturasAlSalir()
    }

    actual suspend fun correoPorVerificar(): Boolean {
        val user = auth.currentUser ?: return false
        // Lee el estado actual del servidor: la persona pudo verificar en otro dispositivo.
        runCatching { user.reload().await() }
        val actual = auth.currentUser ?: return false
        return requiereVerificacion(actual.isEmailVerified, actual.metadata?.creationTimestamp)
    }

    actual suspend fun enviarVerificacion(): String? {
        val user = auth.currentUser ?: return "Inicia sesión nuevamente."
        return try {
            conRetorno { ajustes ->
                if (ajustes != null) user.sendEmailVerification(ajustes).await() else user.sendEmailVerification().await()
            }
            null
        } catch (error: FirebaseException) {
            if (falloDe(error) == FalloIngreso.BLOQUEO_SERVIDOR) {
                "Ya enviamos un correo hace poco. Revisa tu bandeja de entrada (y spam) o espera unos minutos."
            } else {
                mapAuthError(error)
            }
        }
    }

    actual suspend fun recuperarContrasena(correo: String): String? = try {
        conRetorno { ajustes ->
            if (ajustes != null) auth.sendPasswordResetEmail(correo, ajustes).await() else auth.sendPasswordResetEmail(correo).await()
        }
        null
    } catch (error: FirebaseAuthException) {
        when (error.errorCode) {
            // No se revela si el correo tiene cuenta.
            "ERROR_USER_NOT_FOUND" -> null
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
            else -> mapAuthError(error)
        }
    } catch (error: FirebaseException) {
        mapAuthError(error)
    } catch (_: IllegalArgumentException) {
        "El correo electrónico no es válido."
    }
}
