
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



public interface RegistrarseMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarseMutation.Data,
      RegistrarseMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val rolId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rut: String,
  
    val nombre: String,
  
    val apellido: String,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val email: String,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var rolId: java.util.UUID
        public var rut: String
        public var nombre: String
        public var apellido: String
        public var telefono: String?
        public var email: String
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          rolId: java.util.UUID,rut: String,nombre: String,apellido: String,email: String,
          block_: Builder.() -> Unit
        ): Variables {
          var rolId= rolId
            var rut= rut
            var nombre= nombre
            var apellido= apellido
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var email= email
            

          return object : Builder {
            override var rolId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rolId = value_ }
              
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
              
            
          }.apply(block_)
          .let {
            Variables(
              rolId=rolId,rut=rut,nombre=nombre,apellido=apellido,telefono=telefono,email=email,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario_insert: UsuarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "Registrarse"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarseMutation.ref(
  
    rolId: java.util.UUID,rut: String,nombre: String,apellido: String,email: String,

  
    block_: RegistrarseMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarseMutation.Data,
    RegistrarseMutation.Variables
  > =
  ref(
    
      RegistrarseMutation.Variables.build(
        rolId=rolId,rut=rut,nombre=nombre,apellido=apellido,email=email,
  
    block_
      )
    
  )

public suspend fun RegistrarseMutation.execute(

  
    
      rolId: java.util.UUID,rut: String,nombre: String,apellido: String,email: String,

  
    block_: RegistrarseMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarseMutation.Data,
    RegistrarseMutation.Variables
  > =
  ref(
    
      rolId=rolId,rut=rut,nombre=nombre,apellido=apellido,email=email,
  
    block_
    
  ).execute()


