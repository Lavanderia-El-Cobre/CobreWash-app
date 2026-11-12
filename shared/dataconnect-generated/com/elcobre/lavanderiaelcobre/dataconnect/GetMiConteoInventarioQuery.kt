
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


public interface GetMiConteoInventarioQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetMiConteoInventarioQuery.Data,
      GetMiConteoInventarioQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventario: ConteoInventario?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConteoInventario(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidadContada: Double,
  
    val stockSistema: Double,
  
    val revisionStock: Int,
  
    val estado: @kotlinx.serialization.Serializable(with = EstadoConteoInventario.EnumValueSerializer::class) EnumValue<EstadoConteoInventario>,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMiConteoInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMiConteoInventarioQuery.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetMiConteoInventarioQuery.Data,
    GetMiConteoInventarioQuery.Variables
  > =
  ref(
    
      GetMiConteoInventarioQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetMiConteoInventarioQuery.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMiConteoInventarioQuery.Data,
    GetMiConteoInventarioQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetMiConteoInventarioQuery.flow(
    
      id: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetMiConteoInventarioQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

