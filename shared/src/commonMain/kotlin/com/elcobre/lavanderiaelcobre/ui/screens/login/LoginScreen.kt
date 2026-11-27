package com.elcobre.lavanderiaelcobre.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.auth.AuthRepository
import com.elcobre.lavanderiaelcobre.data.auth.AuthResultado
import com.elcobre.lavanderiaelcobre.data.auth.DatosSesionGuardada
import com.elcobre.lavanderiaelcobre.data.auth.PerfilRepository
import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.auth.SesionPersistente
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Recepcionista
import com.elcobre.lavanderiaelcobre.data.model.Repartidor
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.components.AvisoPrivacidad
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreCheckbox
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import com.elcobre.lavanderiaelcobre.data.auth.EstadoIntentos
import com.elcobre.lavanderiaelcobre.data.auth.FalloIngreso
import com.elcobre.lavanderiaelcobre.data.auth.IntentosIngreso
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

/** Mapea el rol de Data Connect (igual que lib/roles.ts en la web) a una sesión de la app. */
internal fun sesionParaRol(
    rolNombre: String,
    nombre: String,
): Sesion? = when (val rol = rolNombre.lowercase().trim()) {
    // Solo los roles que existen en la tabla `rol` del backend (lib/roles.ts).
    "operario" -> Sesion.DeOperario(Operario(nombre = nombre, rol = "Operario de planta"))
    "admin" -> Sesion.DeAdministrador(Administrador(nombre = nombre))
    "recepcionista" -> Sesion.DeRecepcionista(Recepcionista(nombre = nombre))
    "repartidor", "conductor" -> Sesion.DeRepartidor(Repartidor(nombre = nombre, rol = rol.replaceFirstChar { it.uppercase() }))
    else -> null // El rol "cliente" usa el portal web, no esta app.
}

/** Aviso de intentos restantes o bloqueo vigente, como el modal de ingreso de la web. */
internal fun mensajeIntentos(estado: EstadoIntentos, ahoraMs: Long): String? {
    val hasta = estado.bloqueadoHasta
    return when {
        hasta != null ->
            "Demasiados intentos fallidos. Espera ${IntentosIngreso.formatoEspera(hasta - ahoraMs)} " +
                "o restablece tu contraseña con «¿Olvidaste tu contraseña?»."
        estado.restantes in 1..2 ->
            "Correo o contraseña incorrectos. Te quedan ${estado.restantes} " +
                (if (estado.restantes == 1) "intento" else "intentos") + " antes de un bloqueo temporal."
        else -> null
    }
}

/** Resultado de un intento de ingreso completo (credenciales, verificación y perfil). */
private sealed interface ResultadoIngreso {
    data class Entrar(val sesion: Sesion) : ResultadoIngreso
    data class Rechazado(val mensaje: String) : ResultadoIngreso
}

/**
 * Ingreso como `resolverIngreso` de la web: credenciales (con bloqueo progresivo),
 * verificación de correo para cuentas nuevas, perfil activo con rol de personal y, si se
 * pidió, la sesión recordada por 30 días. Ante cualquier rechazo se cierra la sesión.
 */
