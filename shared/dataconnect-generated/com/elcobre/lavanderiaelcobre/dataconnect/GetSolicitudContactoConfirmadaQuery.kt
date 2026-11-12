
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


public interface GetSolicitudContactoConfirmadaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetSolicitudContactoConfirmadaQuery.Data,
      GetSolicitudContactoConfirmadaQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val solicitudContacto: SolicitudContacto?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SolicitudContacto(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val hashContenido: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetSolicitudContactoConfirmada"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetSolicitudContactoConfirmadaQuery.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetSolicitudContactoConfirmadaQuery.Data,
    GetSolicitudContactoConfirmadaQuery.Variables
  > =
  ref(
    
      GetSolicitudContactoConfirmadaQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetSolicitudContactoConfirmadaQuery.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetSolicitudContactoConfirmadaQuery.Data,
    GetSolicitudContactoConfirmadaQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetSolicitudContactoConfirmadaQuery.flow(
    
      id: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetSolicitudContactoConfirmadaQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

