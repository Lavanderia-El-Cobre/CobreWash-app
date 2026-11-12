
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



public interface CrearSalidaVehiculoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearSalidaVehiculoMutation.Data,
      CrearSalidaVehiculoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val vehiculoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val repartidorId: String,
  
    val observaciones: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var vehiculoId: java.util.UUID
        public var repartidorId: String
        public var observaciones: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          vehiculoId: java.util.UUID,repartidorId: String,
          block_: Builder.() -> Unit
        ): Variables {
          var vehiculoId= vehiculoId
            var repartidorId= repartidorId
            var observaciones: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var vehiculoId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { vehiculoId = value_ }
              
            override var repartidorId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { repartidorId = value_ }
              
            override var observaciones: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { observaciones = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              vehiculoId=vehiculoId,repartidorId=repartidorId,observaciones=observaciones,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val bloqueo: VehiculoKey?,
  
    val salidaVehiculo_insert: SalidaVehiculoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearSalidaVehiculo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearSalidaVehiculoMutation.ref(
  
    vehiculoId: java.util.UUID,repartidorId: String,

  
    block_: CrearSalidaVehiculoMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CrearSalidaVehiculoMutation.Data,
    CrearSalidaVehiculoMutation.Variables
  > =
  ref(
    
      CrearSalidaVehiculoMutation.Variables.build(
        vehiculoId=vehiculoId,repartidorId=repartidorId,
  
    block_
      )
    
  )

public suspend fun CrearSalidaVehiculoMutation.execute(

  
    
      vehiculoId: java.util.UUID,repartidorId: String,

  
    block_: CrearSalidaVehiculoMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CrearSalidaVehiculoMutation.Data,
    CrearSalidaVehiculoMutation.Variables
  > =
  ref(
    
      vehiculoId=vehiculoId,repartidorId=repartidorId,
  
    block_
    
  ).execute()