@Suppress("ReturnCount") // Una salida por cada motivo de rechazo, como la web.
private suspend fun resolverIngreso(
    auth: AuthRepository,
    perfiles: PerfilRepository,
    correo: String,
    password: String,
    recordarme: Boolean,
): ResultadoIngreso {
    val credenciales = auth.iniciarSesion(correo, password)
    if (credenciales is AuthResultado.Error) {
        val ahora = IntentosIngreso.ahoraMs()
        val estado = IntentosIngreso.registrarFallo(credenciales.fallo, ahora)
        val aviso = if (credenciales.fallo == FalloIngreso.OTRO) null else mensajeIntentos(estado, ahora)
        return ResultadoIngreso.Rechazado(aviso ?: credenciales.mensaje)
    }
    IntentosIngreso.limpiar()
    // Cuentas creadas desde el 2026-11-24: deben verificar su correo antes de entrar.
    val rechazo = if (auth.correoPorVerificar()) {
        val envio = auth.enviarVerificacion()
        envio?.let { "Verifica tu correo para entrar. $it" }
            ?: "Verifica tu correo para entrar: te enviamos un enlace a $correo. Ábrelo y vuelve a ingresar."
    } else {
        null
    }
    val perfil = if (rechazo == null) perfiles.obtenerMiPerfil() else null
    val sesion = (perfil as? PerfilResultado.Exito)?.let { sesionParaRol(it.rolNombre, it.nombre) }
    if (perfil !is PerfilResultado.Exito || sesion == null) {
        auth.cerrarSesion()
        return ResultadoIngreso.Rechazado(
            rechazo ?: (perfil as? PerfilResultado.Error)?.mensaje ?: "Tu cuenta no tiene acceso a la app móvil.",
        )
    }
    SesionPersistente.setRecordarme(recordarme)
    if (recordarme) {
        SesionPersistente.guardar(
            DatosSesionGuardada(email = correo, nombre = perfil.nombre, rol = perfil.rolNombre),
            IntentosIngreso.ahoraMs(),
        )
    } else {
        SesionPersistente.limpiar()
    }
    return ResultadoIngreso.Entrar(sesion)
}

/**
 * Pantalla de inicio de sesión para el personal autorizado de Lavandería El Cobre
 * (operario, administración, recepción y reparto/conducción; excluye clientes web).
 *
 * Flujos principales: autenticación contra Firebase con bloqueo progresivo ante intentos
 * fallidos, verificación obligatoria de correo en cuentas nuevas, persistencia opcional
 * de sesión («Recordarme»), recuperación de contraseña por correo y cambio rápido de tema.
 *
 * Gestiona en estado local las credenciales, carga, avisos/errores y la pantalla breve de
 * confirmación exitosa antes de entregar la [Sesion] resuelta mediante [onLogin].
 */
