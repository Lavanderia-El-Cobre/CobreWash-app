package com.elcobre.lavanderiaelcobre.data.auth

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

actual suspend fun restaurarPerfilAutenticado(): PerfilResultado? {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: return null
    return try {
        user.getIdToken(true).await()
        when {
            auth.currentUser?.uid != user.uid -> null
            AuthRepository().correoPorVerificar() ->
                PerfilResultado.Error("Verifica tu correo e inicia sesión nuevamente.", definitivo = true)
            else -> PerfilRepository().obtenerMiPerfil()
        }
    } catch (_: FirebaseAuthInvalidUserException) {
        // La cuenta fue deshabilitada o eliminada en Firebase Authentication.
        PerfilResultado.Error("Tu cuenta fue deshabilitada. Contacta a administración.", definitivo = true)
    } catch (_: FirebaseException) {
        PerfilResultado.Error("No se pudo validar tu sesión. Conéctate e inicia sesión nuevamente.")
    }
}
