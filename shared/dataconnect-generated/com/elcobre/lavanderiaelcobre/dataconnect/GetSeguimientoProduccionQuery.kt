
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


public interface GetSeguimientoProduccionQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetSeguimientoProduccionQuery.Data,
      GetSeguimientoProduccionQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val buscar: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var limit: Int?
        public var offset: Int?
        public var buscar: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var buscar: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var buscar: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { buscar = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              limit=limit,offset=offset,buscar=buscar,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandas: List<ComandasItem>,
  
    val total: List<TotalItem>,
  
    val operarios: List<OperariosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val estado: @kotlinx.serialization.Serializable(with = ComandaEstado.EnumValueSerializer::class) EnumValue<ComandaEstado>,
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val cliente: Cliente,
  
    val comandaDetalles_on_comanda: List<ComandaDetallesOnComandaItem>,
  
    val comandaEtapas_on_comanda: List<ComandaEtapasOnComandaItem>,
  
    val reasignacionOperarios_on_comanda: List<ReasignacionOperariosOnComandaItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Cliente(
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ComandaDetallesOnComandaItem(
  
    val cantidad: Int,
  
    val tipoPrenda: TipoPrenda,
  
    val tipoServicio: TipoServicio,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoPrenda(
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ComandaEtapasOnComandaItem(
  
    val etapaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombreEtapa: String?,
  
    val ordenEtapa: Int?,
  
    val descripcionEtapa: String?,
  
    val tiempoEstimadoMin: Int?,
  
    val estado: @kotlinx.serialization.Serializable(with = EtapaEstado.EnumValueSerializer::class) EnumValue<EtapaEstado>,
  
    val fechaInicio: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaCompletado: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val operario: Operario?,
  
    val asignadoA: AsignadoA?,
  
    val etapa: Etapa,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Operario(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class AsignadoA(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Etapa(
  
    val nombre: String,
  
    val orden: Int,
  
    val descripcion: String?,
  
    val tiempoEstimadoMin: Int?,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ReasignacionOperariosOnComandaItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val motivo: String?,
  
    val etapa: Etapa,
  
    val operarioAnterior: OperarioAnterior?,
  
    val operarioNuevo: OperarioNuevo,
  
    val realizadoPor: RealizadoPor,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Etapa(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val orden: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class OperarioAnterior(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class OperarioNuevo(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class RealizadoPor(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TotalItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class OperariosItem(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetSeguimientoProduccion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetSeguimientoProduccionQuery.ref(
  
    

  
    block_: GetSeguimientoProduccionQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetSeguimientoProduccionQuery.Data,
    GetSeguimientoProduccionQuery.Variables
  > =
  ref(
    
      GetSeguimientoProduccionQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetSeguimientoProduccionQuery.execute(

  
    
      

  
    block_: GetSeguimientoProduccionQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetSeguimientoProduccionQuery.Data,
    GetSeguimientoProduccionQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetSeguimientoProduccionQuery.flow(
    
      

  
    block_: GetSeguimientoProduccionQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetSeguimientoProduccionQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

