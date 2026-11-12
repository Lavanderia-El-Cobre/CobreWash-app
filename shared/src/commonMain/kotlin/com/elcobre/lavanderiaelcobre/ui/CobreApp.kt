package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.elcobre.lavanderiaelcobre.data.FirebaseVehiculosRepository
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.FiltroFlota
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.ui.components.AdminSidebar
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackHandler
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.WebSidebar
import com.elcobre.lavanderiaelcobre.ui.components.WebTopbar
import com.elcobre.lavanderiaelcobre.ui.screens.avisos.AvisosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.configuracion.ConfiguracionScreen
import com.elcobre.lavanderiaelcobre.ui.screens.dashboard.DashboardScreen
import com.elcobre.lavanderiaelcobre.ui.screens.detalle.DetalleScreen
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.EscanerScreen
import com.elcobre.lavanderiaelcobre.ui.screens.insumos.InsumosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.login.LoginScreen
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.VehiculosScreen
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.components.GlassSurface
import com.elcobre.lavanderiaelcobre.ui.components.LocalGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.LocalRootGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.glassBackdropSource
import com.elcobre.lavanderiaelcobre.ui.components.rememberGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Raíz de la aplicación: aplica el tema de marca y gestiona la navegación con
 * transiciones animadas. El estado vive en [CobreViewModel]; las pantallas son
 * sin estado de dominio, reciben datos y delegan acciones al ViewModel.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CobreApp() {
    val vm: CobreViewModel = viewModel { CobreViewModel() }
    ActualizarAvisosVisibles(vm)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(vm.sesion, lifecycle) {
        if (vm.operario != null) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                kotlinx.coroutines.delay(30_000)
                vm.sincronizarTurnoAutomatico()
                vm.refrescarComandas()
            }
        }
    }
    val temaOscuro = when (vm.modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SISTEMA -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    CobreBackHandler(enabled = vm.puedeVolver) {
        vm.volver()
    }

    CobreTheme(darkTheme = temaOscuro, reduceTransparency = vm.reducirTransparencia) {
        val pantalla = vm.pantalla
        // Fondo que desenfoca cualquier superficie de vidrio de la app. Es hermano (no
        // ancestro) de las pantallas, así ningún vidrio se desenfoca a sí mismo.
        val fondoVidrio = rememberGlassBackdrop()

        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo persistente detrás de toda la navegación: no se recrea en cada
            // cambio de pantalla ni se desliza junto con la transición.
            CobreBackdrop(modifier = Modifier.glassBackdropSource(fondoVidrio))

            CompositionLocalProvider(
                LocalRootGlassBackdrop provides fondoVidrio,
                LocalGlassBackdrop provides fondoVidrio,
            ) {

            // Permite que el icono de etapa "vuele" de la tarjeta del Dashboard a la cabecera del Detalle.
            SharedTransitionLayout {
                val sharedScope = this
                AnimatedContent(
                    targetState = pantalla,
                    transitionSpec = {
                        val isTopLevel = fun(p: Pantalla): Boolean = p in listOf(
                            Pantalla.Dashboard, Pantalla.Avisos, Pantalla.Insumos, Pantalla.Configuracion, Pantalla.Vehiculos
                        )

                        if (isTopLevel(initialState) && isTopLevel(targetState)) {
                            // Entre tabs principales: crossfade suave sin slide.
                            // Esto hace que la Sidebar y la Topbar parezcan fijas y solo el contenido cambie.
                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                        } else {
                            // Navegación profunda (ej. a Detalles, Escáner, Login): slide completo.
                            val haciaAdelante = orden(targetState) >= orden(initialState)
                            val dir = if (haciaAdelante) 1 else -1
                            (slideInHorizontally(tween(300)) { w -> dir * w } + fadeIn(tween(300))) togetherWith
                                (slideOutHorizontally(tween(300)) { w -> -dir * w } + fadeOut(tween(300)))
                        }
                    },
                    label = "navegacion",
                ) { destino ->
                    DestinoContent(
                        destino = destino,
                        vm = vm,
                        sharedScope = sharedScope,
                        animatedScope = this,
                    )
                }
            }
            }
            vm.errorOperacion?.let { mensaje ->
                Text(
                    mensaje,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            if (vm.cargando && vm.pedidos.isEmpty()) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics { contentDescription = "Cargando datos de planta" },
                    color = Brand400,
                )
            }
        }
    }
}

