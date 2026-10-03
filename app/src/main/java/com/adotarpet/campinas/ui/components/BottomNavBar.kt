package com.adotarpet.campinas.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adotarpet.campinas.navigation.bottomNavItems
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TextSecondary

/**
 * IMPORTANTE: não force uma altura fixa aqui (ex.: Modifier.height(72.dp)).
 * O NavigationBar do Material3 já reserva espaço extra automaticamente para
 * não ficar atrás da barra de gestos/navegação do Android (windowInsets).
 * Uma altura fixa conflita com esse espaçamento e corta os ícones/labels.
 */
@Composable
fun AdotarPetBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = CreamCard,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item.screen.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.bodySmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Terracotta,
                    selectedTextColor = Terracotta,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CreamCard
                )
            )
        }
    }
}
