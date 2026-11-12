
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


public interface GetAvisosParaEquipoQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetAvisosParaEquipoQuery.Data,
      GetAvisosParaEquipoQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val rol: String,
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var rol: String
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          rol: String,
          block_: Builder.() -> Unit
        ): Variables {
          var rol= rol
            var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var rol: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rol = value_ }
              
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              rol=rol,limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val avisos: List<AvisosItem>,
  
    val total: List<TotalItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class AvisosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val titulo: String,
  
    val contenido: String,
  
    val fechaPublicacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val autor: Autor,
  
    val rolDestinatario: RolDestinatario?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Autor(
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class RolDestinatario(
  
    val nombre: String,
  
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
    public val operationName: String = "GetAvisosParaEquipo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAvisosParaEquipoQuery.ref(
  
    rol: String,

  
    block_: GetAvisosParaEquipoQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetAvisosParaEquipoQuery.Data,
    GetAvisosParaEquipoQuery.Variables
  > =
  ref(
    
      GetAvisosParaEquipoQuery.Variables.build(
        rol=rol,
  
    block_
      )
    
  )

public suspend fun GetAvisosParaEquipoQuery.execute(

  
    
      rol: String,

  
    block_: GetAvisosParaEquipoQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetAvisosParaEquipoQuery.Data,
    GetAvisosParaEquipoQuery.Variables
  > =
  ref(
    
      rol=rol,
  
    block_
    
  ).execute()


  public fun GetAvisosParaEquipoQuery.flow(
    
      rol: String,

  
    block_: GetAvisosParaEquipoQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetAvisosParaEquipoQuery.Data> =
    ref(
        
          rol=rol,
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

