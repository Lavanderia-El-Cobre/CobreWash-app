
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



public interface BorrarSimulacionesIaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      BorrarSimulacionesIaMutation.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val produccionSimulada_deleteMany: Int,
  
    val conteoInventario_deleteMany: Int,
  
    val movimientoInventario_deleteMany: Int,
  
    val simulacionIa_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "BorrarSimulacionesIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun BorrarSimulacionesIaMutation.ref(
  
): com.google.firebase.dataconnect.MutationRef<
    BorrarSimulacionesIaMutation.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun BorrarSimulacionesIaMutation.execute(

  

  ): com.google.firebase.dataconnect.MutationResult<
    BorrarSimulacionesIaMutation.Data,
    Unit
  > =
  ref(
    
  ).execute()


