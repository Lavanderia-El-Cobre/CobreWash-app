package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

/**
 * Base de los ViewModels de recepción, administración y reparto: lectura con
 * [cargar] (no bloquea la pantalla) y escrituras con [operar] (una a la vez, con
 * mensaje de confirmación). Un cambio de sesión invalida todo lo que esté en vuelo
 * para que los datos de una cuenta nunca aparezcan en la siguiente.
 */
abstract class GestionViewModel(private val scopeOverride: CoroutineScope? = null) : ViewModel() {
    protected val scope: CoroutineScope get() = scopeOverride ?: viewModelScope

    var cargando by mutableStateOf(false)
        private set
    var guardando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        protected set
    var mensaje by mutableStateOf<String?>(null)
        protected set

    private var generacion = 0
    private var lecturas = 0
    private var sesion: Any? = null

    /**
     * Asocia el modelo a la sesión activa. Los ViewModels sobreviven al cierre de sesión,
     * así que al entrar otra cuenta (otra instancia de sesión) se descarta todo lo anterior.
     */
    fun vincularSesion(nueva: Any) {
        if (sesion === nueva) return
        if (sesion != null) reiniciar()
        sesion = nueva
    }

    /** Lectura: muestra el progreso y, si falla, el mensaje del servidor. */
    protected fun cargar(accion: suspend () -> Unit) {
        val inicio = generacion
        lecturas++
        cargando = true
        scope.launch {
            try {
                accion()
                if (inicio == generacion) error = null
            } catch (fallo: OperacionComandaException) {
                if (inicio == generacion) error = fallo.message ?: "No se pudieron cargar los datos."
            } finally {
                if (inicio == generacion) {
                    lecturas--
                    cargando = lecturas > 0
                }
            }
        }
    }

    /**
     * Escritura: se ignora si ya hay otra en curso (evita dobles envíos). [alTerminar]
     * recibe `null` si salió bien o el mensaje de error, para que el formulario lo muestre.
     */
    protected fun operar(confirmacion: String?, alTerminar: (String?) -> Unit = {}, accion: suspend () -> Unit) {
        if (guardando) return
        val inicio = generacion
        guardando = true
        error = null
        scope.launch {
            try {
                accion()
                if (inicio == generacion) {
                    if (confirmacion != null) mensaje = confirmacion
                    alTerminar(null)
                }
            } catch (fallo: OperacionComandaException) {
                if (inicio == generacion) alTerminar(fallo.message ?: "No se pudo completar la operación.")
            } finally {
                if (inicio == generacion) guardando = false
            }
        }
    }

    fun limpiarMensajes() {
        mensaje = null
        error = null
    }

    /** Descarta operaciones pendientes y el estado propio al cerrar o cambiar de sesión. */
    open fun reiniciar() {
        generacion++
        scopeOverride?.coroutineContext?.cancelChildren() ?: viewModelScope.coroutineContext.cancelChildren()
        lecturas = 0
        cargando = false
        guardando = false
        error = null
        mensaje = null
    }
}

/** Hora actual en milisegundos desde la época (atrasos de producción, periodos de reportes). */
@OptIn(kotlin.time.ExperimentalTime::class)
fun ahoraMs(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
