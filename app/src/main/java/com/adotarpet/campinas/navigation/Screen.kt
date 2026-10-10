package com.adotarpet.campinas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Adotar : Screen("adotar")
    data object PetDetail : Screen("pet/{petId}") {
        fun createRoute(petId: String) = "pet/$petId"
    }
    data object Formulario : Screen("formulario/{petId}") {
        fun createRoute(petId: String) = "formulario/$petId"
    }
    data object Confirmacao : Screen("confirmacao/{petId}") {
        fun createRoute(petId: String) = "confirmacao/$petId"
    }
    data object Perfil : Screen("perfil")
    data object Resgate : Screen("resgate")
    data object CadastroAnimal : Screen("cadastro-animal")
    data object Doar : Screen("doar")
    data object Voluntario : Screen("voluntario")
    data object EditarAnimal : Screen("editar-animal/{petId}") {
        fun createRoute(petId: String) = "editar-animal/$petId"
    }
    data object Dados : Screen("meus-dados")
    data object MeusAnimais : Screen("meus-animais")
    data object Ajuda : Screen("ajuda")
    data object AdminPainel : Screen("admin")
    data object Relatorios : Screen("admin-relatorios")
    data object Historico : Screen("historico/{tipo}") {
        fun createRoute(tipo: String) = "historico/$tipo"
    }
}

/** Itens exibidos na barra de navegação inferior, na mesma ordem do protótipo. */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Início", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Adotar, "Adotar", Icons.Filled.FavoriteBorder, Icons.Outlined.FavoriteBorder),
    BottomNavItem(Screen.Perfil, "Perfil", Icons.Filled.Person, Icons.Outlined.Person),
    BottomNavItem(Screen.Resgate, "Resgate", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop),
)
