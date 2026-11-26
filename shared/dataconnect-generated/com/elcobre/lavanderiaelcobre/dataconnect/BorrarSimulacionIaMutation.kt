
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



public interface BorrarSimulacionIaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      BorrarSimulacionIaMutation.Data,
      BorrarSimulacionIaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val produccionSimulada_deleteMany: Int,
  
    val conteoInventario_deleteMany: Int,
  
    val movimientoInventario_deleteMany: Int,
  
    val simulacionIa_delete: SimulacionIaKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "BorrarSimulacionIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun BorrarSimulacionIaMutation.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    BorrarSimulacionIaMutation.Data,
    BorrarSimulacionIaMutation.Variables
  > =
  ref(
    
      BorrarSimulacionIaMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun BorrarSimulacionIaMutation.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    BorrarSimulacionIaMutation.Data,
    BorrarSimulacionIaMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


