
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



public interface RegistrarInspeccionAntesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarInspeccionAntesMutation.Data,
      RegistrarInspeccionAntesMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val salidaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estadoVehiculo: EstadoVehiculo,
  
    val kilometraje: Double,
  
    val observaciones: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var salidaId: java.util.UUID
        public var estadoVehiculo: EstadoVehiculo
        public var kilometraje: Double
        public var observaciones: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          salidaId: java.util.UUID,estadoVehiculo: EstadoVehiculo,kilometraje: Double,
          block_: Builder.() -> Unit
        ): Variables {
          var salidaId= salidaId
            var estadoVehiculo= estadoVehiculo
            var kilometraje= kilometraje
            var observaciones: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var salidaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { salidaId = value_ }
              
            override var estadoVehiculo: EstadoVehiculo
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estadoVehiculo = value_ }
              
            override var kilometraje: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { kilometraje = value_ }
              
            override var observaciones: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observaciones = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              salidaId=salidaId,estadoVehiculo=estadoVehiculo,kilometraje=kilometraje,observaciones=observaciones,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bloqueo: VehiculoKey?,
  
    val inspeccionVehiculo_insert: InspeccionVehiculoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarInspeccionAntes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarInspeccionAntesMutation.ref(
  
    salidaId: java.util.UUID,estadoVehiculo: EstadoVehiculo,kilometraje: Double,

  
    block_: RegistrarInspeccionAntesMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarInspeccionAntesMutation.Data,
    RegistrarInspeccionAntesMutation.Variables
  > =
  ref(
    
      RegistrarInspeccionAntesMutation.Variables.build(
        salidaId=salidaId,estadoVehiculo=estadoVehiculo,kilometraje=kilometraje,
  
    block_
      )
    
  )

public suspend fun RegistrarInspeccionAntesMutation.execute(

  
    
      salidaId: java.util.UUID,estadoVehiculo: EstadoVehiculo,kilometraje: Double,

  
    block_: RegistrarInspeccionAntesMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarInspeccionAntesMutation.Data,
    RegistrarInspeccionAntesMutation.Variables
  > =
  ref(
    
      salidaId=salidaId,estadoVehiculo=estadoVehiculo,kilometraje=kilometraje,
  
    block_
    
  ).execute()


