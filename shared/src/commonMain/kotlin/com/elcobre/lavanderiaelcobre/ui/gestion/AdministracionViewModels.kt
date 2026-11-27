package com.elcobre.lavanderiaelcobre.ui.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.AdministracionRepository
import com.elcobre.lavanderiaelcobre.data.ComunicacionRepository
import com.elcobre.lavanderiaelcobre.data.EdicionUsuarioInput
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.data.crearAdministracionRepository
import com.elcobre.lavanderiaelcobre.data.crearComunicacionRepository
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.ConteoGestion
import com.elcobre.lavanderiaelcobre.data.model.EtapaConfig
import com.elcobre.lavanderiaelcobre.data.model.MovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.NuevoAvisoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoInsumoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.Telefonos
import com.elcobre.lavanderiaelcobre.data.model.PeriodoReporte
import com.elcobre.lavanderiaelcobre.data.model.ReporteGestion
import com.elcobre.lavanderiaelcobre.data.model.ResumenPanel
import com.elcobre.lavanderiaelcobre.data.model.RolRef
import com.elcobre.lavanderiaelcobre.data.model.TipoMovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.UsuarioGestion
import com.elcobre.lavanderiaelcobre.data.model.validarAviso
import com.elcobre.lavanderiaelcobre.data.model.validarEdicionUsuario
import com.elcobre.lavanderiaelcobre.data.model.validarInsumo
import com.elcobre.lavanderiaelcobre.data.model.validarMovimiento
import com.elcobre.lavanderiaelcobre.data.model.validarNuevoUsuario
import kotlinx.coroutines.CoroutineScope

/**
 * Avisos filtrados por rol. Recepción lee únicamente los dirigidos a su equipo;
 * administración ([rolEquipo] nulo) consulta todos los publicados y puede emitir nuevos
 * (`/intranet/comunicacion`).
 */
class AvisosEquipoViewModel(
    private val rolEquipo: String?,
    private val repository: ComunicacionRepository = crearComunicacionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var avisos by mutableStateOf<List<Aviso>>(emptyList())
        private set
    var total by mutableStateOf(0)
        private set
    private var limite = PAGINA_COMANDAS
    private var consulta = 0

    // Identificadores marcados como leídos en la sesión actual para la insignia local.
    private var vistos by mutableStateOf<Set<String>>(emptySet())

    /** Solo administración (sin filtro de rol de equipo) puede publicar avisos. */
    val puedePublicar: Boolean get() = rolEquipo == null
    val hayMas: Boolean get() = avisos.size < total
    val noLeidos: Int get() = avisos.count { it.id !in vistos }

    /** Recarga los avisos; si [mas] es `true`, amplía el límite en una página adicional. */
    fun refrescar(mas: Boolean = false) {
        if (mas && (cargando || !hayMas)) return
        if (mas) limite += PAGINA_COMANDAS
        val solicitud = ++consulta
        val limiteActual = limite
        cargar {
            val pagina = if (rolEquipo == null) {
                repository.avisosAdministracion(0, limiteActual)
            } else {
                repository.avisosEquipo(rolEquipo, 0, limiteActual)
            }
            if (solicitud == consulta) {
                avisos = pagina.avisos
                total = pagina.total
            }
        }
    }

    /** Marca como leídos los avisos actualmente cargados para limpiar la insignia. */
    fun marcarLeidos() {
        vistos = vistos + avisos.map { it.id }
    }

    /**
     * Valida y publica un aviso institucional; [alTerminar] recibe el error si falla.
     * Solo se ejecuta cuando [puedePublicar] es `true`.
     */
    fun publicar(input: NuevoAvisoInput, alTerminar: (String?) -> Unit) {
        if (!puedePublicar) return
        validarAviso(input)?.let {
            alTerminar(it)
            return
        }
        operar("Aviso publicado correctamente.", alTerminar = { problema ->
            alTerminar(problema?.let { "No se pudo confirmar la publicación. Revisa los avisos antes de volver a intentarlo." })
            if (problema == null) refrescar()
        }) { repository.publicarAviso(input) }
    }

    override fun reiniciar() {
        super.reiniciar()
        consulta++
        avisos = emptyList()
        total = 0
        limite = PAGINA_COMANDAS
        vistos = emptySet()
    }
}

