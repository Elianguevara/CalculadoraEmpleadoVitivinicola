package com.elian.calculadora_empleado_vitivinicola.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Esquema de color — Modo claro ─────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary              = Borravino40,
    onPrimary            = Color.White,
    primaryContainer     = Borravino90,
    onPrimaryContainer   = Borravino10,

    secondary            = VerdeVid40,
    onSecondary          = Color.White,
    secondaryContainer   = VerdeVid90,
    onSecondaryContainer = VerdeVid10,

    tertiary             = Roble40,
    onTertiary           = Color.White,
    tertiaryContainer    = Roble90,
    onTertiaryContainer  = Roble10,

    error                = Color(0xFFBA1A1A),
    onError              = Color.White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),

    background           = WarmWhite,
    onBackground         = Color(0xFF22191A),
    surface              = WarmWhite,
    onSurface            = Color(0xFF22191A),
    surfaceVariant       = WarmSurface,
    onSurfaceVariant     = Color(0xFF534345),
    outline              = Color(0xFF867375),
)

// ── Esquema de color — Modo oscuro ────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary              = Borravino80,
    onPrimary            = Color(0xFF680019),
    primaryContainer     = BorravinoContainerDark,
    onPrimaryContainer   = Borravino80,

    secondary            = VerdeVid80,
    onSecondary          = Color(0xFF0E3000),
    secondaryContainer   = VerdeVidContainerDark,
    onSecondaryContainer = VerdeVid80,

    tertiary             = Roble80,
    onTertiary           = Color(0xFF3A2E00),
    tertiaryContainer    = RobleContainerDark,
    onTertiaryContainer  = Roble80,

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    background           = WarmDark,
    onBackground         = Color(0xFFEDDFE0),
    surface              = WarmDark,
    onSurface            = Color(0xFFEDDFE0),
    surfaceVariant       = WarmDarkVar,
    onSurfaceVariant     = Color(0xFFD6C2C3),
    outline              = Color(0xFFA08C8D),
)

// ── Tipografía ────────────────────────────────────────────────────────────────
val AppTypography = Typography(
    displaySmall  = Typography().displaySmall.copy(letterSpacing  = 0.sp),
    titleLarge    = Typography().titleLarge.copy(letterSpacing    = 0.3.sp),
    titleMedium   = Typography().titleMedium.copy(letterSpacing   = 0.2.sp),
    bodyLarge     = Typography().bodyLarge.copy(lineHeight        = 22.sp),
    bodyMedium    = Typography().bodyMedium.copy(lineHeight       = 20.sp),
    bodySmall     = Typography().bodySmall.copy(lineHeight        = 18.sp),
    labelMedium   = Typography().labelMedium.copy(letterSpacing   = 0.5.sp),
    labelSmall    = Typography().labelSmall.copy(letterSpacing    = 0.4.sp),
)

// ── Tema principal ────────────────────────────────────────────────────────────
@Composable
fun CalculadoraSalarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // dynamicColor desactivado: la paleta vitivinícola siempre prevalece
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Barra de estado transparente — los íconos adoptan el color del contenido detrás
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small      = RoundedCornerShape(8.dp),
            medium     = RoundedCornerShape(16.dp),
            large      = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp),
        ),
        content = content,
    )
}
