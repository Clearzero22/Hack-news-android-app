package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Hacker 风格颜色定义
val HackerGreen = Color(0xFF00FF41)  // 经典黑客绿色
val MatrixGreen = Color(0xFF00F41E)  // 矩阵风格绿色
val CyberBlue = Color(0xFF00D4FF)   // 赛博朋克蓝色
val NeonPurple = Color(0xFFB300FF)  // 霓虹紫色
val TerminalBlack = Color(0xFF0D0D0D) // 终端黑色
val HackerBlack = Color(0xFF0A0A0A)  // 黑客黑色
val CodeGrey = Color(0xFF1E1E1E)     // 代码灰色
val AccentGreen = Color(0xFF39FF14)  // 强调绿色

// 黑客风格深色主题
val HackerDarkColorScheme = darkColorScheme(
    primary = HackerGreen,
    onPrimary = Color.Black,
    secondary = CyberBlue,
    onSecondary = Color.Black,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = HackerBlack,
    onBackground = HackerGreen,
    surface = CodeGrey,
    onSurface = HackerGreen,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = MatrixGreen,
    error = Color(0xFFFF6B6B),
    onError = Color.Black,
    outline = HackerGreen.copy(alpha = 0.3f),
    outlineVariant = HackerGreen.copy(alpha = 0.1f),
    scrim = Color.Black.copy(alpha = 0.8f),
    inverseSurface = HackerGreen,
    inverseOnSurface = Color.Black,
    inversePrimary = Color.Black
)

// 黑客风格浅色主题（备用）
val HackerLightColorScheme = lightColorScheme(
    primary = Color(0xFF006E00),
    onPrimary = Color.White,
    secondary = Color(0xFF0099CC),
    onSecondary = Color.White,
    tertiary = Color(0xFF8B00FF),
    onTertiary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF666666),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFFF0F0F0),
    scrim = Color.Black.copy(alpha = 0.6f),
    inverseSurface = Color(0xFF2A2A2A),
    inverseOnSurface = Color.White,
    inversePrimary = Color.White
)

@Composable
fun HackerTheme(
    darkTheme: Boolean = true, // 强制使用深色主题
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        HackerDarkColorScheme
    } else {
        HackerLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HackerTypography,
        content = content
    )
}