
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


  
  
  public enum class ComandaEstado {
  PENDIENTE,
  EN_PROCESO,
  FINALIZADA,
  ENTREGADA,
  ANULADA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<ComandaEstado>(ComandaEstado.entries)
  
  }

  
  
  public enum class DerechoPrivacidad {
  ACCESO,
  RECTIFICACION,
  SUPRESION,
  OPOSICION,
  BLOQUEO,
  PORTABILIDAD,
  REVOCACION;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<DerechoPrivacidad>(DerechoPrivacidad.entries)
  
  }

  
  
  public enum class EstadoConteoInventario {
  PENDIENTE,
  APROBADO,
  RECHAZADO;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<EstadoConteoInventario>(EstadoConteoInventario.entries)
  
  }

  
  
  public enum class EstadoDerecho {
  RECIBIDA,
  VERIFICACION,
  EN_GESTION,
  RESPONDIDA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<EstadoDerecho>(EstadoDerecho.entries)
  
  }

  
  
  public enum class EstadoVehiculo {
  APTO,
  CON_OBSERVACIONES,
  NO_APTO;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<EstadoVehiculo>(EstadoVehiculo.entries)
  
  }

  
  
  public enum class EtapaEstado {
  PENDIENTE,
  EN_PROCESO,
  COMPLETADA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<EtapaEstado>(EtapaEstado.entries)
  
  }

  
  
  public enum class IncidenciaEstado {
  ABIERTA,
  EN_REVISION,
  RESUELTA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<IncidenciaEstado>(IncidenciaEstado.entries)
  
  }

  
  
  public enum class MomentoInspeccion {
  ANTES,
  DESPUES;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<MomentoInspeccion>(MomentoInspeccion.entries)
  
  }

  
  
  public enum class SalidaVehiculoEstado {
  PROGRAMADA,
  EN_SERVICIO,
  FINALIZADA,
  CANCELADA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<SalidaVehiculoEstado>(SalidaVehiculoEstado.entries)
  
  }

  
  
  public enum class SolicitudContactoEstado {
  RECIBIDA,
  EN_REVISION,
  ATENDIDA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<SolicitudContactoEstado>(SolicitudContactoEstado.entries)
  
  }

  
  
  public enum class TipoCliente {
  HOTEL,
  PARTICULAR;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<TipoCliente>(TipoCliente.entries)
  
  }

  
  
  public enum class TipoMovimiento {
  ENTRADA,
  SALIDA;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<TipoMovimiento>(TipoMovimiento.entries)
  
  }

  
  
  public enum class UnidadCobro {
  PRENDA,
  KILO;
  
  
    public object EnumValueSerializer :
      com.elcobre.lavanderiaelcobre.dataconnect.EnumValueSerializer<UnidadCobro>(UnidadCobro.entries)
  
  }