@Composable
private fun ActualizarAvisosVisibles(vm: CobreViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(vm.sesion, lifecycle) {
        if (vm.operario != null) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                vm.refrescarAvisos()
                vm.inventario.refrescar()
                kotlinx.coroutines.delay(30_000)
            }
        }
    }
}

/** Contenido de cada [Pantalla], despachado desde [CobreApp]; una función por destino para mantener esto legible. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DestinoContent(
    destino: Pantalla,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    when (destino) {
        Pantalla.Login -> LoginDestino(vm)
        Pantalla.Dashboard -> DashboardDestino(vm, sharedScope, animatedScope)
        Pantalla.Avisos -> OperarioScaffold(destino, vm) { isTablet ->
            AvisosScreen(
                avisos = vm.avisos, applySystemBarsInsets = !isTablet,
                cargando = vm.cargandoAvisos, error = vm.errorAvisos,
                hayMas = vm.avisos.size < vm.totalAvisos,
                onActualizar = { vm.refrescarAvisos() }, onMas = { vm.refrescarAvisos(mas = true) },
            )
        }
        Pantalla.Configuracion -> ConfiguracionDestino(destino, vm)
        Pantalla.Escaner -> EscanerDestino(vm)
        Pantalla.Insumos -> InsumosDestino(destino, vm)
        Pantalla.Vehiculos -> VehiculosDestino(vm)
        is Pantalla.Detalle -> DetalleDestino(destino, vm, sharedScope, animatedScope)
    }
}

@Composable
private fun LoginDestino(vm: CobreViewModel) {
    val isDark = cobreIsDark()
    LoginScreen(
        onLogin = { sesion -> vm.iniciarSesion(sesion) },
        onToggleTema = { vm.alternarTema(isDark) },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DashboardDestino(vm: CobreViewModel, sharedScope: SharedTransitionScope, animatedScope: AnimatedVisibilityScope) {
    val operario = recordarOperarioActivo(vm.operario)
    OperarioScaffold(Pantalla.Dashboard, vm) { isTablet ->
        DashboardScreen(
            operario = operario,
            pedidos = vm.pedidos,
            onAbrirPedido = { vm.navegar(Pantalla.Detalle(it.id)) },
            onAvanzarEtapa = { vm.avanzarEtapa(it.id, "") },
            onComentar = { id, nota -> vm.agregarComentario(id, nota) },
            onAlerta = { id, tipo, nota -> vm.registrarAlerta(id, tipo, nota) },
            onResolverAlerta = { id -> vm.resolverAlerta(id) },
            onCerrarSesion = { vm.cerrarSesion() },
            mostrarTopBar = !isTablet,
            sharedScope = sharedScope,
            animatedScope = animatedScope,
        )
    }
}

@Composable
private fun ConfiguracionDestino(destino: Pantalla, vm: CobreViewModel) {
    val operario = recordarOperarioActivo(vm.operario)
    OperarioScaffold(destino, vm) { isTablet ->
        ConfiguracionScreen(
            operario = operario,
            modoTema = vm.modoTema,
            onCambiarTema = { vm.cambiarModoTema(it) },
            onCerrarSesion = { vm.cerrarSesion() },
            applySystemBarsInsets = !isTablet,
            reducirTransparencia = vm.reducirTransparencia,
            onCambiarReducirTransparencia = { vm.cambiarReducirTransparencia(it) },
        )
    }
}

@Composable
private fun recordarOperarioActivo(actual: Operario?): Operario {
    var ultimo by remember { mutableStateOf(actual) }
    if (actual != null) ultimo = actual
    return ultimo ?: Operario(nombre = "Operario", rol = "Operario de planta", turno = "")
}

@Composable
private fun recordarAdministradorActivo(actual: Administrador?): Administrador {
    var ultimo by remember { mutableStateOf(actual) }
    if (actual != null) ultimo = actual
    return ultimo ?: Administrador(nombre = "Administrador")
}

@Composable
private fun EscanerDestino(vm: CobreViewModel) {
    var pendingCallback by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf<((String?) -> Unit)?>(null)
    }

    LaunchedEffect(vm.qrPendiente) {
        val pendiente = vm.qrPendiente
        if (pendiente != null) {
            vm.procesarQrEscaneado(
                codigoQr = pendiente,
                onExito = { idInterno -> vm.navegar(Pantalla.Detalle(idInterno)) },
                onError = { pendingCallback?.invoke("No encontramos esa comanda o hubo un error de red.") }
            )
        }
    }

    EscanerScreen(
        onVolver = { vm.volver() },
        buscarComanda = { codigo -> vm.pedidoPorComanda(codigo) },
        buscarRemota = { codigo, onExito, onError -> vm.buscarNumero(codigo, onExito, onError) },
        onEscanearQr = { uuid, onResult ->
            pendingCallback = onResult
            vm.procesarQrEscaneado(
                codigoQr = uuid,
                onExito = { idInterno -> vm.navegar(Pantalla.Detalle(idInterno)) },
                onError = { onResult("No encontramos esa comanda. Verifica la red y vuelve a intentar.") }
            )
        },
        onAbrir = { pedido -> vm.navegar(Pantalla.Detalle(pedido.id)) },
        onEscanearInsumo = { codigo, onResult ->
            vm.inventario.escanear(codigo) { error ->
                if (error == null) vm.navegar(Pantalla.Insumos) else onResult(error)
            }
        },
    )
}

@Composable
private fun InsumosDestino(destino: Pantalla, vm: CobreViewModel) {
    LaunchedEffect(vm.sesion) { vm.inventario.refrescar() }
    OperarioScaffold(destino, vm) { isTablet ->
        InsumosScreen(
            inventario = vm.inventario,
            applySystemBarsInsets = !isTablet,
        )
    }
}

@Composable
private fun VehiculosDestino(vm: CobreViewModel) {
    val vehiculosVm: VehiculosViewModel = viewModel { VehiculosViewModel(repository = FirebaseVehiculosRepository()) }
    val administrador = recordarAdministradorActivo(vm.administrador)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val isDark = cobreIsDark()
    val cerrarSesionAdmin = {
        vehiculosVm.cambiarSesion(false)
        vm.cerrarSesion()
    }

    LaunchedEffect(vm.sesion, lifecycle) {
        vehiculosVm.cambiarSesion(vm.administrador != null)
        if (vm.administrador != null) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    vehiculosVm.refrescarFlota()
                    kotlinx.coroutines.delay(30_000)
                }
            }
        }
    }

    AdminScaffold(
        administrador = administrador,
        vm = vm,
        vehiculosVm = vehiculosVm,
        onCerrarSesion = cerrarSesionAdmin,
    ) { isTablet ->
        VehiculosScreen(
            administrador = administrador,
            vehiculos = vehiculosVm.vehiculosFiltrados,
            chequeoEnRuta = { id -> vehiculosVm.chequeoEnRuta(id) },
            historialVehiculo = { id -> vehiculosVm.historialVehiculo(id) },
            onRegistrarSalida = { id, km, fotos, obs ->
                vehiculosVm.registrarSalidaVehiculo(id, km, fotos, obs, administrador.nombre)
            },
            onRegistrarEntrada = { id, km, fotos, obs -> vehiculosVm.registrarEntradaVehiculo(id, km, fotos, obs) },
            onAgregarVehiculo = { patente, modelo, km -> vehiculosVm.registrarVehiculo(patente, modelo, km) },
            onCerrarSesion = cerrarSesionAdmin,
            conductores = vehiculosVm.conductores,
            kpis = vehiculosVm.kpis,
            filtroActual = vehiculosVm.filtro,
            busqueda = vehiculosVm.busqueda,
            cargando = vehiculosVm.cargando,
            operacionEnCurso = vehiculosVm.operacionEnCurso,
            error = vehiculosVm.error,
            mensajeExito = vehiculosVm.mensajeExito,
            tieneFotosPendientes = vehiculosVm::tieneFotosPendientes,
            onDescartarReintentos = vehiculosVm::descartarReintentos,
            mostrarTopBar = !isTablet,
            onToggleTema = { vm.alternarTema(isDark) },
            onCambiarBusqueda = { vehiculosVm.actualizarBusqueda(it) },
            onSeleccionarFiltro = { vehiculosVm.seleccionarFiltro(it) },
            onRefrescar = { vehiculosVm.refrescarFlota() },
            onLimpiarMensajes = { vehiculosVm.limpiarMensajes() },
            onCambiarEstadoVehiculo = { id, activo -> vehiculosVm.cambiarEstadoVehiculo(id, activo) },
            onRegistrarVehiculoDetallado = { patente, marca, modelo, anio, km ->
                vehiculosVm.registrarVehiculo(
                    patente = patente,
                    modelo = modelo,
                    kilometrajeInicial = km,
                    marca = marca,
                    anio = anio,
                )
            },
            onRegistrarSalidaDetallada = { id, repartidorId, responsable, km, estado, fotos, obs ->
                vehiculosVm.registrarSalidaVehiculo(id, km, fotos, obs, responsable, repartidorId, estado)
            },
            onRegistrarEntradaDetallada = { id, km, estado, fotos, obs ->
                vehiculosVm.registrarEntradaVehiculo(id, km, fotos, obs, estado)
            },
        )
    }
}

@Composable
private fun AdminScaffold(
    administrador: Administrador,
    vm: CobreViewModel,
    vehiculosVm: VehiculosViewModel,
    onCerrarSesion: () -> Unit,
    content: @Composable (isTablet: Boolean) -> Unit,
) {
    val isDark = cobreIsDark()
    val window = CobreTheme.window
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= ANCHO_TABLET && !window.isPhoneLandscape
        if (isTablet) {
            TabletShell(
                sidebar = { modifier ->
                    AdminSidebar(
                        filtroActual = vehiculosVm.filtro,
                        kpis = vehiculosVm.kpis,
                        adminNombre = administrador.nombre,
                        adminRol = administrador.rol,
                        onSeleccionarFiltro = { vehiculosVm.seleccionarFiltro(it) },
                        onRefrescar = { vehiculosVm.refrescarFlota() },
                        onCerrarSesion = onCerrarSesion,
                        modifier = modifier,
                    )
                },
                topbar = { onMenu ->
                    WebTopbar(
                        usuarioNombre = administrador.nombre,
                        usuarioRol = "ADMINISTRADOR",
                        modoTema = vm.modoTema,
                        onToggleTema = { vm.alternarTema(isDark) },
                        showSearch = false,
                        alertasCount = vehiculosVm.kpis.enRuta,
                        onAlertasClick = { vehiculosVm.seleccionarFiltro(FiltroFlota.EN_RUTA) },
                        onMenuClick = onMenu,
                    )
                },
                content = { content(true) },
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                content(false)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetalleDestino(
    destino: Pantalla.Detalle,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    val currentPedido = vm.pedido(destino.pedidoId)
    LaunchedEffect(destino.pedidoId) {
        vm.cargarPedido(destino.pedidoId) {}
    }
    if (currentPedido == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (vm.cargando) androidx.compose.material3.CircularProgressIndicator(color = Brand400)
            else Column {
                Text(vm.errorOperacion ?: "Comanda no encontrada")
                androidx.compose.material3.TextButton(onClick = { vm.cargarPedido(destino.pedidoId) {} }) {
                    Text("Reintentar")
                }
                androidx.compose.material3.TextButton(onClick = { vm.volver() }) { Text("Volver") }
            }
        }
        return
    }

    DetalleScreen(
        pedido = currentPedido,
        operacionPendiente = vm.cargando,
        errorOperacion = vm.errorOperacion,
        onVolver = { vm.volver() },
        onAvanzar = { comentario -> vm.avanzarEtapa(currentPedido.id, comentario) },
        onComentar = { texto -> vm.agregarComentario(currentPedido.id, texto) },
        onAlerta = { tipo, comentario -> vm.registrarAlerta(currentPedido.id, tipo, comentario) },
        onResolver = { vm.resolverAlerta(currentPedido.id) },
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

/** Ancho a partir del cual se usa la barra lateral de tablet (breakpoint "medium" de Material 3). */
private val ANCHO_TABLET = 600.dp