@Composable
fun LoginScreen(
    onLogin: (Sesion) -> Unit,
    onToggleTema: () -> Unit = {},
    onInicioLoginManual: () -> Unit = {},
) {
    val sesionGuardada = remember { SesionPersistente.obtener(IntentosIngreso.ahoraMs()) }
    var correo by remember { mutableStateOf(sesionGuardada?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(SesionPersistente.getRecordarme()) }
    var error by remember { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var sesionLista by remember { mutableStateOf<Sesion?>(null) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val perfilRepository = remember { PerfilRepository() }

    LaunchedEffect(Unit) { visible = true }

    // Éxito → redirección, igual que el modal de la web (Navbar.tsx).
    LaunchedEffect(success) {
        val sesion = sesionLista
        if (success && sesion != null) {
            delay(1200)
            onLogin(sesion)
            success = false
        }
    }

    fun recuperar() {
        if (isLoading || success) return
        solicitarRecuperacion(
            scope = scope,
            email = correo.trim(),
            authRepository = authRepository,
            alCambiarCarga = { isLoading = it },
            alResultado = { err, msg ->
                error = err
                if (msg != null) aviso = msg
            },
        )
    }

    fun intentarEntrar() {
        if (isLoading || success) return
        val errorLocal = validarIngresoLocal(correo, password)
        if (errorLocal != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            error = errorLocal
            return
        }
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        error = null
        aviso = null
        isLoading = true
        onInicioLoginManual()
        scope.launch {
            val resultado = resolverIngreso(authRepository, perfilRepository, correo.trim(), password, recordarme)
            isLoading = false
            when (resultado) {
                is ResultadoIngreso.Entrar -> {
                    sesionLista = resultado.sesion
                    success = true
                }
                is ResultadoIngreso.Rechazado -> error = resultado.mensaje
            }
        }
    }

    LoginLayout(
        esquina = { LoginThemeToggle(isDark = cobreIsDark(), onClick = onToggleTema) },
        formulario = {
            LoginCard(
                visible = visible,
                success = success,
                correo = correo,
                onCorreoChange = { correo = it; error = null; aviso = null },
                password = password,
                onPasswordChange = { password = it; error = null },
                recordarme = recordarme,
                onRecordarmeChange = { recordarme = it },
                error = error,
                aviso = aviso,
                isLoading = isLoading,
                onSubmit = { intentarEntrar() },
                onOlvide = { recuperar() },
            )
        },
    )
}

/** Valida el correo y solicita el enlace de restablecimiento en Firebase Auth. */
private fun solicitarRecuperacion(
    scope: kotlinx.coroutines.CoroutineScope,
    email: String,
    authRepository: AuthRepository,
    alCambiarCarga: (Boolean) -> Unit,
    alResultado: (error: String?, aviso: String?) -> Unit,
) {
    if (!Validaciones.correoValido(email)) {
        alResultado("Escribe tu correo arriba para enviarte el enlace de restablecimiento.", null)
        return
    }
    alResultado(null, null)
    alCambiarCarga(true)
    scope.launch {
        val problema = authRepository.recuperarContrasena(email)
        alCambiarCarga(false)
        val ok = if (problema == null) {
            "Si $email tiene una cuenta, te enviamos un enlace para restablecer la contraseña. Revisa también spam."
        } else {
            null
        }
        alResultado(problema, ok)
    }
}

/** Valida credenciales no vacías y el bloqueo local por intentos fallidos antes de llamar a Firebase. */
private fun validarIngresoLocal(correo: String, password: String): String? {
    if (correo.isBlank() || password.isBlank()) return "Por favor ingresa tu correo y contraseña."
    val ahora = IntentosIngreso.ahoraMs()
    val bloqueo = IntentosIngreso.estado(ahora)
    return if (bloqueo.bloqueadoHasta != null) mensajeIntentos(bloqueo, ahora) else null
}

/** Ancho máximo del formulario. */
private val AnchoFormulario = 420.dp

/**
 * Centra el contenido con scroll y respeta barras del sistema y teclado (`safeDrawing`).
 * En pantallas horizontales anchas se divide en dos columnas: marca a la izquierda y
 * formulario a la derecha, para que todo entre sin scroll en un teléfono acostado.
 */
@Composable
private fun LoginLayout(esquina: @Composable () -> Unit, formulario: @Composable () -> Unit) {
    val window = CobreTheme.window
    val dosColumnas = window.isLandscape && window.width >= 640.dp
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val altoDisponible = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .heightIn(min = altoDisponible)
                .padding(horizontal = window.gutter, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (dosColumnas) {
                Row(
                    modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Marca(centrada = false)
                        Spacer(Modifier.height(24.dp))
                        LoginFooter(centrado = false)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        formulario()
                    }
                }
            } else {
                Marca(centrada = true)
                Spacer(Modifier.height(24.dp))
                formulario()
                Spacer(Modifier.height(24.dp))
                LoginFooter(centrado = true)
            }
        }
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) { esquina() }
    }
}

@Composable
private fun LoginThemeToggle(isDark: Boolean, onClick: () -> Unit) {
    GlassIconButton(
        icon = if (isDark) CobreIcons.Sun else CobreIcons.Moon,
        contentDescription = if (isDark) "Tema oscuro activo, cambiar a claro" else "Tema claro activo, cambiar a oscuro",
        onClick = onClick,
        size = 44.dp,
    )
}

@Composable
private fun Marca(centrada: Boolean) {
    val colors = CobreTheme.colors
    Column(horizontalAlignment = if (centrada) Alignment.CenterHorizontally else Alignment.Start) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Lavandería El Cobre",
            modifier = Modifier.size(if (centrada) 56.dp else 72.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Lavandería El Cobre",
            style = if (centrada) MaterialTheme.typography.titleLarge else MaterialTheme.typography.displaySmall,
            color = colors.textPrimary,
            textAlign = if (centrada) TextAlign.Center else TextAlign.Start,
        )
        Spacer(Modifier.height(4.dp))
        Text("Intranet de planta", style = CobreTheme.text.label, color = colors.accentText)
    }
}

