
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



public interface CompletarEtapaComandaMovilMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CompletarEtapaComandaMovilMutation.Data,
      CompletarEtapaComandaMovilMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val etapaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val orden: Int,
  
    val estadoComanda: ComandaEstado,
  
    val idOperacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val ocurridoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var comandaId: java.util.UUID
        public var etapaId: java.util.UUID
        public var orden: Int
        public var estadoComanda: ComandaEstado
        public var idOperacion: java.util.UUID
        public var ocurridoEn: com.google.firebase.Timestamp
        public var relojDudoso: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          comandaId: java.util.UUID,etapaId: java.util.UUID,orden: Int,estadoComanda: ComandaEstado,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,
          block_: Builder.() -> Unit
        ): Variables {
          var comandaId= comandaId
            var etapaId= etapaId
            var orden= orden
            var estadoComanda= estadoComanda
            var idOperacion= idOperacion
            var ocurridoEn= ocurridoEn
            var relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var comandaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { comandaId = value_ }
              
            override var etapaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { etapaId = value_ }
              
            override var orden: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { orden = value_ }
              
            override var estadoComanda: ComandaEstado
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estadoComanda = value_ }
              
            override var idOperacion: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { idOperacion = value_ }
              
            override var ocurridoEn: com.google.firebase.Timestamp
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { ocurridoEn = value_ }
              
            override var relojDudoso: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { relojDudoso = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              comandaId=comandaId,etapaId=etapaId,orden=orden,estadoComanda=estadoComanda,idOperacion=idOperacion,ocurridoEn=ocurridoEn,relojDudoso=relojDudoso,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaEtapa_update: ComandaEtapaKey?,
  
    val siguiente: Int,
  
    val comanda_update: ComandaKey?,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
    val notificacion: Int?,
  
    val operacion: OperacionMovilKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CompletarEtapaComandaMovil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CompletarEtapaComandaMovilMutation.ref(
  
    comandaId: java.util.UUID,etapaId: java.util.UUID,orden: Int,estadoComanda: ComandaEstado,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: CompletarEtapaComandaMovilMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CompletarEtapaComandaMovilMutation.Data,
    CompletarEtapaComandaMovilMutation.Variables
  > =
  ref(
    
      CompletarEtapaComandaMovilMutation.Variables.build(
        comandaId=comandaId,etapaId=etapaId,orden=orden,estadoComanda=estadoComanda,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
      )
    
  )

public suspend fun CompletarEtapaComandaMovilMutation.execute(

  
    
      comandaId: java.util.UUID,etapaId: java.util.UUID,orden: Int,estadoComanda: ComandaEstado,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: CompletarEtapaComandaMovilMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CompletarEtapaComandaMovilMutation.Data,
    CompletarEtapaComandaMovilMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,etapaId=etapaId,orden=orden,estadoComanda=estadoComanda,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
    
  ).execute()


