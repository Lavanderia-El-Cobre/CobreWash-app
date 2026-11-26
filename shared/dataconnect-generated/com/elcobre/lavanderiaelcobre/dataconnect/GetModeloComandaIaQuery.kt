
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


public interface GetModeloComandaIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetModeloComandaIaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val configuracionIa: ConfiguracionIa?,
  
    val tasaConsumos: List<TasaConsumosItem>,
  
    val insumos: List<InsumosItem>,
  
    val modeloVigente: List<ModeloVigenteItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConfiguracionIa(
  
    val margenSeguridad: Double,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TasaConsumosItem(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tasaInicial: Double,
  
    val tasaActual: Double,
  
    val unidad: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InsumosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockActual: Double,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ModeloVigenteItem(
  
    val desviacionRelativa: Double?,
  
    val tasasJson: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetModeloComandaIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetModeloComandaIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetModeloComandaIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetModeloComandaIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetModeloComandaIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetModeloComandaIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetModeloComandaIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

