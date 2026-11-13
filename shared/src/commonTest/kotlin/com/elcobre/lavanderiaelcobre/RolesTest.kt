package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.MasAdmin
import com.elcobre.lavanderiaelcobre.ui.MasRecepcion
import com.elcobre.lavanderiaelcobre.ui.NavAdmin
import com.elcobre.lavanderiaelcobre.ui.NavOperario
import com.elcobre.lavanderiaelcobre.ui.NavRecepcion
import com.elcobre.lavanderiaelcobre.ui.NavRepartidor
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import com.elcobre.lavanderiaelcobre.ui.SeccionMas
import com.elcobre.lavanderiaelcobre.ui.TodasLasPestanas
import com.elcobre.lavanderiaelcobre.ui.screens.login.sesionParaRol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Mapeo de roles de Data Connect a sesiones y pestañas de cada rol. */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RolesTest {
    @BeforeTest
    fun setUp() = SesionPersistente.limpiar()

    @AfterTest
    fun tearDown() = SesionPersistente.limpiar()

    private fun vm() = CobreViewModel(FakeComandasRepository(), CoroutineScope(UnconfinedTestDispatcher()), restaurarPerfil = { null })

    @Test
    fun sesionParaRol_aceptaLosCuatroRolesInternos() {
        assertIs<Sesion.DeOperario>(sesionParaRol("operario", "Ana"))
        assertIs<Sesion.DeAdministrador>(sesionParaRol("admin", "Ana"))
        assertIs<Sesion.DeRecepcionista>(sesionParaRol("recepcionista", "Ana"))
        assertIs<Sesion.DeRepartidor>(sesionParaRol("repartidor", "Ana"))
    }

    @Test
    fun sesionParaRol_normalizaMayusculasYEspacios() {
        assertIs<Sesion.DeRecepcionista>(sesionParaRol("  Recepcionista ", "Ana"))
        assertIs<Sesion.DeAdministrador>(sesionParaRol("ADMIN", "Ana"))
    }

    @Test
    fun sesionParaRol_conductorEntraComoRepartidorConSuEtiqueta() {
        val sesion = assertIs<Sesion.DeRepartidor>(sesionParaRol("conductor", "Luis"))
        assertEquals("Conductor", sesion.rolVisible)
        assertEquals("Luis", sesion.nombre)
    }

    @Test
    fun sesionParaRol_clienteYDesconocidosNoTienenApp() {
        assertNull(sesionParaRol("cliente", "Ana"))
        assertNull(sesionParaRol("supervisor", "Ana"))
        assertNull(sesionParaRol("", "Ana"))
    }

    @Test
    fun cadaRolEntraASuPrimeraPestana() {
        val casos = mapOf(
            "operario" to Pantalla.Dashboard,
            "admin" to Pantalla.Inicio,
            "recepcionista" to Pantalla.Comandas,
            "repartidor" to Pantalla.Reparto,
        )
        for ((rol, esperada) in casos) {
            val modelo = vm()
            modelo.iniciarSesion(requireNotNull(sesionParaRol(rol, "Ana")))
            assertEquals(esperada, modelo.pantalla, "Rol $rol")
        }
    }

    @Test
    fun ningunRolTieneMasDeCincoPestanas() {
        for (lista in listOf(NavOperario, NavRecepcion, NavAdmin, NavRepartidor)) {
            assertTrue(lista.size <= 5, "Pestañas: ${lista.map { it.label }}")
            assertEquals(lista.size, lista.map { it.pantalla }.toSet().size)
        }
    }

    @Test
    fun lasPrimerasPestanasCoincidenConElAterrizaje() {
        assertEquals(Pantalla.Dashboard, NavOperario.first().pantalla)
        assertEquals(Pantalla.Inicio, NavAdmin.first().pantalla)
        assertEquals(Pantalla.Comandas, NavRecepcion.first().pantalla)
        assertEquals(Pantalla.Reparto, NavRepartidor.first().pantalla)
    }

    @Test
    fun recepcionNoVeSeccionesExclusivasDeAdministracion() {
        val soloAdmin = setOf(SeccionMas.USUARIOS, SeccionMas.REPORTES, SeccionMas.ETAPAS)
        assertTrue(MasRecepcion.none { it in soloAdmin })
        assertTrue(MasAdmin.containsAll(soloAdmin))
        assertTrue(Pantalla.Stock !in NavRecepcion.map { it.pantalla })
        assertTrue(Pantalla.Vehiculos !in NavRecepcion.map { it.pantalla })
    }

    @Test
    fun todasLasPestanasReemplazanLaPila() {
        val modelo = vm()
        modelo.iniciarSesion(requireNotNull(sesionParaRol("admin", "Ana")))
        for (pantalla in TodasLasPestanas) {
            modelo.navegar(pantalla)
            assertEquals(pantalla, modelo.pantalla)
            assertFalse(modelo.puedeVolver, "Pestaña $pantalla")
        }
    }

    @Test
    fun seccionDeMas_seCierraConVolverYAlTocarLaPestana() {
        val modelo = vm()
        modelo.iniciarSesion(requireNotNull(sesionParaRol("recepcionista", "Ana")))
        modelo.navegar(Pantalla.Mas)
        modelo.abrirSeccion(SeccionMas.INCIDENCIAS)
        assertTrue(modelo.puedeVolver)
        modelo.volver()
        assertNull(modelo.seccionMas)
        assertEquals(Pantalla.Mas, modelo.pantalla)

        modelo.abrirSeccion(SeccionMas.COTIZACIONES)
        modelo.navegar(Pantalla.Mas)
        assertNull(modelo.seccionMas)
    }

    @Test
    fun cerrarSesion_olvidaLaSeccionAbierta() {
        val modelo = vm()
        modelo.iniciarSesion(requireNotNull(sesionParaRol("admin", "Ana")))
        modelo.navegar(Pantalla.Mas)
        modelo.abrirSeccion(SeccionMas.USUARIOS)
        modelo.cerrarSesion()
        assertNull(modelo.seccionMas)
        assertEquals(Pantalla.Login, modelo.pantalla)
    }
}
