
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


public interface GetMiPerfilQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetMiPerfilQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario: Usuario?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Usuario(
  
    val id: String,
  
    val rut: String?,
  
    val nombre: String,
  
    val apellido: String?,
  
    val email: String,
  
    val telefono: String?,
  
    val activo: Boolean,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val versionPrivacidad: String?,
  
    val versionTerminos: String?,
  
    val rol: Rol,
  
    val clientes_on_usuario: List<ClientesOnUsuarioItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val descripcion: String?,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ClientesOnUsuarioItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val tipoCliente: @kotlinx.serialization.Serializable(with = TipoCliente.EnumValueSerializer::class) EnumValue<TipoCliente>,
  
    val direccion: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMiPerfil"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMiPerfilQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetMiPerfilQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetMiPerfilQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMiPerfilQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetMiPerfilQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetMiPerfilQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

