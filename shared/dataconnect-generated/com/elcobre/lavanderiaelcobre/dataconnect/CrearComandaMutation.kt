
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



public interface CrearComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearComandaMutation.Data,
      CrearComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val numeroComanda: String,
  
    val clienteId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val empresa: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val proyecto: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val valorTotal: Double,
  
    val observaciones: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var numeroComanda: String
        public var clienteId: java.util.UUID
        public var empresa: String?
        public var proyecto: String?
        public var valorTotal: Double
        public var observaciones: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          numeroComanda: String,clienteId: java.util.UUID,valorTotal: Double,
          block_: Builder.() -> Unit
        ): Variables {
          var numeroComanda= numeroComanda
            var clienteId= clienteId
            var empresa: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var proyecto: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var valorTotal= valorTotal
            var observaciones: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var numeroComanda: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { numeroComanda = value_ }
              
            override var clienteId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clienteId = value_ }
              
            override var empresa: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { empresa = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var proyecto: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { proyecto = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var valorTotal: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { valorTotal = value_ }
              
            override var observaciones: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observaciones = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              numeroComanda=numeroComanda,clienteId=clienteId,empresa=empresa,proyecto=proyecto,valorTotal=valorTotal,observaciones=observaciones,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda_insert: ComandaKey,
  
    val comandaEtapa_insertMany: List<ComandaEtapaKey>,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearComandaMutation.ref(
  
    numeroComanda: String,clienteId: java.util.UUID,valorTotal: Double,

  
    block_: CrearComandaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CrearComandaMutation.Data,
    CrearComandaMutation.Variables
  > =
  ref(
    
      CrearComandaMutation.Variables.build(
        numeroComanda=numeroComanda,clienteId=clienteId,valorTotal=valorTotal,
  
    block_
      )
    
  )

public suspend fun CrearComandaMutation.execute(

  
    
      numeroComanda: String,clienteId: java.util.UUID,valorTotal: Double,

  
    block_: CrearComandaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CrearComandaMutation.Data,
    CrearComandaMutation.Variables
  > =
  ref(
    
      numeroComanda=numeroComanda,clienteId=clienteId,valorTotal=valorTotal,
  
    block_
    
  ).execute()


