
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



public interface RegistrarVehiculoFlotaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarVehiculoFlotaMutation.Data,
      RegistrarVehiculoFlotaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val descripcion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var patente: String
        public var marca: String
        public var modelo: String
        public var anio: Int?
        public var descripcion: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          patente: String,marca: String,modelo: String,
          block_: Builder.() -> Unit
        ): Variables {
          var patente= patente
            var marca= marca
            var modelo= modelo
            var anio: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var descripcion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var patente: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { patente = value_ }
              
            override var marca: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { marca = value_ }
              
            override var modelo: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { modelo = value_ }
              
            override var anio: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { anio = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var descripcion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { descripcion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              patente=patente,marca=marca,modelo=modelo,anio=anio,descripcion=descripcion,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val vehiculo_insert: VehiculoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarVehiculoFlota"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarVehiculoFlotaMutation.ref(
  
    patente: String,marca: String,modelo: String,

  
    block_: RegistrarVehiculoFlotaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarVehiculoFlotaMutation.Data,
    RegistrarVehiculoFlotaMutation.Variables
  > =
  ref(
    
      RegistrarVehiculoFlotaMutation.Variables.build(
        patente=patente,marca=marca,modelo=modelo,
  
    block_
      )
    
  )

public suspend fun RegistrarVehiculoFlotaMutation.execute(

  
    
      patente: String,marca: String,modelo: String,

  
    block_: RegistrarVehiculoFlotaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarVehiculoFlotaMutation.Data,
    RegistrarVehiculoFlotaMutation.Variables
  > =
  ref(
    
      patente=patente,marca=marca,modelo=modelo,
  
    block_
    
  ).execute()


