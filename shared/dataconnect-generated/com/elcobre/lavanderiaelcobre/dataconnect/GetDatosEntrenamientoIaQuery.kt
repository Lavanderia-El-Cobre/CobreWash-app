
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


public interface GetDatosEntrenamientoIaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetDatosEntrenamientoIaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val configuracionIa: ConfiguracionIa?,
  
    val tasaConsumos: List<TasaConsumosItem>,
  
    val insumos: List<InsumosItem>,
  
    val conteoInventarios: List<ConteoInventariosItem>,
  
    val movimientoInventarios: List<MovimientoInventariosItem>,
  
    val comandaDetalles: List<ComandaDetallesItem>,
  
    val produccionSimuladas: List<ProduccionSimuladasItem>,
  
    val modelos: List<ModelosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConfiguracionIa(
  
    val margenSeguridad: Double,
  
    val lambda: Double,
  
    val incluirSimulado: Boolean,
  
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
  
    val tipoServicio: TipoServicio,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
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
      
        @kotlinx.serialization.Serializable
  public data class ConteoInventariosItem(
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidadContada: Double,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fechaConteo: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val origen: @kotlinx.serialization.Serializable(with = OrigenDato.EnumValueSerializer::class) EnumValue<OrigenDato>,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class MovimientoInventariosItem(
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val cantidad: Double,
  
    val origen: @kotlinx.serialization.Serializable(with = OrigenDato.EnumValueSerializer::class) EnumValue<OrigenDato>,
  
    val tipoMovimiento: @kotlinx.serialization.Serializable(with = TipoMovimiento.EnumValueSerializer::class) EnumValue<TipoMovimiento>,
  
    val clase: @kotlinx.serialization.Serializable(with = ClaseMovimiento.EnumValueSerializer::class) EnumValue<ClaseMovimiento>?,
  
    val motivo: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ComandaDetallesItem(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val pesoKg: Double?,
  
    val comanda: Comanda,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Comanda(
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ProduccionSimuladasItem(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val kg: Double,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ModelosItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetDatosEntrenamientoIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetDatosEntrenamientoIaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetDatosEntrenamientoIaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetDatosEntrenamientoIaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetDatosEntrenamientoIaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetDatosEntrenamientoIaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetDatosEntrenamientoIaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

