
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



public interface AdjuntarFotoInspeccionFlotaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AdjuntarFotoInspeccionFlotaMutation.Data,
      AdjuntarFotoInspeccionFlotaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val inspeccionId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rutaStorage: String,
  
    val descripcion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val orden: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var inspeccionId: java.util.UUID
        public var rutaStorage: String
        public var descripcion: String?
        public var orden: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          inspeccionId: java.util.UUID,rutaStorage: String,
          block_: Builder.() -> Unit
        ): Variables {
          var inspeccionId= inspeccionId
            var rutaStorage= rutaStorage
            var descripcion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var orden: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var inspeccionId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { inspeccionId = value_ }
              
            override var rutaStorage: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rutaStorage = value_ }
              
            override var descripcion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { descripcion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var orden: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { orden = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              inspeccionId=inspeccionId,rutaStorage=rutaStorage,descripcion=descripcion,orden=orden,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val fotoInspeccionVehiculo_insert: FotoInspeccionVehiculoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AdjuntarFotoInspeccionFlota"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AdjuntarFotoInspeccionFlotaMutation.ref(
  
    inspeccionId: java.util.UUID,rutaStorage: String,

  
    block_: AdjuntarFotoInspeccionFlotaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    AdjuntarFotoInspeccionFlotaMutation.Data,
    AdjuntarFotoInspeccionFlotaMutation.Variables
  > =
  ref(
    
      AdjuntarFotoInspeccionFlotaMutation.Variables.build(
        inspeccionId=inspeccionId,rutaStorage=rutaStorage,
  
    block_
      )
    
  )

public suspend fun AdjuntarFotoInspeccionFlotaMutation.execute(

  
    
      inspeccionId: java.util.UUID,rutaStorage: String,

  
    block_: AdjuntarFotoInspeccionFlotaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    AdjuntarFotoInspeccionFlotaMutation.Data,
    AdjuntarFotoInspeccionFlotaMutation.Variables
  > =
  ref(
    
      inspeccionId=inspeccionId,rutaStorage=rutaStorage,
  
    block_
    
  ).execute()


