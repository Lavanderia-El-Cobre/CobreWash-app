package com.elcobre.lavanderiaelcobre.data

// Stubs de inventario en iOS: el SDK de Firebase Data Connect aún solo está habilitado en Android.
// Cada operación falla con OperacionComandaException para notificar al usuario sin simular datos.
private const val NO_DISPONIBLE = "El inventario de Firebase aún no está disponible en iOS."

/** Implementación provisional en iOS; rechaza la consulta del catálogo de insumos. */
internal actual suspend fun catalogoInsumosFirebase(): List<InsumoInventario> =
    throw OperacionComandaException(NO_DISPONIBLE)

/** Implementación provisional en iOS; rechaza la búsqueda de insumos por código QR. */
internal actual suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario? =
    throw OperacionComandaException(NO_DISPONIBLE)

/** Implementación provisional en iOS; rechaza la consulta de conteos físicos propios. */
internal actual suspend fun misConteosInventarioFirebase(): List<ConteoFisico> =
    throw OperacionComandaException(NO_DISPONIBLE)

/** Implementación provisional en iOS; rechaza el registro remoto de conteos de inventario. */
internal actual suspend fun registrarConteoInventarioFirebase(insumoId: String, cantidad: Double, revisionStock: Int): Unit =
    throw OperacionComandaException(NO_DISPONIBLE)
