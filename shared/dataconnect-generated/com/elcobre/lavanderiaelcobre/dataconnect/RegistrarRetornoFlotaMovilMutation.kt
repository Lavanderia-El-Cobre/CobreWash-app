
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



public interface RegistrarRetornoFlotaMovilMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarRetornoFlotaMovilMutation.Data,
      RegistrarRetornoFlotaMovilMutation.Variables
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
  
    val idOperacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val ocurridoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
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
        public var idOperacion: java.util.UUID
        public var ocurridoEn: com.google.firebase.Timestamp
        public var relojDudoso: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,
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
            var idOperacion= idOperacion
            var ocurridoEn= ocurridoEn
            var relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
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
              inspeccionId=inspeccionId,salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,observacionesRetorno=observacionesRetorno,observacionesInspeccion=observacionesInspeccion,idOperacion=idOperacion,ocurridoEn=ocurridoEn,relojDudoso=relojDudoso,
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
  
    val operacion: OperacionMovilKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarRetornoFlotaMovil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarRetornoFlotaMovilMutation.ref(
  
    salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarRetornoFlotaMovilMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarRetornoFlotaMovilMutation.Data,
    RegistrarRetornoFlotaMovilMutation.Variables
  > =
  ref(
    
      RegistrarRetornoFlotaMovilMutation.Variables.build(
        salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
      )
    
  )

public suspend fun RegistrarRetornoFlotaMovilMutation.execute(

  
    
      salidaId: java.util.UUID,kilometraje: Double,estadoVehiculo: EstadoVehiculo,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarRetornoFlotaMovilMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarRetornoFlotaMovilMutation.Data,
    RegistrarRetornoFlotaMovilMutation.Variables
  > =
  ref(
    
      salidaId=salidaId,kilometraje=kilometraje,estadoVehiculo=estadoVehiculo,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
    
  ).execute()


