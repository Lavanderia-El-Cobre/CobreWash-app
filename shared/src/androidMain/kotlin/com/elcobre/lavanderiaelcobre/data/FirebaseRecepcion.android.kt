package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EventoComanda
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.IncidenciaGestion
import com.elcobre.lavanderiaelcobre.data.model.LineaComanda
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaComandas
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.TipoPrendaCatalogo
import com.elcobre.lavanderiaelcobre.data.model.TipoServicioCatalogo
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.detalleParaGuardar
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetComandaDetalleQuery
import com.elcobre.lavanderiaelcobre.dataconnect.GetComandasPaginadasQuery
import com.elcobre.lavanderiaelcobre.dataconnect.IncidenciaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.TipoCliente
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID

actual fun crearRecepcionRepository(): RecepcionRepository = FirebaseRecepcionRepository()

private val SERVER = QueryRef.FetchPolicy.SERVER_ONLY

private fun tipoClienteDc(tipo: TipoClienteFicha): TipoCliente =
    if (tipo == TipoClienteFicha.HOTEL) TipoCliente.HOTEL else TipoCliente.PARTICULAR

private fun opcional(valor: String): String? = valor.trim().ifEmpty { null }

private fun GetComandasPaginadasQuery.Data.ComandasItem.ComandaEtapasOnComandaItem.cruda() = EtapaCruda(
    etapaId.toString(), nombreEtapa, ordenEtapa, tiempoEstimadoMin, estado.stringValue, fechaInicio, fechaCompletado,
    operario?.let { nombreCompleto(it.nombre, it.apellido) }, asignadoA?.let { nombreCompleto(it.nombre, it.apellido) }, asignadoA?.id,
    etapa.nombre, etapa.orden, etapa.tiempoEstimadoMin,
)

private fun GetComandaDetalleQuery.Data.Comanda.ComandaEtapasOnComandaItem.cruda() = EtapaCruda(
    etapaId.toString(), nombreEtapa, ordenEtapa, tiempoEstimadoMin, estado.stringValue, fechaInicio, fechaCompletado,
    operario?.let { nombreCompleto(it.nombre, it.apellido) }, asignadoA?.let { nombreCompleto(it.nombre, it.apellido) }, asignadoA?.id,
    etapa.nombre, etapa.orden, etapa.tiempoEstimadoMin,
)

private fun GetComandasPaginadasQuery.Data.ComandasItem.aGestion() = ComandaGestion(
    id = id.toString(),
    numero = numeroComanda,
    estado = EstadoComanda.desdeCodigo(estado.stringValue),
    cliente = ClienteFicha(
        id = cliente.id.toString(), nombre = cliente.nombre, tipo = TipoClienteFicha.desdeCodigo(cliente.tipoCliente.stringValue),
        telefono = cliente.telefono, email = cliente.email, direccion = cliente.direccion,
    ),
    valorTotal = valorTotal,
    fechaRecepcion = fechaCorta(fechaRecepcion),
    fechaRecepcionMs = fechaRecepcion.millis(),
    actualizadoEn = versionDe(actualizadoEn),
    codigoQr = codigoQr.toString(),
    empresa = empresa, proyecto = proyecto, observaciones = observaciones, motivoAnulacion = motivoAnulacion,
    fechaEntregaEstimada = fechaEntregaEstimada?.let(::fechaCorta),
    lineas = comandaDetalles_on_comanda.map {
        LineaComanda(it.tipoPrenda.nombre, it.tipoServicio.nombre, it.cantidad, it.precioUnitario, it.detalle.orEmpty())
    },
    etapas = normalizarEtapas(comandaEtapas_on_comanda.map { it.cruda() }),
)

