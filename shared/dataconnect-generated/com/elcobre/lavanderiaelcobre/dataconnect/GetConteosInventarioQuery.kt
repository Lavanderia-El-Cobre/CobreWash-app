
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetConteosInventarioQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetConteosInventarioQuery.Data,
      GetConteosInventarioQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val estado: com.google.firebase.dataconnect.OptionalVariable<EstadoConteoInventario?>,
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var estado: EstadoConteoInventario?
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var estado: com.google.firebase.dataconnect.OptionalVariable<EstadoConteoInventario?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var estado: EstadoConteoInventario?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estado = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              estado=estado,limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventarios: List<ConteoInventariosItem>,
  
    val total: List<TotalItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ConteoInventariosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidadContada: Double,
  
    val stockSistema: Double,
  
    val revisionStock: Int,
  
    val estado: @kotlinx.serialization.Serializable(with = EstadoConteoInventario.EnumValueSerializer::class) EnumValue<EstadoConteoInventario>,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val resueltoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val motivoResolucion: String?,
  
    val insumo: Insumo,
  
    val registradoPor: RegistradoPor,
  
    val resueltoPor: ResueltoPor?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Insumo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockActual: Double,
  
    val revisionStock: Int,
  
    val activo: Boolean,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class RegistradoPor(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ResueltoPor(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TotalItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetConteosInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetConteosInventarioQuery.ref(
  
    

  
    block_: GetConteosInventarioQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetConteosInventarioQuery.Data,
    GetConteosInventarioQuery.Variables
  > =
  ref(
    
      GetConteosInventarioQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetConteosInventarioQuery.execute(

  
    
      

  
    block_: GetConteosInventarioQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetConteosInventarioQuery.Data,
    GetConteosInventarioQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetConteosInventarioQuery.flow(
    
      

  
    block_: GetConteosInventarioQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetConteosInventarioQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

