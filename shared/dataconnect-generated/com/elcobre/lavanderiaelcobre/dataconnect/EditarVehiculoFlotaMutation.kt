
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



public interface EditarVehiculoFlotaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EditarVehiculoFlotaMutation.Data,
      EditarVehiculoFlotaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val descripcion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val activo: Boolean,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var patente: String
        public var marca: String
        public var modelo: String
        public var anio: Int?
        public var descripcion: String?
        public var activo: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,patente: String,marca: String,modelo: String,activo: Boolean,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var patente= patente
            var marca= marca
            var modelo= modelo
            var anio: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var descripcion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var activo= activo
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
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
              
            override var activo: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { activo = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,patente=patente,marca=marca,modelo=modelo,anio=anio,descripcion=descripcion,activo=activo,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val vehiculo_update: VehiculoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EditarVehiculoFlota"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EditarVehiculoFlotaMutation.ref(
  
    id: java.util.UUID,patente: String,marca: String,modelo: String,activo: Boolean,

  
    block_: EditarVehiculoFlotaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    EditarVehiculoFlotaMutation.Data,
    EditarVehiculoFlotaMutation.Variables
  > =
  ref(
    
      EditarVehiculoFlotaMutation.Variables.build(
        id=id,patente=patente,marca=marca,modelo=modelo,activo=activo,
  
    block_
      )
    
  )

public suspend fun EditarVehiculoFlotaMutation.execute(

  
    
      id: java.util.UUID,patente: String,marca: String,modelo: String,activo: Boolean,

  
    block_: EditarVehiculoFlotaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    EditarVehiculoFlotaMutation.Data,
    EditarVehiculoFlotaMutation.Variables
  > =
  ref(
    
      id=id,patente=patente,marca=marca,modelo=modelo,activo=activo,
  
    block_
    
  ).execute()


