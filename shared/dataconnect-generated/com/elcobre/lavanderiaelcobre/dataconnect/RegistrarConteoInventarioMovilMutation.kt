
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



public interface RegistrarConteoInventarioMovilMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarConteoInventarioMovilMutation.Data,
      RegistrarConteoInventarioMovilMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidad: Double,
  
    val revisionStock: Int,
  
    val idOperacion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val ocurridoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var insumoId: java.util.UUID
        public var cantidad: Double
        public var revisionStock: Int
        public var idOperacion: java.util.UUID
        public var ocurridoEn: com.google.firebase.Timestamp
        public var relojDudoso: Boolean
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var insumoId= insumoId
            var cantidad= cantidad
            var revisionStock= revisionStock
            var idOperacion= idOperacion
            var ocurridoEn= ocurridoEn
            var relojDudoso: com.google.firebase.dataconnect.OptionalVariable<Boolean> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var insumoId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { insumoId = value_ }
              
            override var cantidad: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { cantidad = value_ }
              
            override var revisionStock: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { revisionStock = value_ }
              
            override var idOperacion: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { idOperacion = value_ }
              
            override var ocurridoEn: com.google.firebase.Timestamp
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { ocurridoEn = value_ }
              
            override var relojDudoso: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { relojDudoso = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,idOperacion=idOperacion,ocurridoEn=ocurridoEn,relojDudoso=relojDudoso,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventario_insert: ConteoInventarioKey,
  
    val operacion: OperacionMovilKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarConteoInventarioMovil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarConteoInventarioMovilMutation.ref(
  
    id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarConteoInventarioMovilMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarConteoInventarioMovilMutation.Data,
    RegistrarConteoInventarioMovilMutation.Variables
  > =
  ref(
    
      RegistrarConteoInventarioMovilMutation.Variables.build(
        id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
      )
    
  )

public suspend fun RegistrarConteoInventarioMovilMutation.execute(

  
    
      id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,idOperacion: java.util.UUID,ocurridoEn: com.google.firebase.Timestamp,

  
    block_: RegistrarConteoInventarioMovilMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarConteoInventarioMovilMutation.Data,
    RegistrarConteoInventarioMovilMutation.Variables
  > =
  ref(
    
      id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,idOperacion=idOperacion,ocurridoEn=ocurridoEn,
  
    block_
    
  ).execute()


