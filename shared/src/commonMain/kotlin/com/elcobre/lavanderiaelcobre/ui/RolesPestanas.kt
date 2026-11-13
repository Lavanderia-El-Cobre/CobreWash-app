package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.crearCuentaRepository
import com.elcobre.lavanderiaelcobre.ui.gestion.AvisosEquipoViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ClientesViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ComandasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.CotizacionesViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.EtapasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.GestionViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.IncidenciasViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.PanelViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ProduccionViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.RepartoViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ReportesViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.StockViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.UsuariosViewModel
import com.elcobre.lavanderiaelcobre.ui.gestion.ahoraMs
import com.elcobre.lavanderiaelcobre.ui.screens.admin.EtapasScreen
import com.elcobre.lavanderiaelcobre.ui.screens.admin.InicioAdminScreen
import com.elcobre.lavanderiaelcobre.ui.screens.admin.ReportesScreen
import com.elcobre.lavanderiaelcobre.ui.screens.admin.StockScreen
import com.elcobre.lavanderiaelcobre.ui.screens.admin.UsuariosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.avisos.AvisosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.avisos.PublicarAvisoSheet
import com.elcobre.lavanderiaelcobre.ui.screens.clientes.ClientesScreen
import com.elcobre.lavanderiaelcobre.ui.screens.comandas.ComandasScreen
import com.elcobre.lavanderiaelcobre.ui.screens.configuracion.ConfiguracionScreen
import com.elcobre.lavanderiaelcobre.ui.screens.gestion.CotizacionesScreen
import com.elcobre.lavanderiaelcobre.ui.screens.gestion.IncidenciasScreen
import com.elcobre.lavanderiaelcobre.ui.screens.mas.MasScreen
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.PermisosProduccion
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ProduccionScreen
import com.elcobre.lavanderiaelcobre.ui.screens.reparto.RepartoScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val REFRESCO_MS = 30_000L

/**
 * Refresca [accion] al mostrarse y luego cada 30 s mientras la app está visible, como
 * el refresco silencioso de la web. Se reinicia al cambiar [clave] (p. ej. de pestaña).
 */
@Composable
internal fun RefrescoPeriodico(clave: Any?, accion: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(clave, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                accion()
                delay(REFRESCO_MS)
            }
        }
    }
}

/** Hora actual que avanza cada minuto, para los atrasos de producción. */
@Composable
internal fun rememberAhora(): Long {
    val ahora by produceState(ahoraMs()) {
        while (true) {
            delay(60_000)
            value = ahoraMs()
        }
    }
    return ahora
}

/** Reinicia los ViewModels de gestión cuando cambia la sesión: nada de una cuenta se ve en otra. */
@Composable
private fun ReiniciarAlCambiarSesion(vm: CobreViewModel, modelos: List<GestionViewModel>) {
    val sesion = vm.sesion ?: return
    remember(sesion) { modelos.forEach { it.vincularSesion(sesion) } }
}

/** Ajustes compartidos por todos los roles, con cambio de contraseña. */
@Composable
internal fun Ajustes(vm: CobreViewModel, nombre: String, rol: String, onCerrarSesion: () -> Unit) {
    val cuenta = remember { crearCuentaRepository() }
    val scope = rememberCoroutineScope()
    ConfiguracionScreen(
        nombre = nombre,
        rol = rol,
        modoTema = vm.modoTema,
        onCambiarTema = { vm.cambiarModoTema(it) },
        onCerrarSesion = onCerrarSesion,
        reducirTransparencia = vm.reducirTransparencia,
        onCambiarReducirTransparencia = { vm.cambiarReducirTransparencia(it) },
        onCambiarContrasena = { actual, nueva, resultado ->
            scope.launch {
                try {
                    cuenta.cambiarContrasena(actual, nueva)
                    resultado(null)
                } catch (error: OperacionComandaException) {
                    resultado(error.message ?: "No se pudo cambiar la contraseña.")
                }
            }
        },
    )
}

