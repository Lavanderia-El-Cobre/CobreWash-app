
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



public interface RegistrarRetornoFlotaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarRetornoFlotaMutation.Data,
      RegistrarRetornoFlotaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val inspeccionId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val salidaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val kilometraje: Double,
  
    val estadoVehiculo: EstadoVehiculo,
  
    val observacionesRetorno: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val observacionesInspeccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var inspeccionId: java.util.UUID?
        public var salidaId: java.util.UUID
        public var kilometraje: Double
        public var estadoVehiculo: EstadoVehiculo
        public var observacionesRetorno: String?
        public var observacionesInspeccion: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,
          block_: Builder.() -> Unit
        ): Variables {
          var inspeccionId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var salidaId= salidaId
            var kilometraje= kilometraje
            var estadoVehiculo= estadoVehiculo
            var observacionesRetorno: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var observacionesInspeccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var inspeccionId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { inspeccionId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var salidaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { salidaId = value_ }
              
            override var kilometraje: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { kilometraje = value_ }
              
            override var estadoVehiculo: EstadoVehiculo
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estadoVehiculo = value_ }
              
            override var observacionesRetorno: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observacionesRetorno = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var observacionesInspeccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observacionesInspeccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              inspeccionId=inspeccionId,salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,observacionesRetorno=observacionesRetorno,observacionesInspeccion=observacionesInspeccion,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bloqueo: VehiculoKey?,
  
    val inspeccion: InspeccionVehiculoKey,
  
    val salidaVehiculo_update: SalidaVehiculoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarRetornoFlota"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarRetornoFlotaMutation.ref(
  
    salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,

  
    block_: RegistrarRetornoFlotaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarRetornoFlotaMutation.Data,
    RegistrarRetornoFlotaMutation.Variables
  > =
  ref(
    
      RegistrarRetornoFlotaMutation.Variables.build(
        salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,
  
    block_
      )
    
  )

public suspend fun RegistrarRetornoFlotaMutation.execute(

  
    
      salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,

  
    block_: RegistrarRetornoFlotaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarRetornoFlotaMutation.Data,
    RegistrarRetornoFlotaMutation.Variables
  > =
  ref(
    
      salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,
  
    block_
    
  ).execute()


