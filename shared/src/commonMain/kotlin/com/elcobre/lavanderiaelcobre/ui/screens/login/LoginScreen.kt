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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.components.AvisoPrivacidad
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassIconButton
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

/** Mapea el rol de Data Connect (igual que lib/roles.ts en la web) a una sesión de la app. */
internal fun sesionParaRol(
    rolNombre: String,
    nombre: String,
): Sesion? = when (rolNombre.lowercase().trim()) {
    "operario", "operario de planta" -> Sesion.DeOperario(Operario(nombre = nombre, rol = "Operario de planta"))
    "admin", "administrador" -> Sesion.DeAdministrador(Administrador(nombre = nombre))
    else -> null // "recepcionista" y "cliente" no tienen vista en esta app.
}

@Composable
fun LoginScreen(
    onLogin: (Sesion) -> Unit,
    onToggleTema: () -> Unit = {},
) {
    val sesionGuardada = remember { SesionPersistente.obtener() }
    var correo by remember { mutableStateOf(sesionGuardada?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(SesionPersistente.getRecordarme()) }
    var error by remember { mutableStateOf<String?>(null) }
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

    fun intentarEntrar() {
        if (correo.isBlank() || password.isBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            error = "Por favor ingresa tu correo y contraseña."
            return
        }
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        error = null
        isLoading = true
        scope.launch {
            when (val resultado = authRepository.iniciarSesion(correo.trim(), password)) {
                is AuthResultado.Exito -> {
                    when (val perfil = perfilRepository.obtenerMiPerfil()) {
                        is PerfilResultado.Exito -> {
                            val sesion = sesionParaRol(perfil.rolNombre, perfil.nombre)
                            isLoading = false
                            if (sesion == null) {
                                authRepository.cerrarSesion()
                                error = "Tu cuenta no tiene acceso a la app móvil."
                            } else {
                                SesionPersistente.setRecordarme(recordarme)
                                if (recordarme) {
                                    SesionPersistente.guardar(
                                        DatosSesionGuardada(
                                            email = correo.trim(),
                                            nombre = perfil.nombre,
                                            rol = perfil.rolNombre,
                                        ),
                                    )
                                } else {
                                    SesionPersistente.limpiar()
                                }
                                sesionLista = sesion
                                success = true
                            }
                        }
                        is PerfilResultado.Error -> {
                            authRepository.cerrarSesion()
                            isLoading = false
                            error = perfil.mensaje
                        }
                    }
                }
                is AuthResultado.Error -> {
                    isLoading = false
                    error = resultado.mensaje
                }
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
                onCorreoChange = { correo = it; error = null },
                password = password,
                onPasswordChange = { password = it; error = null },
                recordarme = recordarme,
                onRecordarmeChange = { recordarme = it },
                error = error,
                isLoading = isLoading,
                onSubmit = { intentarEntrar() },
            )
        },
    )
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
        Text("INTRANET DE PLANTA", style = CobreTheme.text.labelCaps, color = colors.accentText)
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
    isLoading: Boolean,
    onSubmit: () -> Unit,
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
                    isLoading = isLoading,
                    onSubmit = onSubmit,
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
    isLoading: Boolean,
    onSubmit: () -> Unit,
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
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) CobreIcons.VisibilityOff else CobreIcons.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        )
        Spacer(Modifier.height(12.dp))
        val haptic = LocalHapticFeedback.current
        var aviso by remember { mutableStateOf(false) }
        LaunchedEffect(aviso) { if (aviso) { delay(2000); aviso = false } }
        RecordarmeYOlvideFila(
            recordarme = recordarme,
            onRecordarmeChange = onRecordarmeChange,
            onOlvideContrasena = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                aviso = true
            },
        )
        AnimatedVisibility(visible = aviso) {
            Text(
                "Disponible próximamente",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                modifier = Modifier.align(Alignment.End).semantics { liveRegion = LiveRegionMode.Polite },
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
    onOlvideContrasena: () -> Unit,
) {
    val colors = CobreTheme.colors
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CobreShapes.md)
                .toggleable(value = recordarme, role = Role.Checkbox) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRecordarmeChange(it)
                }
                .padding(vertical = 12.dp, horizontal = 2.dp),
        ) {
            Checkbox(
                checked = recordarme,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.accentFill,
                    checkmarkColor = colors.onAccentFill,
                    uncheckedColor = colors.fieldBorder,
                ),
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Recordarme", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
        }
        Text(
            "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.labelMedium,
            color = colors.accentText,
            modifier = Modifier
                .clickable(role = Role.Button, onClick = onOlvideContrasena)
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
        Text("LAVANDERÍA EL COBRE · GESTIÓN INTERNA", style = CobreTheme.text.labelCaps, color = colors.textSecondary)
        Text("Acceso seguro para personal autorizado", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
    }
}
