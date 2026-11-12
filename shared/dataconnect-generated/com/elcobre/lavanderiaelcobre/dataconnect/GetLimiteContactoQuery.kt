
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


public interface GetLimiteContactoQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetLimiteContactoQuery.Data,
      GetLimiteContactoQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContacto: LimiteContacto?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class LimiteContacto(
  
    val id: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetLimiteContacto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetLimiteContactoQuery.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetLimiteContactoQuery.Data,
    GetLimiteContactoQuery.Variables
  > =
  ref(
    
      GetLimiteContactoQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetLimiteContactoQuery.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetLimiteContactoQuery.Data,
    GetLimiteContactoQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetLimiteContactoQuery.flow(
    
      id: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetLimiteContactoQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

