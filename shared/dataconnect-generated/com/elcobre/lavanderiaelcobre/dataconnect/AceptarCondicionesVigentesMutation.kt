
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



public interface AceptarCondicionesVigentesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AceptarCondicionesVigentesMutation.Data,
      AceptarCondicionesVigentesMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val versionPrivacidad: String,
  
    val versionTerminos: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario_update: UsuarioKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AceptarCondicionesVigentes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AceptarCondicionesVigentesMutation.ref(
  
    versionPrivacidad: String,versionTerminos: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AceptarCondicionesVigentesMutation.Data,
    AceptarCondicionesVigentesMutation.Variables
  > =
  ref(
    
      AceptarCondicionesVigentesMutation.Variables(
        versionPrivacidad=versionPrivacidad,versionTerminos=versionTerminos,
  
      )
    
  )

public suspend fun AceptarCondicionesVigentesMutation.execute(

  
    
      versionPrivacidad: String,versionTerminos: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AceptarCondicionesVigentesMutation.Data,
    AceptarCondicionesVigentesMutation.Variables
  > =
  ref(
    
      versionPrivacidad=versionPrivacidad,versionTerminos=versionTerminos,
  
    
  ).execute()


