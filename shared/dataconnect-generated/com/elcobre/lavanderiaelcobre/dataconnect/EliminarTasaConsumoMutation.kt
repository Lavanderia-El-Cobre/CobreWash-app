
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



public interface EliminarTasaConsumoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EliminarTasaConsumoMutation.Data,
      EliminarTasaConsumoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val tipoServicioId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val insumoId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tasaConsumo_delete: TasaConsumoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EliminarTasaConsumo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EliminarTasaConsumoMutation.ref(
  
    tipoServicioId: java.util.UUID,insumoId: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    EliminarTasaConsumoMutation.Data,
    EliminarTasaConsumoMutation.Variables
  > =
  ref(
    
      EliminarTasaConsumoMutation.Variables(
        tipoServicioId=tipoServicioId,insumoId=insumoId,
  
      )
    
  )

public suspend fun EliminarTasaConsumoMutation.execute(

  
    
      tipoServicioId: java.util.UUID,insumoId: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    EliminarTasaConsumoMutation.Data,
    EliminarTasaConsumoMutation.Variables
  > =
  ref(
    
      tipoServicioId=tipoServicioId,insumoId=insumoId,
  
    
  ).execute()


