
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


public interface GetComandasParaAlertasQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetComandasParaAlertasQuery.Data,
      GetComandasParaAlertasQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandas: List<ComandasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val estado: @kotlinx.serialization.Serializable(with = ComandaEstado.EnumValueSerializer::class) EnumValue<ComandaEstado>,
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val comandaEtapas_on_comanda: List<ComandaEtapasOnComandaItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandaEtapasOnComandaItem(
  
    val etapaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombreEtapa: String?,
  
    val ordenEtapa: Int?,
  
    val tiempoEstimadoMin: Int?,
  
    val estado: @kotlinx.serialization.Serializable(with = EtapaEstado.EnumValueSerializer::class) EnumValue<EtapaEstado>,
  
    val fechaInicio: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val asignadoA: AsignadoA?,
  
    val etapa: Etapa,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class AsignadoA(
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Etapa(
  
    val nombre: String,
  
    val orden: Int,
  
    val tiempoEstimadoMin: Int?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComandasParaAlertas"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetComandasParaAlertasQuery.ref(
  
    

  
    block_: GetComandasParaAlertasQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetComandasParaAlertasQuery.Data,
    GetComandasParaAlertasQuery.Variables
  > =
  ref(
    
      GetComandasParaAlertasQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetComandasParaAlertasQuery.execute(

  
    
      

  
    block_: GetComandasParaAlertasQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetComandasParaAlertasQuery.Data,
    GetComandasParaAlertasQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetComandasParaAlertasQuery.flow(
    
      

  
    block_: GetComandasParaAlertasQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetComandasParaAlertasQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

