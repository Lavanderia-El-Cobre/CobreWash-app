
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


public interface AutorizarGestionPrivacidadQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      Unit,
      Unit
    >
{
  

  

  public companion object {
    public val operationName: String = "AutorizarGestionPrivacidad"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Unit> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun AutorizarGestionPrivacidadQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    Unit,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun AutorizarGestionPrivacidadQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    Unit,
    Unit
  > =
  ref(
    
  ).execute()


  public fun AutorizarGestionPrivacidadQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<Unit> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

