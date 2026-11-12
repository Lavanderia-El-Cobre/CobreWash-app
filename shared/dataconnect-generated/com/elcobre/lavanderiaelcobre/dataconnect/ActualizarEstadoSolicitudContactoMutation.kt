
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



public interface ActualizarEstadoSolicitudContactoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ActualizarEstadoSolicitudContactoMutation.Data,
      ActualizarEstadoSolicitudContactoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: SolicitudContactoEstado,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val solicitudContacto_update: SolicitudContactoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ActualizarEstadoSolicitudContacto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ActualizarEstadoSolicitudContactoMutation.ref(
  
    id: java.util.UUID,estado: SolicitudContactoEstado,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ActualizarEstadoSolicitudContactoMutation.Data,
    ActualizarEstadoSolicitudContactoMutation.Variables
  > =
  ref(
    
      ActualizarEstadoSolicitudContactoMutation.Variables(
        id=id,estado=estado,
  
      )
    
  )

public suspend fun ActualizarEstadoSolicitudContactoMutation.execute(

  
    
      id: java.util.UUID,estado: SolicitudContactoEstado,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ActualizarEstadoSolicitudContactoMutation.Data,
    ActualizarEstadoSolicitudContactoMutation.Variables
  > =
  ref(
    
      id=id,estado=estado,
  
    
  ).execute()


