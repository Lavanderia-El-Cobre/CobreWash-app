package com.elcobre.lavanderiaelcobre.data

private const val NO_DISPONIBLE = "El inventario de Firebase aún no está disponible en iOS."

internal actual suspend fun catalogoInsumosFirebase(): List<InsumoInventario> =
    throw OperacionComandaException(NO_DISPONIBLE)

internal actual suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario? =
    throw OperacionComandaException(NO_DISPONIBLE)

internal actual suspend fun misConteosInventarioFirebase(): List<ConteoFisico> =
    throw OperacionComandaException(NO_DISPONIBLE)

internal actual suspend fun registrarConteoInventarioFirebase(insumoId: String, cantidad: Double, revisionStock: Int): Unit =
    throw OperacionComandaException(NO_DISPONIBLE)
