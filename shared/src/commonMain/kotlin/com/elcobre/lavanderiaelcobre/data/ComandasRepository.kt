package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion
import com.elcobre.lavanderiaelcobre.data.offline.CacheLectura
import kotlinx.serialization.builtins.ListSerializer

/** Excepción de operación de dominio con un mensaje legible listo para mostrar en pantalla. */
open class OperacionComandaException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Contrato operativo de planta para el rol operario: consulta de comandas asignadas,
 * búsqueda por número o QR, avance de etapas, comentarios y registro/resolución de incidencias.
 */
interface ComandasRepository {
    suspend fun asignadas(): List<Pedido>
    suspend fun detalle(id: String): Pedido?
    suspend fun buscarNumero(numero: String): Pedido?

    /** Busca la comanda por código QR, la autoasigna al operario actual y devuelve su detalle. */
    suspend fun escanear(codigo: String): Pedido?
    suspend fun avanzar(pedido: Pedido)

    /** Avanza la etapa y opcionalmente adjunta un comentario operativo (seguro sin conexión). */
    suspend fun avanzar(pedido: Pedido, comentario: String) {
        avanzar(pedido)
        if (comentario.isNotBlank()) comentar(pedido.id, comentario)
    }

    suspend fun comentar(id: String, texto: String)
    suspend fun alertar(id: String, motivo: String, texto: String)
    suspend fun resolver(pedido: Pedido)
    fun cerrarSesion()
}

/**
 * Implementación sobre Firebase con soporte parcial sin conexión:
 * - [asignadas] y [detalle] recurren a [CacheLectura] cuando no hay red.
 * - [avanzar] (con comentario opcional) y [alertar] pasan por [AccionesSinConexion] para encolarse sin perder la hora.
 * - [escanear], [comentar] independiente y [resolver] requieren conexión directa con el servidor.
 */
class FirebaseComandasRepository : ComandasRepository {
    // Sin conexión, el operario sigue viendo sus últimas comandas asignadas.
    override suspend fun asignadas() =
        CacheLectura.leer("comandas-operario", ListSerializer(Pedido.serializer())) { getMisComandasAsignadasFirebase() }

    override suspend fun detalle(id: String): Pedido? = try {
        getComandaDetalleFirebase(id)
    } catch (error: OperacionComandaException) {
        if (!CacheLectura.esErrorSinConexion(error)) throw error
        CacheLectura.guardado("comandas-operario", ListSerializer(Pedido.serializer()))
            ?.firstOrNull { it.id == id } ?: throw error
    }

    override suspend fun buscarNumero(numero: String) = buscarNumeroComandaFirebase(numero)
    override suspend fun escanear(codigo: String): Pedido? {
        val encontrado = buscarComandaPorQrFirebase(codigo) ?: return null
        autoAsignarComandaFirebase(encontrado.id)
        return detalle(encontrado.id)
            ?: throw OperacionComandaException("La asignación se guardó, pero no se pudo cargar la comanda. Reintenta.")
    }

    override suspend fun avanzar(pedido: Pedido) = avanzar(pedido, "")

    override suspend fun avanzar(pedido: Pedido, comentario: String) {
        val etapa = pedido.etapaIdDb ?: throw OperacionComandaException("La comanda no tiene una etapa activa.")
        val orden = pedido.ordenEtapaDb ?: throw OperacionComandaException("La comanda no tiene un flujo válido.")
        val nota = comentario.trim().ifEmpty { null }
        // Pasa por la cola: sin conexión queda guardada con su hora y comentario opcional.
        AccionesSinConexion.ejecutar(
            TipoAccion.COMPLETAR_ETAPA,
            entidad = pedido.id,
            datos = mapOf(
                "comandaId" to pedido.id,
                "etapaId" to etapa,
                "orden" to "$orden",
                "estadoComanda" to estadoTrasCompletar(orden),
                "comentario" to nota,
            ),
            descripcion = "Completar etapa $orden · ${pedido.comanda}",
        )
    }
    override fun cerrarSesion() = com.elcobre.lavanderiaelcobre.data.auth.AuthRepository().cerrarSesion()
    override suspend fun comentar(id: String, texto: String) = comentarComandaFirebase(id, texto)
    override suspend fun alertar(id: String, motivo: String, texto: String) = AccionesSinConexion.ejecutar(
        TipoAccion.REGISTRAR_INCIDENCIA,
        entidad = id,
        datos = mapOf("comandaId" to id, "motivo" to motivo, "descripcion" to texto.trim().ifEmpty { null }),
        descripcion = "Incidencia: $motivo",
    )
    override suspend fun resolver(pedido: Pedido) = resolverComandaFirebase(pedido.id)
}

/**
 * Estado global al que transita la comanda al completar la etapa de [orden]:
 * etapas 1..3 mantienen `EN_PROCESO`, la etapa 4 deja la comanda `FINALIZADA` y la 5 `ENTREGADA`.
 */
fun estadoTrasCompletar(orden: Int): String = when (orden) {
    in 1..3 -> "EN_PROCESO"
    4 -> "FINALIZADA"
    5 -> "ENTREGADA"
    else -> throw OperacionComandaException("Orden de etapa inválido.")
}