/** Ancho de la barra lateral de tablet. */
private val ANCHO_SIDEBAR = 240.dp

/** Separación de los paneles de vidrio flotantes respecto del borde y del contenido. */
private val MARGEN_VIDRIO = 12.dp

/**
 * Navegación del rol Operario, adaptativa (NFR05: la app debe operarse en tablets de 10"):
 * - teléfono vertical: barra de pestañas flotante de vidrio con el QR al lado;
 * - teléfono horizontal: rail flotante a la izquierda, para no quitarle alto a las listas;
 * - tablet: barra lateral y barra superior de vidrio (las de la intranet web).
 */
@Composable
private fun OperarioScaffold(
    tabActual: Pantalla,
    vm: CobreViewModel,
    content: @Composable (isTablet: Boolean) -> Unit,
) {
    val isDark = cobreIsDark()
    val window = CobreTheme.window
    val onSeleccionarTab: (Pantalla) -> Unit = { vm.navegar(it) }
    val onEscanearQR: () -> Unit = { vm.navegar(Pantalla.Escaner) }
    val badges: BadgesNav = mapOf(Pantalla.Avisos to vm.avisosNoLeidos)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= ANCHO_TABLET && !window.isPhoneLandscape
        when {
            isTablet -> TabletShell(
                sidebar = { modifier ->
                    WebSidebar(
                        tabActual = tabActual,
                        onSeleccionarTab = onSeleccionarTab,
                        onEscanearQR = onEscanearQR,
                        comandasActivas = vm.pedidos.count { !it.esFinal },
                        avisosCount = vm.avisosNoLeidos,
                        alertasStockCount = vm.inventario.insumos.count { it.estado != "OK" },
                        operarioNombre = vm.operario?.nombre ?: "Operario Planta",
                        operarioRol = vm.operario?.turno ?: "Turno Mañana",
                        onCerrarSesion = { vm.cerrarSesion() },
                        modifier = modifier,
                    )
                },
                topbar = { onMenu ->
                    WebTopbar(
                        usuarioNombre = vm.operario?.nombre ?: "Operario Planta",
                        usuarioRol = "OPERARIO - " + (vm.operario?.turno ?: "PLANTA"),
                        modoTema = vm.modoTema,
                        onToggleTema = { vm.alternarTema(isDark) },
                        showSearch = false,
                        alertasCount = vm.pedidos.count { it.estaBloqueado } + vm.avisosNoLeidos,
                        onAlertasClick = { vm.navegar(Pantalla.Avisos) },
                        onMenuClick = onMenu,
                    )
                },
                content = { content(true) },
            )
            window.isPhoneLandscape -> Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize().padding(start = EspacioRail)) { content(false) }
                FloatingRail(
                    tabActual = tabActual,
                    onSeleccionarTab = onSeleccionarTab,
                    onEscanearQR = onEscanearQR,
                    compacto = true,
                    badges = badges,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            else -> Box(modifier = Modifier.fillMaxSize()) {
                // Las pantallas aplican el inset de la barra del sistema; aquí solo se
                // reserva lo que ocupa la barra flotante por encima de ella.
                Box(modifier = Modifier.fillMaxSize().padding(bottom = EspacioBarraInferior)) { content(false) }
                FloatingTabBar(
                    tabActual = tabActual,
                    onSeleccionarTab = onSeleccionarTab,
                    onEscanearQR = onEscanearQR,
                    badges = badges,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

/**
 * Esqueleto de tablet compartido por Operario y Administrador: barra lateral plegable y
 * barra superior como paneles de vidrio flotantes, con el contenido a la derecha.
 */
@Composable
private fun TabletShell(
    sidebar: @Composable (Modifier) -> Unit,
    topbar: @Composable (onMenu: () -> Unit) -> Unit,
    content: @Composable () -> Unit,
) {
    var sidebarVisible by remember { mutableStateOf(true) }
    val sidebarWidth by animateDpAsState(
        targetValue = if (sidebarVisible) ANCHO_SIDEBAR + MARGEN_VIDRIO else 0.dp,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "sidebarWidth",
    )
    Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        // clipToBounds: al plegarse, el panel se recorta en vez de comprimir su contenido.
        Box(modifier = Modifier.width(sidebarWidth).fillMaxHeight().clipToBounds()) {
            GlassSurface(
                modifier = Modifier
                    .requiredWidth(ANCHO_SIDEBAR + MARGEN_VIDRIO)
                    .fillMaxHeight()
                    .padding(start = MARGEN_VIDRIO, top = MARGEN_VIDRIO, bottom = MARGEN_VIDRIO),
                shape = CobreShapes.lg,
            ) {
                sidebar(Modifier.requiredWidth(ANCHO_SIDEBAR))
            }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth().padding(MARGEN_VIDRIO),
                shape = CobreShapes.lg,
                floating = true,
            ) {
                topbar { sidebarVisible = !sidebarVisible }
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                content()
            }
        }
    }
}