private fun GetComandaDetalleQuery.Data.Comanda.aGestion() = ComandaGestion(
    id = id.toString(),
    numero = numeroComanda,
    estado = EstadoComanda.desdeCodigo(estado.stringValue),
    cliente = ClienteFicha(
        id = cliente.id.toString(), nombre = cliente.nombre, tipo = TipoClienteFicha.desdeCodigo(cliente.tipoCliente.stringValue),
        rut = cliente.rut, telefono = cliente.telefono, email = cliente.email, direccion = cliente.direccion,
    ),
    valorTotal = valorTotal,
    fechaRecepcion = fechaHora(fechaRecepcion),
    fechaRecepcionMs = fechaRecepcion.millis(),
    actualizadoEn = versionDe(actualizadoEn),
    codigoQr = codigoQr.toString(),
    empresa = empresa, proyecto = proyecto, observaciones = observaciones, motivoAnulacion = motivoAnulacion,
    fechaEntregaEstimada = fechaEntregaEstimada?.let(::fechaCorta),
    fechaEntregaReal = fechaEntregaReal?.let(::fechaHora),
    lineas = comandaDetalles_on_comanda.map {
        LineaComanda(it.tipoPrenda.nombre, it.tipoServicio.nombre, it.cantidad, it.precioUnitario, it.detalle.orEmpty())
    },
    etapas = normalizarEtapas(comandaEtapas_on_comanda.map { it.cruda() }),
    historial = comandaHistorialEstados_on_comanda.map {
        val titulo = it.estadoAnterior?.let { a -> "${EstadoComanda.desdeCodigo(a.stringValue).etiqueta} → " }.orEmpty() +
            EstadoComanda.desdeCodigo(it.estadoNuevo.stringValue).etiqueta
        EventoComanda(titulo, it.motivo.orEmpty(), fechaHora(it.fecha), it.usuario?.nombre ?: "Sistema")
    } + incidenciaComandas_on_comanda.map {
        EventoComanda(
            "Incidencia: ${it.motivo} · ${EstadoIncidencia.desdeCodigo(it.estado.stringValue).etiqueta}",
            it.descripcion.orEmpty(), fechaHora(it.fecha), it.reportadaPor.nombre,
        )
    },
    incidenciasAbiertas = incidenciaComandas_on_comanda.count { it.estado.value != IncidenciaEstado.RESUELTA },
)

/** Datos de Data Connect para recepción y administración, igual que `app/intranet/comandas`. */
@Suppress("TooManyFunctions")
private class FirebaseRecepcionRepository : RecepcionRepository {
    private val dc get() = ExampleConnector.instance

    override suspend fun comandas(filtro: FiltroComandas, offset: Int, limit: Int): PaginaComandas = solicitud {
        val data = dc.getComandasPaginadas.ref {
            this.limit = limit
            this.offset = offset
            // Los filtros vacíos se omiten (como `undefined` en la web): un null explícito
            // llega a `contains: null` y el servidor rechaza la consulta ("$cliente is null").
            if (filtro.estados.isNotEmpty()) estados = filtro.estados.map { ComandaEstado.valueOf(it.name) }
            filtro.cliente.trim().takeIf { it.isNotEmpty() }?.let { cliente = it }
        }.execute(SERVER).data
        PaginaComandas(
            comandas = data.comandas.map { it.aGestion() },
            total = data.total.firstOrNull()?._count ?: data.comandas.size,
            conteos = mapOf(
                EstadoComanda.PENDIENTE to (data.pendientes.firstOrNull()?._count ?: 0),
                EstadoComanda.EN_PROCESO to (data.enProceso.firstOrNull()?._count ?: 0),
                EstadoComanda.FINALIZADA to (data.finalizadas.firstOrNull()?._count ?: 0),
                EstadoComanda.ENTREGADA to (data.entregadas.firstOrNull()?._count ?: 0),
                EstadoComanda.ANULADA to (data.anuladas.firstOrNull()?._count ?: 0),
            ),
        )
    }

    override suspend fun detalle(id: String): ComandaGestion? = solicitud {
        dc.getComandaDetalle.ref(UUID.fromString(id)).execute(SERVER).data.comanda?.aGestion()
    }

