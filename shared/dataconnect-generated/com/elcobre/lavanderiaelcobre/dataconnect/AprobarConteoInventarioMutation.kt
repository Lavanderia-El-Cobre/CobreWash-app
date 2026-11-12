
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



public interface AprobarConteoInventarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AprobarConteoInventarioMutation.Data,
      AprobarConteoInventarioMutation.Variables
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
  
    val insumo_update: InsumoKey?,
  
    val movimientoInventario_insert: MovimientoInventarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AprobarConteoInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AprobarConteoInventarioMutation.ref(
  
    id: java.util.UUID,motivo: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AprobarConteoInventarioMutation.Data,
    AprobarConteoInventarioMutation.Variables
  > =
  ref(
    
      AprobarConteoInventarioMutation.Variables(
        id=id,motivo=motivo,
  
      )
    
  )

public suspend fun AprobarConteoInventarioMutation.execute(

  
    
      id: java.util.UUID,motivo: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AprobarConteoInventarioMutation.Data,
    AprobarConteoInventarioMutation.Variables
  > =
  ref(
    
      id=id,motivo=motivo,
  
    
  ).execute()


