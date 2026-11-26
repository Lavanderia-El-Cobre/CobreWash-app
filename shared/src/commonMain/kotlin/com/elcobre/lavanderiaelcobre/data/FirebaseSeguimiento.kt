package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido

/**
 * Busca una comanda por su código QR público en Firebase Data Connect.
 * Devuelve el [Pedido] si existe y la cuenta tiene permisos, o `null` si no se encuentra.
 */
expect suspend fun buscarComandaPorQrFirebase(codigoQr: String): Pedido?

/** Obtiene la lista de comandas activas desde Firebase Data Connect (puente por plataforma). */
expect suspend fun getComandasActivasFirebase(): List<Pedido>

/** Obtiene las comandas asignadas actualmente al operario autenticado. */
expect suspend fun getMisComandasAsignadasFirebase(): List<Pedido>

/** Asigna al operario autenticado la etapa activa de la comanda [comandaId]. */
expect suspend fun autoAsignarComandaFirebase(comandaId: String)

/**
 * Marca como completada la etapa [etapaId] (con [orden]) en Firebase y actualiza el estado
 * de la comanda según [estadoComandaActual]. Devuelve `true` si el servidor aplicó el cambio.
 */
expect suspend fun completarEtapaFirebase(comandaId: String, etapaId: String, orden: Int, estadoComandaActual: String): Boolean

/** Obtiene el detalle completo de un pedido desde Firebase dado su ID interno. */
expect suspend fun getComandaDetalleFirebase(id: String): Pedido?

/** Registra un comentario operativo en el historial de la comanda [id]. */
expect suspend fun comentarComandaFirebase(id: String, texto: String)

/** Registra una incidencia de producción en la comanda [id] con [motivo] y detalle. */
expect suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String)

/** Marca como resuelta la alerta o incidencia activa asociada a la comanda [id]. */
expect suspend fun resolverComandaFirebase(id: String)

/** Busca una comanda por su número visible (por ejemplo `ELCOBRE-A1B2`), o `null` si no existe. */
expect suspend fun buscarNumeroComandaFirebase(numero: String): Pedido?
