package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.dataconnect.ComandaDetalleKey
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaEtapaKey
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaHistorialEstadoKey
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaKey
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.google.firebase.Timestamp
import com.google.firebase.dataconnect.ConnectorConfig
import com.google.firebase.dataconnect.FirebaseDataConnect
import com.google.firebase.dataconnect.getInstance
import com.google.firebase.dataconnect.serializers.TimestampSerializer
import com.google.firebase.dataconnect.serializers.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/*
 * Conector `produccion` de la web (dataconnect/produccion/mutations.gql). El generador
 * de SDK aún no admite sus entradas de lote con @allow, así que la web lo llama con un
 * `mutationRef` tipado a mano (lib/produccion/guardar.ts); aquí se hace lo mismo con
 * clases serializables propias que respetan exactamente los campos permitidos.
 */

private val ConectorProduccion: FirebaseDataConnect by lazy {
    val base = ExampleConnector.config
    FirebaseDataConnect.getInstance(ConnectorConfig(connector = "produccion", location = base.location, serviceId = base.serviceId))
}

/** Línea ya resuelta a ids de catálogo; los campos coinciden con el @allow de `CrearComandaConFlujo`. */
@Serializable
internal data class DetalleResuelto(
    @Serializable(with = UUIDSerializer::class) val tipoPrendaId: UUID,
    @Serializable(with = UUIDSerializer::class) val tipoServicioId: UUID,
    val cantidad: Int,
    val detalle: String?,
    val precioUnitario: Double,
    val subtotal: Double,
) {
    fun conComanda(comandaId: UUID) = DetalleEdicion(comandaId, tipoPrendaId, tipoServicioId, cantidad, detalle, precioUnitario, subtotal)
}

/** Igual que [DetalleResuelto] más `comandaId`, que `EditarComandaConDetalles` exige en cada línea. */
@Serializable
internal data class DetalleEdicion(
    @Serializable(with = UUIDSerializer::class) val comandaId: UUID,
    @Serializable(with = UUIDSerializer::class) val tipoPrendaId: UUID,
    @Serializable(with = UUIDSerializer::class) val tipoServicioId: UUID,
    val cantidad: Int,
    val detalle: String?,
    val precioUnitario: Double,
    val subtotal: Double,
)

@Serializable
internal data class CrearComandaConFlujoVars(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val numeroComanda: String,
    @Serializable(with = UUIDSerializer::class) val clienteId: UUID,
    val empresa: String?,
    val proyecto: String?,
    val observaciones: String?,
    val detalles: List<DetalleResuelto>,
)

@Serializable
@Suppress("ConstructorParameterNaming")
internal data class CrearComandaConFlujoData(
    val comanda_insert: ComandaKey,
    val comandaEtapa_insertMany: List<ComandaEtapaKey>,
    val comanda_update: ComandaKey?,
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
)

@Serializable
internal data class EditarComandaConDetallesVars(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @Serializable(with = TimestampSerializer::class) val version: Timestamp,
    val empresa: String?,
    val proyecto: String?,
    val observaciones: String?,
    val detalles: List<DetalleEdicion>,
)

@Serializable
@Suppress("ConstructorParameterNaming")
internal data class EditarComandaConDetallesData(
    val bloqueo: ComandaKey?,
    val comandaDetalle_deleteMany: Int,
    val comandaDetalle_insertMany: List<ComandaDetalleKey>,
    val comanda_update: ComandaKey?,
)

/** Inserta cabecera, prendas, las cinco etapas y el historial en una sola transacción. */
internal suspend fun crearComandaConFlujo(variables: CrearComandaConFlujoVars): String = solicitud {
    ConectorProduccion.mutation(
        "CrearComandaConFlujo",
        variables,
        serializer<CrearComandaConFlujoData>(),
        serializer<CrearComandaConFlujoVars>(),
    ).execute().data.comanda_insert.id.toString()
}

/** Reemplazo atómico de prendas; la versión impide pisar cambios que el formulario no leyó. */
internal suspend fun editarComandaConDetalles(variables: EditarComandaConDetallesVars) {
    solicitud {
        ConectorProduccion.mutation(
            "EditarComandaConDetalles",
            variables,
            serializer<EditarComandaConDetallesData>(),
            serializer<EditarComandaConDetallesVars>(),
        ).execute()
    }
}
