package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.CoroutineScope
import com.elcobre.lavanderiaelcobre.data.ComandasRepository
import com.elcobre.lavanderiaelcobre.data.AvisosRepository
import com.elcobre.lavanderiaelcobre.data.FirebaseAvisosRepository
import com.elcobre.lavanderiaelcobre.data.FirebaseComandasRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.data.InventarioRepository
import com.elcobre.lavanderiaelcobre.data.FirebaseInventarioRepository
import com.elcobre.lavanderiaelcobre.data.auth.DatosSesionGuardada
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.auth.VigilanciaCuenta
import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.auth.restaurarPerfilAutenticado
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.gestion.ahoraMs
import com.elcobre.lavanderiaelcobre.ui.screens.login.sesionParaRol
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema
import com.elcobre.lavanderiaelcobre.data.offline.AccionEnColaException

/** Destinos raíz: navegar a uno reemplaza la pila en vez de apilar (semántica de barra de pestañas). */
private val TABS: Set<Pantalla> get() = TodasLasPestanas


/** Cada cuánto se revisa en el servidor que la cuenta siga activa y con el mismo rol. */
internal const val REVISION_CUENTA_MS = 3 * 60_000L

/**
 * Dueño del estado raíz de la app y del flujo operativo del Operario: sesión autenticada,
 * pila de navegación, preferencias visuales (tema y reducción de transparencia), pedidos,
 * avisos y catálogo de insumos.
 *
 * Estas responsabilidades comparten ciclo de vida de sesión, por lo que superan el umbral por
 * defecto de funciones por clase a propósito (la flota de reparto vive aparte en
 * [VehiculosViewModel] al pertenecer a la sesión de Administrador).
 *
 * Contrato de estado y concurrencia:
 * - Los modelos de dominio ([Pedido]) son inmutables; [_pedidos] es una [SnapshotStateList]
 *   que reemplaza cada instancia por su copia actualizada para minimizar recomposiciones.
 * - [generacion] invalida respuestas en vuelo cuando cambia o se cierra la sesión.
 * - [revision] evita que un refresco de lista atrasado sobrescriba una mutación más reciente.
 */
