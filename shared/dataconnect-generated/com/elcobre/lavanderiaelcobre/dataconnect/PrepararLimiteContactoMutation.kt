
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



public interface PrepararLimiteContactoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      PrepararLimiteContactoMutation.Data,
      PrepararLimiteContactoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val limiteContacto_insert: LimiteContactoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "PrepararLimiteContacto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun PrepararLimiteContactoMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    PrepararLimiteContactoMutation.Data,
    PrepararLimiteContactoMutation.Variables
  > =
  ref(
    
      PrepararLimiteContactoMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun PrepararLimiteContactoMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    PrepararLimiteContactoMutation.Data,
    PrepararLimiteContactoMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


