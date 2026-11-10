package com.adotarpet.campinas.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AdotarPetTheme(

    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        val colorScheme = if (darkTheme) {
            darkColorScheme(
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
        } else {
            lightColorScheme(
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
        }
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
