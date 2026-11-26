
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



public interface EntregarComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EntregarComandaMutation.Data,
      EntregarComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val detalle: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var detalle: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var detalle: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var detalle: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { detalle = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,detalle=detalle,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda_update: ComandaKey?,
  
    val entrega: Int,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
    val problemasResueltos: Int,
  
    val comandaNotificacion_insert: ComandaNotificacionKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EntregarComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EntregarComandaMutation.ref(
  
    id: java.util.UUID,

  
    block_: EntregarComandaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    EntregarComandaMutation.Data,
    EntregarComandaMutation.Variables
  > =
  ref(
    
      EntregarComandaMutation.Variables.build(
        id=id,
  
    block_
      )
    
  )

public suspend fun EntregarComandaMutation.execute(

  
    
      id: java.util.UUID,

  
    block_: EntregarComandaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    EntregarComandaMutation.Data,
    EntregarComandaMutation.Variables
  > =
  ref(
    
      id=id,
  
    block_
    
  ).execute()


