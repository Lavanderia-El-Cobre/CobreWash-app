
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



public interface RegistrarConteoFisicoIaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarConteoFisicoIaMutation.Data,
      RegistrarConteoFisicoIaMutation.Variables
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
  
    val insumo_update: InsumoKey?,
  
    val movimientoInventario_insert: MovimientoInventarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarConteoFisicoIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarConteoFisicoIaMutation.ref(
  
    id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarConteoFisicoIaMutation.Data,
    RegistrarConteoFisicoIaMutation.Variables
  > =
  ref(
    
      RegistrarConteoFisicoIaMutation.Variables(
        id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,
  
      )
    
  )

public suspend fun RegistrarConteoFisicoIaMutation.execute(

  
    
      id: java.util.UUID,insumoId: java.util.UUID,cantidad: Double,revisionStock: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarConteoFisicoIaMutation.Data,
    RegistrarConteoFisicoIaMutation.Variables
  > =
  ref(
    
      id=id,insumoId=insumoId,cantidad=cantidad,revisionStock=revisionStock,
  
    
  ).execute()


