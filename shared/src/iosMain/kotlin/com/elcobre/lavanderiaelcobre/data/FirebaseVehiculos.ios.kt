package com.elcobre.lavanderiaelcobre.data

// Stubs de flota y vehículos en iOS: el SDK de Firebase Data Connect aún no está integrado en iOS,
// por lo que las operaciones remotas fallan explícitamente con OperacionComandaException.
private const val MENSAJE_IOS = "La gestión de vehículos de Firebase aún no está disponible en iOS."

/** Sin efecto en iOS porque no existe una cola local de reintentos de Firebase que descartar. */
internal actual fun descartarReintentosFlotaFirebase() = Unit

/** Stub de iOS: rechaza la consulta del estado remoto de la flota. */
internal actual suspend fun consultarFlotaFirebase(): EstadoFlotaRemota =
    throw OperacionComandaException(MENSAJE_IOS)

/** Stub de iOS: rechaza el alta remota de vehículos. */
internal actual suspend fun registrarVehiculoFirebase(input: NuevoVehiculoInput): String =
    throw OperacionComandaException(MENSAJE_IOS)

/** Stub de iOS: rechaza la edición remota de vehículos. */
internal actual suspend fun editarVehiculoFirebase(input: EditarVehiculoInput): Unit =
    throw OperacionComandaException(MENSAJE_IOS)

/** Stub de iOS: rechaza el registro de salidas de vehículos. */
internal actual suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String =
    throw OperacionComandaException(MENSAJE_IOS)

/** Stub de iOS: rechaza el registro de retornos de vehículos. */
internal actual suspend fun registrarRetornoFirebase(input: RetornoFlotaInput): Unit =
    throw OperacionComandaException(MENSAJE_IOS)

/** Stub de iOS: rechaza la programación remota de salidas de flota. */
internal actual suspend fun programarSalidaFirebase(vehiculoId: String, repartidorId: String, observaciones: String?): Unit =
    throw OperacionComandaException(MENSAJE_IOS)
