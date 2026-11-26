package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.auth.VigilanciaCuenta
import com.google.firebase.dataconnect.DataConnectException
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlin.coroutines.cancellation.CancellationException

private fun mensajeTransporte(status: Status): String = when (status.code) {
    Status.Code.NOT_FOUND -> "El servicio de datos aún no está actualizado. Intenta nuevamente más tarde."
    Status.Code.UNAUTHENTICATED -> "No se pudo validar tu sesión. Vuelve a ingresar."
    Status.Code.PERMISSION_DENIED -> "Tu cuenta no tiene permiso para realizar esta operación."
    else -> "No se pudo conectar con Firebase. Verifica tu conexión e intenta nuevamente."
}

internal suspend fun <T> solicitud(block: suspend () -> T): T = try {
    block()
} catch (error: CancellationException) {
    throw error
} catch (error: StatusException) {
    throw OperacionComandaException(mensajeTransporte(error.status), error)
} catch (error: StatusRuntimeException) {
    throw OperacionComandaException(mensajeTransporte(error.status), error)
} catch (error: DataConnectException) {
    // Si el servidor rechaza porque la cuenta quedó inactiva o sin perfil, se cierra la sesión.
    VigilanciaCuenta.revisar(error.message)
    throw OperacionComandaException("No se pudo guardar o consultar los datos: ${error.message}", error)
} catch (error: IllegalArgumentException) {
    throw OperacionComandaException("El código o los datos son inválidos.", error)
} catch (error: IllegalStateException) {
    throw OperacionComandaException("No se pudo completar la operación.", error)
}
