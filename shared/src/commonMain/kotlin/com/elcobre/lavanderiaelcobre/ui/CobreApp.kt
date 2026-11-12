package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elcobre.lavanderiaelcobre.data.FirebaseVehiculosRepository
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.ui.components.BarrasDelSistema
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackHandler
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.LocalGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.LocalRootGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.LocalShellPadding
import com.elcobre.lavanderiaelcobre.ui.components.glassBackdropSource
import com.elcobre.lavanderiaelcobre.ui.components.rememberGlassBackdrop
import com.elcobre.lavanderiaelcobre.ui.screens.avisos.AvisosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.configuracion.ConfiguracionScreen
import com.elcobre.lavanderiaelcobre.ui.screens.dashboard.DashboardScreen
import com.elcobre.lavanderiaelcobre.ui.screens.detalle.DetalleScreen
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.EscanerScreen
import com.elcobre.lavanderiaelcobre.ui.screens.insumos.InsumosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.login.LoginScreen
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.VehiculosScreen
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Las pestañas de cada rol comparten un mismo destino de la navegación raíz: así la barra
 * de vidrio no se anima ni se recrea al cambiar de pestaña, solo el contenido de adentro.
 */
private const val CLAVE_PESTANAS = "pestanas"

private fun esPestana(p: Pantalla): Boolean = NavOperario.any { it.pantalla == p } || NavAdmin.any { it.pantalla == p }

private fun claveDestino(p: Pantalla): Any = if (esPestana(p)) CLAVE_PESTANAS else p

// Curvas "emphasized" de Material 3: arrancan rápido y frenan suave, como iOS.
private val EntradaEnfatizada = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
private val SalidaEnfatizada = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

/**
 * Raíz de la aplicación: aplica el tema de marca y gestiona la navegación con
 * transiciones animadas. El estado vive en [CobreViewModel]; las pantallas son
 * sin estado de dominio, reciben datos y delegan acciones al ViewModel.
 */
