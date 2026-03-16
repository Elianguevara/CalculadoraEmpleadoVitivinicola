package com.elian.calculadora_empleado_vitivinicola.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// 🎨 Colores personalizados
private val LightColors = lightColorScheme(
    primary = Color(0xFF7B1FA2),          // Violeta elegante
    onPrimary = Color.White,
    secondary = Color(0xFF6A1B9A),
    onSecondary = Color.White,
    surface = Color(0xFFF8F5FB),
    surfaceVariant = Color(0xFFEDE7F6),
    background = Color(0xFFFDFBFE),
    onBackground = Color(0xFF1D1B20)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD1A9E8),
    onPrimary = Color(0xFF3A0A52),
    secondary = Color(0xFFBB86FC),
    onSecondary = Color(0xFF240046),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF2A2433),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5)
)

// 🪶 Tipografía moderna y legible
val AppTypography = Typography(
    titleLarge = Typography().titleLarge.copy(letterSpacing = 0.5.sp),
    titleMedium = Typography().titleMedium.copy(letterSpacing = 0.4.sp),
    bodyLarge = Typography().bodyLarge.copy(lineHeight = 20.sp),
    bodyMedium = Typography().bodyMedium.copy(lineHeight = 18.sp),
    labelMedium = Typography().labelMedium.copy(letterSpacing = 0.5.sp)
)

// 🌗 Tema adaptable (modo claro / oscuro / dinámico)
@Composable
fun CalculadoraSalarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        content = content
    )
}
