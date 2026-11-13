package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo

/**
 * Flota vista por el repartidor: sus salidas asignadas y el catálogo de vehículos.
 * Las reglas de Data Connect solo le permiten operar salidas propias:
 * `RegistrarInspeccionAntes` + `IniciarSalidaVehiculo` y `RegistrarInspeccionDespues`.
 */
interface RepartoRepository {
    suspend fun misSalidas(): List<SalidaReparto> = noDisponible()
    suspend fun vehiculos(): List<Vehiculo> = noDisponible()

    /** Inspección previa (si aún no existe), fotos y paso a EN_SERVICIO. */
    suspend fun iniciarSalida(salida: SalidaReparto, input: InspeccionInput): Unit = noDisponible()

    /** Inspección posterior y fotos: la salida queda FINALIZADA. */
    suspend fun registrarRetorno(input: InspeccionInput): Unit = noDisponible()

    fun descartarReintentos() = Unit
}

expect fun crearRepartoRepository(): RepartoRepository
