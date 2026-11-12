
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



public interface ActualizarUsuarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ActualizarUsuarioMutation.Data,
      ActualizarUsuarioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val rolId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val apellido: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val activo: Boolean,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var rolId: java.util.UUID
        public var nombre: String
        public var apellido: String?
        public var telefono: String?
        public var activo: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,rolId: java.util.UUID,nombre: String,activo: Boolean,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var rolId= rolId
            var nombre= nombre
            var apellido: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var activo= activo
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var rolId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rolId = value_ }
              
            override var nombre: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombre = value_ }
              
            override var apellido: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { apellido = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var telefono: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { telefono = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var activo: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { activo = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,rolId=rolId,nombre=nombre,apellido=apellido,telefono=telefono,activo=activo,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario_update: UsuarioKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ActualizarUsuario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ActualizarUsuarioMutation.ref(
  
    id: String,rolId: java.util.UUID,nombre: String,activo: Boolean,

  
    block_: ActualizarUsuarioMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    ActualizarUsuarioMutation.Data,
    ActualizarUsuarioMutation.Variables
  > =
  ref(
    
      ActualizarUsuarioMutation.Variables.build(
        id=id,rolId=rolId,nombre=nombre,activo=activo,
  
    block_
      )
    
  )

public suspend fun ActualizarUsuarioMutation.execute(

  
    
      id: String,rolId: java.util.UUID,nombre: String,activo: Boolean,

  
    block_: ActualizarUsuarioMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    ActualizarUsuarioMutation.Data,
    ActualizarUsuarioMutation.Variables
  > =
  ref(
    
      id=id,rolId=rolId,nombre=nombre,activo=activo,
  
    block_
    
  ).execute()


