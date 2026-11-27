package com.elcobre.lavanderiaelcobre.data.model

import kotlinx.serialization.Serializable

// Patrones chilenos soportados: moderna (AAAA-00), antigua (AA-0000) y moto (AAA-000).
private val PATENTE_MODERNA_REGEX = Regex("^[A-Z]{4}\\d{2}$")
private val PATENTE_ANTIGUA_REGEX = Regex("^[A-Z]{2}\\d{4}$")
private val PATENTE_MOTO_REGEX = Regex("^[A-Z]{3}\\d{3}$")
private val PATENTE_CHILENA_REGEX = Regex("^([A-Z]{4}-?\\d{2}|[A-Z]{2}-?\\d{4}|[A-Z]{3}-?\\d{3})$")

/** Condición técnica registrada durante la inspección de salida o retorno (`RF-VE02` / `RF-VE03`). */
@Serializable
enum class EstadoCondicionVehiculo(val codigo: String, val etiqueta: String) {
    APTO("APTO", "Apto"),
    CON_OBSERVACIONES("CON_OBSERVACIONES", "Con observaciones"),
    NO_APTO("NO_APTO", "No apto");

    companion object {
        /**
         * Mapea el código persistido en backend sin distinguir mayúsculas; usa [APTO] como
         * respaldo seguro ante valores nulos o desconocidos.
         */
        fun desdeCodigo(codigo: String?): EstadoCondicionVehiculo =
            entries.firstOrNull { it.codigo.equals(codigo, ignoreCase = true) } ?: APTO
    }
}

/** Filtro de estado operativo de la flota en la vista del Administrador. */
enum class FiltroFlota(val etiqueta: String) {
    TODOS("Todos"),
    DISPONIBLES("Disponibles"),
    EN_RUTA("En ruta"),
    INACTIVOS("Inactivos"),
}

private val ROLES_CONDUCTOR_PERMITIDOS = setOf("conductor", "repartidor")

/** Integrante del equipo interno habilitado como conductor de reparto (`conductor` o `repartidor`). */
@Serializable
data class ConductorFlota(
    val id: String,
    val nombreCompleto: String,
    val rut: String? = null,
    val rol: String = "repartidor",
)

/** Verifica si el nombre del rol corresponde exclusivamente a `"conductor"` o `"repartidor"`. */
fun esRolConductorORepartidor(rol: String?): Boolean =
    rol?.trim()?.lowercase() in ROLES_CONDUCTOR_PERMITIDOS

/** Indicadores operativos de la flota, equivalentes a `KpisFlota` en la web (`lib/vehiculos/modelo.ts`). */
data class KpisFlota(
    val total: Int = 0,
    val disponibles: Int = 0,
    val enRuta: Int = 0,
    val inactivos: Int = 0,
)

/** Vehículo de la flota de reparto ([TipoComanda.DOMICILIO]); check-in/checkout diario en [ChequeoVehiculo]. */
@Serializable
data class Vehiculo(
    val id: String,
    val patente: String,
    val modelo: String,
    val kilometrajeActual: Int,
    val marca: String = "",
    val anio: Int? = null,
    val descripcion: String? = null,
    val activo: Boolean = true,
) {
    /**
     * Etiqueta legible para UI combinando [marca], [modelo] y [anio] sin repetir texto
     * cuando la marca o el año ya vienen incluidos en el modelo.
     */
    val nombreCompleto: String
        get() {
            val base = listOf(marca.trim(), modelo.trim())
                .filter { it.isNotEmpty() }
                .distinct()
                .joinToString(" ")
                .ifEmpty { modelo }
            return if (anio != null && !base.contains(anio.toString())) "$base ($anio)" else base
        }
}

/** Foto adjunta a una inspección de vehículo (`FotoInspeccionVehiculo` en Data Connect). */
@Serializable
data class FotoChequeo(
    val id: String,
    val uri: String,
    val descripcion: String? = null,
    val orden: Int = 1,
)

/**
 * Registro de salida/entrada diaria de un [Vehiculo]: nace al dar la salida
 * (`kmSalida` no nulo) y se cierra al registrar la entrada (`kmEntrada` no nulo).
 */
