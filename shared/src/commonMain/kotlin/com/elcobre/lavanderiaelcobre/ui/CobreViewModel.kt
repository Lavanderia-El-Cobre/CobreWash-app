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
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.auth.restaurarPerfilAutenticado
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.screens.login.sesionParaRol
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema

/** Destinos de nivel superior: navegar a uno de estos reemplaza la pila en vez de apilar (semántica de bottom-nav). */
private val TABS: Set<Pantalla> = setOf(
    Pantalla.Dashboard,
    Pantalla.Avisos,
    Pantalla.Insumos,
    Pantalla.Configuracion,
    Pantalla.Vehiculos,
)

/**
 * Dueño del estado de la app del Operario: pedidos, avisos, catálogo de insumos,
 * sesión y navegación son responsabilidades cohesivas de un mismo flujo, así que
 * superan el umbral por defecto de funciones por clase a propósito (el estado de
 * vehículos, que pertenece a una sesión de rol distinto, vive aparte en
 * [VehiculosViewModel]). Los modelos de dominio ([Pedido]) son inmutables; este
 * holder mantiene la lista observable y reemplaza la instancia afectada por su
 * copia actualizada en cada operación.
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
    var cargando by mutableStateOf(false)
        private set
    private var generacion = 0
    private var revision = 0
    private val operaciones = mutableSetOf<String>()

    private val _pedidos: SnapshotStateList<Pedido> = mutableStateListOf()

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

    val avisosNoLeidos: Int get() = avisos.count { it.id !in idsAvisosVistos }

    fun marcarAvisosComoLeidos() {
        idsAvisosVistos = idsAvisosVistos + avisos.map { it.id }
    }

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


    var sesion by mutableStateOf<Sesion?>(null)
        private set

    var qrPendiente by mutableStateOf<String?>(null)
        private set

    var modoTema by mutableStateOf(ModoTema.CLARO)
        private set

    fun cambiarModoTema(modo: ModoTema) { modoTema = modo }

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

    /** Los destinos de [TABS] reemplazan la pila (como tabs de bottom-nav); el resto se apila. */
    internal fun navegar(destino: Pantalla) {
        if (destino in TABS) {
            _backStack.clear()
            _backStack.add(destino)
        } else {
            _backStack.add(destino)
        }
        if (destino == Pantalla.Avisos) marcarAvisosComoLeidos()
    }

    val puedeVolver: Boolean get() {
        if (sesion != null && _backStack.size == 2 && _backStack.first() == Pantalla.Login) {
            return false
        }
        return _backStack.size > 1
    }

    /** Regresa al destino anterior real de la pila; no hace nada si ya está en la raíz ni al login con sesión activa. */
    internal fun volver() {
        if (_backStack.size > 1) {
            if (sesion != null && _backStack.size == 2 && _backStack.first() == Pantalla.Login) {
                return
            }
            _backStack.removeAt(_backStack.lastIndex)
        }
    }

    fun restaurarSesionSiExiste() {
        if (SesionPersistente.obtener() == null) return
        val inicio = generacion
        scope.launch {
            val perfil = restaurarPerfil()
            if (inicio != generacion) return@launch
            val valida = (perfil as? PerfilResultado.Exito)?.let {
                sesionParaRol(it.rolNombre, it.nombre)
            }
            if (valida != null) iniciarSesion(valida)
            else {
                errorOperacion = (perfil as? PerfilResultado.Error)?.mensaje
                    ?: "La sesión guardada ya no es válida. Inicia sesión nuevamente."
                if (perfil == null) SesionPersistente.limpiar()
            }
        }
    }

    init {
        restaurarSesionSiExiste()
    }

    fun iniciarSesion(s: Sesion) {
        generacion++
        scope.coroutineContext.cancelChildren()
        operaciones.clear()
        _pedidos.clear()
        idsComandasConocidas = emptySet()
        comandasInicializadas = false
        sesion = s
        limpiarAvisos()
        inventario.cambiarSesion(s is Sesion.DeOperario)

        val destino = when (s) {
            is Sesion.DeOperario -> {
                refrescarComandas()
                Pantalla.Dashboard
            }
            is Sesion.DeAdministrador -> Pantalla.Vehiculos
        }

        _backStack.clear()
        _backStack.add(destino)

        if (qrPendiente != null) {
            _backStack.add(Pantalla.Escaner)
        }
    }

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
        cargando = false
        qrPendiente = null
        sesion = null
        _backStack.clear()
        _backStack.add(Pantalla.Login)
    }

    fun pedido(id: String): Pedido? = _pedidos.firstOrNull { it.id == id }

    fun avanzarEtapa(id: String, comentario: String) {
        val i = _pedidos.indexOfFirst { it.id == id }
        if (i == -1) return
        val pedido = _pedidos[i]

        if (pedido.estaBloqueado) return
        ejecutar("avance:$id") {
            revision++
            repository.avanzar(pedido)
            val actualizado = repository.detalle(id)
                ?: throw OperacionComandaException("El avance se guardó, pero no se pudo cargar el detalle.")
            actualizarPedido(actualizado)
            if (comentario.isNotBlank()) {
                repository.comentar(id, comentario)
                repository.detalle(id)?.let(::actualizarPedido)
            }
        }
    }

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

    fun buscarNumero(numero: String, onExito: (Pedido) -> Unit, onError: () -> Unit) = ejecutar("buscar", onError) {
        val encontrado = repository.buscarNumero(numero)
            ?: throw OperacionComandaException("No se encontró la comanda.")
        actualizarPedido(encontrado)
        onExito(encontrado)
    }

    private fun actualizarPedido(pedido: Pedido) {
        val indice = _pedidos.indexOfFirst { it.id == pedido.id }
        if (indice >= 0) _pedidos[indice] = pedido else _pedidos.add(0, pedido)
    }

    private fun ejecutar(clave: String, onError: () -> Unit = {}, accion: suspend () -> Unit) {
        if (!operaciones.add(clave)) return
        val inicio = generacion
        cargando = true
        errorOperacion = null
        scope.launch {
            try {
                accion()
            } catch (error: OperacionComandaException) {
                if (inicio == generacion) {
                    errorOperacion = error.message
                    onError()
                }
            } finally {
                if (inicio == generacion) {
                    operaciones.remove(clave)
                    cargando = operaciones.isNotEmpty()
                }
            }
        }
    }

    fun agregarComentario(id: String, texto: String) = mutarPedido(id) { repository.comentar(id, texto) }

    fun registrarAlerta(id: String, tipo: TipoAlerta, comentario: String) =
        mutarPedido(id) { repository.alertar(id, tipo.displayName, comentario) }

    fun resolverAlerta(id: String) = mutarPedido(id) { pedido(id)?.let { repository.resolver(it) } }

    private fun mutarPedido(id: String, accion: suspend () -> Unit) = ejecutar("mutacion:$id") {
        revision++
        accion()
        repository.detalle(id)?.let(::actualizarPedido)
    }


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