/** Recepción: comandas, producción, clientes, avisos de su equipo y "Más" (incidencias, cotizaciones, ajustes). */
@Composable
internal fun RecepcionPestanas(tab: Pantalla, vm: CobreViewModel) {
    val m = ModelosRecepcion(
        comandas = viewModel { ComandasViewModel() },
        produccion = viewModel { ProduccionViewModel() },
        clientes = viewModel { ClientesViewModel() },
        avisos = viewModel(key = "avisos-recepcion") { AvisosEquipoViewModel(rolEquipo = "recepcionista") },
        incidencias = viewModel { IncidenciasViewModel() },
        cotizaciones = viewModel { CotizacionesViewModel() },
    )
    ReiniciarAlCambiarSesion(vm, m.todos)
    val nombre = remember { vm.sesion?.nombre ?: "Recepción" }
    val rol = remember { vm.sesion?.rolVisible ?: "Recepción" }
    val ahora = rememberAhora()

    RefrescoPeriodico(Unit) { m.avisos.refrescar() }
    RefrescoPeriodico(tab to vm.seccionMas) { m.refrescar(tab, vm.seccionMas) }
    LaunchedEffect(tab, m.avisos.avisos) { if (tab == Pantalla.Avisos) m.avisos.marcarLeidos() }

    PestanasScaffold(
        items = NavRecepcion,
        tabActual = tab,
        onSeleccionarTab = { vm.navegar(it) },
        onEscanearQR = null,
        badges = mapOf(Pantalla.Avisos to m.avisos.noLeidos, Pantalla.Mas to m.incidencias.abiertas),
    ) {
        PestanasAnimadas(tab = tab, items = NavRecepcion) { pestana ->
            when (pestana) {
                Pantalla.Comandas -> ComandasScreen(m.comandas)
                Pantalla.Produccion -> ProduccionScreen(m.produccion, PermisosProduccion(asociarFlujo = true), ahora)
                Pantalla.Clientes -> ClientesScreen(m.clientes)
                Pantalla.Avisos -> AvisosEquipo(m.avisos, "Avisos", "Comunicados de administración para recepción.")
                Pantalla.Mas -> MasScreen(
                    secciones = MasRecepcion,
                    seccion = vm.seccionMas,
                    onAbrir = vm::abrirSeccion,
                    insignias = mapOf(SeccionMas.INCIDENCIAS to m.incidencias.abiertas),
                ) { seccion ->
                    when (seccion) {
                        SeccionMas.INCIDENCIAS -> IncidenciasScreen(m.incidencias)
                        SeccionMas.COTIZACIONES -> CotizacionesScreen(m.cotizaciones)
                        else -> Ajustes(vm, nombre, rol, onCerrarSesion = { vm.cerrarSesion() })
                    }
                }
                else -> Unit
            }
        }
    }
}

/** ViewModels de recepción, uno por pestaña o sección. */
private class ModelosRecepcion(
    val comandas: ComandasViewModel,
    val produccion: ProduccionViewModel,
    val clientes: ClientesViewModel,
    val avisos: AvisosEquipoViewModel,
    val incidencias: IncidenciasViewModel,
    val cotizaciones: CotizacionesViewModel,
) {
    val todos: List<GestionViewModel> get() = listOf(comandas, produccion, clientes, avisos, incidencias, cotizaciones)

    /** Refresca lo visible; las incidencias siempre, porque alimentan la insignia de "Más". */
    fun refrescar(tab: Pantalla, seccion: SeccionMas?) {
        when (tab) {
            Pantalla.Comandas -> comandas.refrescar()
            Pantalla.Produccion -> produccion.refrescar()
            Pantalla.Clientes -> clientes.refrescar()
            else -> Unit
        }
        incidencias.refrescar()
        if (tab == Pantalla.Mas && seccion == SeccionMas.COTIZACIONES) cotizaciones.refrescar()
    }
}

