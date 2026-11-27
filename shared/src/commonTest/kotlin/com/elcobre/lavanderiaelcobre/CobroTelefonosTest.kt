@file:Suppress("MaxLineLength") // Casos de prueba en una línea, como las pruebas de la web.

package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.model.Cobro
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.EstadoComanda
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaComanda
import com.elcobre.lavanderiaelcobre.data.model.LineaFormulario
import com.elcobre.lavanderiaelcobre.data.model.ModalidadCobro
import com.elcobre.lavanderiaelcobre.data.model.Telefonos
import com.elcobre.lavanderiaelcobre.data.model.formularioDesde
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Mismos casos que `lib/comandas/cobro.test.ts` y `lib/telefonos.test.ts` de la web. */
class CobroTelefonosTest {

    @Test
    fun cobroPorKg_totalEsPesoPorPrecioRedondeado() {
        assertEquals(31250.0, Cobro.totalPorKg(12.5, 2500.0))
        assertEquals(8358.0, Cobro.totalPorKg(3.33, 2510.0))
    }

    @Test
    fun cobroPorKg_subtotalesSumanElTotalYNoSeAlejanMasDeUnPeso() {
        val casos = listOf(Triple(10.0, listOf(1, 1, 1), 2333.0), Triple(7.77, listOf(3, 2, 5, 1), 2490.0), Triple(0.35, listOf(1, 1), 3900.0))
        for ((peso, cantidades, precio) in casos) {
            val pesos = Cobro.repartirPeso(peso, cantidades)
            assertEquals(peso, pesos.sum(), 1e-9)
            val subtotales = Cobro.subtotalesPorKg(pesos, precio)
            assertEquals(Cobro.totalPorKg(peso, precio), subtotales.sum())
            subtotales.forEachIndexed { i, v ->
                assertEquals(v, kotlin.math.floor(v))
                assertTrue(abs(v - pesos[i] * precio) <= 1.0 + 1e-9)
            }
        }
    }

    @Test
    fun cobroPorKg_leePreciosYPesosEscritos() {
        assertEquals(2500.0, Cobro.leerPrecioKg("2.500"))
        assertEquals(3500.0, Cobro.leerPrecioKg("$ 3500"))
        assertNull(Cobro.leerPrecioKg("0"))
        assertNull(Cobro.leerPrecioKg("abc"))
        assertNull(Cobro.leerPrecioKg(""))
        assertEquals(12.5, Cobro.leerPesoKg("12,5"))
        assertNull(Cobro.leerPesoKg("0"))
        assertNull(Cobro.leerPesoKg("10001"))
    }

    @Test
    fun prendasNuevas_enMayusculasSinEspaciosDeMasYHasta60() {
        assertEquals("OVEROL TÉRMICO", Cobro.normalizarPrendaNueva("  overol   térmico "))
        assertEquals("PIÑATA", Cobro.normalizarPrendaNueva("piñata"))
        assertEquals(60, Cobro.normalizarPrendaNueva("x".repeat(80)).length)
    }

    private val base = FormularioComanda(
        clienteId = "c1",
        lineas = listOf(LineaFormulario(prenda = "CAMISA", cantidad = 2, precioUnitario = 1000.0)),
        pesoKg = "3,33",
    )

    @Test
    fun formulario_exigePesoYPrecioPorKg() {
        assertEquals(
            "Ingresa el peso total de la comanda en kg (mayor que 0 y hasta 10.000).",
            base.copy(pesoKg = "").validar(),
        )
        assertNull(base.validar())
        val porKg = base.copy(modalidad = ModalidadCobro.KILO)
        assertEquals("Ingresa el precio por kg (mayor que 0).", porKg.validar())
        assertNull(porKg.copy(precioKg = "2500").validar())
    }

    @Test
    fun formulario_porPrendasCobraCantidadPorPrecioYRegistraElPeso() {
        val cobro = assertNotNull(base.resolverCobro())
        assertEquals(ModalidadCobro.PRENDA, cobro.modalidad)
        assertNull(cobro.precioKg)
        assertEquals(2000.0, cobro.total)
        assertEquals(3.33, cobro.lineas.single().pesoKg)
    }

    @Test
    fun formulario_porKgCobraPesoPorPrecioYAceptaSinPrendas() {
        val porKg = base.copy(modalidad = ModalidadCobro.KILO, precioKg = "2500", lineas = emptyList())
        assertNull(porKg.validar(), "Por kg las prendas son opcionales")
        val cobro = assertNotNull(porKg.resolverCobro())
        assertEquals(Cobro.PRENDA_POR_KILO, cobro.lineas.single().linea.prenda)
        assertEquals(2500.0, cobro.precioKg)
        assertEquals(8325.0, cobro.total)
        assertEquals(2500.0, cobro.lineas.single().precioUnitario)
    }