@Serializable
data class ChequeoVehiculo(
    val id: String,
    val vehiculoId: String,
    val fecha: String,
    val responsable: String,
    val repartidorId: String? = null,
    val estadoSalida: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    val estadoEntrada: EstadoCondicionVehiculo? = null,
    val kmSalida: Int? = null,
    val fotosSalida: List<FotoChequeo> = emptyList(),
    val observacionesSalida: String? = null,
    val horaSalida: String? = null,
    val kmEntrada: Int? = null,
    val fotosEntrada: List<FotoChequeo> = emptyList(),
    val observacionesEntrada: String? = null,
    val horaEntrada: String? = null,
) {
    /** `true` si ya se dio la salida pero el vehículo no ha vuelto. */
    val estaEnRuta: Boolean get() = kmSalida != null && kmEntrada == null

    /** `true` si el chequeo tiene tanto salida como entrada registradas. */
    val estaCerrado: Boolean get() = kmSalida != null && kmEntrada != null

    /** Kilómetros recorridos en la jornada, o `null` si el chequeo no está cerrado. */
    val kilometrosRecorridos: Int?
        get() = if (estaCerrado) (kmEntrada!! - kmSalida!!).coerceAtLeast(0) else null

    /** Registra la inspección de salida y marca la copia resultante como en ruta ([estaEnRuta]). */
    fun registrarSalida(
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        hora: String,
        estado: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    ): ChequeoVehiculo = copy(
        estadoSalida = estado,
        kmSalida = km,
        fotosSalida = fotos,
        observacionesSalida = observaciones,
        horaSalida = hora,
    )

    /** Registra la inspección de retorno, cierra la jornada ([estaCerrado]) y habilita [kilometrosRecorridos]. */
    fun registrarEntrada(
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        hora: String,
        estado: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    ): ChequeoVehiculo = copy(
        estadoEntrada = estado,
        kmEntrada = km,
        fotosEntrada = fotos,
        observacionesEntrada = observaciones,
        horaEntrada = hora,
    )
}

/**
 * Normaliza una patente chilena a mayúsculas y formato con guion (`HLRT-24`, `AB-1234`),
 * idéntico a `normalizarPatente` en `lib/vehiculos/validaciones.ts` de la web.
 */
fun normalizarPatenteChilena(valor: String): String {
    val limpia = valor.uppercase().replace(Regex("[^A-Z0-9]"), "")
    return when {
        PATENTE_MODERNA_REGEX.matches(limpia) -> "${limpia.substring(0, 4)}-${limpia.substring(4)}"
        PATENTE_ANTIGUA_REGEX.matches(limpia) -> "${limpia.substring(0, 2)}-${limpia.substring(2)}"
        PATENTE_MOTO_REGEX.matches(limpia) -> "${limpia.substring(0, 3)}-${limpia.substring(3)}"
        else -> valor.trim().uppercase().replace(Regex("\\s+"), "-")
    }
}

/** Verifica que la patente cumpla el formato chileno estándar de 5 a 10 caracteres. */
fun esPatenteChilenaValida(valor: String): Boolean {
    val normalizada = normalizarPatenteChilena(valor)
    return normalizada.length in 5..10 && PATENTE_CHILENA_REGEX.matches(normalizada)
}

/**
 * Separa marca y modelo cuando el formulario provee un texto combinado (ej. `"Suzuki APV 2019"`)
 * o usa la marca explícita, garantizando las reglas del backend (`marca >= 2`, `modelo >= 1`).
 */
fun extraerMarcaYModelo(marca: String, modelo: String): Pair<String, String> {
    val marcaLimpia = marca.trim()
    val modeloLimpio = modelo.trim()
    if (marcaLimpia.length >= 2) {
        return marcaLimpia to modeloLimpio.ifEmpty { marcaLimpia }
    }
    val partes = modeloLimpio.split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        partes.size >= 2 && partes.first().length >= 2 ->
            partes.first() to partes.drop(1).joinToString(" ")
        modeloLimpio.length >= 2 -> modeloLimpio to modeloLimpio
        else -> "Flota" to modeloLimpio.ifEmpty { "Reparto" }
    }
}

/** Calcula los KPIs de flota según vehículos activos/inactivos y salidas actualmente en ruta. */
fun calcularKpisFlota(vehiculos: List<Vehiculo>, chequeos: List<ChequeoVehiculo>): KpisFlota {
    val idsEnRuta = chequeos.asSequence().filter { it.estaEnRuta }.map { it.vehiculoId }.toSet()
    var disponibles = 0
    var enRuta = 0
    var inactivos = 0
    for (vehiculo in vehiculos) {
        when {
            vehiculo.id in idsEnRuta -> enRuta++
            vehiculo.activo -> disponibles++
            else -> inactivos++
        }
    }
    return KpisFlota(
        total = vehiculos.size,
        disponibles = disponibles,
        enRuta = enRuta,
        inactivos = inactivos,
    )
}
