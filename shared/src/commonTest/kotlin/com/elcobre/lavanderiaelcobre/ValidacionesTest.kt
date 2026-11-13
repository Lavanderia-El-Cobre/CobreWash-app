package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.DestinatarioAviso
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoEtapaProduccion
import com.elcobre.lavanderiaelcobre.data.model.EtapaComanda
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.NuevoAvisoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.TipoMovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.data.model.clp
import com.elcobre.lavanderiaelcobre.data.model.detalleParaGuardar
import com.elcobre.lavanderiaelcobre.data.model.formularioDesde
import com.elcobre.lavanderiaelcobre.data.model.leerDetalleHotel
import com.elcobre.lavanderiaelcobre.data.model.minutosAtraso
import com.elcobre.lavanderiaelcobre.data.model.validarAviso
import com.elcobre.lavanderiaelcobre.data.model.validarFichaCliente
import com.elcobre.lavanderiaelcobre.data.model.validarInspeccion
import com.elcobre.lavanderiaelcobre.data.model.validarMovimiento
import com.elcobre.lavanderiaelcobre.data.model.validarNuevoUsuario
import com.elcobre.lavanderiaelcobre.ui.screens.configuracion.problemaCambioContrasena
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Validaciones portadas de la web (`lib/validators.ts`, formularios de comandas, usuarios y avisos). */
class ValidacionesTest {

    @Test
    fun rut_validaDigitoVerificadorYFormatea() {
        assertTrue(Validaciones.rutValido("12.345.678-5"))
        assertTrue(Validaciones.rutValido("123456785"))
        assertTrue(Validaciones.rutValido("11.111.111-1"))
        assertFalse(Validaciones.rutValido("12.345.678-9"))
        assertFalse(Validaciones.rutValido("1"))
        assertEquals("12.345.678-5", Validaciones.formatearRut("123456785"))
        assertEquals("9.876.543-K", Validaciones.formatearRut("9876543k"))
    }

    @Test
    fun telefono_reconoceCelularYFijoChilenos() {
        assertEquals("Celular", Validaciones.tipoTelefono("+56 9 1234 5678"))
        assertEquals("Celular", Validaciones.tipoTelefono("912345678"))
        assertEquals("Teléfono fijo", Validaciones.tipoTelefono("221234567"))
        assertEquals("Teléfono fijo", Validaciones.tipoTelefono("552123456"))
        assertNull(Validaciones.tipoTelefono("12345"))
        assertNull(Validaciones.tipoTelefono("812345678"))
        assertEquals("+56 9 1234 5678", Validaciones.formatearTelefono("56912345678"))
        assertEquals("+56 55 212 3456", Validaciones.formatearTelefono("552123456"))
    }

    @Test
    fun contrasena_exigeLargoLetrasYNumeros() {
        assertEquals("Mínimo 8 caracteres", Validaciones.problemaContrasena("abc1"))
        assertEquals("Debe incluir al menos una letra", Validaciones.problemaContrasena("12345678"))
        assertEquals("Debe incluir al menos un número", Validaciones.problemaContrasena("abcdefgh"))
        assertNull(Validaciones.problemaContrasena("cobre2026"))
    }

    @Test
    fun cambioContrasena_pideConfirmacionYQueSeaDistinta() {
        assertNotNull(problemaCambioContrasena("", "nueva2026", "nueva2026"))
        assertEquals("Las contraseñas no coinciden.", problemaCambioContrasena("vieja2025", "nueva2026", "nueva2027"))
        assertEquals("La nueva contraseña debe ser distinta de la actual.", problemaCambioContrasena("igual2026", "igual2026", "igual2026"))
        assertNull(problemaCambioContrasena("vieja2025", "nueva2026", "nueva2026"))
    }

    @Test
    fun numeroComanda_tieneFormatoQueAceptaElServidor() {
        val patron = Regex("^ELCOBRE-[A-Z0-9]{4}$")
        val random = Random(42)
        repeat(200) {
            val numero = Validaciones.generarNumeroComanda(random)
            assertTrue(patron.matches(numero), numero)
            val sufijo = numero.removePrefix("ELCOBRE-")
            assertTrue(sufijo.any { it.isLetter() } && sufijo.any { it.isDigit() }, numero)
        }
    }

    @Test
    fun normalizarCatalogo_ignoraTildesMayusculasYEspacios() {
        assertEquals(Validaciones.normalizarCatalogo("PANTALÓN"), Validaciones.normalizarCatalogo(" pantalon "))
        assertEquals("sabanas king", Validaciones.normalizarCatalogo("SÁBANAS KING"))
    }

    private val clienteValido = NuevoClienteInput("Hotel Cobre", TipoClienteFicha.HOTEL, "12.345.678-5")