    override suspend fun catalogos(): CatalogosComanda = solicitud {
        val data = dc.getCatalogosComanda.ref().execute(SERVER).data
        CatalogosComanda(
            servicios = data.tipoServicios.map {
                TipoServicioCatalogo(it.id.toString(), it.nombre, it.precioBase, it.unidadCobro.stringValue)
            },
            prendas = data.tipoPrendas.map { TipoPrendaCatalogo(it.id.toString(), it.nombre) },
            clientes = data.clientes.map {
                ClienteFicha(
                    id = it.id.toString(), nombre = it.nombre, tipo = TipoClienteFicha.desdeCodigo(it.tipoCliente.stringValue),
                    rut = it.rut, telefono = it.telefono, email = it.email, direccion = it.direccion,
                )
            },
        )
    }

    /**
     * Resuelve (o crea) los tipos de prenda y servicio por nombre normalizado antes de
     * insertar la comanda, como la web: así una cabecera nunca queda sin sus prendas.
     */
    private suspend fun resolverDetalles(formulario: FormularioComanda): List<DetalleResuelto> {
        val catalogo = catalogos()
        val prendas = catalogo.prendas.associate { Validaciones.normalizarCatalogo(it.nombre) to it.id }.toMutableMap()
        val servicios = catalogo.servicios.associate { Validaciones.normalizarCatalogo(it.nombre) to it.id }.toMutableMap()
        return formulario.lineasValidas.map { linea ->
            val nombrePrenda = linea.prenda.trim()
            val prendaId = prendas.getOrPut(Validaciones.normalizarCatalogo(nombrePrenda)) {
                dc.crearTipoPrenda.execute(nombrePrenda).data.tipoPrenda_insert.id.toString()
            }
            val nombreServicio = linea.servicio.trim().ifEmpty { "Lavado" }
            val servicioId = servicios.getOrPut(Validaciones.normalizarCatalogo(nombreServicio)) {
                dc.crearTipoServicio.execute(nombreServicio, 0.0).data.tipoServicio_insert.id.toString()
            }
            DetalleResuelto(
                tipoPrendaId = UUID.fromString(prendaId),
                tipoServicioId = UUID.fromString(servicioId),
                cantidad = linea.cantidad,
                detalle = detalleParaGuardar(linea, formulario.tipoCliente),
                precioUnitario = linea.precioUnitario,
                subtotal = linea.subtotal,
            )
        }
    }

    override suspend fun crearComanda(identidad: IdentidadComanda, formulario: FormularioComanda): String = solicitud {
        val id = UUID.fromString(identidad.id)
        // Un reintento tras una respuesta perdida no debe insertar dos veces.
        comandaGuardada(id)?.let { return@solicitud it }
        val detalles = resolverDetalles(formulario)
        try {
            crearComandaConFlujo(
                CrearComandaConFlujoVars(
                    id = id,
                    numeroComanda = identidad.numero,
                    clienteId = UUID.fromString(formulario.clienteId),
                    empresa = opcional(formulario.empresa),
                    proyecto = opcional(formulario.proyecto),
                    observaciones = opcional(formulario.observaciones),
                    detalles = detalles,
                ),
            )
        } catch (error: OperacionComandaException) {
            // Una respuesta perdida no debe provocar otro INSERT: se confirma por UUID.
            comandaGuardada(id) ?: throw error
        }
    }

    private suspend fun comandaGuardada(id: UUID): String? = runCatching {
        dc.getMiComandaGuardada.ref(id).execute(SERVER).data.comanda?.id?.toString()
    }.getOrNull()

