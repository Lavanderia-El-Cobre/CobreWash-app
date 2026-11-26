package com.elcobre.lavanderiaelcobre.data.auth

import android.util.Log
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.DataConnectException
import com.google.firebase.dataconnect.DataConnectOperationException
import com.google.firebase.dataconnect.QueryRef
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlin.coroutines.cancellation.CancellationException

private const val PERFIL_TAG = "PerfilRepository"

/** Traduce errores de Data Connect al cargar el perfil en mensajes orientados al usuario. */
private fun mensajePerfil(error: DataConnectException): String {
    val detalle = error.message.orEmpty().lowercase()
    return when {
        "unauthenticated" in detalle || "unauthorized" in detalle ->
            "No se pudo validar tu sesión. Vuelve a ingresar."
        "operation" in detalle && "not found" in detalle ->
            "El servicio de perfiles aún no está disponible. Actualiza la app o intenta más tarde."
        else -> "No se pudo cargar tu perfil. Verifica tu conexión e intenta nuevamente."
    }
}

/** Implementación Android de [PerfilRepository] respaldada por la consulta `GetMiPerfil`. */
actual class PerfilRepository actual constructor() {
    /**
     * Consulta el perfil del usuario autenticado directo al servidor (`SERVER_ONLY`).
     * Marca `definitivo = true` cuando la cuenta no tiene perfil o está inactiva para
     * forzar el cierre de sesión, y propaga [CancellationException] sin interceptarla.
     */
    actual suspend fun obtenerMiPerfil(): PerfilResultado = try {
        val usuario = ExampleConnector.instance.getMiPerfil
            .ref()
            .execute(QueryRef.FetchPolicy.SERVER_ONLY)
            .data
            .usuario
        when {
            usuario == null -> PerfilResultado.Error("Tu cuenta no tiene un perfil asociado.", definitivo = true)
            !usuario.activo -> PerfilResultado.Error("Tu cuenta está inactiva.", definitivo = true)
            else -> PerfilResultado.Exito(nombre = usuario.nombre, rolNombre = usuario.rol.nombre)
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: StatusException) {
        Log.e(PERFIL_TAG, "No fue posible consultar GetMiPerfil: ${error::class.simpleName}")
        PerfilResultado.Error("No se pudo cargar tu perfil. Verifica tu conexión e intenta nuevamente.")
    } catch (error: StatusRuntimeException) {
        Log.e(PERFIL_TAG, "No fue posible consultar GetMiPerfil: ${error::class.simpleName}")
        PerfilResultado.Error("No se pudo cargar tu perfil. Verifica tu conexión e intenta nuevamente.")
    } catch (error: DataConnectOperationException) {
        Log.e(PERFIL_TAG, "GetMiPerfil fue rechazado por Data Connect: ${error::class.simpleName}")
        PerfilResultado.Error(mensajePerfil(error))
    } catch (error: DataConnectException) {
        Log.e(PERFIL_TAG, "No fue posible consultar GetMiPerfil: ${error::class.simpleName}")
        PerfilResultado.Error(mensajePerfil(error))
    } catch (error: IllegalStateException) {
        Log.e(PERFIL_TAG, "Firebase Data Connect no está inicializado correctamente: ${error::class.simpleName}")
        PerfilResultado.Error("No se pudo iniciar el servicio de perfiles. Reinicia la app e intenta nuevamente.")
    }
}
