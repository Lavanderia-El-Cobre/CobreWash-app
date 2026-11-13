package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.EstadoSolicitud
import com.elcobre.lavanderiaelcobre.data.model.NuevoAvisoInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaSolicitudes
import com.elcobre.lavanderiaelcobre.data.model.SolicitudContacto
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.SolicitudContactoEstado
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID

actual fun crearComunicacionRepository(): ComunicacionRepository = FirebaseComunicacionRepository()

/** Cotizaciones (`/intranet/solicitudes`) y comunicación interna (`/intranet/comunicacion`). */
private class FirebaseComunicacionRepository : ComunicacionRepository {
    private val dc get() = ExampleConnector.instance

    override suspend fun solicitudes(estado: EstadoSolicitud?, offset: Int, limit: Int): PaginaSolicitudes = solicitud {
        val data = dc.getSolicitudesContacto.ref {
            // Sin filtro se omite la variable: un null explícito no equivale a "todas".
            if (estado != null) this.estado = SolicitudContactoEstado.valueOf(estado.name)
            this.offset = offset
            this.limit = limit
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data
        PaginaSolicitudes(
            solicitudes = data.solicitudContactos.map {
                SolicitudContacto(
                    id = it.id.toString(), nombre = it.nombre, email = it.email, telefono = it.telefono,
                    servicio = it.servicio, mensaje = it.mensaje,
                    estado = EstadoSolicitud.desdeCodigo(it.estado.stringValue), creadoEn = fechaHora(it.creadoEn),
                )
            },
            total = data.total.firstOrNull()?._count ?: 0,
        )
    }

    override suspend fun actualizarSolicitud(id: String, estado: EstadoSolicitud): Unit = solicitud {
        dc.actualizarEstadoSolicitudContacto.execute(UUID.fromString(id), SolicitudContactoEstado.valueOf(estado.name))
    }

    override suspend fun avisosAdministracion(offset: Int, limit: Int): PaginaAvisos = solicitud {
        val data = dc.getAvisosAdministracion.ref {
            this.offset = offset
            this.limit = limit
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data
        PaginaAvisos(
            data.avisos.map {
                Aviso(
                    id = it.id.toString(), titulo = it.titulo, mensaje = it.contenido, hora = fechaHora(it.fechaPublicacion),
                    dirigidoA = etiquetaDestinatario(it.rolDestinatario?.nombre),
                    autor = nombreCompleto(it.autor.nombre, it.autor.apellido),
                )
            },
            data.total.firstOrNull()?._count ?: 0,
        )
    }

    override suspend fun avisosEquipo(rol: String, offset: Int, limit: Int): PaginaAvisos = avisosEquipoFirebase(rol, offset, limit)

    override suspend fun publicarAviso(input: NuevoAvisoInput): Unit = solicitud {
        val rolId = input.destinatario.rol?.let { nombre ->
            dc.getRoles.ref().execute(QueryRef.FetchPolicy.SERVER_ONLY).data.rols.firstOrNull { it.nombre == nombre }?.id
                ?: throw OperacionComandaException("No se encontró el rol destinatario.")
        }
        dc.crearAviso.execute(input.titulo.trim(), input.contenido.trim()) { rolDestinatarioId = rolId }
    }
}