@Composable
fun CobreApp() {
    val vm: CobreViewModel = viewModel { CobreViewModel() }
    ActualizarAvisosVisibles(vm)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(vm.sesion, lifecycle) {
        if (vm.operario != null) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                kotlinx.coroutines.delay(30_000)
                vm.refrescarComandas()
            }
        }
    }
    val temaOscuro = when (vm.modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SISTEMA -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    BarrasDelSistema(temaOscuro)

    CobreBackHandler(enabled = vm.puedeVolver) {
        vm.volver()
    }

    CobreTheme(darkTheme = temaOscuro, reduceTransparency = vm.reducirTransparencia) {
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
                NavegacionRaiz(vm)
            }
            AvisoErrorOperacion(vm)
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

/**
 * Navegación raíz: Login, pestañas del rol (un solo destino), Escáner y Detalle. Entre
 * destinos distintos desliza la pantalla completa; el cambio de pestaña lo anima
 * [PestanasAnimadas] dentro de la barra fija.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun NavegacionRaiz(vm: CobreViewModel) {
    // Permite que el icono de etapa "vuele" de la tarjeta del Dashboard a la cabecera del Detalle.
    SharedTransitionLayout {
        val sharedScope = this
        AnimatedContent(
            targetState = vm.pantalla,
            contentKey = ::claveDestino,
            transitionSpec = { transicionRaiz() },
            label = "navegacion",
        ) { destino ->
            if (esPestana(destino)) {
                // El rol se fija al crear el destino: al cerrar sesión la sesión ya es nula
                // mientras la barra todavía se está yendo.
                val esAdministrador = remember { vm.administrador != null }
                if (esAdministrador) {
                    AdminPestanas(tab = destino, vm = vm)
                } else {
                    OperarioPestanas(tab = destino, vm = vm, sharedScope = sharedScope, animatedScope = this)
                }
            } else {
                DestinoContent(destino = destino, vm = vm, sharedScope = sharedScope, animatedScope = this)
            }
        }
    }
}

/** Login ↔ app: fundido con un leve zoom. Navegación profunda: deslizamiento completo. */
private fun AnimatedContentTransitionScope<Pantalla>.transicionRaiz(): ContentTransform {
    val desdeLogin = initialState == Pantalla.Login || targetState == Pantalla.Login
    if (desdeLogin) {
        return (fadeIn(tween(420, delayMillis = 80)) + scaleIn(tween(420, easing = EntradaEnfatizada), initialScale = 0.96f)) togetherWith
            (fadeOut(tween(200)) + scaleOut(tween(260), targetScale = 1.02f))
    }
    val haciaAdelante = orden(targetState) >= orden(initialState)
    val dir = if (haciaAdelante) 1 else -1
    return (slideInHorizontally(tween(360, easing = EntradaEnfatizada)) { w -> dir * w } + fadeIn(tween(240))) togetherWith
        (slideOutHorizontally(tween(360, easing = EntradaEnfatizada)) { w -> -dir * w / 3 } + fadeOut(tween(240)))
}

/**
 * Contenido de las pestañas: entra desde el lado de la pestaña elegida siguiendo el
 * mismo eje que la píldora de la barra (horizontal con la barra abajo, vertical con el
 * rail), con fundido y un leve zoom, para que la barra y el contenido se muevan juntos.
 */
@Composable
private fun PestanasAnimadas(
    tab: Pantalla,
    items: List<NavItem>,
    content: @Composable (Pantalla) -> Unit,
) {
    val vertical = CobreTheme.window.isLandscape
    AnimatedContent(
        targetState = tab,
        transitionSpec = {
            val dir = if (items.indexOfPantalla(targetState) >= items.indexOfPantalla(initialState)) 1 else -1
            val entrada: EnterTransition = if (vertical) {
                slideInVertically(tween(380, easing = EntradaEnfatizada)) { h -> dir * h / 6 }
            } else {
                slideInHorizontally(tween(380, easing = EntradaEnfatizada)) { w -> dir * w / 6 }
            }
            val salida: ExitTransition = if (vertical) {
                slideOutVertically(tween(220, easing = SalidaEnfatizada)) { h -> -dir * h / 10 }
            } else {
                slideOutHorizontally(tween(220, easing = SalidaEnfatizada)) { w -> -dir * w / 10 }
            }
            val enter = entrada + fadeIn(tween(260, delayMillis = 60)) +
                scaleIn(tween(380, easing = EntradaEnfatizada), initialScale = 0.98f)
            (enter togetherWith (salida + fadeOut(tween(160)))).using(SizeTransform(clip = false))
        },
        label = "pestanas",
    ) { pestana -> content(pestana) }
}

private fun List<NavItem>.indexOfPantalla(p: Pantalla): Int = indexOfFirst { it.pantalla == p }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun OperarioPestanas(
    tab: Pantalla,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    PestanasScaffold(
        items = NavOperario,
        tabActual = tab,
        onSeleccionarTab = { vm.navegar(it) },
        onEscanearQR = { vm.navegar(Pantalla.Escaner) },
        badges = mapOf(Pantalla.Avisos to vm.avisosNoLeidos),
    ) {
        PestanasAnimadas(tab = tab, items = NavOperario) { pestana ->
            PestanaOperario(tab = pestana, vm = vm, sharedScope = sharedScope, animatedScope = animatedScope)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PestanaOperario(
    tab: Pantalla,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    when (tab) {
        Pantalla.Dashboard -> {
            val operario = recordarOperarioActivo(vm.operario)
            DashboardScreen(
                operario = operario,
                pedidos = vm.pedidos,
                onAbrirPedido = { vm.navegar(Pantalla.Detalle(it.id)) },
                onAvanzarEtapa = { vm.avanzarEtapa(it.id, "") },
                onComentar = { id, nota -> vm.agregarComentario(id, nota) },
                onAlerta = { id, tipo, nota -> vm.registrarAlerta(id, tipo, nota) },
                onResolverAlerta = { id -> vm.resolverAlerta(id) },
                onCerrarSesion = { vm.cerrarSesion() },
                sharedScope = sharedScope,
                animatedScope = animatedScope,
            )
        }
        Pantalla.Avisos -> AvisosScreen(
            avisos = vm.avisos,
            cargando = vm.cargandoAvisos,
            error = vm.errorAvisos,
            hayMas = vm.avisos.size < vm.totalAvisos,
            onActualizar = { vm.refrescarAvisos() },
            onMas = { vm.refrescarAvisos(mas = true) },
        )
        Pantalla.Insumos -> {
            LaunchedEffect(vm.sesion) { vm.inventario.refrescar() }
            InsumosScreen(inventario = vm.inventario)
        }
        Pantalla.Configuracion -> {
            val operario = recordarOperarioActivo(vm.operario)
            Ajustes(vm = vm, nombre = operario.nombre, rol = operario.rol, onCerrarSesion = { vm.cerrarSesion() })
        }
        else -> Unit
    }
}

/** Pestañas de administración: flota de reparto y ajustes, con la misma barra de vidrio que el operario. */
@Composable
private fun AdminPestanas(tab: Pantalla, vm: CobreViewModel) {
    val vehiculosVm: VehiculosViewModel = viewModel { VehiculosViewModel(repository = FirebaseVehiculosRepository()) }
    val administrador = recordarAdministradorActivo(vm.administrador)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val cerrarSesion = {
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

    PestanasScaffold(
        items = NavAdmin,
        tabActual = tab,
        onSeleccionarTab = { vm.navegar(it) },
        onEscanearQR = null,
        badges = mapOf(Pantalla.Vehiculos to vehiculosVm.kpis.enRuta),
    ) {
        PestanasAnimadas(tab = tab, items = NavAdmin) { pestana ->
            when (pestana) {
                Pantalla.Vehiculos -> FlotaPestana(administrador = administrador, vehiculosVm = vehiculosVm)
                Pantalla.Configuracion -> Ajustes(
                    vm = vm,
                    nombre = administrador.nombre,
                    rol = administrador.rol,
                    onCerrarSesion = cerrarSesion,
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun Ajustes(vm: CobreViewModel, nombre: String, rol: String, onCerrarSesion: () -> Unit) {
    ConfiguracionScreen(
        nombre = nombre,
        rol = rol,
        modoTema = vm.modoTema,
        onCambiarTema = { vm.cambiarModoTema(it) },
        onCerrarSesion = onCerrarSesion,
        reducirTransparencia = vm.reducirTransparencia,
        onCambiarReducirTransparencia = { vm.cambiarReducirTransparencia(it) },
    )
}

@Composable
private fun FlotaPestana(administrador: Administrador, vehiculosVm: VehiculosViewModel) {
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

/**
 * Navegación por pestañas, la misma en teléfono y tablet y para ambos roles:
 * - vertical: barra de pestañas flotante de vidrio, fija abajo; el contenido pasa por
 *   debajo y basta con desplazarse para verlo completo;
 * - horizontal: rail de vidrio permanente a la izquierda que ocupa su propio espacio,
 *   para no quitarle alto a las listas ni tapar contenido.
 */
@Composable
private fun PestanasScaffold(
    items: List<NavItem>,
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: (() -> Unit)?,
    badges: BadgesNav,
    content: @Composable () -> Unit,
) {
    val window = CobreTheme.window
    if (window.isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            FloatingRail(
                items = items,
                tabActual = tabActual,
                onSeleccionarTab = onSeleccionarTab,
                onEscanearQR = onEscanearQR,
                compacto = window.isPhoneLandscape,
                badges = badges,
            )
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) { content() }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            // Las listas reservan este espacio como contentPadding: se desplazan por debajo
            // del vidrio y el último elemento queda visible por encima de la barra.
            CompositionLocalProvider(LocalShellPadding provides PaddingValues(bottom = EspacioBarraInferior)) {
                content()
            }
            FloatingTabBar(
                items = items,
                tabActual = tabActual,
                onSeleccionarTab = onSeleccionarTab,
                onEscanearQR = onEscanearQR,
                badges = badges,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun AvisoErrorOperacion(vm: CobreViewModel) {
    val mensaje = vm.errorOperacion ?: return
    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
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

/** Destinos fuera de las pestañas, despachados desde [NavegacionRaiz]. */
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
        Pantalla.Escaner -> EscanerDestino(vm)
        is Pantalla.Detalle -> DetalleDestino(destino, vm, sharedScope, animatedScope)
        else -> Unit
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

@Composable
private fun recordarOperarioActivo(actual: Operario?): Operario {
    var ultimo by remember { mutableStateOf(actual) }
    if (actual != null) ultimo = actual
    return ultimo ?: Operario(nombre = "Operario", rol = "Operario de planta")
}

@Composable
private fun recordarAdministradorActivo(actual: Administrador?): Administrador {
    var ultimo by remember { mutableStateOf(actual) }
    if (actual != null) ultimo = actual
    return ultimo ?: Administrador(nombre = "Administrador")
}

@Composable
private fun EscanerDestino(vm: CobreViewModel) {
    var pendingCallback by remember { mutableStateOf<((String?) -> Unit)?>(null) }

    LaunchedEffect(vm.qrPendiente) {
        val pendiente = vm.qrPendiente
        if (pendiente != null) {
            vm.procesarQrEscaneado(
                codigoQr = pendiente,
                onExito = { idInterno -> vm.navegar(Pantalla.Detalle(idInterno)) },
                onError = { pendingCallback?.invoke("No encontramos esa comanda o hubo un error de red.") },
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
                onError = { onResult("No encontramos esa comanda. Verifica la red y vuelve a intentar.") },
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
            if (vm.cargando) {
                androidx.compose.material3.CircularProgressIndicator(color = Brand400)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(vm.errorOperacion ?: "Comanda no encontrada", color = CobreTheme.colors.textPrimary)
                    CobreButton(text = "Reintentar", onClick = { vm.cargarPedido(destino.pedidoId) {} })
                    CobreButton(text = "Volver", onClick = { vm.volver() }, variant = ButtonVariant.Tonal)
                }
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