    override suspend fun editarComanda(id: String, version: String, formulario: FormularioComanda): Unit = solicitud {
        val comandaId = UUID.fromString(id)
        val detalles = resolverDetalles(formulario)
        editarComandaConDetalles(
            EditarComandaConDetallesVars(
                id = comandaId,
                version = timestampDeVersion(version),
                empresa = opcional(formulario.empresa),
                proyecto = opcional(formulario.proyecto),
                observaciones = opcional(formulario.observaciones),
                detalles = detalles.map { it.conComanda(comandaId) },
            ),
        )
    }

    override suspend fun anular(id: String, motivo: String): Unit = solicitud {
        dc.anularComanda.execute(UUID.fromString(id), motivo.trim().ifEmpty { "Anulación sin motivo" }.take(300))
    }

    override suspend fun entregar(id: String): Unit = solicitud {
        dc.entregarComanda.execute(UUID.fromString(id))
    }

    override suspend fun asociarFlujo(id: String): Unit = solicitud {
        dc.asociarFlujoComandaPendiente.execute(UUID.fromString(id))
    }

    override suspend fun fichasClientes(): List<ClienteFicha> = solicitud {
        dc.getFichasClientes.ref().execute(SERVER).data.clientes.map { c ->
            ClienteFicha(
                id = c.id.toString(), nombre = c.nombre, tipo = TipoClienteFicha.desdeCodigo(c.tipoCliente.stringValue),
                rut = c.rut, telefono = c.telefono, email = c.email, direccion = c.direccion,
                creadoEn = fechaCorta(c.creadoEn),
                comandas = c.comandas_on_cliente.size,
                comandasActivas = c.comandas_on_cliente.count { EstadoComanda.desdeCodigo(it.estado.stringValue).esActiva },
                prendas = c.comandas_on_cliente.sumOf { k -> k.comandaDetalles_on_comanda.sumOf { it.cantidad } },
                totalFacturado = c.comandas_on_cliente
                    .filter { it.estado.value != ComandaEstado.ANULADA }
                    .sumOf { it.valorTotal },
                ultimaComanda = c.comandas_on_cliente.firstOrNull()?.let { fechaCorta(it.fechaRecepcion) },
            )
        }
    }

    override suspend fun crearCliente(input: NuevoClienteInput): String = solicitud {
        dc.crearClienteComanda.execute(input.nombre.trim(), tipoClienteDc(input.tipo)) {
            rut = opcional(input.rut)?.let(Validaciones::formatearRut)
            telefono = opcional(input.telefono)?.let(Validaciones::formatearTelefono)
            email = opcional(input.email)
            direccion = opcional(input.direccion)
        }.data.cliente_insert.id.toString()
    }

    override suspend fun editarCliente(id: String, input: NuevoClienteInput): Unit = solicitud {
        dc.editarFichaCliente.execute(UUID.fromString(id), input.nombre.trim(), tipoClienteDc(input.tipo)) {
            rut = opcional(input.rut)?.let(Validaciones::formatearRut)
            telefono = opcional(input.telefono)?.let(Validaciones::formatearTelefono)
            email = opcional(input.email)
            direccion = opcional(input.direccion)
        }
    }

    override suspend fun incidencias(): List<IncidenciaGestion> = solicitud {
        dc.getIncidencias.ref().execute(SERVER).data.incidenciaComandas.map {
            IncidenciaGestion(
                id = it.id.toString(),
                motivo = it.motivo,
                descripcion = it.descripcion.orEmpty(),
                estado = EstadoIncidencia.desdeCodigo(it.estado.stringValue),
                fecha = fechaHora(it.fecha),
                comandaId = it.comanda.id.toString(),
                numeroComanda = it.comanda.numeroComanda,
                cliente = it.comanda.cliente.nombre,
                reportadaPor = nombreCompleto(it.reportadaPor.nombre, it.reportadaPor.apellido),
            )
        }
    }

    override suspend fun actualizarIncidencia(id: String, estado: EstadoIncidencia): Unit = solicitud {
        dc.actualizarEstadoIncidencia.execute(UUID.fromString(id), IncidenciaEstado.valueOf(estado.name))
    }
}
