
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


public interface GetHistorialInventarioQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetHistorialInventarioQuery.Data,
      GetHistorialInventarioQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val insumoId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val tipo: com.google.firebase.dataconnect.OptionalVariable<TipoMovimiento?>,
  
    val fechaDesde: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val fechaHastaExclusiva: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var insumoId: java.util.UUID?
        public var tipo: TipoMovimiento?
        public var fechaDesde: com.google.firebase.Timestamp?
        public var fechaHastaExclusiva: com.google.firebase.Timestamp?
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var insumoId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var tipo: com.google.firebase.dataconnect.OptionalVariable<TipoMovimiento?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var fechaDesde: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var fechaHastaExclusiva: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var insumoId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { insumoId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var tipo: TipoMovimiento?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipo = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var fechaDesde: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fechaDesde = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var fechaHastaExclusiva: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fechaHastaExclusiva = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              insumoId=insumoId,tipo=tipo,fechaDesde=fechaDesde,fechaHastaExclusiva=fechaHastaExclusiva,limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movimientoInventarios: List<MovimientoInventariosItem>,
  
    val total: List<TotalItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class MovimientoInventariosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tipoMovimiento: @kotlinx.serialization.Serializable(with = TipoMovimiento.EnumValueSerializer::class) EnumValue<TipoMovimiento>,
  
    val cantidad: Double,
  
    val motivo: String?,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val insumo: Insumo,
  
    val usuario: Usuario?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Insumo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Usuario(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TotalItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetHistorialInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetHistorialInventarioQuery.ref(
  
    

  
    block_: GetHistorialInventarioQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetHistorialInventarioQuery.Data,
    GetHistorialInventarioQuery.Variables
  > =
  ref(
    
      GetHistorialInventarioQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetHistorialInventarioQuery.execute(

  
    
      

  
    block_: GetHistorialInventarioQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetHistorialInventarioQuery.Data,
    GetHistorialInventarioQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetHistorialInventarioQuery.flow(
    
      

  
    block_: GetHistorialInventarioQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetHistorialInventarioQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

