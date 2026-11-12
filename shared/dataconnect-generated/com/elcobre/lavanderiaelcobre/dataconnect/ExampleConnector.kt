
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

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface ExampleConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<ExampleConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val actualizarEstadoIncidencia: ActualizarEstadoIncidenciaMutation
  
    public val actualizarEstadoSolicitudContacto: ActualizarEstadoSolicitudContactoMutation
  
    public val actualizarInsumo: ActualizarInsumoMutation
  
    public val actualizarUsuario: ActualizarUsuarioMutation
  
    public val actualizarVehiculo: ActualizarVehiculoMutation
  
    public val adjuntarFotoInspeccionFlota: AdjuntarFotoInspeccionFlotaMutation
  
    public val agregarComandaDetalle: AgregarComandaDetalleMutation
  
    public val agregarComentarioComanda: AgregarComentarioComandaMutation
  
    public val agregarFotoInspeccionVehiculo: AgregarFotoInspeccionVehiculoMutation
  
    public val anularComanda: AnularComandaMutation
  
    public val aprobarConteoInventario: AprobarConteoInventarioMutation
  
    public val asociarFlujoComandaPendiente: AsociarFlujoComandaPendienteMutation
  
    public val autoAsignarComandaOperario: AutoAsignarComandaOperarioMutation
  
    public val autorizarGestionPrivacidad: AutorizarGestionPrivacidadQuery
  
    public val completarEtapaComanda: CompletarEtapaComandaMutation
  
    public val configurarEtapaProduccion: ConfigurarEtapaProduccionMutation
  
    public val configurarLimitesEtapas: ConfigurarLimitesEtapasMutation
  
    public val contarLimitesContactoVencidos: ContarLimitesContactoVencidosQuery
  
    public val crearAviso: CrearAvisoMutation
  
    public val crearClienteAdministrado: CrearClienteAdministradoMutation
  
    public val crearClienteComanda: CrearClienteComandaMutation
  
    public val crearComanda: CrearComandaMutation
  
    public val crearInsumo: CrearInsumoMutation
  
    public val crearSalidaVehiculo: CrearSalidaVehiculoMutation
  
    public val crearTipoPrenda: CrearTipoPrendaMutation
  
    public val crearTipoServicio: CrearTipoServicioMutation
  
    public val crearUsuarioAdministrado: CrearUsuarioAdministradoMutation
  
    public val crearVehiculo: CrearVehiculoMutation
  
    public val diagnosticoComandas: DiagnosticoComandasQuery
  
    public val editarComanda: EditarComandaMutation
  
    public val editarFichaCliente: EditarFichaClienteMutation
  
    public val editarVehiculoFlota: EditarVehiculoFlotaMutation
  
    public val eliminarDetallesComanda: EliminarDetallesComandaMutation
  
    public val entregarComanda: EntregarComandaMutation
  
    public val gestionarSolicitudDerecho: GestionarSolicitudDerechoMutation
  
    public val getAvisosAdministracion: GetAvisosAdministracionQuery
  
    public val getAvisosParaEquipo: GetAvisosParaEquipoQuery
  
    public val getCatalogosComanda: GetCatalogosComandaQuery
  
    public val getComandaDetalle: GetComandaDetalleQuery
  
    public val getComandaDetalleOperario: GetComandaDetalleOperarioQuery
  
    public val getComandaOperativaPorQr: GetComandaOperativaPorQrQuery
  
    public val getComandaPorQr: GetComandaPorQrQuery
  
    public val getComandasActivasCount: GetComandasActivasCountQuery
  
    public val getComandasPaginadas: GetComandasPaginadasQuery
  
    public val getComandasParaAlertas: GetComandasParaAlertasQuery
  
    public val getComprobanteDerecho: GetComprobanteDerechoQuery
  
    public val getConteosInventario: GetConteosInventarioQuery
  
    public val getDetalleReporteCuentas: GetDetalleReporteCuentasQuery
  
    public val getDetalleReporteServicios: GetDetalleReporteServiciosQuery
  
    public val getEtapasProduccion: GetEtapasProduccionQuery
  
    public val getFichasClientes: GetFichasClientesQuery
  
    public val getFiltrosReportes: GetFiltrosReportesQuery
  
    public val getFlotaVehiculos: GetFlotaVehiculosQuery
  
    public val getHistorialInventario: GetHistorialInventarioQuery
  
    public val getHistorialVehiculos: GetHistorialVehiculosQuery
  
    public val getIncidencias: GetIncidenciasQuery
  
    public val getInspeccionParaEvidencias: GetInspeccionParaEvidenciasQuery
  
    public val getInsumoPorQr: GetInsumoPorQrQuery
  
    public val getInsumosOperario: GetInsumosOperarioQuery
  
    public val getInventario: GetInventarioQuery
  
    public val getLimiteContacto: GetLimiteContactoQuery
  
    public val getMiComandaGuardada: GetMiComandaGuardadaQuery
  
    public val getMiConteoInventario: GetMiConteoInventarioQuery
  
    public val getMiPerfil: GetMiPerfilQuery
  
    public val getMisComandas: GetMisComandasQuery
  
    public val getMisComandasAsignadas: GetMisComandasAsignadasQuery
  
    public val getMisConteosInventario: GetMisConteosInventarioQuery
  
    public val getMisSalidasVehiculo: GetMisSalidasVehiculoQuery
  
    public val getMovimientoFlotaPorInspeccion: GetMovimientoFlotaPorInspeccionQuery
  
    public val getPanelProduccion: GetPanelProduccionQuery
  
    public val getReporteCuentas: GetReporteCuentasQuery
  
    public val getReporteServicios: GetReporteServiciosQuery
  
    public val getReporteVolumen: GetReporteVolumenQuery
  
    public val getRoles: GetRolesQuery
  
    public val getSeguimientoProduccion: GetSeguimientoProduccionQuery
  
    public val getSeguimientoPublicoPorNumero: GetSeguimientoPublicoPorNumeroQuery
  
    public val getSeguimientoPublicoPorQr: GetSeguimientoPublicoPorQrQuery
  
    public val getSolicitudContactoConfirmada: GetSolicitudContactoConfirmadaQuery
  
    public val getSolicitudesContacto: GetSolicitudesContactoQuery
  
    public val getStockInventario: GetStockInventarioQuery
  
    public val getUsuarios: GetUsuariosQuery
  
    public val getVehiculos: GetVehiculosQuery
  
    public val iniciarSalidaVehiculo: IniciarSalidaVehiculoMutation
  
    public val listarSolicitudesDerecho: ListarSolicitudesDerechoQuery
  
    public val prepararLimiteContacto: PrepararLimiteContactoMutation
  
    public val purgarLimitesContacto: PurgarLimitesContactoMutation
  
    public val reasignarOperarioEtapa: ReasignarOperarioEtapaMutation
  
    public val rechazarConteoInventario: RechazarConteoInventarioMutation
  
    public val recibirSolicitudDerecho: RecibirSolicitudDerechoMutation
  
    public val registrarConteoInventario: RegistrarConteoInventarioMutation
  
    public val registrarEntradaInventario: RegistrarEntradaInventarioMutation
  
    public val registrarEvidenciaGuardada: RegistrarEvidenciaGuardadaMutation
  
    public val registrarIncidenciaComanda: RegistrarIncidenciaComandaMutation
  
    public val registrarInspeccionAntes: RegistrarInspeccionAntesMutation
  
    public val registrarInspeccionDespues: RegistrarInspeccionDespuesMutation
  
    public val registrarRetornoFlota: RegistrarRetornoFlotaMutation
  
    public val registrarSalidaFlota: RegistrarSalidaFlotaMutation
  
    public val registrarSalidaInventario: RegistrarSalidaInventarioMutation
  
    public val registrarSolicitudContacto: RegistrarSolicitudContactoMutation
  
    public val registrarVehiculoFlota: RegistrarVehiculoFlotaMutation
  
    public val registrarse: RegistrarseMutation
  
    public val registrarseComoCliente: RegistrarseComoClienteMutation
  
    public val resolverMiIncidencia: ResolverMiIncidenciaMutation
  
    public val vincularMiComanda: VincularMiComandaMutation
  
    public val vincularMiFichaVerificada: VincularMiFichaVerificadaMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "example",
      location = "southamerica-west1",
      serviceId = "lavanderia-el-cobre",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):ExampleConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        ExampleConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, ExampleConnectorImpl>()

    
  }
}

public val ExampleConnector.Companion.instance:ExampleConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config
  ))

