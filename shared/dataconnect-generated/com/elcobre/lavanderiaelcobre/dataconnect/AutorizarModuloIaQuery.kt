
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


public interface AutorizarModuloIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      AutorizarModuloIaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val yo: Yo?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Yo(
  
    val id: String,
  
    val activo: Boolean,
  
    val rol: Rol,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "AutorizarModuloIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun AutorizarModuloIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    AutorizarModuloIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun AutorizarModuloIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    AutorizarModuloIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun AutorizarModuloIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<AutorizarModuloIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

