
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



public interface RegistrarSalidaInventarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarSalidaInventarioMutation.Data,
      RegistrarSalidaInventarioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidad: Double,
  
    val motivo: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var insumoId: java.util.UUID
        public var cantidad: Double
        public var motivo: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          insumoId: java.util.UUID,cantidad: Double,
          block_: Builder.() -> Unit
        ): Variables {
          var insumoId= insumoId
            var cantidad= cantidad
            var motivo: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var insumoId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { insumoId = value_ }
              
            override var cantidad: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { cantidad = value_ }
              
            override var motivo: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { motivo = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              insumoId=insumoId,cantidad=cantidad,motivo=motivo,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val insumo_update: InsumoKey?,
  
    val movimientoInventario_insert: MovimientoInventarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarSalidaInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarSalidaInventarioMutation.ref(
  
    insumoId: java.util.UUID,cantidad: Double,

  
    block_: RegistrarSalidaInventarioMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarSalidaInventarioMutation.Data,
    RegistrarSalidaInventarioMutation.Variables
  > =
  ref(
    
      RegistrarSalidaInventarioMutation.Variables.build(
        insumoId=insumoId,cantidad=cantidad,
  
    block_
      )
    
  )

public suspend fun RegistrarSalidaInventarioMutation.execute(

  
    
      insumoId: java.util.UUID,cantidad: Double,

  
    block_: RegistrarSalidaInventarioMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarSalidaInventarioMutation.Data,
    RegistrarSalidaInventarioMutation.Variables
  > =
  ref(
    
      insumoId=insumoId,cantidad=cantidad,
  
    block_
    
  ).execute()


