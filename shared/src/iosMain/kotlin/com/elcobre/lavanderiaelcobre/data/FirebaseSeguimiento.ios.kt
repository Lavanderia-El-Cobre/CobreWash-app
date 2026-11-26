package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido

// Implementaciones provisionales de seguimiento en iOS mientras Firebase Data Connect siga siendo
// exclusivo de Android: las consultas pasivas devuelven resultados vacíos para no bloquear la UI,
// y las mutaciones o búsquedas explícitas fallan con OperacionComandaException.

/** Stub de iOS: devuelve `null` al no contar con conexión a Firebase en esta plataforma. */
actual suspend fun buscarComandaPorQrFirebase(codigoQr: String): Pedido? {
    return null
}

/** Stub de iOS: devuelve una lista vacía para permitir renderizar la bandeja sin fallos. */
actual suspend fun getComandasActivasFirebase(): List<Pedido> {
    return emptyList()
}

/** Stub de iOS: devuelve `null` al no disponer de detalle remoto desde Firebase. */
actual suspend fun getComandaDetalleFirebase(id: String): Pedido? {
    return null
}

/** Stub de iOS: devuelve una lista vacía mientras no exista integración de seguimiento en iOS. */
actual suspend fun getMisComandasAsignadasFirebase(): List<Pedido> = emptyList()

/** Stub de iOS: rechaza la autoasignación porque requiere escritura en Firebase Data Connect. */
actual suspend fun autoAsignarComandaFirebase(comandaId: String): Unit =
    throw OperacionComandaException("El seguimiento de Firebase todavía no está disponible en iOS.")

/** Stub de iOS: devuelve `false` indicando que la etapa no se pudo completar en el servidor. */
actual suspend fun completarEtapaFirebase(comandaId: String, etapaId: String, orden: Int, estadoComandaActual: String): Boolean = false

/** Stub de iOS: rechaza el envío de comentarios al no estar integrado Firebase. */
actual suspend fun comentarComandaFirebase(id: String, texto: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")

/** Stub de iOS: rechaza el registro de alertas al no estar integrado Firebase. */
actual suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")

/** Stub de iOS: rechaza la resolución de alertas al no estar integrado Firebase. */
actual suspend fun resolverComandaFirebase(id: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")

/** Stub de iOS: rechaza la búsqueda por número de comanda al no estar integrado Firebase. */
actual suspend fun buscarNumeroComandaFirebase(numero: String): Pedido? =
    throw OperacionComandaException("Firebase no está disponible en iOS.")

