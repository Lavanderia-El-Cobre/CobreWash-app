
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


public interface GetInsumoPorQrQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetInsumoPorQrQuery.Data,
      GetInsumoPorQrQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val insumo: Insumo?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Insumo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockActual: Double,
  
    val stockMinimo: Double,
  
    val revisionStock: Int,
  
    val activo: Boolean,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetInsumoPorQr"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetInsumoPorQrQuery.ref(
  
    codigoQr: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetInsumoPorQrQuery.Data,
    GetInsumoPorQrQuery.Variables
  > =
  ref(
    
      GetInsumoPorQrQuery.Variables(
        codigoQr=codigoQr,
  
      )
    
  )

public suspend fun GetInsumoPorQrQuery.execute(

  
    
      codigoQr: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetInsumoPorQrQuery.Data,
    GetInsumoPorQrQuery.Variables
  > =
  ref(
    
      codigoQr=codigoQr,
  
    
  ).execute()


  public fun GetInsumoPorQrQuery.flow(
    
      codigoQr: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetInsumoPorQrQuery.Data> =
    ref(
        
          codigoQr=codigoQr,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

