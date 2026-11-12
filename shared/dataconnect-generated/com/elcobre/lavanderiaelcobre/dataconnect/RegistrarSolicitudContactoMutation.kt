
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



public interface RegistrarSolicitudContactoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarSolicitudContactoMutation.Data,
      RegistrarSolicitudContactoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val email: String,
  
    val telefono: String,
  
    val servicio: String,
  
    val mensaje: String,
  
    val hashContenido: String,
  
    val limiteId: String,
  
    val versionPrivacidad: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContacto_update: LimiteContactoKey?,
  
    val solicitudContacto_insert: SolicitudContactoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarSolicitudContacto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarSolicitudContactoMutation.ref(
  
    id: java.util.UUID,nombre: String,email: String,telefono: String,servicio: String,mensaje: String,hashContenido: String,limiteId: String,versionPrivacidad: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarSolicitudContactoMutation.Data,
    RegistrarSolicitudContactoMutation.Variables
  > =
  ref(
    
      RegistrarSolicitudContactoMutation.Variables(
        id=id,nombre=nombre,email=email,telefono=telefono,servicio=servicio,mensaje=mensaje,hashContenido=hashContenido,limiteId=limiteId,versionPrivacidad=versionPrivacidad,
  
      )
    
  )

public suspend fun RegistrarSolicitudContactoMutation.execute(

  
    
      id: java.util.UUID,nombre: String,email: String,telefono: String,servicio: String,mensaje: String,hashContenido: String,limiteId: String,versionPrivacidad: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarSolicitudContactoMutation.Data,
    RegistrarSolicitudContactoMutation.Variables
  > =
  ref(
    
      id=id,nombre=nombre,email=email,telefono=telefono,servicio=servicio,mensaje=mensaje,hashContenido=hashContenido,limiteId=limiteId,versionPrivacidad=versionPrivacidad,
  
    
  ).execute()


