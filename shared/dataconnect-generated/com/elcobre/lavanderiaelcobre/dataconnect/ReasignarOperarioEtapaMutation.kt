
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



public interface ReasignarOperarioEtapaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ReasignarOperarioEtapaMutation.Data,
      ReasignarOperarioEtapaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val etapaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val operarioId: String,
  
    val motivo: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var comandaId: java.util.UUID
        public var etapaId: java.util.UUID
        public var operarioId: String
        public var motivo: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          comandaId: java.util.UUID,etapaId: java.util.UUID,operarioId: String,
          block_: Builder.() -> Unit
        ): Variables {
          var comandaId= comandaId
            var etapaId= etapaId
            var operarioId= operarioId
            var motivo: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var comandaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { comandaId = value_ }
              
            override var etapaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { etapaId = value_ }
              
            override var operarioId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { operarioId = value_ }
              
            override var motivo: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { motivo = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              comandaId=comandaId,etapaId=etapaId,operarioId=operarioId,motivo=motivo,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaEtapa_update: ComandaEtapaKey?,
  
    val reasignacionOperario_insert: ReasignacionOperarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ReasignarOperarioEtapa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ReasignarOperarioEtapaMutation.ref(
  
    comandaId: java.util.UUID,etapaId: java.util.UUID,operarioId: String,

  
    block_: ReasignarOperarioEtapaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    ReasignarOperarioEtapaMutation.Data,
    ReasignarOperarioEtapaMutation.Variables
  > =
  ref(
    
      ReasignarOperarioEtapaMutation.Variables.build(
        comandaId=comandaId,etapaId=etapaId,operarioId=operarioId,
  
    block_
      )
    
  )

public suspend fun ReasignarOperarioEtapaMutation.execute(

  
    
      comandaId: java.util.UUID,etapaId: java.util.UUID,operarioId: String,

  
    block_: ReasignarOperarioEtapaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    ReasignarOperarioEtapaMutation.Data,
    ReasignarOperarioEtapaMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,etapaId=etapaId,operarioId=operarioId,
  
    block_
    
  ).execute()