    @Test
    fun formularioComanda_exigeClienteYPrendas() {
        assertEquals("Selecciona un cliente y agrega al menos una prenda.", FormularioComanda().validar())
        val conCliente = FormularioComanda(clienteId = "c1", lineas = listOf(LineaFormulario(prenda = "")))
        assertEquals("Selecciona un cliente y agrega al menos una prenda.", conCliente.validar())
        assertNull(conCliente.copy(lineas = listOf(LineaFormulario(prenda = "CAMISA", precioUnitario = 1500.0))).validar())
    }

    @Test
    fun formularioComanda_validaClienteNuevo() {
        val base = FormularioComanda(nuevoCliente = true, cliente = clienteValido, lineas = listOf(LineaFormulario(prenda = "TOALLA")))
        assertNull(base.validar())
        assertEquals(
            "Ingresa un RUT chileno válido para el cliente.",
            base.copy(cliente = clienteValido.copy(rut = "11.111.111-2")).validar(),
        )
        assertEquals(
            "Ingresa un celular o teléfono fijo chileno válido.",
            base.copy(cliente = clienteValido.copy(telefono = "123")).validar(),
        )
        assertEquals("Ingresa un correo válido.", base.copy(cliente = clienteValido.copy(email = "hotel@")).validar())
    }

    @Test
    fun formularioComanda_rechazaCantidadesYPreciosInvalidos() {
        val base = FormularioComanda(clienteId = "c1")
        assertNotNull(base.copy(lineas = listOf(LineaFormulario(prenda = "CAMISA", cantidad = 0))).validar())
        assertNotNull(base.copy(lineas = listOf(LineaFormulario(prenda = "CAMISA", precioUnitario = -1.0))).validar())
        assertNotNull(base.copy(observaciones = "x".repeat(301), lineas = listOf(LineaFormulario(prenda = "CAMISA"))).validar())
    }

    @Test
    fun formularioComanda_totalSumaSoloLineasConPrenda() {
        val f = FormularioComanda(
            clienteId = "c1",
            lineas = listOf(
                LineaFormulario(prenda = "CAMISA", cantidad = 3, precioUnitario = 1500.0),
                LineaFormulario(prenda = "", cantidad = 9, precioUnitario = 9999.0),
                LineaFormulario(prenda = "PANTALÓN", cantidad = 2, precioUnitario = 2000.0),
            ),
        )
        assertEquals(8500.0, f.total)
        assertEquals("$8.500", clp(f.total))
    }

    @Test
    fun detalleHotel_seGuardaYSeLeeIgualQueEnLaWeb() {
        val linea = LineaFormulario(prenda = "TOALLA BAÑO", cantidad = 10, recibido = 7, detalle = "Manchas leves")
        val texto = detalleParaGuardar(linea, TipoClienteFicha.HOTEL)
        assertEquals("Entregado: 10 · Recibido: 7 · Pendiente: 3 · Manchas leves", texto)
        assertEquals(7 to "Manchas leves", leerDetalleHotel(texto))
        assertEquals(0 to "Nota libre", leerDetalleHotel("Nota libre"))
        assertNull(detalleParaGuardar(linea.copy(detalle = " "), TipoClienteFicha.PARTICULAR))
    }

    @Test
    fun formularioDesde_recuperaRecibidoDeLasLineasDeHotel() {
        val comanda = comanda(EstadoComanda.PENDIENTE).copy(
            cliente = ClienteFicha("c1", "Hotel", TipoClienteFicha.HOTEL),
            lineas = listOf(LineaComanda("TOALLA", "Lavado", 4, 500.0, "Entregado: 4 · Recibido: 2 · Pendiente: 2")),
        )
        val f = formularioDesde(comanda)
        assertEquals(2, f.lineas.single().recibido)
        assertEquals("", f.lineas.single().detalle)
        assertEquals("c1", f.clienteId)
    }

    @Test
    fun accionesDeComanda_dependenDelEstado() {
        assertTrue(comanda(EstadoComanda.PENDIENTE).puedeEditar)
        assertFalse(comanda(EstadoComanda.EN_PROCESO).puedeEditar)
        assertTrue(comanda(EstadoComanda.FINALIZADA).puedeEntregar)
        assertFalse(comanda(EstadoComanda.EN_PROCESO).puedeEntregar)
        assertTrue(comanda(EstadoComanda.FINALIZADA).puedeAnular)
        assertFalse(comanda(EstadoComanda.ENTREGADA).puedeAnular)
        assertFalse(comanda(EstadoComanda.ANULADA).puedeAnular)
        assertTrue(comanda(EstadoComanda.PENDIENTE).necesitaFlujo)
    }

