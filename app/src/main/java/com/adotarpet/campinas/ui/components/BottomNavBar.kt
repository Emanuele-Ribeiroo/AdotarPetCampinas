package com.adotarpet.campinas.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.navigation.bottomNavItems
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TextSecondary

@Composable
fun AdotarPetBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = CreamCard,
        modifier = Modifier.height(72.dp)
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
