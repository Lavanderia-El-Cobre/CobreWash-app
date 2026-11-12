
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



public interface RegistrarConteoInventarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarConteoInventarioMutation.Data,
      RegistrarConteoInventarioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val cantidad: Double,
  
    val revisionStock: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val conteoInventario_insert: ConteoInventarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarConteoInventario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarConteoInventarioMutation.ref(
  
    id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarConteoInventarioMutation.Data,
    RegistrarConteoInventarioMutation.Variables
  > =
  ref(
    
      RegistrarConteoInventarioMutation.Variables(
        id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,
  
      )
    
  )

public suspend fun RegistrarConteoInventarioMutation.execute(

  
    
      id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarConteoInventarioMutation.Data,
    RegistrarConteoInventarioMutation.Variables
  > =
  ref(
    
      id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,
  
    
  ).execute()


