package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.ui.graphics.Color

// Primitivas de color. Las pantallas no deberían usarlas directamente: los roles
// semánticos viven en [CobreColors] (Tokens.kt) y se leen con `CobreTheme.colors`.
//
// Fuente: DESIGN.md (sistema "Minimal") + la paleta cobre de la organización, que se
// conserva para los acentos importantes (CTA, QR, pestaña activa, foco).

// ── DESIGN.md ────────────────────────────────────────────────────────────────
val Ink = Color(0xFF0C0C09)      // primary / text
val Paper = Color(0xFFF4F4F1)    // surface / neutral
val Indigo = Color(0xFF312C85)   // secondary
val Success = Color(0xFF16A34A)
val Warning = Color(0xFFD97706)
val Danger = Color(0xFFDC2626)

// Variantes derivadas (no están en DESIGN.md). Los tonos de estado de DESIGN.md no
// llegan a AA (4.5:1) como texto chico sobre Paper: success 3.0, warning 3.2,
// danger 4.4. Se usan tal cual para íconos/rellenos y estas variantes para texto.
val SuccessText = Color(0xFF15803D)
val WarningText = Color(0xFFB45309)
val DangerText = Color(0xFFB91C1C)
// Oscuro: DESIGN.md no define modo oscuro; tonos luminosos con contraste AA sobre Ink.
val IndigoLight = Color(0xFFA9A5F5)
val SuccessLight = Color(0xFF4ADE80)
val WarningLight = Color(0xFFFBBF24)
val DangerLight = Color(0xFFF87171)

// Neutros cálidos derivados de Ink/Paper (misma temperatura que la pareja base).
val InkSoft = Color(0xFF5C5C55)       // texto secundario en claro (6.1:1 sobre Paper)
val InkFaint = Color(0xFF8A8A82)      // bordes de campos en claro (3:1, no-texto)
val PaperDeep = Color(0xFFEBEBE7)     // superficie hundida en claro
val PaperLine = Color(0xFFDEDED9)     // separadores finos en claro
val Graphite1 = Color(0xFF141411)     // contenedor bajo en oscuro
val Graphite2 = Color(0xFF1A1A17)     // superficie de tarjeta en oscuro
val Graphite3 = Color(0xFF24241F)     // superficie elevada en oscuro
val Graphite4 = Color(0xFF2E2E29)     // separadores en oscuro
val GraphiteFaint = Color(0xFF77776F) // bordes de campos en oscuro
val PaperSoft = Color(0xFFA8A8A0)     // texto secundario en oscuro (8.2:1 sobre Ink)

// ── Identidad de la organización: cobre ──────────────────────────────────────
// brand — Copper Orange
val Brand50 = Color(0xFFFFF7ED)
val Brand100 = Color(0xFFFFEDD5)
val Brand200 = Color(0xFFFED7AA)
val Brand300 = Color(0xFFFDBA74)
val Brand400 = Color(0xFFFB923C)
val Brand500 = Color(0xFFF97316)
val Brand600 = Color(0xFFEA580C)
val Brand700 = Color(0xFFC2410C)
val Brand800 = Color(0xFF9A3412)
val Brand900 = Color(0xFF7C2D12)

// copper — Pure Copper
val Copper50 = Color(0xFFFDF8F5)
val Copper100 = Color(0xFFFBEEE6)
val Copper200 = Color(0xFFF6D8C6)
val Copper300 = Color(0xFFEEA883)
val Copper400 = Color(0xFFE37643)
val Copper500 = Color(0xFFDB541A)
val Copper600 = Color(0xFFCC4114) // relleno de CTA en claro: 4.8:1 con texto blanco
val Copper700 = Color(0xFFA93011) // cobre como texto en claro: 6.1:1 sobre Paper
val Copper800 = Color(0xFF882713)
val Copper900 = Color(0xFF6E2112)

// ── Estados ──────────────────────────────────────────────────────────────────
// Pares claro/oscuro que usan los mapeos de estado (etapa, prioridad, severidad,
// alerta, evento). Se reducen a la paleta de DESIGN.md: índigo = proceso/info,
// éxito/aviso/peligro, neutro para lo de baja prioridad.
val StatusSlate = InkSoft
val StatusSlateDark = PaperSoft
val StatusBlue = Indigo
val StatusBlueDark = IndigoLight
val StatusCyan = Indigo
val StatusCyanDark = IndigoLight
val StatusViolet = Indigo
val StatusVioletDark = IndigoLight
val StatusGreen = SuccessText
val StatusGreenDark = SuccessLight
val StatusAmber = WarningText
val StatusAmberDark = WarningLight
val StatusRed = DangerText
val StatusRedDark = DangerLight
val StatusRose = DangerText
val StatusRoseDark = DangerLight

// ── Alias heredados ──────────────────────────────────────────────────────────
// Nombres de la paleta anterior que todavía usan muchas pantallas y componentes.
// Apuntan a los valores de DESIGN.md para que toda la app adopte la paleta nueva sin
// tocar su lógica; el código nuevo debe leer `CobreTheme.colors` en su lugar.
val Neutral50 = Color(0xFFFCFCFB)
val Neutral100 = Paper
val Neutral800 = Graphite3
val Neutral900 = Ink

val Stone50 = Color(0xFFFAFAF8)
val Stone100 = Paper
val Stone200 = PaperDeep
val Stone300 = PaperLine
val Stone400 = PaperSoft
val Stone500 = InkFaint
val Stone600 = InkSoft
val Stone700 = Graphite4
val Stone800 = Graphite3
val Stone900 = Graphite2
val Stone950 = Ink

val Sky200 = Color(0xFFCFCDEB)
val Sky500 = Indigo

val BackgroundWarm = Paper
val ForegroundStone = Ink
val TextSecondaryLight = InkSoft

val DarkBg = Ink
val DarkSurface1 = Graphite2
val DarkSurface2 = Graphite3
val DarkOnSurface = Paper
val DarkOnSurfaceMuted = PaperSoft
val DarkOutline = Graphite4