    @Test
    fun minutosAtraso_cuentaDesdeElInicioDeLaEtapaActiva() {
        val lavado = EtapaComanda("e2", "Lavado", 2, EstadoEtapaProduccion.EN_PROCESO, tiempoEstimadoMin = 90, inicioMs = 0)
        val c = comanda(EstadoComanda.EN_PROCESO).copy(
            etapas = listOf(EtapaComanda("e1", "Recepción", 1, EstadoEtapaProduccion.COMPLETADA), lavado),
        )
        assertEquals(0, minutosAtraso(c, 90 * 60_000L))
        assertEquals(10, minutosAtraso(c, 100 * 60_000L))
        assertEquals(0, minutosAtraso(c.copy(estado = EstadoComanda.ANULADA), 999 * 60_000L))
    }

    @Test
    fun minutosAtraso_recepcionPendienteUsaLaFechaDeRecepcionYElLimiteInicial() {
        val c = comanda(EstadoComanda.PENDIENTE).copy(
            fechaRecepcionMs = 1_000L,
            etapas = listOf(EtapaComanda("e1", "Recepción", 1, EstadoEtapaProduccion.PENDIENTE)),
        )
        assertEquals(0, minutosAtraso(c, 1_000L + 15 * 60_000L))
        assertEquals(5, minutosAtraso(c, 1_000L + 20 * 60_000L))
    }

    @Test
    fun fichaCliente_rutYContactoOpcionalesPeroValidos() {
        assertNull(validarFichaCliente(NuevoClienteInput("Ana", TipoClienteFicha.PARTICULAR, "")))
        assertNotNull(validarFichaCliente(NuevoClienteInput("", TipoClienteFicha.PARTICULAR, "")))
        assertNotNull(validarFichaCliente(NuevoClienteInput("Ana", TipoClienteFicha.PARTICULAR, "12.345.678-9")))
    }

    @Test
    fun nuevoUsuario_validaComoLaWeb() {
        val base = NuevoUsuarioInput("r1", "operario", "Ana", "Pérez", "12.345.678-5", "", "ana@cobre.cl", "cobre2026")
        assertNull(validarNuevoUsuario(base))
        assertEquals("Todos los campos son obligatorios.", validarNuevoUsuario(base.copy(apellido = "")))
        assertEquals("El RUT ingresado no es válido.", validarNuevoUsuario(base.copy(rut = "1-1")))
        assertEquals("Mínimo 8 caracteres", validarNuevoUsuario(base.copy(contrasena = "abc1")))
        assertEquals("Selecciona el tipo de cliente.", validarNuevoUsuario(base.copy(rolNombre = "cliente")))
    }

    @Test
    fun aviso_exigeTituloYContenidoDentroDelLimite() {
        assertNotNull(validarAviso(NuevoAvisoInput(" ", "Contenido", DestinatarioAviso.TODOS)))
        assertNotNull(validarAviso(NuevoAvisoInput("Título", "x".repeat(1001), DestinatarioAviso.OPERARIO)))
        assertNull(validarAviso(NuevoAvisoInput("Corte de agua", "Mañana de 8 a 10.", DestinatarioAviso.RECEPCIONISTA)))
    }

    @Test
    fun movimientoInventario_noPermiteSalidasMayoresAlStock() {
        assertNotNull(validarMovimiento(null, 10.0, TipoMovimientoInventario.ENTRADA))
        assertNotNull(validarMovimiento(11.0, 10.0, TipoMovimientoInventario.SALIDA))
        assertNull(validarMovimiento(11.0, 10.0, TipoMovimientoInventario.ENTRADA))
        assertNull(validarMovimiento(10.0, 10.0, TipoMovimientoInventario.SALIDA))
    }

    @Test
    fun inspeccion_noAptoNoSaleYElKilometrajeNoRetrocede() {
        assertNull(validarInspeccion(1200, 1000, EstadoCondicionVehiculo.APTO, esSalida = true, observaciones = ""))
        assertNotNull(validarInspeccion(900, 1000, EstadoCondicionVehiculo.APTO, esSalida = true, observaciones = ""))
        assertNotNull(validarInspeccion(1200, 1000, EstadoCondicionVehiculo.NO_APTO, esSalida = true, observaciones = ""))
        assertNull(validarInspeccion(1200, 1000, EstadoCondicionVehiculo.NO_APTO, esSalida = false, observaciones = ""))
    }

    private fun comanda(estado: EstadoComanda) = ComandaGestion(
        id = "k1", numero = "ELCOBRE-AB12", estado = estado, cliente = ClienteFicha("c1", "Ana"),
        valorTotal = 0.0, fechaRecepcion = "", actualizadoEn = "1.0",
    )
}
