
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetPanelRepartidorQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetPanelRepartidorQuery.Data,
      GetPanelRepartidorQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var limit: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              limit=limit,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val vehiculos: List<VehiculosItem>,
  
    val misSalidas: List<MisSalidasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class VehiculosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: Int?,
  
    val descripcion: String?,
  
    val enCurso: List<EnCursoItem>,
  
    val recientes: List<RecientesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class EnCursoItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = SalidaVehiculoEstado.EnumValueSerializer::class) EnumValue<SalidaVehiculoEstado>,
  
    val fechaSalida: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val repartidor: Repartidor,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Repartidor(
  
    val id: String,
  
    val nombre: String,
  
    val apellido: String?,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class RecientesItem(
  
    val inspecciones: List<InspeccionesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class InspeccionesItem(
  
    val kilometraje: Double,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class MisSalidasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = SalidaVehiculoEstado.EnumValueSerializer::class) EnumValue<SalidaVehiculoEstado>,
  
    val fechaSalida: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaRetorno: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val observaciones: String?,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val vehiculo: Vehiculo,
  
    val inspecciones: List<InspeccionesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Vehiculo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: Int?,
  
    val activo: Boolean,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InspeccionesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val momento: @kotlinx.serialization.Serializable(with = MomentoInspeccion.EnumValueSerializer::class) EnumValue<MomentoInspeccion>,
  
    val estadoVehiculo: @kotlinx.serialization.Serializable(with = EstadoVehiculo.EnumValueSerializer::class) EnumValue<EstadoVehiculo>,
  
    val kilometraje: Double,
  
    val observaciones: String?,
  
    val registradoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fotos: List<FotosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FotosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rutaStorage: String,
  
    val descripcion: String?,
  
    val orden: Int,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetPanelRepartidor"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetPanelRepartidorQuery.ref(
  
    

  
    block_: GetPanelRepartidorQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetPanelRepartidorQuery.Data,
    GetPanelRepartidorQuery.Variables
  > =
  ref(
    
      GetPanelRepartidorQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetPanelRepartidorQuery.execute(

  
    
      

  
    block_: GetPanelRepartidorQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetPanelRepartidorQuery.Data,
    GetPanelRepartidorQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetPanelRepartidorQuery.flow(
    
      

  
    block_: GetPanelRepartidorQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetPanelRepartidorQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

