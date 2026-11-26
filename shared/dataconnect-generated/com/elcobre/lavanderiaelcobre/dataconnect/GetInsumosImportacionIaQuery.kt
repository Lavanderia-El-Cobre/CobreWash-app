
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


public interface GetInsumosImportacionIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetInsumosImportacionIaQuery.Data,
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
  
    val nombre: String,
  
    val unidadMedida: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetInsumosImportacionIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetInsumosImportacionIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetInsumosImportacionIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetInsumosImportacionIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetInsumosImportacionIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetInsumosImportacionIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetInsumosImportacionIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