/** Avisos de un equipo (o de administración, con botón de publicar). */
@Composable
private fun AvisosEquipo(avisos: AvisosEquipoViewModel, titulo: String, subtitulo: String, onNuevo: (() -> Unit)? = null) {
    AvisosScreen(
        avisos = avisos.avisos,
        cargando = avisos.cargando,
        error = avisos.error,
        hayMas = avisos.hayMas,
        onActualizar = { avisos.refrescar() },
        onMas = { avisos.refrescar(mas = true) },
        titulo = titulo,
        subtitulo = subtitulo,
        onNuevo = onNuevo,
    )
}

/** ViewModels de administración agrupados para no repetir la lista en cada llamada. */
@Suppress("LongParameterList") // Un ViewModel por sección de administración.
private class ModelosAdmin(
    val panel: PanelViewModel,
    val comandas: ComandasViewModel,
    val stock: StockViewModel,
    val produccion: ProduccionViewModel,
    val incidencias: IncidenciasViewModel,
    val clientes: ClientesViewModel,
    val cotizaciones: CotizacionesViewModel,
    val avisos: AvisosEquipoViewModel,
    val usuarios: UsuariosViewModel,
    val reportes: ReportesViewModel,
    val etapas: EtapasViewModel,
) {
    val todos: List<GestionViewModel>
        get() = listOf(panel, comandas, stock, produccion, incidencias, clientes, cotizaciones, avisos, usuarios, reportes, etapas)

    fun refrescarSeccion(seccion: SeccionMas?) {
        when (seccion) {
            SeccionMas.PRODUCCION, null -> produccion.refrescar()
            SeccionMas.INCIDENCIAS -> incidencias.refrescar()
            SeccionMas.CLIENTES -> clientes.refrescar()
            SeccionMas.COTIZACIONES -> cotizaciones.refrescar()
            SeccionMas.AVISOS -> avisos.refrescar()
            SeccionMas.USUARIOS -> usuarios.refrescar()
            SeccionMas.REPORTES -> reportes.refrescar()
            SeccionMas.ETAPAS -> etapas.refrescar()
            SeccionMas.AJUSTES -> Unit
        }
    }
}

@Composable
private fun rememberModelosAdmin(): ModelosAdmin = ModelosAdmin(
    panel = viewModel { PanelViewModel() },
    comandas = viewModel(key = "comandas-admin") { ComandasViewModel() },
    stock = viewModel { StockViewModel() },
    produccion = viewModel(key = "produccion-admin") { ProduccionViewModel() },
    incidencias = viewModel(key = "incidencias-admin") { IncidenciasViewModel() },
    clientes = viewModel(key = "clientes-admin") { ClientesViewModel() },
    cotizaciones = viewModel(key = "cotizaciones-admin") { CotizacionesViewModel() },
    avisos = viewModel(key = "avisos-admin") { AvisosEquipoViewModel(rolEquipo = null) },
    usuarios = viewModel { UsuariosViewModel() },
    reportes = viewModel { ReportesViewModel() },
    etapas = viewModel { EtapasViewModel() },
)

