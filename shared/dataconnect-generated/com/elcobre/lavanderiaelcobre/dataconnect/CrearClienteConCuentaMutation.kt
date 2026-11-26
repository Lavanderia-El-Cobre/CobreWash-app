
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



public interface CrearClienteConCuentaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearClienteConCuentaMutation.Data,
      CrearClienteConCuentaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val usuarioId: String,
  
    val nombreUsuario: String,
  
    val apellidoUsuario: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val nombre: String,
  
    val tipoCliente: TipoCliente,
  
    val rut: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val email: String,
  
    val direccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var usuarioId: String
        public var nombreUsuario: String
        public var apellidoUsuario: String?
        public var nombre: String
        public var tipoCliente: TipoCliente
        public var rut: String?
        public var telefono: String?
        public var email: String
        public var direccion: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          usuarioId: String,nombreUsuario: String,nombre: String,tipoCliente: TipoCliente,email: String,
          block_: Builder.() -> Unit
        ): Variables {
          var usuarioId= usuarioId
            var nombreUsuario= nombreUsuario
            var apellidoUsuario: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var nombre= nombre
            var tipoCliente= tipoCliente
            var rut: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var email= email
            var direccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var usuarioId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { usuarioId = value_ }
              
            override var nombreUsuario: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombreUsuario = value_ }
              
            override var apellidoUsuario: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { apellidoUsuario = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
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
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            override var direccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { direccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              usuarioId=usuarioId,nombreUsuario=nombreUsuario,apellidoUsuario=apellidoUsuario,nombre=nombre,tipoCliente=tipoCliente,rut=rut,telefono=telefono,email=email,direccion=direccion,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario_insert: UsuarioKey,
  
    val cliente_insert: ClienteKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearClienteConCuenta"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearClienteConCuentaMutation.ref(
  
    usuarioId: String,nombreUsuario: String,nombre: String,tipoCliente: TipoCliente,email: String,

  
    block_: CrearClienteConCuentaMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CrearClienteConCuentaMutation.Data,
    CrearClienteConCuentaMutation.Variables
  > =
  ref(
    
      CrearClienteConCuentaMutation.Variables.build(
        usuarioId=usuarioId,nombreUsuario=nombreUsuario,nombre=nombre,tipoCliente=tipoCliente,email=email,
  
    block_
      )
    
  )

public suspend fun CrearClienteConCuentaMutation.execute(

  
    
      usuarioId: String,nombreUsuario: String,nombre: String,tipoCliente: TipoCliente,email: String,

  
    block_: CrearClienteConCuentaMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CrearClienteConCuentaMutation.Data,
    CrearClienteConCuentaMutation.Variables
  > =
  ref(
    
      usuarioId=usuarioId,nombreUsuario=nombreUsuario,nombre=nombre,tipoCliente=tipoCliente,email=email,
  
    block_
    
  ).execute()


