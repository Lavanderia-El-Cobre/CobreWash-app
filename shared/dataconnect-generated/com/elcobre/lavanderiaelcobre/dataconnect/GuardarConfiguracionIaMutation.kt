
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



public interface GuardarConfiguracionIaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      GuardarConfiguracionIaMutation.Data,
      GuardarConfiguracionIaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val margenSeguridad: Double,
  
    val lambda: Double,
  
    val incluirSimulado: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val configuracionIa_upsert: ConfiguracionIaKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "GuardarConfiguracionIa"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GuardarConfiguracionIaMutation.ref(
  
    margenSeguridad: Double,lambda: Double,incluirSimulado: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    GuardarConfiguracionIaMutation.Data,
    GuardarConfiguracionIaMutation.Variables
  > =
  ref(
    
      GuardarConfiguracionIaMutation.Variables(
        margenSeguridad=margenSeguridad,lambda=lambda,incluirSimulado=incluirSimulado,
  
      )
    
  )

public suspend fun GuardarConfiguracionIaMutation.execute(

  
    
      margenSeguridad: Double,lambda: Double,incluirSimulado: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    GuardarConfiguracionIaMutation.Data,
    GuardarConfiguracionIaMutation.Variables
  > =
  ref(
    
      margenSeguridad=margenSeguridad,lambda=lambda,incluirSimulado=incluirSimulado,
  
    
  ).execute()


