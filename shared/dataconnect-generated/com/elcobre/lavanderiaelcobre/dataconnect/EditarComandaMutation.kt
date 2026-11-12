
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



public interface EditarComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EditarComandaMutation.Data,
      EditarComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val valorTotal: Double,
  
    val empresa: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val proyecto: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val observaciones: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var valorTotal: Double
        public var empresa: String?
        public var proyecto: String?
        public var observaciones: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,valorTotal: Double,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var valorTotal= valorTotal
            var empresa: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var proyecto: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var observaciones: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var valorTotal: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { valorTotal = value_ }
              
            override var empresa: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { empresa = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var proyecto: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { proyecto = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var observaciones: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observaciones = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,valorTotal=valorTotal,empresa=empresa,proyecto=proyecto,observaciones=observaciones,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda_update: ComandaKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EditarComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EditarComandaMutation.ref(
  
    id: java.util.UUID,valorTotal: Double,

  
    block_: EditarComandaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    EditarComandaMutation.Data,
    EditarComandaMutation.Variables
  > =
  ref(
    
      EditarComandaMutation.Variables.build(
        id=id,valorTotal=valorTotal,
  
    block_
      )
    
  )

public suspend fun EditarComandaMutation.execute(

  
    
      id: java.util.UUID,valorTotal: Double,

  
    block_: EditarComandaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    EditarComandaMutation.Data,
    EditarComandaMutation.Variables
  > =
  ref(
    
      id=id,valorTotal=valorTotal,
  
    block_
    
  ).execute()


