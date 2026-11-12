
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


public interface GetComprobanteDerechoQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetComprobanteDerechoQuery.Data,
      GetComprobanteDerechoQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val solicitudDerecho: SolicitudDerecho?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SolicitudDerecho(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val secretoHash: String,
  
    val contenidoHash: String,
  
    val estado: @kotlinx.serialization.Serializable(with = EstadoDerecho.EnumValueSerializer::class) EnumValue<EstadoDerecho>,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val objetivoAtencion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComprobanteDerecho"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetComprobanteDerechoQuery.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetComprobanteDerechoQuery.Data,
    GetComprobanteDerechoQuery.Variables
  > =
  ref(
    
      GetComprobanteDerechoQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetComprobanteDerechoQuery.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetComprobanteDerechoQuery.Data,
    GetComprobanteDerechoQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetComprobanteDerechoQuery.flow(
    
      id: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetComprobanteDerechoQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

