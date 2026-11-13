package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo

/** Fotografía instantánea del estado de la flota devuelto por `GetFlotaVehiculos`. */
data class EstadoFlotaRemota(
    val vehiculos: List<Vehiculo>,
    val conductores: List<ConductorFlota>,
    val chequeos: List<ChequeoVehiculo>,
)

/** Datos para dar de alta un vehículo en el catálogo de flota (`RF-VE01`). */
data class NuevoVehiculoInput(
    val patente: String,
    val marca: String,
    val modelo: String,
    val anio: Int? = null,
    val descripcion: String? = null,
)

/** Datos para actualizar o habilitar/deshabilitar un vehículo de la flota (`RF-VE01`). */
data class EditarVehiculoInput(
    val id: String,
    val patente: String,
    val marca: String,
    val modelo: String,
    val anio: Int? = null,
    val descripcion: String? = null,
    val activo: Boolean = true,
)

/** Datos para registrar la salida a terreno y su inspección previa (`RF-VE02`). */
data class SalidaFlotaInput(
    val vehiculoId: String,
    val repartidorId: String,
    val kilometraje: Int,
    val estadoVehiculo: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    val observaciones: String? = null,
    val fotos: List<FotoChequeo> = emptyList(),
)

/** Datos para registrar el regreso del vehículo y su inspección posterior (`RF-VE03`). */
data class RetornoFlotaInput(
    val salidaId: String,
    val kilometraje: Int,
    val estadoVehiculo: EstadoCondicionVehiculo = EstadoCondicionVehiculo.APTO,
    val observaciones: String? = null,
    val fotos: List<FotoChequeo> = emptyList(),
)

interface VehiculosRepository {
    fun descartarReintentos() = Unit
    suspend fun consultarFlota(): EstadoFlotaRemota
    suspend fun registrarVehiculo(input: NuevoVehiculoInput): String
    suspend fun editarVehiculo(input: EditarVehiculoInput)
    suspend fun registrarSalida(input: SalidaFlotaInput): String
    suspend fun registrarRetorno(input: RetornoFlotaInput)

    /** Deja una salida PROGRAMADA para que el repartidor la inspeccione e inicie (`CrearSalidaVehiculo`). */
    suspend fun programarSalida(vehiculoId: String, repartidorId: String, observaciones: String?): Unit = noDisponible()
}

class FirebaseVehiculosRepository : VehiculosRepository {
    override fun descartarReintentos() = descartarReintentosFlotaFirebase()
    override suspend fun consultarFlota(): EstadoFlotaRemota = consultarFlotaFirebase()
    override suspend fun registrarVehiculo(input: NuevoVehiculoInput): String = registrarVehiculoFirebase(input)
    override suspend fun editarVehiculo(input: EditarVehiculoInput) = editarVehiculoFirebase(input)
    override suspend fun registrarSalida(input: SalidaFlotaInput): String = registrarSalidaFirebase(input)
    override suspend fun registrarRetorno(input: RetornoFlotaInput) = registrarRetornoFirebase(input)
    override suspend fun programarSalida(vehiculoId: String, repartidorId: String, observaciones: String?) =
        programarSalidaFirebase(vehiculoId, repartidorId, observaciones)
}

internal expect suspend fun programarSalidaFirebase(vehiculoId: String, repartidorId: String, observaciones: String?)

internal expect suspend fun consultarFlotaFirebase(): EstadoFlotaRemota
internal expect fun descartarReintentosFlotaFirebase()
internal expect suspend fun registrarVehiculoFirebase(input: NuevoVehiculoInput): String
internal expect suspend fun editarVehiculoFirebase(input: EditarVehiculoInput)
internal expect suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String
internal expect suspend fun registrarRetornoFirebase(input: RetornoFlotaInput)
