
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



public interface GuardarTasaConsumoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      GuardarTasaConsumoMutation.Data,
      GuardarTasaConsumoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tasaInicial: Double,
  
    val tasaActual: Double,
  
    val unidad: String,
  
    val fuente: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var tipoServicioId: java.util.UUID
        public var insumoId: java.util.UUID
        public var tasaInicial: Double
        public var tasaActual: Double
        public var unidad: String
        public var fuente: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          tipoServicioId: java.util.UUID,insumoId: java.util.UUID,tasaInicial: Double,tasaActual: Double,unidad: String,
          block_: Builder.() -> Unit
        ): Variables {
          var tipoServicioId= tipoServicioId
            var insumoId= insumoId
            var tasaInicial= tasaInicial
            var tasaActual= tasaActual
            var unidad= unidad
            var fuente: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var tipoServicioId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoServicioId = value_ }
              
            override var insumoId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { insumoId = value_ }
              
            override var tasaInicial: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tasaInicial = value_ }
              
            override var tasaActual: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tasaActual = value_ }
              
            override var unidad: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { unidad = value_ }
              
            override var fuente: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { fuente = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              tipoServicioId=tipoServicioId,insumoId=insumoId,tasaInicial=tasaInicial,tasaActual=tasaActual,unidad=unidad,fuente=fuente,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tasaConsumo_upsert: TasaConsumoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "GuardarTasaConsumo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GuardarTasaConsumoMutation.ref(
  
    tipoServicioId: java.util.UUID,insumoId: java.util.UUID,tasaInicial: Double,tasaActual: Double,unidad: String,

  
    block_: GuardarTasaConsumoMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    GuardarTasaConsumoMutation.Data,
    GuardarTasaConsumoMutation.Variables
  > =
  ref(
    
      GuardarTasaConsumoMutation.Variables.build(
        tipoServicioId=tipoServicioId,insumoId=insumoId,tasaInicial=tasaInicial,tasaActual=tasaActual,unidad=unidad,
  
    block_
      )
    
  )

public suspend fun GuardarTasaConsumoMutation.execute(

  
    
      tipoServicioId: java.util.UUID,insumoId: java.util.UUID,tasaInicial: Double,tasaActual: Double,unidad: String,

  
    block_: GuardarTasaConsumoMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    GuardarTasaConsumoMutation.Data,
    GuardarTasaConsumoMutation.Variables
  > =
  ref(
    
      tipoServicioId=tipoServicioId,insumoId=insumoId,tasaInicial=tasaInicial,tasaActual=tasaActual,unidad=unidad,
  
    block_
    
  ).execute()


