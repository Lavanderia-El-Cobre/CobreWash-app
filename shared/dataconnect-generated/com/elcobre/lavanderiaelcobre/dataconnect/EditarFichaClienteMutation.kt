
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



public interface EditarFichaClienteMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EditarFichaClienteMutation.Data,
      EditarFichaClienteMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val tipoCliente: TipoCliente,
  
    val rut: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val email: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val direccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var nombre: String
        public var tipoCliente: TipoCliente
        public var rut: String?
        public var telefono: String?
        public var email: String?
        public var direccion: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,nombre: String,tipoCliente: TipoCliente,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var nombre= nombre
            var tipoCliente= tipoCliente
            var rut: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var email: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var direccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var nombre: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombre = value_ }
              
            override var tipoCliente: TipoCliente
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoCliente = value_ }
              
            override var rut: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rut = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var telefono: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { telefono = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var email: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var direccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { direccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,nombre=nombre,tipoCliente=tipoCliente,rut=rut,telefono=telefono,email=email,direccion=direccion,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val cliente_update: ClienteKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EditarFichaCliente"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EditarFichaClienteMutation.ref(
  
    id: java.util.UUID,nombre: String,tipoCliente: TipoCliente,

  
    block_: EditarFichaClienteMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    EditarFichaClienteMutation.Data,
    EditarFichaClienteMutation.Variables
  > =
  ref(
    
      EditarFichaClienteMutation.Variables.build(
        id=id,nombre=nombre,tipoCliente=tipoCliente,
  
    block_
      )
    
  )

public suspend fun EditarFichaClienteMutation.execute(

  
    
      id: java.util.UUID,nombre: String,tipoCliente: TipoCliente,

  
    block_: EditarFichaClienteMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    EditarFichaClienteMutation.Data,
    EditarFichaClienteMutation.Variables
  > =
  ref(
    
      id=id,nombre=nombre,tipoCliente=tipoCliente,
  
    block_
    
  ).execute()


