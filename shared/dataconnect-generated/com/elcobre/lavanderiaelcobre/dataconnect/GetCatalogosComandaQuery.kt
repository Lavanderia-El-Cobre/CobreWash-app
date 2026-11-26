
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


public interface GetCatalogosComandaQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetCatalogosComandaQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tipoServicios: List<TipoServiciosItem>,
  
    val tipoPrendas: List<TipoPrendasItem>,
  
    val clientes: List<ClientesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class TipoServiciosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val precioBase: Double,
  
    val precioKg: Double,
  
    val unidadCobro: @kotlinx.serialization.Serializable(with = UnidadCobro.EnumValueSerializer::class) EnumValue<UnidadCobro>,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class TipoPrendasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ClientesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rut: String?,
  
    val nombre: String,
  
    val tipoCliente: @kotlinx.serialization.Serializable(with = TipoCliente.EnumValueSerializer::class) EnumValue<TipoCliente>,
  
    val telefono: String?,
  
    val email: String?,
  
    val direccion: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetCatalogosComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCatalogosComandaQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetCatalogosComandaQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetCatalogosComandaQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetCatalogosComandaQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetCatalogosComandaQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetCatalogosComandaQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

