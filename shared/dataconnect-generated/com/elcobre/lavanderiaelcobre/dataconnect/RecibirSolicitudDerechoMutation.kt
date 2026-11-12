
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



public interface RecibirSolicitudDerechoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RecibirSolicitudDerechoMutation.Data,
      RecibirSolicitudDerechoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val secretoHash: String,
  
    val contenidoHash: String,
  
    val nombre: String,
  
    val email: String,
  
    val derecho: DerechoPrivacidad,
  
    val detalle: String,
  
    val versionAviso: String,
  
    val objetivoAtencion: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val limiteId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContacto_update: LimiteContactoKey?,
  
    val solicitudDerecho_insert: SolicitudDerechoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RecibirSolicitudDerecho"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RecibirSolicitudDerechoMutation.ref(
  
    id: java.util.UUID,secretoHash: String,contenidoHash: String,nombre: String,email: String,derecho: DerechoPrivacidad,detalle: String,versionAviso: String,objetivoAtencion: com.google.firebase.Timestamp,limiteId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RecibirSolicitudDerechoMutation.Data,
    RecibirSolicitudDerechoMutation.Variables
  > =
  ref(
    
      RecibirSolicitudDerechoMutation.Variables(
        id=id,secretoHash=secretoHash,contenidoHash=contenidoHash,nombre=nombre,email=email,derecho=derecho,detalle=detalle,versionAviso=versionAviso,objetivoAtencion=objetivoAtencion,limiteId=limiteId,
  
      )
    
  )

public suspend fun RecibirSolicitudDerechoMutation.execute(

  
    
      id: java.util.UUID,secretoHash: String,contenidoHash: String,nombre: String,email: String,derecho: DerechoPrivacidad,detalle: String,versionAviso: String,objetivoAtencion: com.google.firebase.Timestamp,limiteId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RecibirSolicitudDerechoMutation.Data,
    RecibirSolicitudDerechoMutation.Variables
  > =
  ref(
    
      id=id,secretoHash=secretoHash,contenidoHash=contenidoHash,nombre=nombre,email=email,derecho=derecho,detalle=detalle,versionAviso=versionAviso,objetivoAtencion=objetivoAtencion,limiteId=limiteId,
  
    
  ).execute()


