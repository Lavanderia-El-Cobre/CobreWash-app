
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


public interface GetMovimientoFlotaPorInspeccionQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetMovimientoFlotaPorInspeccionQuery.Data,
      GetMovimientoFlotaPorInspeccionQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val inspeccionVehiculo: InspeccionVehiculo?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class InspeccionVehiculo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val salidaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val momento: @kotlinx.serialization.Serializable(with = MomentoInspeccion.EnumValueSerializer::class) EnumValue<MomentoInspeccion>,
  
    val kilometraje: Double,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMovimientoFlotaPorInspeccion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMovimientoFlotaPorInspeccionQuery.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetMovimientoFlotaPorInspeccionQuery.Data,
    GetMovimientoFlotaPorInspeccionQuery.Variables
  > =
  ref(
    
      GetMovimientoFlotaPorInspeccionQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetMovimientoFlotaPorInspeccionQuery.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMovimientoFlotaPorInspeccionQuery.Data,
    GetMovimientoFlotaPorInspeccionQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetMovimientoFlotaPorInspeccionQuery.flow(
    
      id: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetMovimientoFlotaPorInspeccionQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

