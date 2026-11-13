package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.google.firebase.auth.FirebaseAuth
import java.io.File
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

internal fun validarArchivosEvidencia(fotos: List<FotoChequeo>) {
    if (fotos.size > 20) throw OperacionComandaException("Máximo 20 fotografías por inspección.")
    for (foto in fotos) {
        val file = File(foto.uri.removePrefix("file://"))
        if (!file.isFile || file.length() !in 4..MAX_FOTO.toLong()) {
            throw OperacionComandaException("Una fotografía no está disponible o supera los 3 MB. Captúrala nuevamente.")
        }
    }
}

internal suspend fun tokenEvidencia(): String = FirebaseAuth.getInstance().currentUser
    ?.getIdToken(false)?.await()?.token ?: throw OperacionComandaException("Inicia sesión nuevamente.")

internal fun endpointFoto(ruta: String): String? {
    val regex = Regex("^vehiculos/inspecciones/([a-fA-F0-9-]{36})/([a-fA-F0-9-]{36})\\.jpg$")
    val match = regex.matchEntire(ruta) ?: return null
    return "$API_ORIGEN/api/inspecciones/${match.groupValues[1]}/fotos/${match.groupValues[2]}"
}

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
            val file = File(foto.uri.removePrefix("file://"))
            conexion.setFixedLengthStreamingMode(file.length())
            file.inputStream().use { input -> conexion.outputStream.use { output -> input.copyTo(output) } }
            if (conexion.responseCode !in 200..299) {
                throw OperacionComandaException("No se pudo confirmar la fotografía en el servidor (${conexion.responseCode}).")
            }
        } catch (error: IOException) {
            throw OperacionComandaException("No se pudo subir la fotografía. Revisa tu conexión y reintenta.", error)
        } finally { conexion.disconnect() }
    }
}

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