/** Panel de inicio del administrador (`/intranet`): producción activa, atrasos e incidencias. */
class PanelViewModel(
    private val repository: AdministracionRepository = crearAdministracionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var resumen by mutableStateOf<ResumenPanel?>(null)
        private set

    /** Obtiene los indicadores actualizados de producción, atrasos e incidencias abiertas. */
    fun refrescar() = cargar { resumen = repository.resumenPanel() }

    override fun reiniciar() {
        super.reiniciar()
        resumen = null
    }
}

/** Gestión de cuentas (`/intranet/usuarios`): alta, edición de rol y datos, y activación. */
class UsuariosViewModel(
    private val repository: AdministracionRepository = crearAdministracionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var usuarios by mutableStateOf<List<UsuarioGestion>>(emptyList())
        private set
    var roles by mutableStateOf<List<RolRef>>(emptyList())
        private set
    var busqueda by mutableStateOf("")
        private set
    var filtroActivo by mutableStateOf<Boolean?>(null)
        private set

    /** Cuentas filtradas localmente por estado activo/inactivo y texto en nombre o correo. */
    val filtrados: List<UsuarioGestion>
        get() {
            val q = busqueda.trim().lowercase()
            return usuarios.filter { u ->
                (filtroActivo == null || u.activo == filtroActivo) &&
                    (q.isEmpty() || u.nombreCompleto.lowercase().contains(q) || u.email.lowercase().contains(q))
            }
        }

    /** Carga el catálogo de roles disponibles y el listado completo de cuentas. */
    fun refrescar(limpiarError: Boolean = true) = cargar(limpiarError = limpiarError) {
        roles = repository.roles()
        usuarios = repository.usuarios()
    }

    fun buscar(texto: String) {
        busqueda = texto
    }

    fun filtrar(activo: Boolean?) {
        filtroActivo = activo
    }

    /**
     * Valida y crea una cuenta nueva. Si el alta tiene éxito pero falla el envío de correo
     * de invitación, igualmente refresca la lista e indica la alternativa de recuperación.
     */
    fun crear(input: NuevoUsuarioInput, alTerminar: (String?) -> Unit) {
        validarNuevoUsuario(input)?.let {
            alTerminar(it)
            return
        }
        operar(null, alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) {
            val email = input.email.trim()
            mensaje = if (repository.crearUsuario(input)) {
                "Cuenta de ${input.nombre.trim()} creada. Enviamos a $email un enlace para activarla y crear su contraseña."
            } else {
                "Cuenta de ${input.nombre.trim()} creada, pero no se pudo enviar la invitación a $email. " +
                    "Pídele usar \"¿Olvidaste tu contraseña?\" para activarla."
            }
        }
    }

    /** Valida rol, nombre y teléfono antes de persistir cambios sobre una cuenta existente. */
    fun editar(input: EdicionUsuarioInput, alTerminar: (String?) -> Unit) {
        (validarEdicionUsuario(input.rolId, input.nombre) ?: Telefonos.validar(input.telefono.orEmpty()))?.let {
            alTerminar(it)
            return
        }
        operar("Datos de ${input.nombre.trim()} actualizados.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) { repository.actualizarUsuario(input) }
    }

    /** Invierte el estado activo/inactivo de [usuario] y sincroniza la lista tras responder. */
    fun alternarActivo(usuario: UsuarioGestion) {
        val input = EdicionUsuarioInput(usuario.id, usuario.rol.id, usuario.nombre, usuario.apellido, usuario.telefono, !usuario.activo)
        val confirmacion = if (usuario.activo) "${usuario.nombreCompleto} quedó inactivo." else "${usuario.nombreCompleto} quedó activo."
        operar(confirmacion, alTerminar = {
            if (it != null) error = it
            refrescar(limpiarError = it == null)
        }) { repository.actualizarUsuario(input) }
    }

    override fun reiniciar() {
        super.reiniciar()
        usuarios = emptyList()
        roles = emptyList()
        busqueda = ""
        filtroActivo = null
    }
}

/** Inventario de administración (`/intranet/inventario`): stock, movimientos, conteos e historial. */
@Suppress("TooManyFunctions")
class StockViewModel(
    private val repository: AdministracionRepository = crearAdministracionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var insumos by mutableStateOf<List<InsumoInventario>>(emptyList())
        private set
    var conteosPendientes by mutableStateOf<List<ConteoGestion>>(emptyList())
        private set
    var conteosResueltos by mutableStateOf<List<ConteoGestion>>(emptyList())
        private set
    var movimientos by mutableStateOf<List<MovimientoInventario>>(emptyList())
        private set
    var totalMovimientos by mutableStateOf(0)
        private set
    var busqueda by mutableStateOf("")
        private set
    private var limiteMovimientos = PAGINA_COMANDAS
    private var consultaMovimientos = 0

    val filtrados: List<InsumoInventario>
        get() = busqueda.trim().lowercase().let { q -> if (q.isEmpty()) insumos else insumos.filter { it.nombre.lowercase().contains(q) } }

    /** Cantidad de insumos activos en estado bajo o crítico respecto a su stock mínimo. */
    val bajoMinimo: Int get() = insumos.count { it.activo && it.estado != "OK" }
    val hayMasMovimientos: Boolean get() = movimientos.size < totalMovimientos

    /** Recarga el stock actual, los conteos pendientes y los 20 conteos resueltos más recientes. */
    fun refrescar() = cargar {
        insumos = repository.stock()
        conteosPendientes = repository.conteos("PENDIENTE")
        conteosResueltos = repository.conteos(null).filter { it.estado != "PENDIENTE" }.take(20)
    }

    /** Carga el historial de movimientos de inventario; con [mas] amplía una página más. */
    fun refrescarMovimientos(mas: Boolean = false) {
        if (mas && (cargando || !hayMasMovimientos)) return
        if (mas) limiteMovimientos += PAGINA_COMANDAS
        val solicitud = ++consultaMovimientos
        val limite = limiteMovimientos
        cargar {
            val pagina = repository.historial(null, 0, limite)
            if (solicitud == consultaMovimientos) {
                movimientos = pagina.movimientos
                totalMovimientos = pagina.total
            }
        }
    }

    fun buscar(texto: String) {
        busqueda = texto
    }

    /** Valida y registra un insumo nuevo en catálogo; el servidor genera su código QR. */
    fun crearInsumo(input: NuevoInsumoInput, alTerminar: (String?) -> Unit) {
        validarInsumo(input.nombre, input.unidad, input.stockMinimo, input.stockInicial)?.let {
            alTerminar(it)
            return
        }
        operar("Insumo ${input.nombre.trim()} registrado.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) { repository.crearInsumo(input) }
    }

    /** Valida y guarda cambios de nombre, unidad, mínimo o vigencia de un insumo existente. */
    fun actualizarInsumo(insumo: InsumoInventario, alTerminar: (String?) -> Unit) {
        validarInsumo(insumo.nombre, insumo.unidad, insumo.stockMinimo)?.let {
            alTerminar(it)
            return
        }
        operar("Insumo ${insumo.nombre.trim()} actualizado.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) { repository.actualizarInsumo(insumo) }
    }

    /** Registra una entrada o salida manual de stock y actualiza tanto inventario como historial. */
    fun registrarMovimiento(
        insumo: InsumoInventario,
        tipo: TipoMovimientoInventario,
        cantidad: Double?,
        motivo: String,
        alTerminar: (String?) -> Unit,
    ) {
        validarMovimiento(cantidad, insumo.stockActual, tipo)?.let {
            alTerminar(it)
            return
        }
        operar("${tipo.etiqueta} de ${insumo.nombre} registrada.", alTerminar = {
            alTerminar(it)
            if (it == null) {
                refrescar()
                refrescarMovimientos()
            }
        }) { repository.registrarMovimiento(insumo.id, tipo, requireNotNull(cantidad), motivo) }
    }

    /**
     * Aprueba o rechaza un conteo físico enviado desde planta. Si el stock del sistema cambió
     * después de tomar el conteo (`desactualizado`), se bloquea la aprobación para evitar
     * sobrescribir movimientos posteriores.
     */
    fun resolverConteo(conteo: ConteoGestion, aprobar: Boolean, motivo: String, alTerminar: (String?) -> Unit) {
        if (motivo.isBlank()) {
            alTerminar("Escribe el motivo de la resolución.")
            return
        }
        if (aprobar && conteo.desactualizado) {
            alTerminar("El stock cambió después del conteo. Recházalo y pide un conteo nuevo.")
            return
        }
        operar(if (aprobar) "Conteo aprobado: stock ajustado." else "Conteo rechazado.", alTerminar = {
            alTerminar(it)
            if (it == null) refrescar()
        }) {
            if (aprobar) repository.aprobarConteo(conteo.id, motivo) else repository.rechazarConteo(conteo.id, motivo)
        }
    }

    override fun reiniciar() {
        super.reiniciar()
        consultaMovimientos++
        insumos = emptyList()
        conteosPendientes = emptyList()
        conteosResueltos = emptyList()
        movimientos = emptyList()
        totalMovimientos = 0
        busqueda = ""
        limiteMovimientos = PAGINA_COMANDAS
    }
}

/** Reportes de administración (`/intranet/reportes`) por periodo: cuentas, servicios y cierres. */
class ReportesViewModel(
    private val repository: AdministracionRepository = crearAdministracionRepository(),
    scopeOverride: CoroutineScope? = null,
    private val ahora: () -> Long = ::ahoraMs,
) : GestionViewModel(scopeOverride) {
    var periodo by mutableStateOf(PeriodoReporte.MES)
        private set
    var reporte by mutableStateOf<ReporteGestion?>(null)
        private set

    /** Consulta el reporte del [periodo] activo descartando respuestas obsoletas si cambió. */
    fun refrescar() {
        val hasta = ahora()
        val desde = hasta - periodo.dias * DIA_MS
        val elegido = periodo
        cargar {
            val resultado = repository.reporte(desde, hasta)
            if (elegido == periodo) reporte = resultado
        }
    }

    /** Cambia el rango temporal y recarga; evita consultas redundantes si ya está cargado. */
    fun elegirPeriodo(nuevo: PeriodoReporte) {
        if (nuevo == periodo && reporte != null) return
        periodo = nuevo
        refrescar()
    }

    override fun reiniciar() {
        super.reiniciar()
        periodo = PeriodoReporte.MES
        reporte = null
    }

    private companion object {
        const val DIA_MS = 86_400_000L
    }
}

/** Etapas del flujo de producción y sus tiempos estimados (configuración de la web). */
class EtapasViewModel(
    private val repository: AdministracionRepository = crearAdministracionRepository(),
    scopeOverride: CoroutineScope? = null,
) : GestionViewModel(scopeOverride) {
    var etapas by mutableStateOf<List<EtapaConfig>>(emptyList())
        private set

    fun refrescar() = cargar { etapas = repository.etapas() }

    /**
     * Valida nombre (hasta 40 caracteres) y tiempo estimado (entre 1 minuto y 30 días)
     * antes de guardar. Los cambios solo afectan a los flujos creados en adelante.
     */
    fun guardar(etapa: EtapaConfig, alTerminar: (String?) -> Unit) {
        val minutos = etapa.tiempoEstimadoMin
        val problema = when {
            etapa.nombre.isBlank() || etapa.nombre.length > 40 -> "Escribe un nombre de hasta 40 caracteres."
            minutos != null && (minutos <= 0 || minutos > MAX_MINUTOS) -> "El tiempo debe estar entre 1 minuto y 30 días."
            else -> null
        }
        if (problema != null) {
            alTerminar(problema)
            return
        }
        operar("Configuración guardada. Se aplicará a los nuevos flujos.", alTerminar = {
            alTerminar(it?.let { "No se pudo guardar la etapa. Revisa que el nombre no esté repetido y el tiempo sea positivo." })
            if (it == null) refrescar()
        }) { repository.configurarEtapa(etapa) }
    }

    override fun reiniciar() {
        super.reiniciar()
        etapas = emptyList()
    }

    private companion object {
        const val MAX_MINUTOS = 43_200
    }
}
