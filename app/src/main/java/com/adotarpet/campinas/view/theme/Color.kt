package com.adotarpet.campinas.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDarkTheme = staticCompositionLocalOf { false }

@Composable
private fun pick(light: Long, dark: Long): Color = Color(if (LocalDarkTheme.current) dark else light)

val CreamBackground: Color @Composable get() = pick(0xFFF3E7D3, 0xFF1F1A16)
val CreamCard: Color @Composable get() = pick(0xFFFAF3E7, 0xFF2B241E)
val Terracotta: Color @Composable get() = pick(0xFFC1702E, 0xFFD58443)
val TerracottaDark: Color @Composable get() = pick(0xFF8A4A1E, 0xFFF0B98A)
val TerracottaLight: Color @Composable get() = pick(0xFFF6D9C4, 0xFF4A3426)
val SalmonBanner: Color @Composable get() = pick(0xFFF9DCC9, 0xFF4A3429)
val GreenTag: Color @Composable get() = pick(0xFFD9E9D1, 0xFF2F4128)
val GreenTagText: Color @Composable get() = pick(0xFF4C6B3F, 0xFFB9D8AA)
val NeutralTagBg: Color @Composable get() = pick(0xFFEDE6D8, 0xFF3A322A)
val TextPrimary: Color @Composable get() = pick(0xFF3B2A1E, 0xFFF1E7DA)
val TextSecondary: Color @Composable get() = pick(0xFF7A6A57, 0xFFBDAE9A)
val OutlineSoft: Color @Composable get() = pick(0xFFD8C9B0, 0xFF5A4C3E)
val SuccessGreen: Color @Composable get() = pick(0xFFDCEFD3, 0xFF2F4128)
val SuccessGreenIcon: Color @Composable get() = pick(0xFF4C6B3F, 0xFFB9D8AA)

val SurfaceWhite: Color @Composable get() = pick(0xFFFFFFFF, 0xFF2B241E)
val White = Color(0xFFFFFFFF)
val BrandBrown = Color(0xFF8A4A1E)
