
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


public interface GetRegistrosExistentesIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetRegistrosExistentesIaQuery.Data,
      GetRegistrosExistentesIaQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val desde: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val hasta: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventarios: List<ConteoInventariosItem>,
  
    val movimientoInventarios: List<MovimientoInventariosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConteoInventariosItem(
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fechaConteo: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class MovimientoInventariosItem(
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val cantidad: Double,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetRegistrosExistentesIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetRegistrosExistentesIaQuery.ref(
  
    desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetRegistrosExistentesIaQuery.Data,
    GetRegistrosExistentesIaQuery.Variables
  > =
  ref(
    
      GetRegistrosExistentesIaQuery.Variables(
        desde=desde,hasta=hasta,
  
      )
    
  )

public suspend fun GetRegistrosExistentesIaQuery.execute(

  
    
      desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetRegistrosExistentesIaQuery.Data,
    GetRegistrosExistentesIaQuery.Variables
  > =
  ref(
    
      desde=desde,hasta=hasta,
  
    
  ).execute()


  public fun GetRegistrosExistentesIaQuery.flow(
    
      desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  
    
    ): kotlinx.coroutines.flow.Flow<GetRegistrosExistentesIaQuery.Data> =
    ref(
        
          desde=desde,hasta=hasta,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