@Suppress("TooManyFunctions")
class CobreViewModel(
    private val repository: ComandasRepository = FirebaseComandasRepository(),
    private val scopeOverride: CoroutineScope? = null,
    private val avisosRepository: AvisosRepository = FirebaseAvisosRepository(),
    inventarioRepository: InventarioRepository = FirebaseInventarioRepository(),
    private val restaurarPerfil: suspend () -> PerfilResultado? = ::restaurarPerfilAutenticado,
) : ViewModel() {
    private val scope get() = scopeOverride ?: viewModelScope
    val inventario = InventarioState(scope, inventarioRepository)
    var errorOperacion by mutableStateOf<String?>(null)
        private set

    /** Descarta el error operativo visible en pantalla. */
    fun limpiarError() { errorOperacion = null }

    /** Aviso informativo (por ejemplo, una acción guardada sin conexión). */
    var avisoOperacion by mutableStateOf<String?>(null)

    /** Descarta el aviso informativo visible en pantalla. */
    fun limpiarAviso() { avisoOperacion = null }

    /** Contador monotónico que incrementa al finalizar cada operación de comanda. */
    var operacionSeq by mutableStateOf(0)
        private set

    var cargando by mutableStateOf(false)
        private set
    // Contadores de concurrencia: `generacion` descarta trabajos de sesiones anteriores;
    // `revision` evita que un refresco de lista sobrescriba mutaciones posteriores;
    // `operaciones` deduplica peticiones simultáneas por clave.
    private var generacion = 0
    private var revision = 0
    private val operaciones = mutableSetOf<String>()

    private val _pedidos: SnapshotStateList<Pedido> = mutableStateListOf()

    /** Lista observable de comandas cargadas para la sesión activa (inmutables por elemento). */
    val pedidos: List<Pedido> get() = _pedidos

    var avisos by mutableStateOf<List<Aviso>>(emptyList())
        private set
    var totalAvisos by mutableStateOf(0)
        private set
    var cargandoAvisos by mutableStateOf(false)
        private set
    var errorAvisos by mutableStateOf<String?>(null)
        private set
    private var limiteAvisos = 20
    private var idsAvisosVistos by mutableStateOf<Set<String>>(emptySet())
    private var avisosInicializados = false
    private var idsComandasConocidas = setOf<String>()
    private var comandasInicializadas = false

    /** Cantidad de avisos actualmente cargados que aún no han sido marcados como vistos. */
    val avisosNoLeidos: Int get() = avisos.count { it.id !in idsAvisosVistos }

    /** Registra todos los avisos visibles como leídos para limpiar el badge de navegación. */
    fun marcarAvisosComoLeidos() {
        idsAvisosVistos = idsAvisosVistos + avisos.map { it.id }
    }

    /**
     * Consulta los avisos del equipo desde el repositorio. Con [mas] amplía el límite paginado
     * en bloques de 20 elementos; ignora llamadas duplicadas o sin sesión de operario.
     */
    fun refrescarAvisos(mas: Boolean = false) {
        if (operario == null || cargandoAvisos) return
        if (mas && avisos.size >= totalAvisos) return
        val inicio = generacion
        val limite = if (mas) limiteAvisos + 20 else limiteAvisos
        cargandoAvisos = true
        errorAvisos = null
        scope.launch {
            try {
                val resultado = avisosRepository.consultar(0, limite)
                if (inicio == generacion) aplicarResultadoAvisos(resultado.avisos, resultado.total, limite, mas)
            } catch (error: OperacionComandaException) {
                if (inicio == generacion) errorAvisos = error.message ?: "No se pudieron cargar los avisos. Intenta nuevamente."
            } finally {
                if (inicio == generacion) cargandoAvisos = false
            }
        }
    }

    private fun aplicarResultadoAvisos(recibidos: List<Aviso>, total: Int, limite: Int, mas: Boolean) {
        val previos = avisos.map { it.id }.toSet()
        val nuevos = recibidos.distinctBy { it.id }
        if (avisosInicializados && !mas) {
            nuevos.firstOrNull { it.id !in previos }?.let { recienLlegado ->
                notificarEventoPlanta("Nuevo aviso de administración", recienLlegado.titulo)
            }
        }
        avisos = nuevos
        totalAvisos = total
        limiteAvisos = limite
        avisosInicializados = true
        if (pantalla == Pantalla.Avisos) marcarAvisosComoLeidos()
    }

    private fun limpiarAvisos() {
        avisos = emptyList()
        totalAvisos = 0
        limiteAvisos = 20
        cargandoAvisos = false
        errorAvisos = null
        idsAvisosVistos = emptySet()
        avisosInicializados = false
    }


    /** Sesión autenticada actual; `null` cuando no hay usuario autenticado. */
    var sesion by mutableStateOf<Sesion?>(null)
        private set

    /** Código QR capturado antes de iniciar sesión; se procesa automáticamente al autenticarse. */
    var qrPendiente by mutableStateOf<String?>(null)
        private set

    /** Preferencia de tema actual (claro, oscuro o sistema). */
    var modoTema by mutableStateOf(ModoTema.CLARO)
        private set

    fun cambiarModoTema(modo: ModoTema) { modoTema = modo }

    /** Alterna explícitamente entre modo claro y oscuro a partir de la polaridad resuelta actual. */
    fun alternarTema(esOscuroActual: Boolean) {
        modoTema = if (esOscuroActual) ModoTema.CLARO else ModoTema.OSCURO
    }

    /** Accesibilidad: superficies de vidrio opacas en vez de translúcidas (equivalente a "Reducir transparencia" de iOS). */
    var reducirTransparencia by mutableStateOf(false)
        private set

    fun cambiarReducirTransparencia(activo: Boolean) { reducirTransparencia = activo }

    // Pila de navegación real (no solo la pantalla actual): vive en el ViewModel para
    // sobrevivir a la recreación de la Activity en cambios de configuración, y permite
    // que `volver()` regrese al destino anterior real en vez de uno fijo hardcodeado.
    private val _backStack: SnapshotStateList<Pantalla> = mutableStateListOf(Pantalla.Login)

    internal val pantalla: Pantalla get() = _backStack.last()

    val operario: Operario? get() = (sesion as? Sesion.DeOperario)?.operario
    val administrador: Administrador? get() = (sesion as? Sesion.DeAdministrador)?.administrador

    /** Sección abierta dentro de la pestaña "Más" (recepción y administración); `null` muestra el menú. */
    var seccionMas by mutableStateOf<SeccionMas?>(null)
        private set

    /** Abre una subsección dentro de "Más" o vuelve a su menú principal cuando [seccion] es `null`. */
    fun abrirSeccion(seccion: SeccionMas?) {
        seccionMas = seccion
    }

    /** Los destinos de [TABS] reemplazan la pila (como barra de pestañas); el resto se apila. */
    internal fun navegar(destino: Pantalla) {
        // Tocar "Más" estando en una de sus secciones vuelve al menú, como en iOS.
        if (destino == Pantalla.Mas && pantalla == Pantalla.Mas) seccionMas = null
        if (destino in TABS) {
            _backStack.clear()
            _backStack.add(destino)
        } else {
            _backStack.add(destino)
        }
        if (destino == Pantalla.Avisos) marcarAvisosComoLeidos()
    }

    /**
     * Indica si el botón atrás tiene un destino válido: primero cierra [seccionMas] y bloquea
     * el regreso a [Pantalla.Login] mientras exista una sesión autenticada.
     */
    val puedeVolver: Boolean get() {
        if (pantalla == Pantalla.Mas && seccionMas != null) return true
        if (sesion != null && _backStack.size == 2 && _backStack.first() == Pantalla.Login) {
            return false
        }
        return _backStack.size > 1
    }

    /** Regresa al destino anterior real de la pila; no hace nada si ya está en la raíz ni al login con sesión activa. */
    internal fun volver() {
        if (pantalla == Pantalla.Mas && seccionMas != null) {
            seccionMas = null
            return
        }
        if (_backStack.size > 1) {
            if (sesion != null && _backStack.size == 2 && _backStack.first() == Pantalla.Login) {
                return
            }
            _backStack.removeAt(_backStack.lastIndex)
        }
    }

    /**
     * Restaura la sesión persistida ("Recordarme", vigente 30 días) al iniciar el ViewModel.
     * Si no hay sesión válida limpia el token de Firebase; ante fallo transitorio de red permite
     * entrar sin conexión vía [entrarSinConexion] y solo purga credenciales ante rechazo definitivo.
     */
    fun restaurarSesionSiExiste() {
        val guardada = SesionPersistente.obtener(ahoraMs())
        if (guardada == null) {
            // Sin "Recordarme" (o vencida a los 30 días) no debe quedar un token de Firebase
            // guardado en el dispositivo: la próxima apertura pide la contraseña, como la web.
            if (sesion == null) repository.cerrarSesion()
            return
        }
        val inicio = generacion
        scope.launch {
            val perfil = restaurarPerfil()
            if (inicio != generacion) return@launch
            val valida = (perfil as? PerfilResultado.Exito)?.let {
                sesionParaRol(it.rolNombre, it.nombre)
            }
            if (valida != null) {
                iniciarSesion(valida)
                return@launch
            }
            if (entrarSinConexion(perfil, guardada)) return@launch
            errorOperacion = when (perfil) {
                is PerfilResultado.Error -> perfil.mensaje
                is PerfilResultado.Exito -> "Tu cuenta no tiene acceso a la app móvil."
                null -> "La sesión guardada ya no es válida. Inicia sesión nuevamente."
            }
            // Cuenta sin perfil, inactiva, sin verificar o que pasó a cliente: se cierra la
            // sesión de Firebase y se borran los datos guardados. Un error de red los conserva
            // para reintentar al reabrir la app.
            val rechazada = perfil == null || perfil is PerfilResultado.Exito || (perfil as? PerfilResultado.Error)?.definitivo == true
            if (rechazada) {
                if (perfil != null) repository.cerrarSesion()
                SesionPersistente.limpiar()
            }
        }
    }

    /**
     * Sin conexión al abrir la app: se entra con la sesión guardada para seguir trabajando;
     * el servidor sigue validando cada envío y la cuenta se revisa en cuanto vuelve la
     * conexión ([verificarCuenta]). Devuelve `true` si entró.
     */
    private fun entrarSinConexion(perfil: PerfilResultado?, guardada: DatosSesionGuardada): Boolean {
        val sinConexion = perfil is PerfilResultado.Error && !perfil.definitivo
        val sesionGuardada = sesionParaRol(guardada.rol, guardada.nombre)
        if (!sinConexion || sesionGuardada == null) return false
        iniciarSesion(sesionGuardada)
        avisoOperacion = "Sin conexión: trabajas con tu sesión guardada. Lo que hagas se enviará al recuperar la red."
        return true
    }

    init {
        VigilanciaCuenta.alRechazar { motivo -> scope.launch { cerrarPorCuenta(motivo) } }
        restaurarSesionSiExiste()
    }

    override fun onCleared() {
        VigilanciaCuenta.alRechazar(null)
        super.onCleared()
    }

    /**
     * Revisa el perfil en el servidor: si administración desactivó la cuenta, le quitó el
     * perfil o le cambió el rol, o Firebase la deshabilitó, se cierra la sesión con el
     * motivo. [CobreApp] la llama al volver a la app y cada [REVISION_CUENTA_MS] mientras
     * está visible. Un error de red no cierra la sesión.
     */
    fun verificarCuenta() {
        val actual = sesion ?: return
        val inicio = generacion
        scope.launch {
            val perfil = restaurarPerfil()
            if (inicio != generacion || sesion !== actual) return@launch
            VigilanciaCuenta.motivoCierre(actual, perfil) { sesionParaRol(it.rolNombre, it.nombre) }
                ?.let { cerrarPorCuenta(it) }
        }
    }

    private fun cerrarPorCuenta(motivo: String) {
        if (sesion == null) return
        cerrarSesion()
        errorOperacion = motivo
    }

    /** Invalida una restauración automática en vuelo cuando el usuario inicia login manual. */
    fun cancelarRestauracion() {
        if (sesion == null) generacion++
    }

    /**
     * Activa una nueva sesión: incrementa [generacion], cancela corrutinas previas, reinicia el
     * estado operativo y reemplaza la pila con la pestaña inicial del rol. Si quedó un
     * [qrPendiente] antes del login, apila [Pantalla.Escaner] para procesarlo de inmediato.
     */
    fun iniciarSesion(s: Sesion) {
        generacion++
        scope.coroutineContext.cancelChildren()
        operaciones.clear()
        _pedidos.clear()
        idsComandasConocidas = emptySet()
        comandasInicializadas = false
        sesion = s
        seccionMas = null
        errorOperacion = null
        avisoOperacion = null
        limpiarAvisos()
        inventario.cambiarSesion(s is Sesion.DeOperario)

        val destino = when (s) {
            is Sesion.DeOperario -> {
                refrescarComandas()
                Pantalla.Dashboard
            }
            is Sesion.DeAdministrador -> Pantalla.Inicio
            is Sesion.DeRecepcionista -> Pantalla.Comandas
            is Sesion.DeRepartidor -> Pantalla.Reparto
        }

        _backStack.clear()
        _backStack.add(destino)

        if (qrPendiente != null) {
            _backStack.add(Pantalla.Escaner)
        }
    }

    /**
     * Procesa un código QR escaneado: si aún no hay sesión lo difiere en [qrPendiente] y redirige
     * a [Pantalla.Login]; con sesión activa solo permite a operarios asignarse comandas por QR.
     */
    fun procesarQrEscaneado(codigoQr: String, onExito: (String) -> Unit, onError: () -> Unit) {
        if (sesion == null) {
            qrPendiente = codigoQr
            navegar(Pantalla.Login)
        } else {
            qrPendiente = null
            ejecutar("qr", onError) {
                revision++
                if (operario == null) throw OperacionComandaException("Solo operarios pueden asignarse comandas por QR.")
                val pedidoNube = repository.escanear(codigoQr)
                    ?: throw OperacionComandaException("No se encontró la comanda.")
                actualizarPedido(pedidoNube)
                onExito(pedidoNube.id)
            }
        }
    }

    /** Devuelve el pedido en caché local si existe o lo consulta en el backend y lo inserta en [_pedidos]. */
    fun cargarPedido(id: String, onResult: (Pedido?) -> Unit) {
        val existente = pedido(id)
        if (existente != null) {
            onResult(existente)
            return
        }
        ejecutar("detalle:$id", { onResult(null) }) {
            val desdeNube = repository.detalle(id)
            desdeNube?.let(::actualizarPedido)
            onResult(desdeNube)
        }
    }

    /**
     * Cierra la sesión activa: invalida trabajos en vuelo con [generacion], cierra Firebase Auth,
     * elimina la persistencia local, limpia colecciones y reinicia la pila en [Pantalla.Login].
     */
    fun cerrarSesion() {
        generacion++
        scope.coroutineContext.cancelChildren()
        repository.cerrarSesion()
        SesionPersistente.limpiar()
        _pedidos.clear()
        operaciones.clear()
        idsComandasConocidas = emptySet()
        comandasInicializadas = false
        limpiarAvisos()
        inventario.cambiarSesion(false)
        errorOperacion = null
        avisoOperacion = null
        cargando = false
        qrPendiente = null
        sesion = null
        seccionMas = null
        _backStack.clear()
        _backStack.add(Pantalla.Login)
    }

    /** Busca una comanda por su identificador interno en la lista cargada en memoria. */
    fun pedido(id: String): Pedido? = _pedidos.firstOrNull { it.id == id }

    /**
     * Avanza la comanda a la siguiente etapa y opcionalmente adjunta un [comentario].
     * Regla de negocio: ignora comandas bloqueadas por alerta activa ([Pedido.estaBloqueado]).
     */
    fun avanzarEtapa(id: String, comentario: String) {
        val i = _pedidos.indexOfFirst { it.id == id }
        if (i == -1) return
        val pedido = _pedidos[i]

        if (pedido.estaBloqueado) return
        ejecutar("avance:$id") {
            revision++
            repository.avanzar(pedido, comentario)
            val actualizado = repository.detalle(id)
                ?: throw OperacionComandaException("El avance se guardó, pero no se pudo cargar el detalle.")
            actualizarPedido(actualizado)
        }
    }

    /**
     * Recarga las comandas asignadas al operario. Compara [revision] para descartar respuestas
     * antiguas si hubo una mutación intermedia, notifica comandas recién asignadas y conserva en
     * memoria el detalle abierto aunque ya no aparezca en la lista asignada.
     */
    fun refrescarComandas() = ejecutar("lista") {
        val antes = revision
        val remotas = repository.asignadas()
        if (antes == revision) {
            if (comandasInicializadas) {
                remotas.firstOrNull { it.id !in idsComandasConocidas && !it.esFinal }?.let { nueva ->
                    notificarEventoPlanta("Nueva comanda asignada", "${nueva.comanda} · ${nueva.cliente}")
                }
            }
            idsComandasConocidas = remotas.map { it.id }.toSet()
            comandasInicializadas = true
            val abierto = (pantalla as? Pantalla.Detalle)?.pedidoId?.let(::pedido)
            _pedidos.clear()
            _pedidos.addAll(remotas)
            if (abierto != null && remotas.none { it.id == abierto.id }) _pedidos.add(abierto)
        }
    }

    /** Busca una comanda por número visible en el servidor y la agrega/actualiza en [_pedidos]. */
    fun buscarNumero(numero: String, onExito: (Pedido) -> Unit, onError: () -> Unit) = ejecutar("buscar", onError) {
        val encontrado = repository.buscarNumero(numero)
            ?: throw OperacionComandaException("No se encontró la comanda.")
        actualizarPedido(encontrado)
        onExito(encontrado)
    }

    /** Reemplaza el pedido en su índice actual de [_pedidos] o lo antepone si es nuevo. */
    private fun actualizarPedido(pedido: Pedido) {
        val indice = _pedidos.indexOfFirst { it.id == pedido.id }
        if (indice >= 0) _pedidos[indice] = pedido else _pedidos.add(0, pedido)
    }

    /**
     * Ejecuta una operación asíncrona deduplicada por [clave]. Convierte [AccionEnColaException]
     * en aviso informativo (modo sin conexión) y [OperacionComandaException] en error visible,
     * descartando actualizaciones si [generacion] cambió durante la espera.
     */
    private fun ejecutar(clave: String, onError: () -> Unit = {}, accion: suspend () -> Unit) {
        if (!operaciones.add(clave)) return
        val inicio = generacion
        cargando = true
        errorOperacion = null
        scope.launch {
            try {
                accion()
            } catch (enCola: AccionEnColaException) {
                // Sin conexión: quedó en la cola; el aviso de pendientes lo muestra.
                if (inicio == generacion) avisoOperacion = enCola.message
            } catch (error: OperacionComandaException) {
                if (inicio == generacion) {
                    errorOperacion = error.message
                    onError()
                }
            } finally {
                if (inicio == generacion) {
                    operaciones.remove(clave)
                    cargando = operaciones.isNotEmpty()
                    operacionSeq++
                }
            }
        }
    }

    /** Registra un comentario en la comanda [id] y recarga su detalle actualizado. */
    fun agregarComentario(id: String, texto: String) = mutarPedido(id) { repository.comentar(id, texto) }

    /** Registra una alerta de bloqueo en la comanda [id] y recarga su detalle actualizado. */
    fun registrarAlerta(id: String, tipo: TipoAlerta, comentario: String) =
        mutarPedido(id) { repository.alertar(id, tipo.displayName, comentario) }

    /** Resuelve la alerta activa de la comanda [id] para desbloquear su avance. */
    fun resolverAlerta(id: String) = mutarPedido(id) { pedido(id)?.let { repository.resolver(it) } }

    private fun mutarPedido(id: String, accion: suspend () -> Unit) = ejecutar("mutacion:$id") {
        revision++
        accion()
        repository.detalle(id)?.let(::actualizarPedido)
    }

    /** Busca una comanda por su código visible en la caché local (sin distinguir mayúsculas). */
    fun pedidoPorComanda(codigo: String): Pedido? {
        val q = codigo.trim()
        if (q.isEmpty()) return null
        return _pedidos.firstOrNull { it.comanda.equals(q, ignoreCase = true) }
    }

    /** Usada por el escaneo simulado: prioriza una activa sin bloqueo, luego cualquiera no finalizada. */
    fun siguientePendiente(): Pedido? =
        _pedidos.firstOrNull { !it.esFinal && !it.estaBloqueado }
            ?: _pedidos.firstOrNull { !it.esFinal }
            ?: _pedidos.firstOrNull()

}
