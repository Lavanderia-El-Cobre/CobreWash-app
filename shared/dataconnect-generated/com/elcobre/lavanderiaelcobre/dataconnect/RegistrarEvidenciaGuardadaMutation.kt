
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



public interface RegistrarEvidenciaGuardadaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarEvidenciaGuardadaMutation.Data,
      RegistrarEvidenciaGuardadaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val inspeccionId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rutaStorage: String,
  
    val descripcion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val orden: Int,
  
    val confirmar: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var inspeccionId: java.util.UUID
        public var rutaStorage: String
        public var descripcion: String?
        public var orden: Int
        public var confirmar: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,inspeccionId: java.util.UUID,rutaStorage: String,orden: Int,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var inspeccionId= inspeccionId
            var rutaStorage= rutaStorage
            var descripcion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var orden= orden
            var confirmar: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var inspeccionId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { inspeccionId = value_ }
              
            override var rutaStorage: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rutaStorage = value_ }
              
            override var descripcion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { descripcion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var orden: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { orden = value_ }
              
            override var confirmar: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { confirmar = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,inspeccionId=inspeccionId,rutaStorage=rutaStorage,descripcion=descripcion,orden=orden,confirmar=confirmar,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val fotoInspeccionVehiculo_upsert: FotoInspeccionVehiculoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarEvidenciaGuardada"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarEvidenciaGuardadaMutation.ref(
  
    id: java.util.UUID,inspeccionId: java.util.UUID,rutaStorage: String,orden: Int,

  
    block_: RegistrarEvidenciaGuardadaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarEvidenciaGuardadaMutation.Data,
    RegistrarEvidenciaGuardadaMutation.Variables
  > =
  ref(
    
      RegistrarEvidenciaGuardadaMutation.Variables.build(
        id=id,inspeccionId=inspeccionId,rutaStorage=rutaStorage,orden=orden,
  
    block_
      )
    
  )

public suspend fun RegistrarEvidenciaGuardadaMutation.execute(

  
    
      id: java.util.UUID,inspeccionId: java.util.UUID,rutaStorage: String,orden: Int,

  
    block_: RegistrarEvidenciaGuardadaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarEvidenciaGuardadaMutation.Data,
    RegistrarEvidenciaGuardadaMutation.Variables
  > =
  ref(
    
      id=id,inspeccionId=inspeccionId,rutaStorage=rutaStorage,orden=orden,
  
    block_
    
  ).execute()


