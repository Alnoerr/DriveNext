package dev.alnoer.drivenext.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.alnoer.drivenext.R

val lightColorPrimary = Color(0xFF2A1246)
val lightColorOnPrimary = Color.White
val lightColorBackground = Color.White
val lightColorSurface = Color.White

val darkColorPrimary = Color(0xFFD0BCFF)
val darkColorOnPrimary = Color(0xFF2A1246)
val darkColorBackground = Color(0xFF141218)
val darkColorSurface = Color(0xFF141218)

@OptIn(ExperimentalTextApi::class)
private val Montserrat = FontFamily(
    listOf(400, 600).map { weight ->
        Font(
            resId = R.font.montserrat,
            weight = FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight))
        )
    }
)

private val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

private val LightColors = lightColorScheme(
    primary = lightColorPrimary,
    onPrimary = lightColorOnPrimary,
    background = lightColorBackground,
    surface = lightColorSurface
)

private val DarkColors = darkColorScheme(
    primary = darkColorPrimary,
    onPrimary = darkColorOnPrimary,
    background = darkColorBackground,
    surface = darkColorSurface
)

@Composable
fun DriveNextTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
