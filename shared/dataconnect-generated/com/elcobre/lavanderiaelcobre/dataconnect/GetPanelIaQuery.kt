
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


public interface GetPanelIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetPanelIaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val configuracionIa: ConfiguracionIa?,
  
    val tasaConsumos: List<TasaConsumosItem>,
  
    val modeloVigente: List<ModeloVigenteItem>,
  
    val insumos: List<InsumosItem>,
  
    val tipoServicios: List<TipoServiciosItem>,
  
    val conteosReales: List<ConteosRealesItem>,
  
    val conteosSimulados: List<ConteosSimuladosItem>,
  
    val simulaciones: List<SimulacionesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConfiguracionIa(
  
    val margenSeguridad: Double,
  
    val lambda: Double,
  
    val incluirSimulado: Boolean,
  
    val actualizadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TasaConsumosItem(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tasaInicial: Double,
  
    val tasaActual: Double,
  
    val unidad: String,
  
    val fuente: String?,
  
    val actualizadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val tipoServicio: TipoServicio,
  
    val insumo: Insumo,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Insumo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ModeloVigenteItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val version: String,
  
    val fechaEntrenamiento: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val mae: Double?,
  
    val mape: Double?,
  
    val desviacionRelativa: Double?,
  
    val observaciones: Int,
  
    val lambda: Double?,
  
    val margenSeguridad: Double?,
  
    val incluyeSimulado: Boolean,
  
    val tasasJson: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InsumosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockActual: Double,
  
    val stockMinimo: Double,
  
    val revisionStock: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TipoServiciosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadCobro: @kotlinx.serialization.Serializable(with = UnidadCobro.EnumValueSerializer::class) EnumValue<UnidadCobro>,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ConteosRealesItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ConteosSimuladosItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class SimulacionesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val semanas: Int,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val parametrosJson: String,
  
    val tasasVerdaderasJson: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetPanelIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetPanelIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetPanelIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetPanelIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetPanelIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetPanelIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetPanelIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

