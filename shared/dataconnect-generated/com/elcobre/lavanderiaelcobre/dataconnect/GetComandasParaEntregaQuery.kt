
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


public interface GetComandasParaEntregaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetComandasParaEntregaQuery.Data,
      GetComandasParaEntregaQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var limit: Int?
        public var offset: Int?
        
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
            

          return object : Builder {
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandas: List<ComandasItem>,
  
    val misEntregas: List<MisEntregasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val empresa: String?,
  
    val proyecto: String?,
  
    val observaciones: String?,
  
    val fechaEntregaEstimada: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val cliente: Cliente,
  
    val prendas: List<PrendasItem>,
  
    val problemas: List<ProblemasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Cliente(
  
    val nombre: String,
  
    val tipoCliente: @kotlinx.serialization.Serializable(with = TipoCliente.EnumValueSerializer::class) EnumValue<TipoCliente>,
  
    val telefono: String?,
  
    val direccion: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class PrendasItem(
  
    val cantidad: Int,
  
    val tipoPrenda: TipoPrenda,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoPrenda(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ProblemasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val motivo: String,
  
    val descripcion: String?,
  
    val estado: @kotlinx.serialization.Serializable(with = IncidenciaEstado.EnumValueSerializer::class) EnumValue<IncidenciaEstado>,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val reportadaPor: ReportadaPor,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ReportadaPor(
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class MisEntregasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val fecha: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val motivo: String?,
  
    val comanda: Comanda,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Comanda(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val cliente: Cliente,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Cliente(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComandasParaEntrega"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetComandasParaEntregaQuery.ref(
  
    

  
    block_: GetComandasParaEntregaQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetComandasParaEntregaQuery.Data,
    GetComandasParaEntregaQuery.Variables
  > =
  ref(
    
      GetComandasParaEntregaQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetComandasParaEntregaQuery.execute(

  
    
      

  
    block_: GetComandasParaEntregaQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetComandasParaEntregaQuery.Data,
    GetComandasParaEntregaQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetComandasParaEntregaQuery.flow(
    
      

  
    block_: GetComandasParaEntregaQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetComandasParaEntregaQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

