
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


public interface GetSeguimientoPublicoPorQrQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetSeguimientoPublicoPorQrQuery.Data,
      GetSeguimientoPublicoPorQrQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda: Comanda?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Comanda(
  
    val codigoQr: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val numeroComanda: String,
  
    val estado: @kotlinx.serialization.Serializable(with = ComandaEstado.EnumValueSerializer::class) EnumValue<ComandaEstado>,
  
    val fechaRecepcion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fechaEntregaEstimada: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaEntregaReal: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val actualizadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val comandaDetalles_on_comanda: List<ComandaDetallesOnComandaItem>,
  
    val comandaEtapas_on_comanda: List<ComandaEtapasOnComandaItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ComandaDetallesOnComandaItem(
  
    val tipoServicio: TipoServicio,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServicio(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ComandaEtapasOnComandaItem(
  
    val nombreEtapa: String?,
  
    val ordenEtapa: Int?,
  
    val estado: @kotlinx.serialization.Serializable(with = EtapaEstado.EnumValueSerializer::class) EnumValue<EtapaEstado>,
  
    val fechaCompletado: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val etapa: Etapa,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Etapa(
  
    val nombre: String,
  
    val orden: Int,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetSeguimientoPublicoPorQr"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetSeguimientoPublicoPorQrQuery.ref(
  
    codigoQr: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetSeguimientoPublicoPorQrQuery.Data,
    GetSeguimientoPublicoPorQrQuery.Variables
  > =
  ref(
    
      GetSeguimientoPublicoPorQrQuery.Variables(
        codigoQr=codigoQr,
  
      )
    
  )

public suspend fun GetSeguimientoPublicoPorQrQuery.execute(

  
    
      codigoQr: java.util.UUID,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetSeguimientoPublicoPorQrQuery.Data,
    GetSeguimientoPublicoPorQrQuery.Variables
  > =
  ref(
    
      codigoQr=codigoQr,
  
    
  ).execute()


  public fun GetSeguimientoPublicoPorQrQuery.flow(
    
      codigoQr: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetSeguimientoPublicoPorQrQuery.Data> =
    ref(
        
          codigoQr=codigoQr,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

