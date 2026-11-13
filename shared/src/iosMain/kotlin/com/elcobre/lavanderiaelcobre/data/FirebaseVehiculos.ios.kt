package com.elcobre.lavanderiaelcobre.data

private const val MENSAJE_IOS = "La gestión de vehículos de Firebase aún no está disponible en iOS."

internal actual fun descartarReintentosFlotaFirebase() = Unit

internal actual suspend fun consultarFlotaFirebase(): EstadoFlotaRemota =
    throw OperacionComandaException(MENSAJE_IOS)

internal actual suspend fun registrarVehiculoFirebase(input: NuevoVehiculoInput): String =
    throw OperacionComandaException(MENSAJE_IOS)

internal actual suspend fun editarVehiculoFirebase(input: EditarVehiculoInput): Unit =
    throw OperacionComandaException(MENSAJE_IOS)

internal actual suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String =
    throw OperacionComandaException(MENSAJE_IOS)

internal actual suspend fun registrarRetornoFirebase(input: RetornoFlotaInput): Unit =
    throw OperacionComandaException(MENSAJE_IOS)

internal actual suspend fun programarSalidaFirebase(vehiculoId: String, repartidorId: String, observaciones: String?): Unit =
    throw OperacionComandaException(MENSAJE_IOS)
