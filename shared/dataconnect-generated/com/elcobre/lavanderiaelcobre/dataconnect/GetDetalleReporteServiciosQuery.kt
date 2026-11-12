
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


public interface GetDetalleReporteServiciosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetDetalleReporteServiciosQuery.Data,
      GetDetalleReporteServiciosQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val desde: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val hasta: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val clienteId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val empresa: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val servicioId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var desde: com.google.firebase.Timestamp
        public var hasta: com.google.firebase.Timestamp
        public var clienteId: java.util.UUID?
        public var empresa: String?
        public var servicioId: java.util.UUID?
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,
          block_: Builder.() -> Unit
        ): Variables {
          var desde= desde
            var hasta= hasta
            var clienteId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var empresa: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var servicioId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var desde: com.google.firebase.Timestamp
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { desde = value_ }
              
            override var hasta: com.google.firebase.Timestamp
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { hasta = value_ }
              
            override var clienteId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clienteId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var empresa: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { empresa = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var servicioId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { servicioId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              desde=desde,hasta=hasta,clienteId=clienteId,empresa=empresa,servicioId=servicioId,limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val detalles: List<DetallesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class DetallesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidad: Int,
  
    val subtotal: Double,
  
    val pesoKg: Double?,
  
    val tipoServicio: TipoServicio,
  
    val tipoPrenda: TipoPrenda,
  
    val comanda: Comanda,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TipoPrenda(
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Comanda(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val estado: @kotlinx.serialization.Serializable(with = ComandaEstado.EnumValueSerializer::class) EnumValue<ComandaEstado>,
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val empresa: String?,
  
    val cliente: Cliente,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Cliente(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetDetalleReporteServicios"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetDetalleReporteServiciosQuery.ref(
  
    desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  
    block_: GetDetalleReporteServiciosQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetDetalleReporteServiciosQuery.Data,
    GetDetalleReporteServiciosQuery.Variables
  > =
  ref(
    
      GetDetalleReporteServiciosQuery.Variables.build(
        desde=desde,hasta=hasta,
  
    block_
      )
    
  )

public suspend fun GetDetalleReporteServiciosQuery.execute(

  
    
      desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  
    block_: GetDetalleReporteServiciosQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetDetalleReporteServiciosQuery.Data,
    GetDetalleReporteServiciosQuery.Variables
  > =
  ref(
    
      desde=desde,hasta=hasta,
  
    block_
    
  ).execute()


  public fun GetDetalleReporteServiciosQuery.flow(
    
      desde: com.google.firebase.Timestamp,hasta: com.google.firebase.Timestamp,

  
    block_: GetDetalleReporteServiciosQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetDetalleReporteServiciosQuery.Data> =
    ref(
        
          desde=desde,hasta=hasta,
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

