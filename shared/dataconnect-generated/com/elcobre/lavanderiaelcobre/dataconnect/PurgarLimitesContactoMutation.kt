
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



public interface PurgarLimitesContactoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      PurgarLimitesContactoMutation.Data,
      PurgarLimitesContactoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val antesDe: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContacto_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "PurgarLimitesContacto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun PurgarLimitesContactoMutation.ref(
  
    antesDe: com.google.firebase.Timestamp,

  
  
): com.google.firebase.dataconnect.MutationRef<
    PurgarLimitesContactoMutation.Data,
    PurgarLimitesContactoMutation.Variables
  > =
  ref(
    
      PurgarLimitesContactoMutation.Variables(
        antesDe=antesDe,
  
      )
    
  )

public suspend fun PurgarLimitesContactoMutation.execute(

  
    
      antesDe: com.google.firebase.Timestamp,

  

  ): com.google.firebase.dataconnect.MutationResult<
    PurgarLimitesContactoMutation.Data,
    PurgarLimitesContactoMutation.Variables
  > =
  ref(
    
      antesDe=antesDe,
  
    
  ).execute()


