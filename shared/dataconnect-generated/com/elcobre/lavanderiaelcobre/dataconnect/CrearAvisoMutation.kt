
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



public interface CrearAvisoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearAvisoMutation.Data,
      CrearAvisoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val titulo: String,
  
    val contenido: String,
  
    val rolDestinatarioId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var titulo: String
        public var contenido: String
        public var rolDestinatarioId: java.util.UUID?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          titulo: String,contenido: String,
          block_: Builder.() -> Unit
        ): Variables {
          var titulo= titulo
            var contenido= contenido
            var rolDestinatarioId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var titulo: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { titulo = value_ }
              
            override var contenido: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { contenido = value_ }
              
            override var rolDestinatarioId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rolDestinatarioId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              titulo=titulo,contenido=contenido,rolDestinatarioId=rolDestinatarioId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val aviso_insert: AvisoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearAviso"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearAvisoMutation.ref(
  
    titulo: String,contenido: String,

  
    block_: CrearAvisoMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CrearAvisoMutation.Data,
    CrearAvisoMutation.Variables
  > =
  ref(
    
      CrearAvisoMutation.Variables.build(
        titulo=titulo,contenido=contenido,
  
    block_
      )
    
  )

public suspend fun CrearAvisoMutation.execute(

  
    
      titulo: String,contenido: String,

  
    block_: CrearAvisoMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CrearAvisoMutation.Data,
    CrearAvisoMutation.Variables
  > =
  ref(
    
      titulo=titulo,contenido=contenido,
  
    block_
    
  ).execute()


