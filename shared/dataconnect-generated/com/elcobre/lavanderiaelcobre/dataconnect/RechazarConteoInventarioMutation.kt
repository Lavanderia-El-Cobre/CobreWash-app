
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



public interface RechazarConteoInventarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RechazarConteoInventarioMutation.Data,
      RechazarConteoInventarioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val motivo: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventario_update: ConteoInventarioKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RechazarConteoInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RechazarConteoInventarioMutation.ref(
  
    id: java.util.UUID,motivo: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RechazarConteoInventarioMutation.Data,
    RechazarConteoInventarioMutation.Variables
  > =
  ref(
    
      RechazarConteoInventarioMutation.Variables(
        id=id,motivo=motivo,
  
      )
    
  )

public suspend fun RechazarConteoInventarioMutation.execute(

  
    
      id: java.util.UUID,motivo: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RechazarConteoInventarioMutation.Data,
    RechazarConteoInventarioMutation.Variables
  > =
  ref(
    
      id=id,motivo=motivo,
  
    
  ).execute()


