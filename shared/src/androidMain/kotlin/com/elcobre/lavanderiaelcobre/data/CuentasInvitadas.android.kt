package com.elcobre.lavanderiaelcobre.data

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import java.security.SecureRandom
import java.util.UUID
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*
 * Altas de cuentas hechas por el personal, igual que `lib/firebase/verificacion.ts` y
 * `createManagedUser` de la web: la cuenta se crea en una instancia secundaria de Firebase
 * (no cierra la sesión del personal) con una contraseña aleatoria que nadie conoce, y la
 * persona la activa con el enlace de restablecimiento que recibe por correo.
 */

private const val ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
private const val LARGO_CONTRASENA = 24

/** Códigos con los que Firebase rechaza el enlace de retorno (dominio aún no autorizado). */
private val CODIGOS_RETORNO = setOf(
    "ERROR_UNAUTHORIZED_CONTINUE_URI", "ERROR_INVALID_CONTINUE_URI", "ERROR_MISSING_CONTINUE_URI", "ERROR_UNAUTHORIZED_DOMAIN",
)

private val aleatorio = SecureRandom()

/** Contraseña aleatoria que nadie conoce: la cuenta se activa con el enlace del correo. */
internal fun contrasenaTemporal(): String =
    (1..LARGO_CONTRASENA).map { ALFABETO[aleatorio.nextInt(ALFABETO.length)] }.joinToString("") + "aA9!"

/** Resultado del alta en Firebase Auth: el usuario nuevo o `null` si el correo ya tenía cuenta. */
internal class AltaAuth(val auth: FirebaseAuth, val usuario: FirebaseUser?)

/**
 * Crea la cuenta de [email] en [auth]. Devuelve `null` si el correo ya tenía cuenta (no
 * es un error: quien llama decide si solo crea la ficha o informa el choque).
 */
@Suppress("SwallowedException") // El choque de correo es un resultado esperado, no una falla.
private suspend fun crearCuenta(auth: FirebaseAuth, email: String): FirebaseUser? = try {
    auth.createUserWithEmailAndPassword(email, contrasenaTemporal()).await().user
        ?: throw OperacionComandaException("No se pudo crear la cuenta.")
} catch (_: FirebaseAuthUserCollisionException) {
    null
} catch (error: FirebaseAuthException) {
    throw OperacionComandaException(mensajeAlta(error), error)
} catch (error: FirebaseException) {
    throw OperacionComandaException("No se pudo conectar con Firebase. Verifica tu conexión.", error)
}

/**
 * Ejecuta [bloque] con una instancia secundaria de Firebase en la que ya se intentó crear
 * la cuenta de [email]. Si [bloque] no termina (error o cancelación), borra la cuenta
 * recién creada para que no quede huérfana. Al terminar siempre cierra la sesión
 * secundaria y elimina la instancia, así no queda ningún token de esa cuenta guardado.
 */
internal suspend fun <T> conCuentaNueva(email: String, bloque: suspend (AltaAuth) -> T): T {
    val principal = FirebaseApp.getInstance()
    val secundaria = FirebaseApp.initializeApp(principal.applicationContext, principal.options, "alta-${UUID.randomUUID()}")
    val auth = FirebaseAuth.getInstance(secundaria)
    var creado: FirebaseUser? = null
    var completado = false
    try {
        creado = crearCuenta(auth, email)
        val resultado = bloque(AltaAuth(auth, creado))
        completado = true
        return resultado
    } finally {
        val huerfano = creado.takeIf { !completado }
        if (huerfano != null) withContext(NonCancellable) { runCatching { huerfano.delete().await() } }
        runCatching { auth.signOut() }
        runCatching { secundaria.delete() }
    }
}

private fun mensajeAlta(error: FirebaseAuthException): String = when (error.errorCode) {
    "ERROR_INVALID_EMAIL" -> "El correo no es válido."
    "ERROR_OPERATION_NOT_ALLOWED" -> "El registro con correo no está habilitado en Firebase."
    "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Espera unos minutos y vuelve a intentarlo."
    else -> "No se pudo crear la cuenta en Firebase Authentication."
}

private fun ajustesRetorno(): ActionCodeSettings =
    ActionCodeSettings.newBuilder().setUrl("$ORIGEN_WEB/?login=1").setHandleCodeInApp(false).build()

/** Reenvío sin enlace de retorno; un choque con el límite de un envío por minuto significa que el primero salió. */
private suspend fun reenviarSinRetorno(auth: FirebaseAuth, email: String): Boolean = try {
    auth.sendPasswordResetEmail(email).await()
    true
} catch (error: FirebaseAuthException) {
    error.errorCode == "ERROR_TOO_MANY_REQUESTS"
} catch (_: FirebaseException) {
    false
}

/**
 * Invitación para cuentas creadas por el personal: un enlace para definir la contraseña
 * que vuelve al inicio de sesión de la web. Si el dominio no está autorizado como
 * enlace de retorno, reintenta sin él. Devuelve `true` si el correo salió.
 */
internal suspend fun enviarInvitacion(auth: FirebaseAuth, email: String): Boolean = try {
    auth.sendPasswordResetEmail(email, ajustesRetorno()).await()
    true
} catch (error: FirebaseAuthException) {
    error.errorCode in CODIGOS_RETORNO && reenviarSinRetorno(auth, email)
} catch (_: FirebaseException) {
    false
}
