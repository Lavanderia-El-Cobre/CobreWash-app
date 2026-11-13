package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.ConteoGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EtapaConfig
import com.elcobre.lavanderiaelcobre.data.model.FilaReporte
import com.elcobre.lavanderiaelcobre.data.model.MovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.NuevoInsumoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaMovimientos
import com.elcobre.lavanderiaelcobre.data.model.ReporteGestion
import com.elcobre.lavanderiaelcobre.data.model.ResumenPanel
import com.elcobre.lavanderiaelcobre.data.model.RolRef
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.TipoMovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.UsuarioGestion
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.minutosAtraso
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoConteoInventario
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetPanelProduccionQuery
import com.elcobre.lavanderiaelcobre.dataconnect.IncidenciaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.TipoCliente
import com.elcobre.lavanderiaelcobre.dataconnect.TipoMovimiento
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.dataconnect.QueryRef
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.tasks.await

actual fun crearAdministracionRepository(): AdministracionRepository = FirebaseAdministracionRepository()

private val SERVIDOR = QueryRef.FetchPolicy.SERVER_ONLY

private fun GetPanelProduccionQuery.Data.ComandasItem.aGestion(): ComandaGestion = ComandaGestion(
    id = id.toString(),
    numero = numeroComanda,
    estado = EstadoComanda.desdeCodigo(estado.stringValue),
    cliente = ClienteFicha(cliente.id.toString(), cliente.nombre, TipoClienteFicha.desdeCodigo(cliente.tipoCliente.stringValue)),
    valorTotal = valorTotal,
    fechaRecepcion = fechaHora(fechaRecepcion),
    fechaRecepcionMs = fechaRecepcion.millis(),
    actualizadoEn = versionDe(actualizadoEn),
    lineas = emptyList(),
    etapas = normalizarEtapas(
        comandaEtapas_on_comanda.map {
            EtapaCruda(
                it.etapaId.toString(), it.nombreEtapa, it.ordenEtapa, it.tiempoEstimadoMin, it.estado.stringValue,
                it.fechaInicio, it.fechaCompletado,
                it.operario?.let { o -> nombreCompleto(o.nombre, o.apellido) },
                it.asignadoA?.let { a -> nombreCompleto(a.nombre, a.apellido) }, it.asignadoA?.id,
                it.etapa.nombre, it.etapa.orden, it.etapa.tiempoEstimadoMin,
            )
        },
    ),
    incidenciasAbiertas = incidenciaComandas_on_comanda.count { it.estado.value != IncidenciaEstado.RESUELTA },
)

private fun mensajeAuth(error: FirebaseAuthException): String = when (error) {
    is FirebaseAuthUserCollisionException -> "Ese correo ya tiene una cuenta registrada."
    is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil."
    else -> when (error.errorCode) {
        "ERROR_INVALID_EMAIL" -> "El correo no es válido."
        else -> "No se pudo crear la cuenta en Firebase Authentication."
    }
}

/** Funciones de administración de la web adaptadas a la app (panel, usuarios, inventario, reportes). */
@Suppress("TooManyFunctions")
private class FirebaseAdministracionRepository : AdministracionRepository {
    private val dc get() = ExampleConnector.instance

    override suspend fun resumenPanel(): ResumenPanel = solicitud {
        val data = dc.getPanelProduccion.ref { limit = 100 }.execute(SERVIDOR).data
        val comandas = data.comandas.map { it.aGestion() }
        val ahora = System.currentTimeMillis()
        ResumenPanel(
            pendientes = data.pendientes.firstOrNull()?._count ?: 0,
            enProceso = data.enProceso.firstOrNull()?._count ?: 0,
            listas = data.listas.firstOrNull()?._count ?: 0,
            conIncidencias = comandas.count { it.incidenciasAbiertas > 0 },
            conAtraso = comandas.count { minutosAtraso(it, ahora) > 0 },
            recientes = comandas,
        )
    }

    override suspend fun roles(): List<RolRef> = solicitud {
        dc.getRoles.ref().execute(SERVIDOR).data.rols.map { RolRef(it.id.toString(), it.nombre, it.descripcion) }
    }

    override suspend fun usuarios(): List<UsuarioGestion> = solicitud {
        dc.getUsuarios.ref().execute(SERVIDOR).data.usuarios.map {
            UsuarioGestion(
                id = it.id, nombre = it.nombre, apellido = it.apellido, email = it.email, rut = it.rut,
                telefono = it.telefono, activo = it.activo, rol = RolRef(it.rol.id.toString(), it.rol.nombre),
                creadoEn = fechaCorta(it.creadoEn),
            )
        }
    }

