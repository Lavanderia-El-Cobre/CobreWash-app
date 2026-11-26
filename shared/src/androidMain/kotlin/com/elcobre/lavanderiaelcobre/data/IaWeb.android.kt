package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.EstimacionConsumo
import com.elcobre.lavanderiaelcobre.data.model.InsumoEstimado
import com.elcobre.lavanderiaelcobre.data.model.NivelStock
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

private const val ENDPOINT_ESTIMACION = "$ORIGEN_WEB/api/ia/estimar-comanda"
private const val LIMITE_RESPUESTA = 64 * 1024

/**
 * Pide a la web la estimación de consumo de insumos de una comanda. La app no incluye el
 * modelo de IA ni ninguna clave: envía el ID token de la sesión y el servidor verifica rol,
 * cuenta activa y revocación antes de calcular.
 */
@Suppress("ThrowsCount") // Un mensaje por tipo de falla: respuesta del servidor, red o JSON.
internal suspend fun estimarConsumoWeb(tipoServicioId: String, pesoKg: Double): EstimacionConsumo {
    val token = tokenEvidencia()
    return withContext(Dispatchers.IO) {
        val conexion = URL(ENDPOINT_ESTIMACION).openConnection() as HttpURLConnection
        try {
            conexion.requestMethod = "POST"
            conexion.connectTimeout = 10_000
            conexion.readTimeout = 15_000
            conexion.doOutput = true
            conexion.setRequestProperty("Authorization", "Bearer $token")
            conexion.setRequestProperty("Content-Type", "application/json")
            val cuerpo = JSONObject().put("tipoServicioId", tipoServicioId).put("pesoKg", pesoKg).toString().toByteArray()
            conexion.setFixedLengthStreamingMode(cuerpo.size)
            conexion.outputStream.use { it.write(cuerpo) }
            val codigo = conexion.responseCode
            val flujo = if (codigo in 200..299) conexion.inputStream else conexion.errorStream
            val texto = flujo?.use { leerAcotado(it) }.orEmpty()
            if (codigo !in 200..299) {
                val mensaje = runCatching { JSONObject(texto).optString("error") }.getOrNull()?.takeIf { it.isNotBlank() }
                throw OperacionComandaException(mensaje ?: "No se pudo cargar la estimación ($codigo).")
            }
            leerEstimacion(JSONObject(texto))
        } catch (error: IOException) {
            throw OperacionComandaException("No se pudo cargar la estimación. Puedes guardar la comanda igualmente.", error)
        } catch (error: JSONException) {
            throw OperacionComandaException("La estimación llegó incompleta. Puedes guardar la comanda igualmente.", error)
        } finally {
            conexion.disconnect()
        }
    }
}

/** Lee la respuesta hasta [LIMITE_RESPUESTA] bytes (la de la web pesa unos pocos KB). */
private fun leerAcotado(entrada: java.io.InputStream): String {
    val salida = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(4096)
    var leidos = entrada.read(buffer)
    while (leidos >= 0 && salida.size() < LIMITE_RESPUESTA) {
        salida.write(buffer, 0, leidos)
        leidos = entrada.read(buffer)
    }
    return salida.toString(Charsets.UTF_8.name())
}

private fun leerEstimacion(json: JSONObject): EstimacionConsumo {
    val lista = json.getJSONArray("insumos")
    val insumos = (0 until lista.length()).map { i ->
        val insumo = lista.getJSONObject(i)
        InsumoEstimado(
            nombre = insumo.getString("nombre"),
            unidad = insumo.getString("unidad"),
            minimo = insumo.getDouble("minimo"),
            maximo = insumo.getDouble("maximo"),
            nivel = NivelStock.desdeCodigo(insumo.optString("nivel")),
            stockActual = if (insumo.has("stockActual")) insumo.getDouble("stockActual") else null,
        )
    }
    return EstimacionConsumo(insumos, json.optBoolean("alerta"), json.optBoolean("sinTasas"))
}
