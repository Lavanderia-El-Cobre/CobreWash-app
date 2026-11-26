
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



public interface ReportarProblemaEntregaMovilMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ReportarProblemaEntregaMovilMutation.Data,
      ReportarProblemaEntregaMovilMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val motivo: String,
  
    val detalle: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val idOperacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val ocurridoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var comandaId: java.util.UUID
        public var motivo: String
        public var detalle: String?
        public var idOperacion: java.util.UUID
        public var ocurridoEn: com.google.firebase.Timestamp
        public var relojDudoso: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          comandaId: java.util.UUID,motivo: String,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,
          block_: Builder.() -> Unit
        ): Variables {
          var comandaId= comandaId
            var motivo= motivo
            var detalle: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var idOperacion= idOperacion
            var ocurridoEn= ocurridoEn
            var relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var comandaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { comandaId = value_ }
              
            override var motivo: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { motivo = value_ }
              
            override var detalle: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { detalle = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
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
              comandaId=comandaId,motivo=motivo,detalle=detalle,idOperacion=idOperacion,ocurridoEn=ocurridoEn,relojDudoso=relojDudoso,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bloqueo: ComandaKey?,
  
    val incidenciaComanda_insert: IncidenciaComandaKey,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
    val operacion: OperacionMovilKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ReportarProblemaEntregaMovil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ReportarProblemaEntregaMovilMutation.ref(
  
    comandaId: java.util.UUID,motivo: String,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: ReportarProblemaEntregaMovilMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    ReportarProblemaEntregaMovilMutation.Data,
    ReportarProblemaEntregaMovilMutation.Variables
  > =
  ref(
    
      ReportarProblemaEntregaMovilMutation.Variables.build(
        comandaId=comandaId,motivo=motivo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
      )
    
  )

public suspend fun ReportarProblemaEntregaMovilMutation.execute(

  
    
      comandaId: java.util.UUID,motivo: String,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: ReportarProblemaEntregaMovilMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    ReportarProblemaEntregaMovilMutation.Data,
    ReportarProblemaEntregaMovilMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,motivo=motivo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
    
  ).execute()


