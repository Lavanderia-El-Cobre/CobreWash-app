package com.elcobre.lavanderiaelcobre.data

// En iOS todavía no hay SDK de Data Connect: cada repositorio usa los métodos por
// defecto de su interfaz, que lanzan OperacionComandaException("…no disponible…").

actual fun crearRecepcionRepository(): RecepcionRepository = object : RecepcionRepository {}

actual fun crearProduccionRepository(): ProduccionRepository = object : ProduccionRepository {}

actual fun crearComunicacionRepository(): ComunicacionRepository = object : ComunicacionRepository {}

actual fun crearAdministracionRepository(): AdministracionRepository = object : AdministracionRepository {}

actual fun crearRepartoRepository(): RepartoRepository = object : RepartoRepository {}

actual fun crearCuentaRepository(): CuentaRepository = CuentaRepository { _, _ -> noDisponible() }
