
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



public interface RegistrarSalidaFlotaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarSalidaFlotaMutation.Data,
      RegistrarSalidaFlotaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val salidaId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val inspeccionId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val vehiculoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val repartidorId: String,
  
    val kilometraje: Double,
  
    val estadoVehiculo: EstadoVehiculo,
  
    val observaciones: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val observacionesInspeccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var salidaId: java.util.UUID?
        public var inspeccionId: java.util.UUID?
        public var vehiculoId: java.util.UUID
        public var repartidorId: String
        public var kilometraje: Double
        public var estadoVehiculo: EstadoVehiculo
        public var observaciones: String?
        public var observacionesInspeccion: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,
          block_: Builder.() -> Unit
        ): Variables {
          var salidaId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var inspeccionId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var vehiculoId= vehiculoId
            var repartidorId= repartidorId
            var kilometraje= kilometraje
            var estadoVehiculo= estadoVehiculo
            var observaciones: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var observacionesInspeccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var salidaId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { salidaId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var inspeccionId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { inspeccionId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var vehiculoId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { vehiculoId = value_ }
              
            override var repartidorId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { repartidorId = value_ }
              
            override var kilometraje: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { kilometraje = value_ }
              
            override var estadoVehiculo: EstadoVehiculo
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estadoVehiculo = value_ }
              
            override var observaciones: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observaciones = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var observacionesInspeccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observacionesInspeccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              salidaId=salidaId,inspeccionId=inspeccionId,vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,observaciones=observaciones,observacionesInspeccion=observacionesInspeccion,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bloqueo: VehiculoKey?,
  
    val salida: SalidaVehiculoKey,
  
    val inspeccion: InspeccionVehiculoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarSalidaFlota"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarSalidaFlotaMutation.ref(
  
    vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,

  
    block_: RegistrarSalidaFlotaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarSalidaFlotaMutation.Data,
    RegistrarSalidaFlotaMutation.Variables
  > =
  ref(
    
      RegistrarSalidaFlotaMutation.Variables.build(
        vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,
  
    block_
      )
    
  )

public suspend fun RegistrarSalidaFlotaMutation.execute(

  
    
      vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,

  
    block_: RegistrarSalidaFlotaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarSalidaFlotaMutation.Data,
    RegistrarSalidaFlotaMutation.Variables
  > =
  ref(
    
      vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,
  
    block_
    
  ).execute()


