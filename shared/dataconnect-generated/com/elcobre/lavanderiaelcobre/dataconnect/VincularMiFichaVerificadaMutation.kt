
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



public interface VincularMiFichaVerificadaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      VincularMiFichaVerificadaMutation.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val cliente_update: ClienteKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "VincularMiFichaVerificada"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun VincularMiFichaVerificadaMutation.ref(
  
): com.google.firebase.dataconnect.MutationRef<
    VincularMiFichaVerificadaMutation.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun VincularMiFichaVerificadaMutation.execute(

  

  ): com.google.firebase.dataconnect.MutationResult<
    VincularMiFichaVerificadaMutation.Data,
    Unit
  > =
  ref(
    
  ).execute()


