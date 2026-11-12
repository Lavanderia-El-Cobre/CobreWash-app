
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



public interface CrearClienteAdministradoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearClienteAdministradoMutation.Data,
      CrearClienteAdministradoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val crearFicha: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
    val rut: String,
  
    val nombre: String,
  
    val apellido: String,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val email: String,
  
    val direccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val tipoCliente: TipoCliente,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var crearFicha: Boolean
        public var rut: String
        public var nombre: String
        public var apellido: String
        public var telefono: String?
        public var email: String
        public var direccion: String?
        public var tipoCliente: TipoCliente
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var crearFicha: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var rut= rut
            var nombre= nombre
            var apellido= apellido
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var email= email
            var direccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var tipoCliente= tipoCliente
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var crearFicha: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { crearFicha = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var rut: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rut = value_ }
              
            override var nombre: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombre = value_ }
              
            override var apellido: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { apellido = value_ }
              
            override var telefono: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { telefono = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            override var direccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { direccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var tipoCliente: TipoCliente
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoCliente = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,crearFicha=crearFicha,rut=rut,nombre=nombre,apellido=apellido,telefono=telefono,email=email,direccion=direccion,tipoCliente=tipoCliente,
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
    public val operationName: String = "CrearClienteAdministrado"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearClienteAdministradoMutation.ref(
  
    id: String,rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,

  
    block_: CrearClienteAdministradoMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CrearClienteAdministradoMutation.Data,
    CrearClienteAdministradoMutation.Variables
  > =
  ref(
    
      CrearClienteAdministradoMutation.Variables.build(
        id=id,rut=rut,nombre=nombre,apellido=apellido,email=email,tipoCliente=tipoCliente,
  
    block_
      )
    
  )

public suspend fun CrearClienteAdministradoMutation.execute(

  
    
      id: String,rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,

  
    block_: CrearClienteAdministradoMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CrearClienteAdministradoMutation.Data,
    CrearClienteAdministradoMutation.Variables
  > =
  ref(
    
      id=id,rut=rut,nombre=nombre,apellido=apellido,email=email,tipoCliente=tipoCliente,
  
    block_
    
  ).execute()


