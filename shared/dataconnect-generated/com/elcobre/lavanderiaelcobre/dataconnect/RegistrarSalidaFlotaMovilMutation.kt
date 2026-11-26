
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



public interface RegistrarSalidaFlotaMovilMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarSalidaFlotaMovilMutation.Data,
      RegistrarSalidaFlotaMovilMutation.Variables
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
  
    val idOperacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val ocurridoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
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
        public var idOperacion: java.util.UUID
        public var ocurridoEn: com.google.firebase.Timestamp
        public var relojDudoso: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,
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
            var idOperacion= idOperacion
            var ocurridoEn= ocurridoEn
            var relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
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
              salidaId=salidaId,inspeccionId=inspeccionId,vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,observaciones=observaciones,observacionesInspeccion=observacionesInspeccion,idOperacion=idOperacion,ocurridoEn=ocurridoEn,relojDudoso=relojDudoso,
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
  
    val operacion: OperacionMovilKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarSalidaFlotaMovil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarSalidaFlotaMovilMutation.ref(
  
    vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarSalidaFlotaMovilMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarSalidaFlotaMovilMutation.Data,
    RegistrarSalidaFlotaMovilMutation.Variables
  > =
  ref(
    
      RegistrarSalidaFlotaMovilMutation.Variables.build(
        vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
      )
    
  )

public suspend fun RegistrarSalidaFlotaMovilMutation.execute(

  
    
      vehiculoId: java.util.UUID,repartidorId: String,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarSalidaFlotaMovilMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarSalidaFlotaMovilMutation.Data,
    RegistrarSalidaFlotaMovilMutation.Variables
  > =
  ref(
    
      vehiculoId=vehiculoId,repartidorId=repartidorId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
    
  ).execute()