    /**
     * Igual que `createManagedUser` de la web: crea la cuenta en una instancia secundaria
     * de Firebase para no cerrar la sesión del administrador, registra el perfil y, si el
     * perfil falla, elimina la cuenta recién creada.
     */
    override suspend fun crearUsuario(input: NuevoUsuarioInput) {
        val principal = FirebaseApp.getInstance()
        val secundaria = FirebaseApp.initializeApp(principal.applicationContext, principal.options, "alta-${UUID.randomUUID()}")
        try {
            val auth = FirebaseAuth.getInstance(secundaria)
            val cuenta = try {
                auth.createUserWithEmailAndPassword(input.email.trim(), input.contrasena).await().user
                    ?: throw OperacionComandaException("No se pudo crear la cuenta.")
            } catch (error: FirebaseAuthException) {
                throw OperacionComandaException(mensajeAuth(error), error)
            } catch (error: com.google.firebase.FirebaseException) {
                throw OperacionComandaException("No se pudo conectar con Firebase. Verifica tu conexión.", error)
            }
            try {
                registrarPerfil(cuenta.uid, input)
            } catch (error: OperacionComandaException) {
                runCatching { cuenta.delete().await() }
                throw error
            }
            auth.signOut()
        } finally {
            secundaria.delete()
        }
    }

    private suspend fun registrarPerfil(uid: String, input: NuevoUsuarioInput): Unit = solicitud {
        val rut = Validaciones.formatearRut(input.rut)
        val telefono = input.telefono.trim().ifEmpty { null }?.let(Validaciones::formatearTelefono)
        if (input.rolNombre == "cliente") {
            val tipo = if (input.tipoCliente == TipoClienteFicha.HOTEL) TipoCliente.HOTEL else TipoCliente.PARTICULAR
            dc.crearClienteAdministrado.execute(uid, rut, input.nombre.trim(), input.apellido.trim(), input.email.trim(), tipo) {
                this.telefono = telefono
                direccion = input.direccion.trim().ifEmpty { null }
            }
        } else {
            dc.crearUsuarioAdministrado.execute(
                uid, UUID.fromString(input.rolId), rut, input.nombre.trim(), input.apellido.trim(), input.email.trim(),
            ) { this.telefono = telefono }
        }
    }

    override suspend fun actualizarUsuario(input: EdicionUsuarioInput): Unit = solicitud {
        dc.actualizarUsuario.execute(input.id, UUID.fromString(input.rolId), input.nombre.trim(), input.activo) {
            apellido = input.apellido?.trim()?.ifEmpty { null }
            telefono = input.telefono?.trim()?.ifEmpty { null }
        }
    }

    override suspend fun stock(): List<InsumoInventario> = solicitud {
        dc.getStockInventario.ref().execute(SERVIDOR).data.insumos.map {
            InsumoInventario(
                it.id.toString(), it.codigoQr.toString(), it.nombre, it.unidadMedida,
                it.stockActual, it.stockMinimo, it.activo, it.revisionStock,
            )
        }
    }

    override suspend fun crearInsumo(input: NuevoInsumoInput): Unit = solicitud {
        dc.crearInsumo.execute(input.nombre.trim(), input.unidad.trim(), input.stockInicial, input.stockMinimo)
    }

    override suspend fun actualizarInsumo(insumo: InsumoInventario): Unit = solicitud {
        dc.actualizarInsumo.execute(
            UUID.fromString(insumo.id),
            insumo.nombre.trim(),
            insumo.unidad.trim(),
            insumo.stockMinimo,
            insumo.activo,
        )
    }

    override suspend fun registrarMovimiento(insumoId: String, tipo: TipoMovimientoInventario, cantidad: Double, motivo: String?) {
        solicitud {
            val id = UUID.fromString(insumoId)
            val nota = motivo?.trim()?.ifEmpty { null }?.take(200)
            if (tipo == TipoMovimientoInventario.ENTRADA) {
                dc.registrarEntradaInventario.execute(id, cantidad) { this.motivo = nota }
            } else {
                dc.registrarSalidaInventario.execute(id, cantidad) { this.motivo = nota }
            }
        }
    }

    override suspend fun historial(insumoId: String?, offset: Int, limit: Int): PaginaMovimientos = solicitud {
        val data = dc.getHistorialInventario.ref {
            if (insumoId != null) this.insumoId = UUID.fromString(insumoId)
            this.offset = offset
            this.limit = limit
        }.execute(SERVIDOR).data
        PaginaMovimientos(
            movimientos = data.movimientoInventarios.map {
                MovimientoInventario(
                    id = it.id.toString(),
                    tipo = if (it.tipoMovimiento.value == TipoMovimiento.SALIDA) {
                        TipoMovimientoInventario.SALIDA
                    } else {
                        TipoMovimientoInventario.ENTRADA
                    },
                    cantidad = it.cantidad, motivo = it.motivo, fecha = fechaHora(it.fecha),
                    insumo = it.insumo.nombre, unidad = it.insumo.unidadMedida,
                    usuario = it.usuario?.let { u -> nombreCompleto(u.nombre, u.apellido) },
                )
            },
            total = data.total.firstOrNull()?._count ?: 0,
        )
    }

