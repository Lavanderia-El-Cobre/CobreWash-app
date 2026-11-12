
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



public interface ConfigurarEtapaProduccionMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ConfigurarEtapaProduccionMutation.Data,
      ConfigurarEtapaProduccionMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val descripcion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val tiempoEstimadoMin: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var nombre: String
        public var descripcion: String?
        public var tiempoEstimadoMin: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,nombre: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var nombre= nombre
            var descripcion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var tiempoEstimadoMin: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var nombre: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombre = value_ }
              
            override var descripcion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { descripcion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var tiempoEstimadoMin: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tiempoEstimadoMin = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,nombre=nombre,descripcion=descripcion,tiempoEstimadoMin=tiempoEstimadoMin,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val etapaProduccion_update: EtapaProduccionKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ConfigurarEtapaProduccion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ConfigurarEtapaProduccionMutation.ref(
  
    id: java.util.UUID,nombre: String,

  
    block_: ConfigurarEtapaProduccionMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    ConfigurarEtapaProduccionMutation.Data,
    ConfigurarEtapaProduccionMutation.Variables
  > =
  ref(
    
      ConfigurarEtapaProduccionMutation.Variables.build(
        id=id,nombre=nombre,
  
    block_
      )
    
  )

public suspend fun ConfigurarEtapaProduccionMutation.execute(

  
    
      id: java.util.UUID,nombre: String,

  
    block_: ConfigurarEtapaProduccionMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    ConfigurarEtapaProduccionMutation.Data,
    ConfigurarEtapaProduccionMutation.Variables
  > =
  ref(
    
      id=id,nombre=nombre,
  
    block_
    
  ).execute()


