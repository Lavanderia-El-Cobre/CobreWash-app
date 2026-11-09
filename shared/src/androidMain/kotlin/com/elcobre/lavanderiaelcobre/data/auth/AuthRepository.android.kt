package com.elcobre.lavanderiaelcobre.data.auth

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

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
        AuthResultado.Error(mapAuthError(error))
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
}
