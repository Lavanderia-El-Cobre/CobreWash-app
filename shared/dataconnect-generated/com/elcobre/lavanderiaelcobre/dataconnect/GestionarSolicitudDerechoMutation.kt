
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



public interface GestionarSolicitudDerechoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      GestionarSolicitudDerechoMutation.Data,
      GestionarSolicitudDerechoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val revision: Int,
  
    val actorId: String,
  
    val estado: EstadoDerecho,
  
    val nota: String,
  
    val evidenciaIdentidad: String,
  
    val evidenciaEnvio: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val solicitudDerecho_update: SolicitudDerechoKey?,
  
    val actuacionDerecho_insert: ActuacionDerechoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "GestionarSolicitudDerecho"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GestionarSolicitudDerechoMutation.ref(
  
    id: java.util.UUID,revision: Int,actorId: String,estado: EstadoDerecho,nota: String,evidenciaIdentidad: String,evidenciaEnvio: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    GestionarSolicitudDerechoMutation.Data,
    GestionarSolicitudDerechoMutation.Variables
  > =
  ref(
    
      GestionarSolicitudDerechoMutation.Variables(
        id=id,revision=revision,actorId=actorId,estado=estado,nota=nota,evidenciaIdentidad=evidenciaIdentidad,evidenciaEnvio=evidenciaEnvio,
  
      )
    
  )

public suspend fun GestionarSolicitudDerechoMutation.execute(

  
    
      id: java.util.UUID,revision: Int,actorId: String,estado: EstadoDerecho,nota: String,evidenciaIdentidad: String,evidenciaEnvio: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    GestionarSolicitudDerechoMutation.Data,
    GestionarSolicitudDerechoMutation.Variables
  > =
  ref(
    
      id=id,revision=revision,actorId=actorId,estado=estado,nota=nota,evidenciaIdentidad=evidenciaIdentidad,evidenciaEnvio=evidenciaEnvio,
  
    
  ).execute()