    @Test
    fun formulario_editarComandaPorKgConservaModalidadYPeso() {
        val comanda = ComandaGestion(
            id = "k1", numero = "ELCOBRE-AB12", estado = EstadoComanda.PENDIENTE,
            cliente = ClienteFicha("c1", "Ana"), valorTotal = 8325.0, fechaRecepcion = "", actualizadoEn = "v1",
            lineas = listOf(LineaComanda("CAMISA", "Lavado", 2, 2500.0, pesoKg = 3.33, subtotalGuardado = 8325.0)),
            modalidad = ModalidadCobro.KILO, precioKg = 2500.0,
        )
        val f = formularioDesde(comanda)
        assertEquals(ModalidadCobro.KILO, f.modalidad)
        assertEquals("2500", f.precioKg)
        assertEquals("3,33", f.pesoKg)
        assertEquals(8325.0, assertNotNull(f.resolverCobro()).total)
    }

    @Test
    fun telefonos_formatoYDeteccionComoLaWeb() {
        assertEquals("+56 9 1234 5678", Telefonos.formatear("CL", "12345678"))
        assertEquals("+56 9 123", Telefonos.formatear("CL", "123"))
        assertEquals("+56 55 234 5678", Telefonos.formatear("CL-FIJO", "552345678"))
        assertEquals("+56 2 2345 6789", Telefonos.formatear("CL-FIJO", "223456789"))
        assertEquals("+56 9 1234 5678", Telefonos.formatear("CL", "123456789999"))
        assertEquals("", Telefonos.formatear("PE", ""))
        assertEquals("PE", Telefonos.detectar("+51 912 345 678").pais.id)
        assertFalse(Telefonos.detectar("+44 20 7946 0958").reconocido)
        assertEquals("912345678", Telefonos.extraerNacional(Telefonos.pais("CL-FIJO"), "+56 9 1234 5678"))
        assertEquals("8091234567", Telefonos.extraerNacional(Telefonos.pais("PR"), "+1 809 123 4567"))
        assertEquals("12345678", Telefonos.interpretarEntrada(Telefonos.pais("CL"), "912345678").second)
        assertEquals("12345678", Telefonos.interpretarEntrada(Telefonos.pais("CL"), "56912345678").second)
        assertEquals("912345678", Telefonos.interpretarEntrada(Telefonos.pais("PE"), "51912345678").second)
        assertEquals("PE", Telefonos.interpretarEntrada(Telefonos.pais("CL"), "+51 912 345 678").first.id)
    }

    @Test
    fun telefonos_validacionComoLaWeb() {
        assertEquals("El celular chileno debe tener 8 dígitos después de +56 9.", Telefonos.validarNacional(Telefonos.pais("CL"), "1234"))
        assertTrue(Telefonos.validarNacional(Telefonos.pais("CL-FIJO"), "912345678")!!.contains("no empiezan con 9"))
        assertEquals("El número de Perú debe tener 8 o 9 dígitos después de +51.", Telefonos.validarNacional(Telefonos.pais("PE"), "9123"))
        assertEquals("El número de Panamá debe tener 7 u 8 dígitos después de +507.", Telefonos.validarNacional(Telefonos.pais("PA"), "123"))
        assertTrue(Telefonos.esValido("+56 9 8765 4321"))
        assertTrue(Telefonos.esValido("+56 41 234 5678"))
        assertTrue(Telefonos.esValido("987654321"))
        assertFalse(Telefonos.esValido("+56 9 1234"))
        assertFalse(Telefonos.esValido("+56 88 123 4567"))
        assertTrue(Telefonos.esValido("+51 912 345 678"))
        assertFalse(Telefonos.esValido("+51 912 345 6789"))
        assertFalse(Telefonos.esValido("123"))
        assertNull(Telefonos.validar(""))
        assertTrue(Telefonos.validar("+44 20 7946 0958")!!.contains("Latinoamérica"))
    }

    @Test
    fun telefonos_normalizarYaNoCorrompeNumerosExtranjeros() {
        assertEquals("+56 9 1234 5678", Telefonos.normalizar(" +56912345678 "))
        assertEquals("+51 912 345 678", Telefonos.normalizar("+51912345678"))
        assertEquals("+51 912 345 678", Telefonos.normalizar("+51 912 345 678"))
        assertEquals("+56 55 234 5678", Telefonos.normalizar("552345678"))
        assertEquals("123", Telefonos.normalizar(" 123 "))
        assertEquals("", Telefonos.normalizar(""))
    }

    @Test
    fun cobroPorKg_repartirPesoNuncaGeneraCerosConPesosPequenos() {
        val caso1 = Cobro.repartirPeso(0.1, listOf(100, 1, 1))
        assertEquals(3, caso1.size)
        assertTrue(caso1.all { it > 0.0 }, "Ningún detalle puede quedar con peso 0: $caso1")
        assertEquals(0.1, caso1.sum(), 1e-6)

        val caso2 = Cobro.repartirPeso(0.01, listOf(1, 1, 1))
        assertEquals(3, caso2.size)
        assertTrue(caso2.all { it > 0.0 }, "Micro-pesos deben mantenerse positivos: $caso2")
        assertEquals(0.01, caso2.sum(), 1e-6)
    }

    @Test
    fun telefonos_rechazaDigitosSobrantesEnNumerosChilenosLocales() {
        assertFalse(Telefonos.esValido("912345678999"))
        assertFalse(Telefonos.esValido("55234567899"))
        assertTrue(Telefonos.esValido("552345678"))
        assertEquals("912345678999", Telefonos.normalizar("912345678999"))
    }
}
