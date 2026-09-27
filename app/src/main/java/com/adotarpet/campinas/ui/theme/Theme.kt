package com.adotarpet.campinas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AdotarPetColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = White,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,
    secondary = GreenTagText,
    secondaryContainer = GreenTag,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = CreamCard,
    onSurface = TextPrimary,
    surfaceVariant = NeutralTagBg,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSoft,
    error = TerracottaDark,
)

@Composable
fun AdotarPetTheme(
    // O protótipo é sempre "light", mas deixamos o parâmetro pronto para o futuro.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AdotarPetColorScheme,
        typography = AppTypography,
        content = content
    )
}