/** Administración: inicio, comandas, inventario, flota y "Más" con el resto de la intranet. */
@Composable
internal fun AdminPestanas(tab: Pantalla, vm: CobreViewModel, flota: @Composable () -> Unit, badgeFlota: Int, onCerrarSesion: () -> Unit) {
    val m = rememberModelosAdmin()
    ReiniciarAlCambiarSesion(vm, m.todos)
    val nombre = remember { vm.sesion?.nombre ?: "Administrador" }
    val rol = remember { vm.sesion?.rolVisible ?: "Administrador" }
    val ahora = rememberAhora()
    var publicando by remember { mutableStateOf(false) }

    RefrescoPeriodico(Unit) { m.stock.refrescar() }
    RefrescoPeriodico(tab to vm.seccionMas) {
        when (tab) {
            Pantalla.Inicio -> m.panel.refrescar()
            Pantalla.Comandas -> m.comandas.refrescar()
            Pantalla.Mas -> m.refrescarSeccion(vm.seccionMas)
            else -> Unit
        }
    }

    PestanasScaffold(
        items = NavAdmin,
        tabActual = tab,
        onSeleccionarTab = { vm.navegar(it) },
        onEscanearQR = null,
        badges = mapOf(Pantalla.Vehiculos to badgeFlota, Pantalla.Stock to m.stock.conteosPendientes.size),
    ) {
        PestanasAnimadas(tab = tab, items = NavAdmin) { pestana ->
            when (pestana) {
                Pantalla.Inicio -> InicioAdminScreen(m.panel, nombre, ahora, onAbrirComanda = { id ->
                    vm.navegar(Pantalla.Comandas)
                    m.comandas.abrirDetalle(id)
                })
                Pantalla.Comandas -> ComandasScreen(m.comandas)
                Pantalla.Stock -> StockScreen(m.stock)
                Pantalla.Vehiculos -> flota()
                Pantalla.Mas -> MasScreen(MasAdmin, vm.seccionMas, vm::abrirSeccion) { seccion ->
                    SeccionAdmin(seccion, m, vm, nombre, rol, ahora, onPublicar = { publicando = true }, onCerrarSesion = onCerrarSesion)
                }
                else -> Unit
            }
        }
    }
    if (publicando) PublicarAvisoSheet(m.avisos, onCerrar = { publicando = false })
}

@Composable
private fun SeccionAdmin(
    seccion: SeccionMas,
    m: ModelosAdmin,
    vm: CobreViewModel,
    nombre: String,
    rol: String,
    ahora: Long,
    onPublicar: () -> Unit,
    onCerrarSesion: () -> Unit,
) {
    when (seccion) {
        SeccionMas.PRODUCCION -> ProduccionScreen(
            m.produccion,
            PermisosProduccion(completar = true, reasignar = true, asociarFlujo = true),
            ahora,
        )
        SeccionMas.INCIDENCIAS -> IncidenciasScreen(m.incidencias)
        SeccionMas.CLIENTES -> ClientesScreen(m.clientes)
        SeccionMas.COTIZACIONES -> CotizacionesScreen(m.cotizaciones)
        SeccionMas.AVISOS -> AvisosEquipo(m.avisos, "Comunicación", "Avisos publicados para el equipo.", onNuevo = onPublicar)
        SeccionMas.USUARIOS -> UsuariosScreen(m.usuarios)
        SeccionMas.REPORTES -> ReportesScreen(m.reportes)
        SeccionMas.ETAPAS -> EtapasScreen(m.etapas)
        SeccionMas.AJUSTES -> Ajustes(vm, nombre, rol, onCerrarSesion)
    }
}

/** Repartidor: sus recorridos y ajustes. */
@Composable
internal fun RepartidorPestanas(tab: Pantalla, vm: CobreViewModel) {
    val reparto: RepartoViewModel = viewModel { RepartoViewModel() }
    ReiniciarAlCambiarSesion(vm, listOf(reparto))
    val nombre = remember { vm.sesion?.nombre ?: "Repartidor" }
    val rol = remember { vm.sesion?.rolVisible ?: "Repartidor" }
    RefrescoPeriodico(tab) { if (tab == Pantalla.Reparto) reparto.refrescar() }
    PestanasScaffold(
        items = NavRepartidor,
        tabActual = tab,
        onSeleccionarTab = { vm.navegar(it) },
        onEscanearQR = null,
        badges = mapOf(Pantalla.Reparto to reparto.programadas.size),
    ) {
        PestanasAnimadas(tab = tab, items = NavRepartidor) { pestana ->
            when (pestana) {
                Pantalla.Reparto -> RepartoScreen(reparto, nombre)
                Pantalla.Configuracion -> Ajustes(vm, nombre, rol, onCerrarSesion = {
                    reparto.reiniciar()
                    vm.cerrarSesion()
                })
                else -> Unit
            }
        }
    }
}