    override suspend fun conteos(estado: String?): List<ConteoGestion> = solicitud {
        dc.getConteosInventario.ref {
            if (estado != null) this.estado = EstadoConteoInventario.valueOf(estado)
            limit = 100
        }.execute(SERVIDOR).data.conteoInventarios.map {
            ConteoGestion(
                id = it.id.toString(), insumoId = it.insumo.id.toString(), insumo = it.insumo.nombre, unidad = it.insumo.unidadMedida,
                cantidadContada = it.cantidadContada, stockSistema = it.stockSistema, stockActual = it.insumo.stockActual,
                revisionConteo = it.revisionStock, revisionActual = it.insumo.revisionStock, estado = it.estado.stringValue,
                registradoPor = nombreCompleto(it.registradoPor.nombre, it.registradoPor.apellido),
                creadoEn = fechaHora(it.creadoEn),
                resueltoPor = it.resueltoPor?.let { r -> nombreCompleto(r.nombre, r.apellido) },
                motivoResolucion = it.motivoResolucion,
            )
        }
    }

    override suspend fun aprobarConteo(id: String, motivo: String): Unit = solicitud {
        dc.aprobarConteoInventario.execute(UUID.fromString(id), motivo.trim().take(200))
    }

    override suspend fun rechazarConteo(id: String, motivo: String): Unit = solicitud {
        dc.rechazarConteoInventario.execute(UUID.fromString(id), motivo.trim().take(200))
    }

    override suspend fun reporte(desdeMs: Long, hastaMs: Long): ReporteGestion = solicitud {
        val desde = Timestamp(Date(desdeMs))
        val hasta = Timestamp(Date(hastaMs))
        val cuentas = dc.getReporteCuentas.ref(desde, hasta).execute(SERVIDOR).data
        val prendasPorCuenta = cuentas.prendas.groupBy { "${it.comanda.cliente.id}|${it.comanda.empresa.orEmpty()}" }
            .mapValues { (_, filas) -> filas.sumOf { it.cantidad_sum ?: 0 } }
        val servicios = dc.getReporteServicios.ref(desde, hasta).execute(SERVIDOR).data.servicios
        val cerradas = dc.getReporteVolumen.ref(desde, hasta).execute(SERVIDOR).data.comandas.size
        ReporteGestion(
            desde = fechaCorta(desde),
            hasta = fechaCorta(Timestamp(Date(hastaMs - 1))),
            porCuenta = cuentas.cuentas.map {
                val clave = "${it.cliente.id}|${it.empresa.orEmpty()}"
                val etiqueta = it.empresa?.takeIf { e -> e.isNotBlank() }?.let { e -> "${it.cliente.nombre} · $e" } ?: it.cliente.nombre
                FilaReporte(etiqueta, it._count, prendasPorCuenta[clave] ?: 0, it.valorTotal_sum ?: 0.0)
            }.sortedByDescending { it.monto },
            porServicio = servicios.map {
                FilaReporte(it.tipoServicio.nombre, it.comandaId_count, it.cantidad_sum ?: 0, it.subtotal_sum ?: 0.0)
            }.sortedByDescending { it.monto },
            cerradas = cerradas,
        )
    }

    override suspend fun etapas(): List<EtapaConfig> = solicitud {
        dc.getEtapasProduccion.ref().execute(SERVIDOR).data.etapaProduccions.map {
            EtapaConfig(it.id.toString(), it.nombre, it.orden, it.descripcion, it.tiempoEstimadoMin)
        }.sortedBy { it.orden }
    }

    override suspend fun configurarEtapa(etapa: EtapaConfig): Unit = solicitud {
        dc.configurarEtapaProduccion.execute(UUID.fromString(etapa.id), etapa.nombre.trim()) {
            descripcion = etapa.descripcion?.trim()?.ifEmpty { null }
            tiempoEstimadoMin = etapa.tiempoEstimadoMin
        }
    }
}

actual fun crearCuentaRepository(): CuentaRepository = CuentaRepository { actual, nueva ->
    val usuario = FirebaseAuth.getInstance().currentUser ?: throw OperacionComandaException("Inicia sesión nuevamente.")
    val correo = usuario.email ?: throw OperacionComandaException("La cuenta no tiene un correo asociado.")
    try {
        usuario.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(correo, actual)).await()
        usuario.updatePassword(nueva).await()
    } catch (error: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
        throw OperacionComandaException("La contraseña actual no es correcta.", error)
    } catch (error: FirebaseAuthException) {
        throw OperacionComandaException("No se pudo cambiar la contraseña. Intenta nuevamente.", error)
    } catch (error: com.google.firebase.FirebaseException) {
        throw OperacionComandaException("Sin conexión. Verifica tu red e intenta nuevamente.", error)
    }
}
