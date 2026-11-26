package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.auth.VigilanciaCuenta
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.NivelStock
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.data.model.formatearCantidad
import com.elcobre.lavanderiaelcobre.data.model.rangoConsumo
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import com.elcobre.lavanderiaelcobre.ui.screens.login.sesionParaRol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Cierre de sesión por cuenta desactivada durante el uso y formato de la estimación de IA. */
class VigilanciaEstimacionTest {
    private val operario = Sesion.DeOperario(Operario("Ana", "Operario de planta"))
    private var perfil: PerfilResultado? = PerfilResultado.Exito("Ana", "operario")

    private fun vm() = CobreViewModel(FakeComandasRepository(), CoroutineScope(UnconfinedTestDispatcher()), restaurarPerfil = { perfil })

    @AfterTest
    fun limpiar() {
        SesionPersistente.limpiar()
        VigilanciaCuenta.alRechazar(null)
    }

    @Test
    fun cuentaDesactivadaDuranteElUso_cierraLaSesion() {
        val vm = vm()
        vm.iniciarSesion(operario)
        vm.verificarCuenta()
        assertNotNull(vm.sesion, "Perfil vigente: la sesión sigue")

        perfil = PerfilResultado.Error("Tu cuenta está inactiva.", definitivo = true)
        vm.verificarCuenta()
        assertNull(vm.sesion)
        assertEquals(Pantalla.Login, vm.pantalla)
        assertEquals("Tu cuenta está inactiva.", vm.errorOperacion)
    }

    @Test
    fun errorDeRed_noCierraLaSesion() {
        val vm = vm()
        vm.iniciarSesion(operario)
        perfil = PerfilResultado.Error("No se pudo validar tu sesión. Conéctate e inicia sesión nuevamente.")
        vm.verificarCuenta()
        assertNotNull(vm.sesion)
    }

    @Test
    fun cambioDeRolOPasoACliente_cierraLaSesion() {
        val vm = vm()
        vm.iniciarSesion(operario)
        perfil = PerfilResultado.Exito("Ana", "admin")
        vm.verificarCuenta()
        assertEquals("Tus permisos cambiaron. Vuelve a iniciar sesión.", vm.errorOperacion)
        assertNull(vm.sesion)

        vm.iniciarSesion(operario)
        perfil = PerfilResultado.Exito("Ana", "cliente")
        vm.verificarCuenta()
        assertEquals("Tu cuenta ya no tiene acceso a la app móvil.", vm.errorOperacion)
    }

    @Test
    fun rechazoDelServidorPorCuentaInactiva_cierraLaSesion() {
        val vm = vm()
        vm.iniciarSesion(operario)
        VigilanciaCuenta.revisar("Error: Otro motivo")
        assertNotNull(vm.sesion)
        VigilanciaCuenta.revisar("FAILED_PRECONDITION: La cuenta está inactiva")
        assertNull(vm.sesion)
        assertNotNull(vm.errorOperacion)
    }

    @Test
    fun motivoCierre_repartidorYConductorSonLaMismaSesion() {
        val repartidor = assertNotNull(sesionParaRol("repartidor", "Juan"))
        val conductor = PerfilResultado.Exito("Juan", "conductor")
        assertNull(VigilanciaCuenta.motivoCierre(repartidor, conductor) { sesionParaRol(it.rolNombre, it.nombre) })
        val admin = Sesion.DeAdministrador(Administrador("Root"))
        assertNotNull(VigilanciaCuenta.motivoCierre(admin, null) { sesionParaRol(it.rolNombre, it.nombre) })
    }

    @Test
    fun estimacion_formatoComoLaWeb() {
        assertEquals("150 a 180 ml", rangoConsumo(0.15, 0.18, "L"))
        assertEquals("2,1 a 2,6 L", rangoConsumo(2.1, 2.6, "L"))
        assertEquals("12,5 a 15 g", rangoConsumo(0.0125, 0.015, "kg"))
        assertEquals("1.250", formatearCantidad(1250.4))
        assertEquals(NivelStock.INSUFICIENTE, NivelStock.desdeCodigo("insuficiente"))
        assertEquals(NivelStock.OK, NivelStock.desdeCodigo("desconocido"))
    }
}
