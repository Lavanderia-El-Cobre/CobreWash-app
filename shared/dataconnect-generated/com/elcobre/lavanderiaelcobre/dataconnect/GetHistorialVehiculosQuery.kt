
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


public interface GetHistorialVehiculosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetHistorialVehiculosQuery.Data,
      GetHistorialVehiculosQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val vehiculoId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
  
    val repartidorId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val estado: com.google.firebase.dataconnect.OptionalVariable<SalidaVehiculoEstado?>,
  
    val desde: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val hasta: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?>,
  
    val limit: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val offset: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var vehiculoId: java.util.UUID?
        public var repartidorId: String?
        public var estado: SalidaVehiculoEstado?
        public var desde: com.google.firebase.Timestamp?
        public var hasta: com.google.firebase.Timestamp?
        public var limit: Int?
        public var offset: Int?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var vehiculoId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var repartidorId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var estado: com.google.firebase.dataconnect.OptionalVariable<SalidaVehiculoEstado?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var desde: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var hasta: com.google.firebase.dataconnect.OptionalVariable<com.google.firebase.Timestamp?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var limit: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var offset: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var vehiculoId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { vehiculoId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var repartidorId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { repartidorId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var estado: SalidaVehiculoEstado?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { estado = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var desde: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { desde = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var hasta: com.google.firebase.Timestamp?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { hasta = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var limit: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { limit = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var offset: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offset = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              vehiculoId=vehiculoId,repartidorId=repartidorId,estado=estado,desde=desde,hasta=hasta,limit=limit,offset=offset,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val salidas: List<SalidasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SalidasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = SalidaVehiculoEstado.EnumValueSerializer::class) EnumValue<SalidaVehiculoEstado>,
  
    val fechaSalida: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaRetorno: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val observaciones: String?,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val vehiculo: Vehiculo,
  
    val repartidor: Repartidor,
  
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
  public data class Repartidor(
  
    val id: String,
  
    val rut: String?,
  
    val nombre: String,
  
    val apellido: String?,
  
    val email: String,
  
    val telefono: String?,
  
    val rol: Rol,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
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
    public val operationName: String = "GetHistorialVehiculos"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetHistorialVehiculosQuery.ref(
  
    

  
    block_: GetHistorialVehiculosQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    GetHistorialVehiculosQuery.Data,
    GetHistorialVehiculosQuery.Variables
  > =
  ref(
    
      GetHistorialVehiculosQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun GetHistorialVehiculosQuery.execute(

  
    
      

  
    block_: GetHistorialVehiculosQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    GetHistorialVehiculosQuery.Data,
    GetHistorialVehiculosQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute()


  public fun GetHistorialVehiculosQuery.flow(
    
      

  
    block_: GetHistorialVehiculosQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<GetHistorialVehiculosQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