public fun ExampleConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):ExampleConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun ExampleConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):ExampleConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class ExampleConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : ExampleConnector {
  
    override val actualizarEstadoIncidencia by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ActualizarEstadoIncidenciaMutationImpl(this)
    }
  
    override val actualizarEstadoSolicitudContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ActualizarEstadoSolicitudContactoMutationImpl(this)
    }
  
    override val actualizarInsumo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ActualizarInsumoMutationImpl(this)
    }
  
    override val actualizarUsuario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ActualizarUsuarioMutationImpl(this)
    }
  
    override val actualizarVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ActualizarVehiculoMutationImpl(this)
    }
  
    override val adjuntarFotoInspeccionFlota by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AdjuntarFotoInspeccionFlotaMutationImpl(this)
    }
  
    override val agregarComandaDetalle by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AgregarComandaDetalleMutationImpl(this)
    }
  
    override val agregarComentarioComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AgregarComentarioComandaMutationImpl(this)
    }
  
    override val agregarFotoInspeccionVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AgregarFotoInspeccionVehiculoMutationImpl(this)
    }
  
    override val anularComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AnularComandaMutationImpl(this)
    }
  
    override val aprobarConteoInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AprobarConteoInventarioMutationImpl(this)
    }
  
    override val asociarFlujoComandaPendiente by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AsociarFlujoComandaPendienteMutationImpl(this)
    }
  
    override val autoAsignarComandaOperario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AutoAsignarComandaOperarioMutationImpl(this)
    }
  
    override val autorizarGestionPrivacidad by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AutorizarGestionPrivacidadQueryImpl(this)
    }
  
    override val completarEtapaComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CompletarEtapaComandaMutationImpl(this)
    }
  
    override val configurarEtapaProduccion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ConfigurarEtapaProduccionMutationImpl(this)
    }
  
    override val configurarLimitesEtapas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ConfigurarLimitesEtapasMutationImpl(this)
    }
  
    override val contarLimitesContactoVencidos by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ContarLimitesContactoVencidosQueryImpl(this)
    }
  
    override val crearAviso by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearAvisoMutationImpl(this)
    }
  
    override val crearClienteAdministrado by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearClienteAdministradoMutationImpl(this)
    }
  
    override val crearClienteComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearClienteComandaMutationImpl(this)
    }
  
    override val crearComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearComandaMutationImpl(this)
    }
  
    override val crearInsumo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearInsumoMutationImpl(this)
    }
  
    override val crearSalidaVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearSalidaVehiculoMutationImpl(this)
    }
  
    override val crearTipoPrenda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearTipoPrendaMutationImpl(this)
    }
  
    override val crearTipoServicio by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearTipoServicioMutationImpl(this)
    }
  
    override val crearUsuarioAdministrado by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearUsuarioAdministradoMutationImpl(this)
    }
  
    override val crearVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CrearVehiculoMutationImpl(this)
    }
  
    override val diagnosticoComandas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DiagnosticoComandasQueryImpl(this)
    }
  
    override val editarComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EditarComandaMutationImpl(this)
    }
  
    override val editarFichaCliente by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EditarFichaClienteMutationImpl(this)
    }
  
    override val editarVehiculoFlota by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EditarVehiculoFlotaMutationImpl(this)
    }
  
    override val eliminarDetallesComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EliminarDetallesComandaMutationImpl(this)
    }
  
    override val entregarComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EntregarComandaMutationImpl(this)
    }
  
    override val gestionarSolicitudDerecho by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GestionarSolicitudDerechoMutationImpl(this)
    }
  
    override val getAvisosAdministracion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAvisosAdministracionQueryImpl(this)
    }
  
    override val getAvisosParaEquipo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAvisosParaEquipoQueryImpl(this)
    }
  
    override val getCatalogosComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCatalogosComandaQueryImpl(this)
    }
  
    override val getComandaDetalle by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandaDetalleQueryImpl(this)
    }
  
    override val getComandaDetalleOperario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandaDetalleOperarioQueryImpl(this)
    }
  
    override val getComandaOperativaPorQr by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandaOperativaPorQrQueryImpl(this)
    }
  
    override val getComandaPorQr by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandaPorQrQueryImpl(this)
    }
  
    override val getComandasActivasCount by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandasActivasCountQueryImpl(this)
    }
  
    override val getComandasPaginadas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandasPaginadasQueryImpl(this)
    }
  
    override val getComandasParaAlertas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComandasParaAlertasQueryImpl(this)
    }
  
    override val getComprobanteDerecho by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetComprobanteDerechoQueryImpl(this)
    }
  
    override val getConteosInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetConteosInventarioQueryImpl(this)
    }
  
    override val getDetalleReporteCuentas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetDetalleReporteCuentasQueryImpl(this)
    }
  
    override val getDetalleReporteServicios by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetDetalleReporteServiciosQueryImpl(this)
    }
  
    override val getEtapasProduccion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetEtapasProduccionQueryImpl(this)
    }
  
    override val getFichasClientes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFichasClientesQueryImpl(this)
    }
  
    override val getFiltrosReportes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFiltrosReportesQueryImpl(this)
    }
  
    override val getFlotaVehiculos by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFlotaVehiculosQueryImpl(this)
    }
  
    override val getHistorialInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetHistorialInventarioQueryImpl(this)
    }
  
    override val getHistorialVehiculos by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetHistorialVehiculosQueryImpl(this)
    }
  
    override val getIncidencias by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetIncidenciasQueryImpl(this)
    }
  
    override val getInspeccionParaEvidencias by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetInspeccionParaEvidenciasQueryImpl(this)
    }
  
    override val getInsumoPorQr by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetInsumoPorQrQueryImpl(this)
    }
  
    override val getInsumosOperario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetInsumosOperarioQueryImpl(this)
    }
  
    override val getInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetInventarioQueryImpl(this)
    }
  
    override val getLimiteContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetLimiteContactoQueryImpl(this)
    }
  
    override val getMiComandaGuardada by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMiComandaGuardadaQueryImpl(this)
    }
  
    override val getMiConteoInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMiConteoInventarioQueryImpl(this)
    }
  
    override val getMiPerfil by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMiPerfilQueryImpl(this)
    }
  
    override val getMisComandas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMisComandasQueryImpl(this)
    }
  
    override val getMisComandasAsignadas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMisComandasAsignadasQueryImpl(this)
    }
  
    override val getMisConteosInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMisConteosInventarioQueryImpl(this)
    }
  
    override val getMisSalidasVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMisSalidasVehiculoQueryImpl(this)
    }
  
    override val getMovimientoFlotaPorInspeccion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMovimientoFlotaPorInspeccionQueryImpl(this)
    }
  
    override val getPanelProduccion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetPanelProduccionQueryImpl(this)
    }
  
    override val getReporteCuentas by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReporteCuentasQueryImpl(this)
    }
  
    override val getReporteServicios by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReporteServiciosQueryImpl(this)
    }
  
    override val getReporteVolumen by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReporteVolumenQueryImpl(this)
    }
  
    override val getRoles by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetRolesQueryImpl(this)
    }
  
    override val getSeguimientoProduccion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetSeguimientoProduccionQueryImpl(this)
    }
  
    override val getSeguimientoPublicoPorNumero by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetSeguimientoPublicoPorNumeroQueryImpl(this)
    }
  
    override val getSeguimientoPublicoPorQr by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetSeguimientoPublicoPorQrQueryImpl(this)
    }
  
    override val getSolicitudContactoConfirmada by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetSolicitudContactoConfirmadaQueryImpl(this)
    }
  
    override val getSolicitudesContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetSolicitudesContactoQueryImpl(this)
    }
  
    override val getStockInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetStockInventarioQueryImpl(this)
    }
  
    override val getUsuarios by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUsuariosQueryImpl(this)
    }
  
    override val getVehiculos by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetVehiculosQueryImpl(this)
    }
  
    override val iniciarSalidaVehiculo by lazy(LazyThreadSafetyMode.PUBLICATION) {
      IniciarSalidaVehiculoMutationImpl(this)
    }
  
    override val listarSolicitudesDerecho by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListarSolicitudesDerechoQueryImpl(this)
    }
  
    override val prepararLimiteContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      PrepararLimiteContactoMutationImpl(this)
    }
  
    override val purgarLimitesContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      PurgarLimitesContactoMutationImpl(this)
    }
  
    override val reasignarOperarioEtapa by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ReasignarOperarioEtapaMutationImpl(this)
    }
  
    override val rechazarConteoInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RechazarConteoInventarioMutationImpl(this)
    }
  
    override val recibirSolicitudDerecho by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RecibirSolicitudDerechoMutationImpl(this)
    }
  
    override val registrarConteoInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarConteoInventarioMutationImpl(this)
    }
  
    override val registrarEntradaInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarEntradaInventarioMutationImpl(this)
    }
  
    override val registrarEvidenciaGuardada by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarEvidenciaGuardadaMutationImpl(this)
    }
  
    override val registrarIncidenciaComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarIncidenciaComandaMutationImpl(this)
    }
  
    override val registrarInspeccionAntes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarInspeccionAntesMutationImpl(this)
    }
  
    override val registrarInspeccionDespues by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarInspeccionDespuesMutationImpl(this)
    }
  
    override val registrarRetornoFlota by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarRetornoFlotaMutationImpl(this)
    }
  
    override val registrarSalidaFlota by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarSalidaFlotaMutationImpl(this)
    }
  
    override val registrarSalidaInventario by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarSalidaInventarioMutationImpl(this)
    }
  
    override val registrarSolicitudContacto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarSolicitudContactoMutationImpl(this)
    }
  
    override val registrarVehiculoFlota by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarVehiculoFlotaMutationImpl(this)
    }
  
    override val registrarse by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarseMutationImpl(this)
    }
  
    override val registrarseComoCliente by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RegistrarseComoClienteMutationImpl(this)
    }
  
    override val resolverMiIncidencia by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ResolverMiIncidenciaMutationImpl(this)
    }
  
    override val vincularMiComanda by lazy(LazyThreadSafetyMode.PUBLICATION) {
      VincularMiComandaMutationImpl(this)
    }
  
    override val vincularMiFichaVerificada by lazy(LazyThreadSafetyMode.PUBLICATION) {
      VincularMiFichaVerificadaMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<ExampleConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<ExampleConnector, *, *>> =
    listOf(
      actualizarEstadoIncidencia,
        actualizarEstadoSolicitudContacto,
        actualizarInsumo,
        actualizarUsuario,
        actualizarVehiculo,
        adjuntarFotoInspeccionFlota,
        agregarComandaDetalle,
        agregarComentarioComanda,
        agregarFotoInspeccionVehiculo,
        anularComanda,
        aprobarConteoInventario,
        asociarFlujoComandaPendiente,
        autoAsignarComandaOperario,
        completarEtapaComanda,
        configurarEtapaProduccion,
        configurarLimitesEtapas,
        crearAviso,
        crearClienteAdministrado,
        crearClienteComanda,
        crearComanda,
        crearInsumo,
        crearSalidaVehiculo,
        crearTipoPrenda,
        crearTipoServicio,
        crearUsuarioAdministrado,
        crearVehiculo,
        editarComanda,
        editarFichaCliente,
        editarVehiculoFlota,
        eliminarDetallesComanda,
        entregarComanda,
        gestionarSolicitudDerecho,
        iniciarSalidaVehiculo,
        prepararLimiteContacto,
        purgarLimitesContacto,
        reasignarOperarioEtapa,
        rechazarConteoInventario,
        recibirSolicitudDerecho,
        registrarConteoInventario,
        registrarEntradaInventario,
        registrarEvidenciaGuardada,
        registrarIncidenciaComanda,
        registrarInspeccionAntes,
        registrarInspeccionDespues,
        registrarRetornoFlota,
        registrarSalidaFlota,
        registrarSalidaInventario,
        registrarSolicitudContacto,
        registrarVehiculoFlota,
        registrarse,
        registrarseComoCliente,
        resolverMiIncidencia,
        vincularMiComanda,
        vincularMiFichaVerificada,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<ExampleConnector, *, *>> =
    listOf(
      autorizarGestionPrivacidad,
        contarLimitesContactoVencidos,
        diagnosticoComandas,
        getAvisosAdministracion,
        getAvisosParaEquipo,
        getCatalogosComanda,
        getComandaDetalle,
        getComandaDetalleOperario,
        getComandaOperativaPorQr,
        getComandaPorQr,
        getComandasActivasCount,
        getComandasPaginadas,
        getComandasParaAlertas,
        getComprobanteDerecho,
        getConteosInventario,
        getDetalleReporteCuentas,
        getDetalleReporteServicios,
        getEtapasProduccion,
        getFichasClientes,
        getFiltrosReportes,
        getFlotaVehiculos,
        getHistorialInventario,
        getHistorialVehiculos,
        getIncidencias,
        getInspeccionParaEvidencias,
        getInsumoPorQr,
        getInsumosOperario,
        getInventario,
        getLimiteContacto,
        getMiComandaGuardada,
        getMiConteoInventario,
        getMiPerfil,
        getMisComandas,
        getMisComandasAsignadas,
        getMisConteosInventario,
        getMisSalidasVehiculo,
        getMovimientoFlotaPorInspeccion,
        getPanelProduccion,
        getReporteCuentas,
        getReporteServicios,
        getReporteVolumen,
        getRoles,
        getSeguimientoProduccion,
        getSeguimientoPublicoPorNumero,
        getSeguimientoPublicoPorQr,
        getSolicitudContactoConfirmada,
        getSolicitudesContacto,
        getStockInventario,
        getUsuarios,
        getVehiculos,
        listarSolicitudesDerecho,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    ExampleConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "ExampleConnectorImpl(dataConnect=$dataConnect)"
}



private open class ExampleConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: ExampleConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<ExampleConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: ExampleConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "ExampleConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class ExampleConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: ExampleConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<ExampleConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: ExampleConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "ExampleConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class ActualizarEstadoIncidenciaMutationImpl(
  connector: ExampleConnector
):
  ActualizarEstadoIncidenciaMutation,
  ExampleConnectorGeneratedMutationImpl<
      ActualizarEstadoIncidenciaMutation.Data,
      ActualizarEstadoIncidenciaMutation.Variables
  >(
    connector,
    ActualizarEstadoIncidenciaMutation.Companion.operationName,
    ActualizarEstadoIncidenciaMutation.Companion.dataDeserializer,
    ActualizarEstadoIncidenciaMutation.Companion.variablesSerializer,
  )


private class ActualizarEstadoSolicitudContactoMutationImpl(
  connector: ExampleConnector
):
  ActualizarEstadoSolicitudContactoMutation,
  ExampleConnectorGeneratedMutationImpl<
      ActualizarEstadoSolicitudContactoMutation.Data,
      ActualizarEstadoSolicitudContactoMutation.Variables
  >(
    connector,
    ActualizarEstadoSolicitudContactoMutation.Companion.operationName,
    ActualizarEstadoSolicitudContactoMutation.Companion.dataDeserializer,
    ActualizarEstadoSolicitudContactoMutation.Companion.variablesSerializer,
  )


private class ActualizarInsumoMutationImpl(
  connector: ExampleConnector
):
  ActualizarInsumoMutation,
  ExampleConnectorGeneratedMutationImpl<
      ActualizarInsumoMutation.Data,
      ActualizarInsumoMutation.Variables
  >(
    connector,
    ActualizarInsumoMutation.Companion.operationName,
    ActualizarInsumoMutation.Companion.dataDeserializer,
    ActualizarInsumoMutation.Companion.variablesSerializer,
  )


private class ActualizarUsuarioMutationImpl(
  connector: ExampleConnector
):
  ActualizarUsuarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      ActualizarUsuarioMutation.Data,
      ActualizarUsuarioMutation.Variables
  >(
    connector,
    ActualizarUsuarioMutation.Companion.operationName,
    ActualizarUsuarioMutation.Companion.dataDeserializer,
    ActualizarUsuarioMutation.Companion.variablesSerializer,
  )


private class ActualizarVehiculoMutationImpl(
  connector: ExampleConnector
):
  ActualizarVehiculoMutation,
  ExampleConnectorGeneratedMutationImpl<
      ActualizarVehiculoMutation.Data,
      ActualizarVehiculoMutation.Variables
  >(
    connector,
    ActualizarVehiculoMutation.Companion.operationName,
    ActualizarVehiculoMutation.Companion.dataDeserializer,
    ActualizarVehiculoMutation.Companion.variablesSerializer,
  )


private class AdjuntarFotoInspeccionFlotaMutationImpl(
  connector: ExampleConnector
):
  AdjuntarFotoInspeccionFlotaMutation,
  ExampleConnectorGeneratedMutationImpl<
      AdjuntarFotoInspeccionFlotaMutation.Data,
      AdjuntarFotoInspeccionFlotaMutation.Variables
  >(
    connector,
    AdjuntarFotoInspeccionFlotaMutation.Companion.operationName,
    AdjuntarFotoInspeccionFlotaMutation.Companion.dataDeserializer,
    AdjuntarFotoInspeccionFlotaMutation.Companion.variablesSerializer,
  )


private class AgregarComandaDetalleMutationImpl(
  connector: ExampleConnector
):
  AgregarComandaDetalleMutation,
  ExampleConnectorGeneratedMutationImpl<
      AgregarComandaDetalleMutation.Data,
      AgregarComandaDetalleMutation.Variables
  >(
    connector,
    AgregarComandaDetalleMutation.Companion.operationName,
    AgregarComandaDetalleMutation.Companion.dataDeserializer,
    AgregarComandaDetalleMutation.Companion.variablesSerializer,
  )


private class AgregarComentarioComandaMutationImpl(
  connector: ExampleConnector
):
  AgregarComentarioComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      AgregarComentarioComandaMutation.Data,
      AgregarComentarioComandaMutation.Variables
  >(
    connector,
    AgregarComentarioComandaMutation.Companion.operationName,
    AgregarComentarioComandaMutation.Companion.dataDeserializer,
    AgregarComentarioComandaMutation.Companion.variablesSerializer,
  )


private class AgregarFotoInspeccionVehiculoMutationImpl(
  connector: ExampleConnector
):
  AgregarFotoInspeccionVehiculoMutation,
  ExampleConnectorGeneratedMutationImpl<
      AgregarFotoInspeccionVehiculoMutation.Data,
      AgregarFotoInspeccionVehiculoMutation.Variables
  >(
    connector,
    AgregarFotoInspeccionVehiculoMutation.Companion.operationName,
    AgregarFotoInspeccionVehiculoMutation.Companion.dataDeserializer,
    AgregarFotoInspeccionVehiculoMutation.Companion.variablesSerializer,
  )


private class AnularComandaMutationImpl(
  connector: ExampleConnector
):
  AnularComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      AnularComandaMutation.Data,
      AnularComandaMutation.Variables
  >(
    connector,
    AnularComandaMutation.Companion.operationName,
    AnularComandaMutation.Companion.dataDeserializer,
    AnularComandaMutation.Companion.variablesSerializer,
  )


private class AprobarConteoInventarioMutationImpl(
  connector: ExampleConnector
):
  AprobarConteoInventarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      AprobarConteoInventarioMutation.Data,
      AprobarConteoInventarioMutation.Variables
  >(
    connector,
    AprobarConteoInventarioMutation.Companion.operationName,
    AprobarConteoInventarioMutation.Companion.dataDeserializer,
    AprobarConteoInventarioMutation.Companion.variablesSerializer,
  )


private class AsociarFlujoComandaPendienteMutationImpl(
  connector: ExampleConnector
):
  AsociarFlujoComandaPendienteMutation,
  ExampleConnectorGeneratedMutationImpl<
      AsociarFlujoComandaPendienteMutation.Data,
      AsociarFlujoComandaPendienteMutation.Variables
  >(
    connector,
    AsociarFlujoComandaPendienteMutation.Companion.operationName,
    AsociarFlujoComandaPendienteMutation.Companion.dataDeserializer,
    AsociarFlujoComandaPendienteMutation.Companion.variablesSerializer,
  )


private class AutoAsignarComandaOperarioMutationImpl(
  connector: ExampleConnector
):
  AutoAsignarComandaOperarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      AutoAsignarComandaOperarioMutation.Data,
      AutoAsignarComandaOperarioMutation.Variables
  >(
    connector,
    AutoAsignarComandaOperarioMutation.Companion.operationName,
    AutoAsignarComandaOperarioMutation.Companion.dataDeserializer,
    AutoAsignarComandaOperarioMutation.Companion.variablesSerializer,
  )


private class AutorizarGestionPrivacidadQueryImpl(
  connector: ExampleConnector
):
  AutorizarGestionPrivacidadQuery,
  ExampleConnectorGeneratedQueryImpl<
      Unit,
      Unit
  >(
    connector,
    AutorizarGestionPrivacidadQuery.Companion.operationName,
    AutorizarGestionPrivacidadQuery.Companion.dataDeserializer,
    AutorizarGestionPrivacidadQuery.Companion.variablesSerializer,
  )


private class CompletarEtapaComandaMutationImpl(
  connector: ExampleConnector
):
  CompletarEtapaComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      CompletarEtapaComandaMutation.Data,
      CompletarEtapaComandaMutation.Variables
  >(
    connector,
    CompletarEtapaComandaMutation.Companion.operationName,
    CompletarEtapaComandaMutation.Companion.dataDeserializer,
    CompletarEtapaComandaMutation.Companion.variablesSerializer,
  )


private class ConfigurarEtapaProduccionMutationImpl(
  connector: ExampleConnector
):
  ConfigurarEtapaProduccionMutation,
  ExampleConnectorGeneratedMutationImpl<
      ConfigurarEtapaProduccionMutation.Data,
      ConfigurarEtapaProduccionMutation.Variables
  >(
    connector,
    ConfigurarEtapaProduccionMutation.Companion.operationName,
    ConfigurarEtapaProduccionMutation.Companion.dataDeserializer,
    ConfigurarEtapaProduccionMutation.Companion.variablesSerializer,
  )


private class ConfigurarLimitesEtapasMutationImpl(
  connector: ExampleConnector
):
  ConfigurarLimitesEtapasMutation,
  ExampleConnectorGeneratedMutationImpl<
      ConfigurarLimitesEtapasMutation.Data,
      ConfigurarLimitesEtapasMutation.Variables
  >(
    connector,
    ConfigurarLimitesEtapasMutation.Companion.operationName,
    ConfigurarLimitesEtapasMutation.Companion.dataDeserializer,
    ConfigurarLimitesEtapasMutation.Companion.variablesSerializer,
  )


private class ContarLimitesContactoVencidosQueryImpl(
  connector: ExampleConnector
):
  ContarLimitesContactoVencidosQuery,
  ExampleConnectorGeneratedQueryImpl<
      ContarLimitesContactoVencidosQuery.Data,
      ContarLimitesContactoVencidosQuery.Variables
  >(
    connector,
    ContarLimitesContactoVencidosQuery.Companion.operationName,
    ContarLimitesContactoVencidosQuery.Companion.dataDeserializer,
    ContarLimitesContactoVencidosQuery.Companion.variablesSerializer,
  )


private class CrearAvisoMutationImpl(
  connector: ExampleConnector
):
  CrearAvisoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearAvisoMutation.Data,
      CrearAvisoMutation.Variables
  >(
    connector,
    CrearAvisoMutation.Companion.operationName,
    CrearAvisoMutation.Companion.dataDeserializer,
    CrearAvisoMutation.Companion.variablesSerializer,
  )


private class CrearClienteAdministradoMutationImpl(
  connector: ExampleConnector
):
  CrearClienteAdministradoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearClienteAdministradoMutation.Data,
      CrearClienteAdministradoMutation.Variables
  >(
    connector,
    CrearClienteAdministradoMutation.Companion.operationName,
    CrearClienteAdministradoMutation.Companion.dataDeserializer,
    CrearClienteAdministradoMutation.Companion.variablesSerializer,
  )


private class CrearClienteComandaMutationImpl(
  connector: ExampleConnector
):
  CrearClienteComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearClienteComandaMutation.Data,
      CrearClienteComandaMutation.Variables
  >(
    connector,
    CrearClienteComandaMutation.Companion.operationName,
    CrearClienteComandaMutation.Companion.dataDeserializer,
    CrearClienteComandaMutation.Companion.variablesSerializer,
  )


private class CrearComandaMutationImpl(
  connector: ExampleConnector
):
  CrearComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearComandaMutation.Data,
      CrearComandaMutation.Variables
  >(
    connector,
    CrearComandaMutation.Companion.operationName,
    CrearComandaMutation.Companion.dataDeserializer,
    CrearComandaMutation.Companion.variablesSerializer,
  )


private class CrearInsumoMutationImpl(
  connector: ExampleConnector
):
  CrearInsumoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearInsumoMutation.Data,
      CrearInsumoMutation.Variables
  >(
    connector,
    CrearInsumoMutation.Companion.operationName,
    CrearInsumoMutation.Companion.dataDeserializer,
    CrearInsumoMutation.Companion.variablesSerializer,
  )


private class CrearSalidaVehiculoMutationImpl(
  connector: ExampleConnector
):
  CrearSalidaVehiculoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearSalidaVehiculoMutation.Data,
      CrearSalidaVehiculoMutation.Variables
  >(
    connector,
    CrearSalidaVehiculoMutation.Companion.operationName,
    CrearSalidaVehiculoMutation.Companion.dataDeserializer,
    CrearSalidaVehiculoMutation.Companion.variablesSerializer,
  )


private class CrearTipoPrendaMutationImpl(
  connector: ExampleConnector
):
  CrearTipoPrendaMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearTipoPrendaMutation.Data,
      CrearTipoPrendaMutation.Variables
  >(
    connector,
    CrearTipoPrendaMutation.Companion.operationName,
    CrearTipoPrendaMutation.Companion.dataDeserializer,
    CrearTipoPrendaMutation.Companion.variablesSerializer,
  )


private class CrearTipoServicioMutationImpl(
  connector: ExampleConnector
):
  CrearTipoServicioMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearTipoServicioMutation.Data,
      CrearTipoServicioMutation.Variables
  >(
    connector,
    CrearTipoServicioMutation.Companion.operationName,
    CrearTipoServicioMutation.Companion.dataDeserializer,
    CrearTipoServicioMutation.Companion.variablesSerializer,
  )


private class CrearUsuarioAdministradoMutationImpl(
  connector: ExampleConnector
):
  CrearUsuarioAdministradoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearUsuarioAdministradoMutation.Data,
      CrearUsuarioAdministradoMutation.Variables
  >(
    connector,
    CrearUsuarioAdministradoMutation.Companion.operationName,
    CrearUsuarioAdministradoMutation.Companion.dataDeserializer,
    CrearUsuarioAdministradoMutation.Companion.variablesSerializer,
  )


private class CrearVehiculoMutationImpl(
  connector: ExampleConnector
):
  CrearVehiculoMutation,
  ExampleConnectorGeneratedMutationImpl<
      CrearVehiculoMutation.Data,
      CrearVehiculoMutation.Variables
  >(
    connector,
    CrearVehiculoMutation.Companion.operationName,
    CrearVehiculoMutation.Companion.dataDeserializer,
    CrearVehiculoMutation.Companion.variablesSerializer,
  )


private class DiagnosticoComandasQueryImpl(
  connector: ExampleConnector
):
  DiagnosticoComandasQuery,
  ExampleConnectorGeneratedQueryImpl<
      DiagnosticoComandasQuery.Data,
      Unit
  >(
    connector,
    DiagnosticoComandasQuery.Companion.operationName,
    DiagnosticoComandasQuery.Companion.dataDeserializer,
    DiagnosticoComandasQuery.Companion.variablesSerializer,
  )


private class EditarComandaMutationImpl(
  connector: ExampleConnector
):
  EditarComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      EditarComandaMutation.Data,
      EditarComandaMutation.Variables
  >(
    connector,
    EditarComandaMutation.Companion.operationName,
    EditarComandaMutation.Companion.dataDeserializer,
    EditarComandaMutation.Companion.variablesSerializer,
  )


private class EditarFichaClienteMutationImpl(
  connector: ExampleConnector
):
  EditarFichaClienteMutation,
  ExampleConnectorGeneratedMutationImpl<
      EditarFichaClienteMutation.Data,
      EditarFichaClienteMutation.Variables
  >(
    connector,
    EditarFichaClienteMutation.Companion.operationName,
    EditarFichaClienteMutation.Companion.dataDeserializer,
    EditarFichaClienteMutation.Companion.variablesSerializer,
  )


private class EditarVehiculoFlotaMutationImpl(
  connector: ExampleConnector
):
  EditarVehiculoFlotaMutation,
  ExampleConnectorGeneratedMutationImpl<
      EditarVehiculoFlotaMutation.Data,
      EditarVehiculoFlotaMutation.Variables
  >(
    connector,
    EditarVehiculoFlotaMutation.Companion.operationName,
    EditarVehiculoFlotaMutation.Companion.dataDeserializer,
    EditarVehiculoFlotaMutation.Companion.variablesSerializer,
  )


private class EliminarDetallesComandaMutationImpl(
  connector: ExampleConnector
):
  EliminarDetallesComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      EliminarDetallesComandaMutation.Data,
      EliminarDetallesComandaMutation.Variables
  >(
    connector,
    EliminarDetallesComandaMutation.Companion.operationName,
    EliminarDetallesComandaMutation.Companion.dataDeserializer,
    EliminarDetallesComandaMutation.Companion.variablesSerializer,
  )


private class EntregarComandaMutationImpl(
  connector: ExampleConnector
):
  EntregarComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      EntregarComandaMutation.Data,
      EntregarComandaMutation.Variables
  >(
    connector,
    EntregarComandaMutation.Companion.operationName,
    EntregarComandaMutation.Companion.dataDeserializer,
    EntregarComandaMutation.Companion.variablesSerializer,
  )


private class GestionarSolicitudDerechoMutationImpl(
  connector: ExampleConnector
):
  GestionarSolicitudDerechoMutation,
  ExampleConnectorGeneratedMutationImpl<
      GestionarSolicitudDerechoMutation.Data,
      GestionarSolicitudDerechoMutation.Variables
  >(
    connector,
    GestionarSolicitudDerechoMutation.Companion.operationName,
    GestionarSolicitudDerechoMutation.Companion.dataDeserializer,
    GestionarSolicitudDerechoMutation.Companion.variablesSerializer,
  )


private class GetAvisosAdministracionQueryImpl(
  connector: ExampleConnector
):
  GetAvisosAdministracionQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetAvisosAdministracionQuery.Data,
      GetAvisosAdministracionQuery.Variables
  >(
    connector,
    GetAvisosAdministracionQuery.Companion.operationName,
    GetAvisosAdministracionQuery.Companion.dataDeserializer,
    GetAvisosAdministracionQuery.Companion.variablesSerializer,
  )


private class GetAvisosParaEquipoQueryImpl(
  connector: ExampleConnector
):
  GetAvisosParaEquipoQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetAvisosParaEquipoQuery.Data,
      GetAvisosParaEquipoQuery.Variables
  >(
    connector,
    GetAvisosParaEquipoQuery.Companion.operationName,
    GetAvisosParaEquipoQuery.Companion.dataDeserializer,
    GetAvisosParaEquipoQuery.Companion.variablesSerializer,
  )


private class GetCatalogosComandaQueryImpl(
  connector: ExampleConnector
):
  GetCatalogosComandaQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetCatalogosComandaQuery.Data,
      Unit
  >(
    connector,
    GetCatalogosComandaQuery.Companion.operationName,
    GetCatalogosComandaQuery.Companion.dataDeserializer,
    GetCatalogosComandaQuery.Companion.variablesSerializer,
  )


private class GetComandaDetalleQueryImpl(
  connector: ExampleConnector
):
  GetComandaDetalleQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandaDetalleQuery.Data,
      GetComandaDetalleQuery.Variables
  >(
    connector,
    GetComandaDetalleQuery.Companion.operationName,
    GetComandaDetalleQuery.Companion.dataDeserializer,
    GetComandaDetalleQuery.Companion.variablesSerializer,
  )


private class GetComandaDetalleOperarioQueryImpl(
  connector: ExampleConnector
):
  GetComandaDetalleOperarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandaDetalleOperarioQuery.Data,
      GetComandaDetalleOperarioQuery.Variables
  >(
    connector,
    GetComandaDetalleOperarioQuery.Companion.operationName,
    GetComandaDetalleOperarioQuery.Companion.dataDeserializer,
    GetComandaDetalleOperarioQuery.Companion.variablesSerializer,
  )


private class GetComandaOperativaPorQrQueryImpl(
  connector: ExampleConnector
):
  GetComandaOperativaPorQrQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandaOperativaPorQrQuery.Data,
      GetComandaOperativaPorQrQuery.Variables
  >(
    connector,
    GetComandaOperativaPorQrQuery.Companion.operationName,
    GetComandaOperativaPorQrQuery.Companion.dataDeserializer,
    GetComandaOperativaPorQrQuery.Companion.variablesSerializer,
  )


private class GetComandaPorQrQueryImpl(
  connector: ExampleConnector
):
  GetComandaPorQrQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandaPorQrQuery.Data,
      GetComandaPorQrQuery.Variables
  >(
    connector,
    GetComandaPorQrQuery.Companion.operationName,
    GetComandaPorQrQuery.Companion.dataDeserializer,
    GetComandaPorQrQuery.Companion.variablesSerializer,
  )


private class GetComandasActivasCountQueryImpl(
  connector: ExampleConnector
):
  GetComandasActivasCountQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandasActivasCountQuery.Data,
      Unit
  >(
    connector,
    GetComandasActivasCountQuery.Companion.operationName,
    GetComandasActivasCountQuery.Companion.dataDeserializer,
    GetComandasActivasCountQuery.Companion.variablesSerializer,
  )


private class GetComandasPaginadasQueryImpl(
  connector: ExampleConnector
):
  GetComandasPaginadasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandasPaginadasQuery.Data,
      GetComandasPaginadasQuery.Variables
  >(
    connector,
    GetComandasPaginadasQuery.Companion.operationName,
    GetComandasPaginadasQuery.Companion.dataDeserializer,
    GetComandasPaginadasQuery.Companion.variablesSerializer,
  )


private class GetComandasParaAlertasQueryImpl(
  connector: ExampleConnector
):
  GetComandasParaAlertasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComandasParaAlertasQuery.Data,
      GetComandasParaAlertasQuery.Variables
  >(
    connector,
    GetComandasParaAlertasQuery.Companion.operationName,
    GetComandasParaAlertasQuery.Companion.dataDeserializer,
    GetComandasParaAlertasQuery.Companion.variablesSerializer,
  )


private class GetComprobanteDerechoQueryImpl(
  connector: ExampleConnector
):
  GetComprobanteDerechoQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetComprobanteDerechoQuery.Data,
      GetComprobanteDerechoQuery.Variables
  >(
    connector,
    GetComprobanteDerechoQuery.Companion.operationName,
    GetComprobanteDerechoQuery.Companion.dataDeserializer,
    GetComprobanteDerechoQuery.Companion.variablesSerializer,
  )


private class GetConteosInventarioQueryImpl(
  connector: ExampleConnector
):
  GetConteosInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetConteosInventarioQuery.Data,
      GetConteosInventarioQuery.Variables
  >(
    connector,
    GetConteosInventarioQuery.Companion.operationName,
    GetConteosInventarioQuery.Companion.dataDeserializer,
    GetConteosInventarioQuery.Companion.variablesSerializer,
  )


private class GetDetalleReporteCuentasQueryImpl(
  connector: ExampleConnector
):
  GetDetalleReporteCuentasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetDetalleReporteCuentasQuery.Data,
      GetDetalleReporteCuentasQuery.Variables
  >(
    connector,
    GetDetalleReporteCuentasQuery.Companion.operationName,
    GetDetalleReporteCuentasQuery.Companion.dataDeserializer,
    GetDetalleReporteCuentasQuery.Companion.variablesSerializer,
  )


private class GetDetalleReporteServiciosQueryImpl(
  connector: ExampleConnector
):
  GetDetalleReporteServiciosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetDetalleReporteServiciosQuery.Data,
      GetDetalleReporteServiciosQuery.Variables
  >(
    connector,
    GetDetalleReporteServiciosQuery.Companion.operationName,
    GetDetalleReporteServiciosQuery.Companion.dataDeserializer,
    GetDetalleReporteServiciosQuery.Companion.variablesSerializer,
  )


private class GetEtapasProduccionQueryImpl(
  connector: ExampleConnector
):
  GetEtapasProduccionQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetEtapasProduccionQuery.Data,
      Unit
  >(
    connector,
    GetEtapasProduccionQuery.Companion.operationName,
    GetEtapasProduccionQuery.Companion.dataDeserializer,
    GetEtapasProduccionQuery.Companion.variablesSerializer,
  )


private class GetFichasClientesQueryImpl(
  connector: ExampleConnector
):
  GetFichasClientesQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetFichasClientesQuery.Data,
      Unit
  >(
    connector,
    GetFichasClientesQuery.Companion.operationName,
    GetFichasClientesQuery.Companion.dataDeserializer,
    GetFichasClientesQuery.Companion.variablesSerializer,
  )


private class GetFiltrosReportesQueryImpl(
  connector: ExampleConnector
):
  GetFiltrosReportesQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetFiltrosReportesQuery.Data,
      GetFiltrosReportesQuery.Variables
  >(
    connector,
    GetFiltrosReportesQuery.Companion.operationName,
    GetFiltrosReportesQuery.Companion.dataDeserializer,
    GetFiltrosReportesQuery.Companion.variablesSerializer,
  )


private class GetFlotaVehiculosQueryImpl(
  connector: ExampleConnector
):
  GetFlotaVehiculosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetFlotaVehiculosQuery.Data,
      GetFlotaVehiculosQuery.Variables
  >(
    connector,
    GetFlotaVehiculosQuery.Companion.operationName,
    GetFlotaVehiculosQuery.Companion.dataDeserializer,
    GetFlotaVehiculosQuery.Companion.variablesSerializer,
  )


private class GetHistorialInventarioQueryImpl(
  connector: ExampleConnector
):
  GetHistorialInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetHistorialInventarioQuery.Data,
      GetHistorialInventarioQuery.Variables
  >(
    connector,
    GetHistorialInventarioQuery.Companion.operationName,
    GetHistorialInventarioQuery.Companion.dataDeserializer,
    GetHistorialInventarioQuery.Companion.variablesSerializer,
  )


private class GetHistorialVehiculosQueryImpl(
  connector: ExampleConnector
):
  GetHistorialVehiculosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetHistorialVehiculosQuery.Data,
      GetHistorialVehiculosQuery.Variables
  >(
    connector,
    GetHistorialVehiculosQuery.Companion.operationName,
    GetHistorialVehiculosQuery.Companion.dataDeserializer,
    GetHistorialVehiculosQuery.Companion.variablesSerializer,
  )


private class GetIncidenciasQueryImpl(
  connector: ExampleConnector
):
  GetIncidenciasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetIncidenciasQuery.Data,
      Unit
  >(
    connector,
    GetIncidenciasQuery.Companion.operationName,
    GetIncidenciasQuery.Companion.dataDeserializer,
    GetIncidenciasQuery.Companion.variablesSerializer,
  )


private class GetInspeccionParaEvidenciasQueryImpl(
  connector: ExampleConnector
):
  GetInspeccionParaEvidenciasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetInspeccionParaEvidenciasQuery.Data,
      GetInspeccionParaEvidenciasQuery.Variables
  >(
    connector,
    GetInspeccionParaEvidenciasQuery.Companion.operationName,
    GetInspeccionParaEvidenciasQuery.Companion.dataDeserializer,
    GetInspeccionParaEvidenciasQuery.Companion.variablesSerializer,
  )


private class GetInsumoPorQrQueryImpl(
  connector: ExampleConnector
):
  GetInsumoPorQrQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetInsumoPorQrQuery.Data,
      GetInsumoPorQrQuery.Variables
  >(
    connector,
    GetInsumoPorQrQuery.Companion.operationName,
    GetInsumoPorQrQuery.Companion.dataDeserializer,
    GetInsumoPorQrQuery.Companion.variablesSerializer,
  )


private class GetInsumosOperarioQueryImpl(
  connector: ExampleConnector
):
  GetInsumosOperarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetInsumosOperarioQuery.Data,
      GetInsumosOperarioQuery.Variables
  >(
    connector,
    GetInsumosOperarioQuery.Companion.operationName,
    GetInsumosOperarioQuery.Companion.dataDeserializer,
    GetInsumosOperarioQuery.Companion.variablesSerializer,
  )


private class GetInventarioQueryImpl(
  connector: ExampleConnector
):
  GetInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetInventarioQuery.Data,
      Unit
  >(
    connector,
    GetInventarioQuery.Companion.operationName,
    GetInventarioQuery.Companion.dataDeserializer,
    GetInventarioQuery.Companion.variablesSerializer,
  )


private class GetLimiteContactoQueryImpl(
  connector: ExampleConnector
):
  GetLimiteContactoQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetLimiteContactoQuery.Data,
      GetLimiteContactoQuery.Variables
  >(
    connector,
    GetLimiteContactoQuery.Companion.operationName,
    GetLimiteContactoQuery.Companion.dataDeserializer,
    GetLimiteContactoQuery.Companion.variablesSerializer,
  )


private class GetMiComandaGuardadaQueryImpl(
  connector: ExampleConnector
):
  GetMiComandaGuardadaQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMiComandaGuardadaQuery.Data,
      GetMiComandaGuardadaQuery.Variables
  >(
    connector,
    GetMiComandaGuardadaQuery.Companion.operationName,
    GetMiComandaGuardadaQuery.Companion.dataDeserializer,
    GetMiComandaGuardadaQuery.Companion.variablesSerializer,
  )


private class GetMiConteoInventarioQueryImpl(
  connector: ExampleConnector
):
  GetMiConteoInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMiConteoInventarioQuery.Data,
      GetMiConteoInventarioQuery.Variables
  >(
    connector,
    GetMiConteoInventarioQuery.Companion.operationName,
    GetMiConteoInventarioQuery.Companion.dataDeserializer,
    GetMiConteoInventarioQuery.Companion.variablesSerializer,
  )


private class GetMiPerfilQueryImpl(
  connector: ExampleConnector
):
  GetMiPerfilQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMiPerfilQuery.Data,
      Unit
  >(
    connector,
    GetMiPerfilQuery.Companion.operationName,
    GetMiPerfilQuery.Companion.dataDeserializer,
    GetMiPerfilQuery.Companion.variablesSerializer,
  )


private class GetMisComandasQueryImpl(
  connector: ExampleConnector
):
  GetMisComandasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMisComandasQuery.Data,
      GetMisComandasQuery.Variables
  >(
    connector,
    GetMisComandasQuery.Companion.operationName,
    GetMisComandasQuery.Companion.dataDeserializer,
    GetMisComandasQuery.Companion.variablesSerializer,
  )


private class GetMisComandasAsignadasQueryImpl(
  connector: ExampleConnector
):
  GetMisComandasAsignadasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMisComandasAsignadasQuery.Data,
      GetMisComandasAsignadasQuery.Variables
  >(
    connector,
    GetMisComandasAsignadasQuery.Companion.operationName,
    GetMisComandasAsignadasQuery.Companion.dataDeserializer,
    GetMisComandasAsignadasQuery.Companion.variablesSerializer,
  )


private class GetMisConteosInventarioQueryImpl(
  connector: ExampleConnector
):
  GetMisConteosInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMisConteosInventarioQuery.Data,
      GetMisConteosInventarioQuery.Variables
  >(
    connector,
    GetMisConteosInventarioQuery.Companion.operationName,
    GetMisConteosInventarioQuery.Companion.dataDeserializer,
    GetMisConteosInventarioQuery.Companion.variablesSerializer,
  )


private class GetMisSalidasVehiculoQueryImpl(
  connector: ExampleConnector
):
  GetMisSalidasVehiculoQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMisSalidasVehiculoQuery.Data,
      Unit
  >(
    connector,
    GetMisSalidasVehiculoQuery.Companion.operationName,
    GetMisSalidasVehiculoQuery.Companion.dataDeserializer,
    GetMisSalidasVehiculoQuery.Companion.variablesSerializer,
  )


private class GetMovimientoFlotaPorInspeccionQueryImpl(
  connector: ExampleConnector
):
  GetMovimientoFlotaPorInspeccionQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMovimientoFlotaPorInspeccionQuery.Data,
      GetMovimientoFlotaPorInspeccionQuery.Variables
  >(
    connector,
    GetMovimientoFlotaPorInspeccionQuery.Companion.operationName,
    GetMovimientoFlotaPorInspeccionQuery.Companion.dataDeserializer,
    GetMovimientoFlotaPorInspeccionQuery.Companion.variablesSerializer,
  )


private class GetPanelProduccionQueryImpl(
  connector: ExampleConnector
):
  GetPanelProduccionQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetPanelProduccionQuery.Data,
      GetPanelProduccionQuery.Variables
  >(
    connector,
    GetPanelProduccionQuery.Companion.operationName,
    GetPanelProduccionQuery.Companion.dataDeserializer,
    GetPanelProduccionQuery.Companion.variablesSerializer,
  )


private class GetReporteCuentasQueryImpl(
  connector: ExampleConnector
):
  GetReporteCuentasQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetReporteCuentasQuery.Data,
      GetReporteCuentasQuery.Variables
  >(
    connector,
    GetReporteCuentasQuery.Companion.operationName,
    GetReporteCuentasQuery.Companion.dataDeserializer,
    GetReporteCuentasQuery.Companion.variablesSerializer,
  )


private class GetReporteServiciosQueryImpl(
  connector: ExampleConnector
):
  GetReporteServiciosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetReporteServiciosQuery.Data,
      GetReporteServiciosQuery.Variables
  >(
    connector,
    GetReporteServiciosQuery.Companion.operationName,
    GetReporteServiciosQuery.Companion.dataDeserializer,
    GetReporteServiciosQuery.Companion.variablesSerializer,
  )


private class GetReporteVolumenQueryImpl(
  connector: ExampleConnector
):
  GetReporteVolumenQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetReporteVolumenQuery.Data,
      GetReporteVolumenQuery.Variables
  >(
    connector,
    GetReporteVolumenQuery.Companion.operationName,
    GetReporteVolumenQuery.Companion.dataDeserializer,
    GetReporteVolumenQuery.Companion.variablesSerializer,
  )


private class GetRolesQueryImpl(
  connector: ExampleConnector
):
  GetRolesQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetRolesQuery.Data,
      Unit
  >(
    connector,
    GetRolesQuery.Companion.operationName,
    GetRolesQuery.Companion.dataDeserializer,
    GetRolesQuery.Companion.variablesSerializer,
  )


private class GetSeguimientoProduccionQueryImpl(
  connector: ExampleConnector
):
  GetSeguimientoProduccionQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetSeguimientoProduccionQuery.Data,
      GetSeguimientoProduccionQuery.Variables
  >(
    connector,
    GetSeguimientoProduccionQuery.Companion.operationName,
    GetSeguimientoProduccionQuery.Companion.dataDeserializer,
    GetSeguimientoProduccionQuery.Companion.variablesSerializer,
  )


private class GetSeguimientoPublicoPorNumeroQueryImpl(
  connector: ExampleConnector
):
  GetSeguimientoPublicoPorNumeroQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetSeguimientoPublicoPorNumeroQuery.Data,
      GetSeguimientoPublicoPorNumeroQuery.Variables
  >(
    connector,
    GetSeguimientoPublicoPorNumeroQuery.Companion.operationName,
    GetSeguimientoPublicoPorNumeroQuery.Companion.dataDeserializer,
    GetSeguimientoPublicoPorNumeroQuery.Companion.variablesSerializer,
  )


private class GetSeguimientoPublicoPorQrQueryImpl(
  connector: ExampleConnector
):
  GetSeguimientoPublicoPorQrQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetSeguimientoPublicoPorQrQuery.Data,
      GetSeguimientoPublicoPorQrQuery.Variables
  >(
    connector,
    GetSeguimientoPublicoPorQrQuery.Companion.operationName,
    GetSeguimientoPublicoPorQrQuery.Companion.dataDeserializer,
    GetSeguimientoPublicoPorQrQuery.Companion.variablesSerializer,
  )


private class GetSolicitudContactoConfirmadaQueryImpl(
  connector: ExampleConnector
):
  GetSolicitudContactoConfirmadaQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetSolicitudContactoConfirmadaQuery.Data,
      GetSolicitudContactoConfirmadaQuery.Variables
  >(
    connector,
    GetSolicitudContactoConfirmadaQuery.Companion.operationName,
    GetSolicitudContactoConfirmadaQuery.Companion.dataDeserializer,
    GetSolicitudContactoConfirmadaQuery.Companion.variablesSerializer,
  )


private class GetSolicitudesContactoQueryImpl(
  connector: ExampleConnector
):
  GetSolicitudesContactoQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetSolicitudesContactoQuery.Data,
      GetSolicitudesContactoQuery.Variables
  >(
    connector,
    GetSolicitudesContactoQuery.Companion.operationName,
    GetSolicitudesContactoQuery.Companion.dataDeserializer,
    GetSolicitudesContactoQuery.Companion.variablesSerializer,
  )


private class GetStockInventarioQueryImpl(
  connector: ExampleConnector
):
  GetStockInventarioQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetStockInventarioQuery.Data,
      Unit
  >(
    connector,
    GetStockInventarioQuery.Companion.operationName,
    GetStockInventarioQuery.Companion.dataDeserializer,
    GetStockInventarioQuery.Companion.variablesSerializer,
  )


private class GetUsuariosQueryImpl(
  connector: ExampleConnector
):
  GetUsuariosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetUsuariosQuery.Data,
      Unit
  >(
    connector,
    GetUsuariosQuery.Companion.operationName,
    GetUsuariosQuery.Companion.dataDeserializer,
    GetUsuariosQuery.Companion.variablesSerializer,
  )


private class GetVehiculosQueryImpl(
  connector: ExampleConnector
):
  GetVehiculosQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetVehiculosQuery.Data,
      Unit
  >(
    connector,
    GetVehiculosQuery.Companion.operationName,
    GetVehiculosQuery.Companion.dataDeserializer,
    GetVehiculosQuery.Companion.variablesSerializer,
  )


private class IniciarSalidaVehiculoMutationImpl(
  connector: ExampleConnector
):
  IniciarSalidaVehiculoMutation,
  ExampleConnectorGeneratedMutationImpl<
      IniciarSalidaVehiculoMutation.Data,
      IniciarSalidaVehiculoMutation.Variables
  >(
    connector,
    IniciarSalidaVehiculoMutation.Companion.operationName,
    IniciarSalidaVehiculoMutation.Companion.dataDeserializer,
    IniciarSalidaVehiculoMutation.Companion.variablesSerializer,
  )


private class ListarSolicitudesDerechoQueryImpl(
  connector: ExampleConnector
):
  ListarSolicitudesDerechoQuery,
  ExampleConnectorGeneratedQueryImpl<
      ListarSolicitudesDerechoQuery.Data,
      ListarSolicitudesDerechoQuery.Variables
  >(
    connector,
    ListarSolicitudesDerechoQuery.Companion.operationName,
    ListarSolicitudesDerechoQuery.Companion.dataDeserializer,
    ListarSolicitudesDerechoQuery.Companion.variablesSerializer,
  )


private class PrepararLimiteContactoMutationImpl(
  connector: ExampleConnector
):
  PrepararLimiteContactoMutation,
  ExampleConnectorGeneratedMutationImpl<
      PrepararLimiteContactoMutation.Data,
      PrepararLimiteContactoMutation.Variables
  >(
    connector,
    PrepararLimiteContactoMutation.Companion.operationName,
    PrepararLimiteContactoMutation.Companion.dataDeserializer,
    PrepararLimiteContactoMutation.Companion.variablesSerializer,
  )


private class PurgarLimitesContactoMutationImpl(
  connector: ExampleConnector
):
  PurgarLimitesContactoMutation,
  ExampleConnectorGeneratedMutationImpl<
      PurgarLimitesContactoMutation.Data,
      PurgarLimitesContactoMutation.Variables
  >(
    connector,
    PurgarLimitesContactoMutation.Companion.operationName,
    PurgarLimitesContactoMutation.Companion.dataDeserializer,
    PurgarLimitesContactoMutation.Companion.variablesSerializer,
  )


private class ReasignarOperarioEtapaMutationImpl(
  connector: ExampleConnector
):
  ReasignarOperarioEtapaMutation,
  ExampleConnectorGeneratedMutationImpl<
      ReasignarOperarioEtapaMutation.Data,
      ReasignarOperarioEtapaMutation.Variables
  >(
    connector,
    ReasignarOperarioEtapaMutation.Companion.operationName,
    ReasignarOperarioEtapaMutation.Companion.dataDeserializer,
    ReasignarOperarioEtapaMutation.Companion.variablesSerializer,
  )


private class RechazarConteoInventarioMutationImpl(
  connector: ExampleConnector
):
  RechazarConteoInventarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      RechazarConteoInventarioMutation.Data,
      RechazarConteoInventarioMutation.Variables
  >(
    connector,
    RechazarConteoInventarioMutation.Companion.operationName,
    RechazarConteoInventarioMutation.Companion.dataDeserializer,
    RechazarConteoInventarioMutation.Companion.variablesSerializer,
  )


private class RecibirSolicitudDerechoMutationImpl(
  connector: ExampleConnector
):
  RecibirSolicitudDerechoMutation,
  ExampleConnectorGeneratedMutationImpl<
      RecibirSolicitudDerechoMutation.Data,
      RecibirSolicitudDerechoMutation.Variables
  >(
    connector,
    RecibirSolicitudDerechoMutation.Companion.operationName,
    RecibirSolicitudDerechoMutation.Companion.dataDeserializer,
    RecibirSolicitudDerechoMutation.Companion.variablesSerializer,
  )


private class RegistrarConteoInventarioMutationImpl(
  connector: ExampleConnector
):
  RegistrarConteoInventarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarConteoInventarioMutation.Data,
      RegistrarConteoInventarioMutation.Variables
  >(
    connector,
    RegistrarConteoInventarioMutation.Companion.operationName,
    RegistrarConteoInventarioMutation.Companion.dataDeserializer,
    RegistrarConteoInventarioMutation.Companion.variablesSerializer,
  )


private class RegistrarEntradaInventarioMutationImpl(
  connector: ExampleConnector
):
  RegistrarEntradaInventarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarEntradaInventarioMutation.Data,
      RegistrarEntradaInventarioMutation.Variables
  >(
    connector,
    RegistrarEntradaInventarioMutation.Companion.operationName,
    RegistrarEntradaInventarioMutation.Companion.dataDeserializer,
    RegistrarEntradaInventarioMutation.Companion.variablesSerializer,
  )


private class RegistrarEvidenciaGuardadaMutationImpl(
  connector: ExampleConnector
):
  RegistrarEvidenciaGuardadaMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarEvidenciaGuardadaMutation.Data,
      RegistrarEvidenciaGuardadaMutation.Variables
  >(
    connector,
    RegistrarEvidenciaGuardadaMutation.Companion.operationName,
    RegistrarEvidenciaGuardadaMutation.Companion.dataDeserializer,
    RegistrarEvidenciaGuardadaMutation.Companion.variablesSerializer,
  )


private class RegistrarIncidenciaComandaMutationImpl(
  connector: ExampleConnector
):
  RegistrarIncidenciaComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarIncidenciaComandaMutation.Data,
      RegistrarIncidenciaComandaMutation.Variables
  >(
    connector,
    RegistrarIncidenciaComandaMutation.Companion.operationName,
    RegistrarIncidenciaComandaMutation.Companion.dataDeserializer,
    RegistrarIncidenciaComandaMutation.Companion.variablesSerializer,
  )


private class RegistrarInspeccionAntesMutationImpl(
  connector: ExampleConnector
):
  RegistrarInspeccionAntesMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarInspeccionAntesMutation.Data,
      RegistrarInspeccionAntesMutation.Variables
  >(
    connector,
    RegistrarInspeccionAntesMutation.Companion.operationName,
    RegistrarInspeccionAntesMutation.Companion.dataDeserializer,
    RegistrarInspeccionAntesMutation.Companion.variablesSerializer,
  )


private class RegistrarInspeccionDespuesMutationImpl(
  connector: ExampleConnector
):
  RegistrarInspeccionDespuesMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarInspeccionDespuesMutation.Data,
      RegistrarInspeccionDespuesMutation.Variables
  >(
    connector,
    RegistrarInspeccionDespuesMutation.Companion.operationName,
    RegistrarInspeccionDespuesMutation.Companion.dataDeserializer,
    RegistrarInspeccionDespuesMutation.Companion.variablesSerializer,
  )


private class RegistrarRetornoFlotaMutationImpl(
  connector: ExampleConnector
):
  RegistrarRetornoFlotaMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarRetornoFlotaMutation.Data,
      RegistrarRetornoFlotaMutation.Variables
  >(
    connector,
    RegistrarRetornoFlotaMutation.Companion.operationName,
    RegistrarRetornoFlotaMutation.Companion.dataDeserializer,
    RegistrarRetornoFlotaMutation.Companion.variablesSerializer,
  )


private class RegistrarSalidaFlotaMutationImpl(
  connector: ExampleConnector
):
  RegistrarSalidaFlotaMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarSalidaFlotaMutation.Data,
      RegistrarSalidaFlotaMutation.Variables
  >(
    connector,
    RegistrarSalidaFlotaMutation.Companion.operationName,
    RegistrarSalidaFlotaMutation.Companion.dataDeserializer,
    RegistrarSalidaFlotaMutation.Companion.variablesSerializer,
  )


private class RegistrarSalidaInventarioMutationImpl(
  connector: ExampleConnector
):
  RegistrarSalidaInventarioMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarSalidaInventarioMutation.Data,
      RegistrarSalidaInventarioMutation.Variables
  >(
    connector,
    RegistrarSalidaInventarioMutation.Companion.operationName,
    RegistrarSalidaInventarioMutation.Companion.dataDeserializer,
    RegistrarSalidaInventarioMutation.Companion.variablesSerializer,
  )


private class RegistrarSolicitudContactoMutationImpl(
  connector: ExampleConnector
):
  RegistrarSolicitudContactoMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarSolicitudContactoMutation.Data,
      RegistrarSolicitudContactoMutation.Variables
  >(
    connector,
    RegistrarSolicitudContactoMutation.Companion.operationName,
    RegistrarSolicitudContactoMutation.Companion.dataDeserializer,
    RegistrarSolicitudContactoMutation.Companion.variablesSerializer,
  )


private class RegistrarVehiculoFlotaMutationImpl(
  connector: ExampleConnector
):
  RegistrarVehiculoFlotaMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarVehiculoFlotaMutation.Data,
      RegistrarVehiculoFlotaMutation.Variables
  >(
    connector,
    RegistrarVehiculoFlotaMutation.Companion.operationName,
    RegistrarVehiculoFlotaMutation.Companion.dataDeserializer,
    RegistrarVehiculoFlotaMutation.Companion.variablesSerializer,
  )


private class RegistrarseMutationImpl(
  connector: ExampleConnector
):
  RegistrarseMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarseMutation.Data,
      RegistrarseMutation.Variables
  >(
    connector,
    RegistrarseMutation.Companion.operationName,
    RegistrarseMutation.Companion.dataDeserializer,
    RegistrarseMutation.Companion.variablesSerializer,
  )


private class RegistrarseComoClienteMutationImpl(
  connector: ExampleConnector
):
  RegistrarseComoClienteMutation,
  ExampleConnectorGeneratedMutationImpl<
      RegistrarseComoClienteMutation.Data,
      RegistrarseComoClienteMutation.Variables
  >(
    connector,
    RegistrarseComoClienteMutation.Companion.operationName,
    RegistrarseComoClienteMutation.Companion.dataDeserializer,
    RegistrarseComoClienteMutation.Companion.variablesSerializer,
  )


private class ResolverMiIncidenciaMutationImpl(
  connector: ExampleConnector
):
  ResolverMiIncidenciaMutation,
  ExampleConnectorGeneratedMutationImpl<
      ResolverMiIncidenciaMutation.Data,
      ResolverMiIncidenciaMutation.Variables
  >(
    connector,
    ResolverMiIncidenciaMutation.Companion.operationName,
    ResolverMiIncidenciaMutation.Companion.dataDeserializer,
    ResolverMiIncidenciaMutation.Companion.variablesSerializer,
  )


private class VincularMiComandaMutationImpl(
  connector: ExampleConnector
):
  VincularMiComandaMutation,
  ExampleConnectorGeneratedMutationImpl<
      VincularMiComandaMutation.Data,
      VincularMiComandaMutation.Variables
  >(
    connector,
    VincularMiComandaMutation.Companion.operationName,
    VincularMiComandaMutation.Companion.dataDeserializer,
    VincularMiComandaMutation.Companion.variablesSerializer,
  )


private class VincularMiFichaVerificadaMutationImpl(
  connector: ExampleConnector
):
  VincularMiFichaVerificadaMutation,
  ExampleConnectorGeneratedMutationImpl<
      VincularMiFichaVerificadaMutation.Data,
      Unit
  >(
    connector,
    VincularMiFichaVerificadaMutation.Companion.operationName,
    VincularMiFichaVerificadaMutation.Companion.dataDeserializer,
    VincularMiFichaVerificadaMutation.Companion.variablesSerializer,
  )


