package com.elcobre.lavanderiaelcobre.data

// En iOS todavía no hay SDK de Firebase Data Connect: los repositorios devuelven implementaciones
// anónimas que heredan los métodos por defecto de cada interfaz y lanzan
// OperacionComandaException("…no disponible…") al invocarse.

/** Crea el stub de recepción en iOS con los métodos por defecto de [RecepcionRepository]. */
actual fun crearRecepcionRepository(): RecepcionRepository = object : RecepcionRepository {}

/** Crea el stub de producción en iOS con los métodos por defecto de [ProduccionRepository]. */
actual fun crearProduccionRepository(): ProduccionRepository = object : ProduccionRepository {}

/** Crea el stub de comunicación en iOS con los métodos por defecto de [ComunicacionRepository]. */
actual fun crearComunicacionRepository(): ComunicacionRepository = object : ComunicacionRepository {}

/** Crea el stub de administración en iOS con los fallbacks de [AdministracionRepository]. */
actual fun crearAdministracionRepository(): AdministracionRepository = object : AdministracionRepository {}

/** Crea el stub de reparto en iOS con los métodos por defecto de [RepartoRepository]. */
actual fun crearRepartoRepository(): RepartoRepository = object : RepartoRepository {}

/** Crea el stub de cuenta en iOS rechazando cualquier cambio mediante `noDisponible()`. */
actual fun crearCuentaRepository(): CuentaRepository = CuentaRepository { _, _ -> noDisponible() }
