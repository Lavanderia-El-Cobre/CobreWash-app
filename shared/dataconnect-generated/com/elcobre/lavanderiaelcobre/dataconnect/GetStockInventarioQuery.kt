
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.elcobre.lavanderiaelcobre.dataconnect


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetStockInventarioQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetStockInventarioQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val insumos: List<InsumosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class InsumosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockActual: Double,
  
    val stockMinimo: Double,
  
    val activo: Boolean,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val revisionStock: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetStockInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetStockInventarioQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetStockInventarioQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetStockInventarioQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetStockInventarioQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetStockInventarioQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetStockInventarioQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

