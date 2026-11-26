package com.elcobre.lavanderiaelcobre.data.auth

import kotlin.math.min

/**
 * Bloqueo progresivo del inicio de sesión tras contraseñas incorrectas, portado de
 * `lib/firebase/intentos-ingreso.ts`. Complementa el bloqueo que Firebase Auth aplica
 * en su servidor (`too-many-requests`):
 *
 * - Los primeros [INTENTOS_LIBRES] fallos seguidos no se penalizan.
 * - Desde ahí cada fallo bloquea el formulario 30 s, 1 min, 2 min… hasta 15 min.
 * - El contador se reinicia al ingresar o tras una hora sin fallos.
 *
 * No guarda el correo ni la contraseña: solo cuántos fallos hubo y cuándo. Se guarda en
 * el dispositivo, así el bloqueo sigue aunque se cierre la app.
 */
data class EstadoIntentos(
    /** Fin del bloqueo vigente (ms) o `null` si se puede intentar. */
    val bloqueadoHasta: Long?,
    /** Intentos que quedan antes del primer bloqueo (0 si ya se bloqueó alguna vez). */
    val restantes: Int,
)

/** Tipo de fallo de un intento de ingreso, según el código de Firebase. */
enum class FalloIngreso { CREDENCIALES, BLOQUEO_SERVIDOR, OTRO }

/**
 * Gestor del bloqueo progresivo local tras contraseñas incorrectas. Persiste su estado
 * en [SesionPersistente] para que el bloqueo sobreviva al cierre de la app.
 */
@Suppress("MagicNumber")
object IntentosIngreso {
    const val INTENTOS_LIBRES = 5
    private const val BLOQUEO_BASE_MS = 30_000L
    private const val BLOQUEO_MAX_MS = 15 * 60_000L

    /** Bloqueo local cuando Firebase ya bloqueó la cuenta en su servidor. */
    private const val BLOQUEO_SERVIDOR_MS = 5 * 60_000L
    private const val OLVIDO_MS = 60 * 60_000L

    private data class Registro(val fallos: Int = 0, val ultimoFallo: Long = 0, val bloqueadoHasta: Long = 0)

    private fun leer(ahora: Long): Registro {
        val partes = SesionPersistente.leerIntentos()?.split(';') ?: return Registro()
        val registro = Registro(
            partes.getOrNull(0)?.toIntOrNull() ?: return Registro(),
            partes.getOrNull(1)?.toLongOrNull() ?: return Registro(),
            partes.getOrNull(2)?.toLongOrNull() ?: return Registro(),
        )
        // Una hora sin fallos y sin bloqueo vigente reinicia el contador.
        return if (ahora - registro.ultimoFallo > OLVIDO_MS && ahora >= registro.bloqueadoHasta) Registro() else registro
    }

    private fun guardar(registro: Registro) =
        SesionPersistente.guardarIntentos("${registro.fallos};${registro.ultimoFallo};${registro.bloqueadoHasta}")

    private fun estado(registro: Registro, ahora: Long) = EstadoIntentos(
        bloqueadoHasta = registro.bloqueadoHasta.takeIf { it > ahora },
        restantes = (INTENTOS_LIBRES - registro.fallos).coerceAtLeast(0),
    )

    /** Calcula la duración del bloqueo exponencial en milisegundos según [fallos] acumulados. */
    fun duracionBloqueo(fallos: Int): Long {
        if (fallos < INTENTOS_LIBRES) return 0
        val exponente = (fallos - INTENTOS_LIBRES).coerceAtMost(10)
        return min(BLOQUEO_BASE_MS * (1L shl exponente), BLOQUEO_MAX_MS)
    }

    /** Estado vigente del contador y bloqueo en el instante [ahora]. */
    fun estado(ahora: Long = ahoraMs()): EstadoIntentos = estado(leer(ahora), ahora)

    /** Registra un intento fallido y devuelve el bloqueo resultante. */
    fun registrarFallo(fallo: FalloIngreso, ahora: Long = ahoraMs()): EstadoIntentos {
        val registro = leer(ahora)
        val nuevo = when (fallo) {
            FalloIngreso.BLOQUEO_SERVIDOR ->
                registro.copy(bloqueadoHasta = maxOf(registro.bloqueadoHasta, ahora + BLOQUEO_SERVIDOR_MS), ultimoFallo = ahora)
            FalloIngreso.CREDENCIALES -> {
                val fallos = registro.fallos + 1
                val bloqueo = duracionBloqueo(fallos)
                registro.copy(
                    fallos = fallos,
                    ultimoFallo = ahora,
                    bloqueadoHasta = if (bloqueo > 0) ahora + bloqueo else registro.bloqueadoHasta,
                )
            }
            // Errores de red u otros no cuentan como intento.
            FalloIngreso.OTRO -> return estado(registro, ahora)
        }
        guardar(nuevo)
        return estado(nuevo, ahora)
    }

    /** Reinicia el contador y levanta cualquier bloqueo local (se llama tras un ingreso exitoso). */
    fun limpiar() = SesionPersistente.guardarIntentos(null)

    /** "1:05 min" o "45 s" para mostrar el tiempo restante de bloqueo. */
    fun formatoEspera(ms: Long): String {
        val segundos = ((ms + 999) / 1000).coerceAtLeast(1)
        if (segundos < 60) return "$segundos s"
        return "${segundos / 60}:${(segundos % 60).toString().padStart(2, '0')} min"
    }

    internal fun ahoraMs(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
