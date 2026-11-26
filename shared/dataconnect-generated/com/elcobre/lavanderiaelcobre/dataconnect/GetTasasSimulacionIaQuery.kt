
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


public interface GetTasasSimulacionIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetTasasSimulacionIaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val configuracionIa: ConfiguracionIa?,
  
    val tasaConsumos: List<TasaConsumosItem>,
  
    val insumos: List<InsumosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConfiguracionIa(
  
    val lambda: Double,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TasaConsumosItem(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tasaInicial: Double,
  
    val tasaActual: Double,
  
    val unidad: String,
  
    val tipoServicio: TipoServicio,
  
    val insumo: Insumo,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Insumo(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InsumosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetTasasSimulacionIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetTasasSimulacionIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetTasasSimulacionIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetTasasSimulacionIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetTasasSimulacionIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetTasasSimulacionIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetTasasSimulacionIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

