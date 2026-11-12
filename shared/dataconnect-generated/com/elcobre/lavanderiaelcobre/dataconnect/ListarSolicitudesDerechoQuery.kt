
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


public interface ListarSolicitudesDerechoQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      ListarSolicitudesDerechoQuery.Data,
      ListarSolicitudesDerechoQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int>,
  
    val estados: com.google.firebase.dataconnect.OptionalVariable<List<EstadoDerecho>>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var offset: Int
        public var estados: List<EstadoDerecho>
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var offset: com.google.firebase.dataconnect.OptionalVariable<Int> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var estados: com.google.firebase.dataconnect.OptionalVariable<List<EstadoDerecho>> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var offset: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var estados: List<EstadoDerecho>
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estados = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              offset=offset,estados=estados,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val solicitudDerechos: List<SolicitudDerechosItem>,
  
    val total: List<TotalItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SolicitudDerechosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val email: String,
  
    val derecho: @kotlinx.serialization.Serializable(with = DerechoPrivacidad.EnumValueSerializer::class) EnumValue<DerechoPrivacidad>,
  
    val detalle: String,
  
    val versionAviso: String,
  
    val estado: @kotlinx.serialization.Serializable(with = EstadoDerecho.EnumValueSerializer::class) EnumValue<EstadoDerecho>,
  
    val revision: Int,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val actualizadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val objetivoAtencion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val actuacionDerechos_on_solicitud: List<ActuacionDerechosOnSolicitudItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ActuacionDerechosOnSolicitudItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = EstadoDerecho.EnumValueSerializer::class) EnumValue<EstadoDerecho>,
  
    val nota: String,
  
    val evidenciaIdentidad: String,
  
    val evidenciaEnvio: String,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val actor: Actor,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Actor(
  
    val id: String,
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TotalItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListarSolicitudesDerecho"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ListarSolicitudesDerechoQuery.ref(
  
    

  
    block_: ListarSolicitudesDerechoQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    ListarSolicitudesDerechoQuery.Data,
    ListarSolicitudesDerechoQuery.Variables
  > =
  ref(
    
      ListarSolicitudesDerechoQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun ListarSolicitudesDerechoQuery.execute(

  
    
      

  
    block_: ListarSolicitudesDerechoQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    ListarSolicitudesDerechoQuery.Data,
    ListarSolicitudesDerechoQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun ListarSolicitudesDerechoQuery.flow(
    
      

  
    block_: ListarSolicitudesDerechoQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<ListarSolicitudesDerechoQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

