
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



public interface ReportarProblemaEntregaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ReportarProblemaEntregaMutation.Data,
      ReportarProblemaEntregaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val motivo: String,
  
    val detalle: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var comandaId: java.util.UUID
        public var motivo: String
        public var detalle: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          comandaId: java.util.UUID,motivo: String,
          block_: Builder.() -> Unit
        ): Variables {
          var comandaId= comandaId
            var motivo= motivo
            var detalle: com.google.firebase.dataconnect.OptionalVariable<String?> =
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
              
            
          }.apply(block_)
          .let {
            Variables(
              comandaId=comandaId,motivo=motivo,detalle=detalle,
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
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ReportarProblemaEntrega"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ReportarProblemaEntregaMutation.ref(
  
    comandaId: java.util.UUID,motivo: String,

  
    block_: ReportarProblemaEntregaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    ReportarProblemaEntregaMutation.Data,
    ReportarProblemaEntregaMutation.Variables
  > =
  ref(
    
      ReportarProblemaEntregaMutation.Variables.build(
        comandaId=comandaId,motivo=motivo,
  
    block_
      )
    
  )

public suspend fun ReportarProblemaEntregaMutation.execute(

  
    
      comandaId: java.util.UUID,motivo: String,

  
    block_: ReportarProblemaEntregaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    ReportarProblemaEntregaMutation.Data,
    ReportarProblemaEntregaMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,motivo=motivo,
  
    block_
    
  ).execute()


