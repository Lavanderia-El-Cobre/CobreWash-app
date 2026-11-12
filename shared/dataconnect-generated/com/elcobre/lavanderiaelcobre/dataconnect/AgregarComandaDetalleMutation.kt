
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



public interface AgregarComandaDetalleMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AgregarComandaDetalleMutation.Data,
      AgregarComandaDetalleMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tipoPrendaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidad: Int,
  
    val detalle: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val precioUnitario: Double,
  
    val subtotal: Double,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var comandaId: java.util.UUID
        public var tipoPrendaId: java.util.UUID
        public var tipoServicioId: java.util.UUID
        public var cantidad: Int
        public var detalle: String?
        public var precioUnitario: Double
        public var subtotal: Double
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          comandaId: java.util.UUID,tipoPrendaId: java.util.UUID,tipoServicioId: java.util.UUID,cantidad: Int,precioUnitario: Double,subtotal: Double,
          block_: Builder.() -> Unit
        ): Variables {
          var comandaId= comandaId
            var tipoPrendaId= tipoPrendaId
            var tipoServicioId= tipoServicioId
            var cantidad= cantidad
            var detalle: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var precioUnitario= precioUnitario
            var subtotal= subtotal
            

          return object : Builder {
            override var comandaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { comandaId = value_ }
              
            override var tipoPrendaId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoPrendaId = value_ }
              
            override var tipoServicioId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoServicioId = value_ }
              
            override var cantidad: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { cantidad = value_ }
              
            override var detalle: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { detalle = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var precioUnitario: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { precioUnitario = value_ }
              
            override var subtotal: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { subtotal = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              comandaId=comandaId,tipoPrendaId=tipoPrendaId,tipoServicioId=tipoServicioId,cantidad=cantidad,detalle=detalle,precioUnitario=precioUnitario,subtotal=subtotal,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaDetalle_insert: ComandaDetalleKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AgregarComandaDetalle"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AgregarComandaDetalleMutation.ref(
  
    comandaId: java.util.UUID,tipoPrendaId: java.util.UUID,tipoServicioId: java.util.UUID,cantidad: Int,precioUnitario: Double,subtotal: Double,

  
    block_: AgregarComandaDetalleMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    AgregarComandaDetalleMutation.Data,
    AgregarComandaDetalleMutation.Variables
  > =
  ref(
    
      AgregarComandaDetalleMutation.Variables.build(
        comandaId=comandaId,tipoPrendaId=tipoPrendaId,tipoServicioId=tipoServicioId,cantidad=cantidad,precioUnitario=precioUnitario,subtotal=subtotal,
  
    block_
      )
    
  )

public suspend fun AgregarComandaDetalleMutation.execute(

  
    
      comandaId: java.util.UUID,tipoPrendaId: java.util.UUID,tipoServicioId: java.util.UUID,cantidad: Int,precioUnitario: Double,subtotal: Double,

  
    block_: AgregarComandaDetalleMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    AgregarComandaDetalleMutation.Data,
    AgregarComandaDetalleMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,tipoPrendaId=tipoPrendaId,tipoServicioId=tipoServicioId,cantidad=cantidad,precioUnitario=precioUnitario,subtotal=subtotal,
  
    block_
    
  ).execute()


