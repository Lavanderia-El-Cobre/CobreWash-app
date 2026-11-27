package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val MAX_FOTO = 3 * 1024 * 1024
// Dominio oficial confirmado y autorizado por el propietario del proyecto.
private const val API_ORIGEN = ORIGEN_WEB

/** Falla al subir una fotografía; [codigo] es la respuesta HTTP o `null` si no hubo conexión. */
internal class FalloSubidaFoto(message: String, val codigo: Int?, cause: Throwable? = null) : OperacionComandaException(message, cause)

/**
 * Valida antes de encolar o subir que no se superen las 20 fotografías por inspección
 * y que cada archivo local exista y pese entre 4 bytes y [MAX_FOTO] (3 MB).
 */
internal fun validarArchivosEvidencia(fotos: List<FotoChequeo>) {
    if (fotos.size > 20) throw OperacionComandaException("Máximo 20 fotografías por inspección.")
    for (foto in fotos) {
        val file = File(foto.uri.removePrefix("file://"))
        if (!file.isFile || file.length() !in 4..MAX_FOTO.toLong()) {
            throw OperacionComandaException("Una fotografía no está disponible o supera los 3 MB. Captúrala nuevamente.")
        }
    }
}

/** Obtiene el ID token vigente de Firebase Auth para autenticar llamadas al backend web. */
internal suspend fun tokenEvidencia(): String = FirebaseAuth.getInstance().currentUser
    ?.getIdToken(false)?.await()?.token ?: throw OperacionComandaException("Inicia sesión nuevamente.")

/**
 * Convierte una ruta de Storage de inspección en su endpoint autenticado del backend web.
 * Valida estrictamente el formato UUID para rechazar rutas inesperadas o manipulación de path.
 */
internal fun endpointFoto(ruta: String): String? {
    val regex = Regex("^vehiculos/inspecciones/([a-fA-F0-9-]{36})/([a-fA-F0-9-]{36})\\.jpg$")
    val match = regex.matchEntire(ruta) ?: return null
    return "$API_ORIGEN/api/inspecciones/${match.groupValues[1]}/fotos/${match.groupValues[2]}"
}

/**
 * Sube una fotografía JPEG de inspección mediante `PUT` autenticado con el ID token.
 * Usa un UUID determinista derivado de inspección, foto y URI para que los reintentos
 * sean idempotentes y no dupliquen evidencias en el servidor.
 */
internal suspend fun subirFotoInspeccion(inspeccionId: String, foto: FotoChequeo, orden: Int) {
    val token = tokenEvidencia()
    withContext(Dispatchers.IO) {
        val id = UUID.nameUUIDFromBytes("$inspeccionId|${foto.id}|${foto.uri}".toByteArray()).toString()
        val descripcion = URLEncoder.encode(foto.descripcion.orEmpty().take(200), "UTF-8")
        val url = "$API_ORIGEN/api/inspecciones/$inspeccionId/fotos/$id?orden=$orden&descripcion=$descripcion"
        val conexion = URL(url).openConnection() as HttpURLConnection
        try {
            conexion.requestMethod = "PUT"
            conexion.connectTimeout = 15_000
            conexion.readTimeout = 45_000
            conexion.doOutput = true
            conexion.setRequestProperty("Authorization", "Bearer $token")
            conexion.setRequestProperty("Content-Type", "image/jpeg")
            val file = resolverArchivoFoto(foto.uri)
            val codigo = enviarArchivoFoto(conexion, file)
            if (codigo !in 200..299) {
                throw FalloSubidaFoto("No se pudo confirmar la fotografía en el servidor ($codigo).", codigo)
            }
        } finally { conexion.disconnect() }
    }
}

private fun resolverArchivoFoto(uri: String): File {
    val file = File(uri.removePrefix("file://"))
    if (!file.isFile) {
        throw FalloSubidaFoto("La fotografía local ya no está disponible en el dispositivo.", 404)
    }
    return file
}

private fun enviarArchivoFoto(conexion: HttpURLConnection, file: File): Int = try {
    // Streaming de longitud fija para evitar cargar el archivo completo en memoria.
    conexion.setFixedLengthStreamingMode(file.length())
    file.inputStream().use { input -> conexion.outputStream.use { output -> input.copyTo(output) } }
    conexion.responseCode
} catch (error: FileNotFoundException) {
    throw FalloSubidaFoto("La fotografía local ya no está disponible en el dispositivo.", 404, error)
} catch (error: IOException) {
    throw FalloSubidaFoto("No se pudo subir la fotografía. Revisa tu conexión y reintenta.", null, error)
}

/**
 * Descarga una fotografía remota de inspección validando su ruta y autenticando con Bearer token.
 * Acota la lectura a [MAX_FOTO] bytes y devuelve `null` ante errores de red o respuestas no válidas.
 */
internal suspend fun descargarFotoEvidencia(ruta: String): ByteArray? {
    val url = endpointFoto(ruta) ?: return null
    val token = tokenEvidencia()
    return withContext(Dispatchers.IO) {
        val conexion = URL(url).openConnection() as HttpURLConnection
        try {
            conexion.connectTimeout = 15_000
            conexion.readTimeout = 30_000
            conexion.setRequestProperty("Authorization", "Bearer $token")
            if (conexion.responseCode != 200) return@withContext null
            conexion.inputStream.use { input ->
                val bytes = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var leidos = input.read(buffer)
                while (leidos >= 0 && bytes.size() <= MAX_FOTO) {
                    bytes.write(buffer, 0, leidos)
                    leidos = input.read(buffer)
                }
                bytes.toByteArray().takeIf { it.size <= MAX_FOTO }
            }
        } catch (_: IOException) { null } finally { conexion.disconnect() }
    }
}
