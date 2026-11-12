
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


public interface GetComandasPaginadasQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetComandasPaginadasQuery.Data,
      GetComandasPaginadasQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val estados: com.google.firebase.dataconnect.OptionalVariable<List<ComandaEstado>?>,
  
    val cliente: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val fechaDesde: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val fechaHasta: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val fechaHastaExclusiva: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var limit: Int?
        public var offset: Int?
        public var estados: List<ComandaEstado>?
        public var cliente: String?
        public var fechaDesde: com.google.firebase.Timestamp?
        public var fechaHasta: com.google.firebase.Timestamp?
        public var fechaHastaExclusiva: com.google.firebase.Timestamp?
        
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
            var estados: com.google.firebase.dataconnect.OptionalVariable<List<ComandaEstado>?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var cliente: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var fechaDesde: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var fechaHasta: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var fechaHastaExclusiva: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var estados: List<ComandaEstado>?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estados = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var cliente: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { cliente = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var fechaDesde: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fechaDesde = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var fechaHasta: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fechaHasta = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var fechaHastaExclusiva: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fechaHastaExclusiva = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              limit=limit,offset=offset,estados=estados,cliente=cliente,fechaDesde=fechaDesde,fechaHasta=fechaHasta,fechaHastaExclusiva=fechaHastaExclusiva,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandas: List<ComandasItem>,
  
    val total: List<TotalItem>,
  
    val pendientes: List<PendientesItem>,
  
    val enProceso: List<EnProcesoItem>,
  
    val finalizadas: List<FinalizadasItem>,
  
    val entregadas: List<EntregadasItem>,
  
    val anuladas: List<AnuladasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val actualizadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = ComandaEstado.EnumValueSerializer::class) EnumValue<ComandaEstado>,
  
    val valorTotal: Double,
  
    val empresa: String?,
  
    val proyecto: String?,
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fechaEntregaEstimada: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val observaciones: String?,
  
    val motivoAnulacion: String?,
  
    val cliente: Cliente,
  
    val comandaEtapas_on_comanda: List<ComandaEtapasOnComandaItem>,
  
    val comandaDetalles_on_comanda: List<ComandaDetallesOnComandaItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Cliente(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val telefono: String?,
  
    val email: String?,
  
    val tipoCliente: @kotlinx.serialization.Serializable(with = TipoCliente.EnumValueSerializer::class) EnumValue<TipoCliente>,
  
    val direccion: String?,
  
  ) {
    
    
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
  public data class ComandaDetallesOnComandaItem(
  
    val cantidad: Int,
  
    val detalle: String?,
  
    val precioUnitario: Double,
  
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
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TotalItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class PendientesItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class EnProcesoItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class FinalizadasItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class EntregadasItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class AnuladasItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComandasPaginadas"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetComandasPaginadasQuery.ref(
  
    

  
    block_: GetComandasPaginadasQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetComandasPaginadasQuery.Data,
    GetComandasPaginadasQuery.Variables
  > =
  ref(
    
      GetComandasPaginadasQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetComandasPaginadasQuery.execute(

  
    
      

  
    block_: GetComandasPaginadasQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetComandasPaginadasQuery.Data,
    GetComandasPaginadasQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetComandasPaginadasQuery.flow(
    
      

  
    block_: GetComandasPaginadasQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetComandasPaginadasQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

