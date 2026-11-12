
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


public interface ContarLimitesContactoVencidosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      ContarLimitesContactoVencidosQuery.Data,
      ContarLimitesContactoVencidosQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val antesDe: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContactos: List<LimiteContactosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class LimiteContactosItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ContarLimitesContactoVencidos"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ContarLimitesContactoVencidosQuery.ref(
  
    antesDe: com.google.firebase.Timestamp,

  
  
): com.google.firebase.dataconnect.QueryRef<
    ContarLimitesContactoVencidosQuery.Data,
    ContarLimitesContactoVencidosQuery.Variables
  > =
  ref(
    
      ContarLimitesContactoVencidosQuery.Variables(
        antesDe=antesDe,
  
      )
    
  )

public suspend fun ContarLimitesContactoVencidosQuery.execute(

  
    
      antesDe: com.google.firebase.Timestamp,

  

  ): com.google.firebase.dataconnect.QueryResult<
    ContarLimitesContactoVencidosQuery.Data,
    ContarLimitesContactoVencidosQuery.Variables
  > =
  ref(
    
      antesDe=antesDe,
  
    
  ).execute()


  public fun ContarLimitesContactoVencidosQuery.flow(
    
      antesDe: com.google.firebase.Timestamp,

  
    
    ): kotlinx.coroutines.flow.Flow<ContarLimitesContactoVencidosQuery.Data> =
    ref(
        
          antesDe=antesDe,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

