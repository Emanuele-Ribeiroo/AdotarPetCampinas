package com.adotarpet.campinas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import com.adotarpet.campinas.navigation.Screen
import com.adotarpet.campinas.ui.components.AdotarPetBottomBar
import com.adotarpet.campinas.ui.screens.AdoptionFormScreen
import com.adotarpet.campinas.ui.screens.AdotarScreen
import com.adotarpet.campinas.ui.screens.ConfirmationScreen
import com.adotarpet.campinas.ui.screens.HomeScreen
import com.adotarpet.campinas.ui.screens.PetDetailScreen
import com.adotarpet.campinas.ui.screens.ProfileScreen
import com.adotarpet.campinas.ui.screens.RegisterAnimalScreen
import com.adotarpet.campinas.ui.screens.RescueScreen
import com.adotarpet.campinas.ui.theme.AdotarPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdotarPetTheme {
                AdotarPetApp()
            }
        }
    }
}

@Composable
fun AdotarPetApp() {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        // A bottom bar agora aparece em TODAS as telas do app, sem exceção.
        bottomBar = {
            AdotarPetBottomBar(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onQuemAdotarClick = { navController.navigate(Screen.Adotar.route) },
                    onDoarClick = { /* TODO: tela/fluxo de doação */ },
                    onVoluntarioClick = { /* TODO: tela/fluxo de voluntariado */ }
                )
            }
            composable(Screen.Adotar.route) {
                AdotarScreen(
                    onConhecerClick = { petId ->
                        navController.navigate(Screen.PetDetail.createRoute(petId))
                    },
                    onAdotarClick = { petId ->
                        navController.navigate(Screen.Formulario.createRoute(petId))
                    },
                    onCadastrarAnimalClick = {
                        navController.navigate(Screen.CadastroAnimal.route)
                    }
                )
            }
            composable(Screen.PetDetail.route) { backStack ->
                val petId = backStack.arguments?.getString("petId") ?: return@composable
                PetDetailScreen(
                    petId = petId,
                    onBackClick = { navController.popBackStack() },
                    onAdotarClick = { id -> navController.navigate(Screen.Formulario.createRoute(id)) }
                )
            }
            composable(Screen.Formulario.route) { backStack ->
                val petId = backStack.arguments?.getString("petId") ?: return@composable
                AdoptionFormScreen(
                    petId = petId,
                    onEnviarClick = { id ->
                        navController.navigate(Screen.Confirmacao.createRoute(id)) {
                            popUpTo(Screen.Adotar.route)
                        }
                    }
                )
            }
            composable(Screen.Confirmacao.route) { backStack ->
                val petId = backStack.arguments?.getString("petId") ?: return@composable
                ConfirmationScreen(
                    petId = petId,
                    onVerOutrosClick = {
                        navController.navigate(Screen.Adotar.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onVoltarInicioClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Perfil.route) {
                ProfileScreen(
                    onPetClick = { petId -> navController.navigate(Screen.PetDetail.createRoute(petId)) },
                    onCadastrarAnimalClick = { navController.navigate(Screen.CadastroAnimal.route) }
                )
            }
            composable(Screen.Resgate.route) {
                RescueScreen(
                    onEnviarResgate = { /* TODO: enviar para backend/API */ }
                )
            }
            composable(Screen.CadastroAnimal.route) {
                RegisterAnimalScreen(
                    onVerListaClick = {
                        navController.navigate(Screen.Adotar.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}