@Composable
private fun LoginCard(
    visible: Boolean,
    success: Boolean,
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    recordarme: Boolean,
    onRecordarmeChange: (Boolean) -> Unit,
    error: String?,
    aviso: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onOlvide: () -> Unit,
) {
    val colors = CobreTheme.colors
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 8 },
    ) {
        ContentCard(
            modifier = Modifier.widthIn(max = AnchoFormulario),
            contentPadding = PaddingValues(24.dp),
        ) {
            Text(
                "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Ingresa tus datos para acceder a la intranet.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(visible = success) { LoginSuccess() }
            AnimatedVisibility(visible = !success) {
                LoginForm(
                    correo = correo,
                    onCorreoChange = onCorreoChange,
                    password = password,
                    onPasswordChange = onPasswordChange,
                    recordarme = recordarme,
                    onRecordarmeChange = onRecordarmeChange,
                    error = error,
                    aviso = aviso,
                    isLoading = isLoading,
                    onSubmit = onSubmit,
                    onOlvide = onOlvide,
                )
            }
        }
    }
}

/** Check verde + mensaje, igual que el estado `authSuccess` del modal de la web antes de redirigir. */
@Composable
private fun LoginSuccess() {
    val colors = CobreTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
    ) {
        Icon(CobreIcons.CheckCircle, contentDescription = null, tint = colors.success, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("Acceso exitoso", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        Spacer(Modifier.height(2.dp))
        Text("Redireccionando a tu intranet…", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
    }
}

@Composable
private fun LoginForm(
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    recordarme: Boolean,
    onRecordarmeChange: (Boolean) -> Unit,
    error: String?,
    aviso: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onOlvide: () -> Unit,
) {
    val colors = CobreTheme.colors
    var passwordVisible by remember { mutableStateOf(false) }
    // Column propio: AnimatedVisibility apila como un Box.
    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(CobreShapes.md)
                    .background(colors.danger.copy(alpha = 0.10f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = colors.dangerText, modifier = Modifier.size(18.dp))
                Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
            }
        }
        CobreTextField(
            value = correo,
            onValueChange = onCorreoChange,
            label = "Correo electrónico",
            leadingIcon = CobreIcons.Mail,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(12.dp))
        CobreTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            leadingIcon = CobreIcons.Lock,
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CobreShapes.full)
                        .clickable(role = Role.Button) { passwordVisible = !passwordVisible },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (passwordVisible) CobreIcons.VisibilityOff else CobreIcons.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) onSubmit() }),
        )
        Spacer(Modifier.height(12.dp))
        val haptic = LocalHapticFeedback.current
        RecordarmeYOlvideFila(
            recordarme = recordarme,
            onRecordarmeChange = onRecordarmeChange,
            enabled = !isLoading,
            onOlvideContrasena = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onOlvide()
            },
        )
        AnimatedVisibility(visible = aviso != null) {
            Text(
                aviso.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp).semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        Spacer(Modifier.height(12.dp))
        CobreButton(text = "Ingresar", onClick = onSubmit, loading = isLoading, fillWidth = true)
    }
}

@Composable
private fun RecordarmeYOlvideFila(
    recordarme: Boolean,
    onRecordarmeChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    onOlvideContrasena: () -> Unit,
) {
    val colors = CobreTheme.colors
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CobreCheckbox(
            checked = recordarme,
            onCheckedChange = {
                if (!enabled) return@CobreCheckbox
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onRecordarmeChange(it)
            },
            label = "Recordarme",
        )
        Text(
            "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.labelMedium,
            color = colors.accentText,
            modifier = Modifier
                .clickable(enabled = enabled, role = Role.Button, onClick = onOlvideContrasena)
                .padding(vertical = 12.dp),
        )
    }
}

@Composable
private fun LoginFooter(centrado: Boolean) {
    val colors = CobreTheme.colors
    Column(horizontalAlignment = if (centrado) Alignment.CenterHorizontally else Alignment.Start) {
        AvisoPrivacidad()
        Spacer(Modifier.height(12.dp))
        Text("Lavandería El Cobre · Gestión interna", style = CobreTheme.text.label, color = colors.textSecondary)
        Text("Acceso seguro para personal autorizado", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
    }
}
