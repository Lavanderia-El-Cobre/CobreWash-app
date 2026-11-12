
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



public interface VincularMiComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      VincularMiComandaMutation.Data,
      VincularMiComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val numeroComanda: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaVinculada_upsert: ComandaVinculadaKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "VincularMiComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun VincularMiComandaMutation.ref(
  
    numeroComanda: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    VincularMiComandaMutation.Data,
    VincularMiComandaMutation.Variables
  > =
  ref(
    
      VincularMiComandaMutation.Variables(
        numeroComanda=numeroComanda,
  
      )
    
  )

public suspend fun VincularMiComandaMutation.execute(

  
    
      numeroComanda: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    VincularMiComandaMutation.Data,
    VincularMiComandaMutation.Variables
  > =
  ref(
    
      numeroComanda=numeroComanda,
  
    
  ).execute()


