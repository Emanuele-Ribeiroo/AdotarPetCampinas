package com.adotarpet.campinas

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import com.adotarpet.campinas.data.SettingsRepository
import com.adotarpet.campinas.data.ThemeMode
import com.adotarpet.campinas.data.UserRole
import androidx.compose.runtime.LaunchedEffect
import com.adotarpet.campinas.ui.screens.AdminPanelScreen
import com.adotarpet.campinas.ui.screens.AdminReportsScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adotarpet.campinas.navigation.Screen
import com.adotarpet.campinas.ui.components.AdotarPetBottomBar
import com.adotarpet.campinas.ui.screens.AdoptionFormScreen
import com.adotarpet.campinas.ui.screens.AdotarScreen
import com.adotarpet.campinas.ui.screens.ConfirmationScreen
import com.adotarpet.campinas.ui.screens.DonationScreen
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.ui.screens.HelpScreen
import com.adotarpet.campinas.ui.screens.LoginScreen
import com.adotarpet.campinas.ui.screens.HistoryScreen
import com.adotarpet.campinas.ui.screens.HomeScreen
import com.adotarpet.campinas.ui.screens.MyDataScreen
import com.adotarpet.campinas.ui.screens.MyPetsScreen
import com.adotarpet.campinas.ui.screens.PetDetailScreen
import com.adotarpet.campinas.ui.screens.ProfileScreen
import com.adotarpet.campinas.ui.screens.RegisterAnimalScreen
import com.adotarpet.campinas.ui.screens.RescueScreen
import com.adotarpet.campinas.ui.screens.VolunteerScreen
import com.adotarpet.campinas.ui.theme.AdotarPetTheme
import kotlinx.coroutines.launch

private val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val mode by SettingsRepository.themeMode.collectAsState()
            val darkTheme = when (mode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            DisposableEffect(darkTheme) {
                // Ícones da barra de status e de navegação acompanham o tema escolhido no app.
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { darkTheme }
                )
                onDispose { }
            }
            AdotarPetTheme(darkTheme = darkTheme) {
                AdotarPetApp()
            }
        }
    }
}

@Composable
fun AdotarPetApp() {
    val loggedIn by SettingsRepository.loggedIn.collectAsState()
    if (loggedIn) {
        // Ao sair da conta, AdotarPetMain deixa a composição e a navegação recomeça do início no próximo login.
        AdotarPetMain()
    } else {
        LoginScreen(
            onLoggedIn = { UserRepository.fillBlanks(email = SettingsRepository.accountEmail.value) }
        )
    }
}

@Composable
private fun AdotarPetMain() {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val role by SettingsRepository.role.collectAsState()
    val isAdmin = role == UserRole.ADMIN
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val aviso: (String) -> Unit = { mensagem ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(mensagem)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // A bottom bar aparece em TODAS as telas do app, sem exceção.
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 14 } },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(260)) { it / 14 } }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onQuemAdotarClick = { navController.navigate(Screen.Adotar.route) },
                    onDoarClick = { navController.navigate(Screen.Doar.route) },
                    onVoluntarioClick = { navController.navigate(Screen.Voluntario.route) }
                )
            }
            composable(Screen.Doar.route) {
                DonationScreen(
                    onVoltarClick = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Voluntario.route) {
                VolunteerScreen(
                    onVoltarClick = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                    onBackClick = { navController.popBackStack() }
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
                    onAdotarClick = { id -> navController.navigate(Screen.Formulario.createRoute(id)) },
                    onEditClick = { id -> navController.navigate(Screen.EditarAnimal.createRoute(id)) },
                    onDeleted = { nome ->
                        navController.popBackStack()
                        aviso("$nome foi excluído")
                    }
                )
            }
            composable(Screen.Formulario.route) { backStack ->
                val petId = backStack.arguments?.getString("petId") ?: return@composable
                AdoptionFormScreen(
                    petId = petId,
                    onBackClick = { navController.popBackStack() },
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
                    onAdminPanelClick = { navController.navigate(Screen.AdminPainel.route) },
                    onMeusDadosClick = { navController.navigate(Screen.Dados.route) },
                    onHistoricoClick = { tipo -> navController.navigate(Screen.Historico.createRoute(tipo)) },
                    onAjudaClick = { navController.navigate(Screen.Ajuda.route) }
                )
            }
            composable(Screen.Resgate.route) {
                RescueScreen(
                    onEnviarResgate = {
                        /* TODO: enviar para backend/API */
                    }
                )
            }
            composable(Screen.CadastroAnimal.route) {
                AdminOnly(isAdmin, onDenied = { navController.popBackStack() }) {
                    RegisterAnimalScreen(
                        onBackClick = { navController.popBackStack() },
                        onSaved = {
                            aviso("Animal cadastrado com sucesso!")
                            navController.navigate(Screen.Adotar.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }
            composable(Screen.EditarAnimal.route) { backStack ->
                val petId = backStack.arguments?.getString("petId") ?: return@composable
                AdminOnly(isAdmin, onDenied = { navController.popBackStack() }) {
                    RegisterAnimalScreen(
                        petId = petId,
                        onBackClick = { navController.popBackStack() },
                        onSaved = {
                            aviso("Alterações salvas")
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable(Screen.Dados.route) {
                MyDataScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaved = {
                        aviso("Seus dados foram salvos")
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Historico.route) { backStack ->
                HistoryScreen(
                    tipo = backStack.arguments?.getString("tipo") ?: "TODOS",
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.AdminPainel.route) {
                AdminOnly(isAdmin, onDenied = { navController.popBackStack() }) {
                    AdminPanelScreen(
                        onBackClick = { navController.popBackStack() },
                        onCadastrarClick = { navController.navigate(Screen.CadastroAnimal.route) },
                        onGerenciarClick = { navController.navigate(Screen.MeusAnimais.route) },
                        onRelatoriosClick = { navController.navigate(Screen.Relatorios.route) }
                    )
                }
            }
            composable(Screen.Relatorios.route) {
                AdminOnly(isAdmin, onDenied = { navController.popBackStack() }) {
                    AdminReportsScreen(onBackClick = { navController.popBackStack() })
                }
            }
            composable(Screen.MeusAnimais.route) {
                AdminOnly(isAdmin, onDenied = { navController.popBackStack() }) {
                    MyPetsScreen(
                        onBackClick = { navController.popBackStack() },
                        onPetClick = { id -> navController.navigate(Screen.PetDetail.createRoute(id)) },
                        onEditClick = { id -> navController.navigate(Screen.EditarAnimal.createRoute(id)) },
                        onCadastrarClick = { navController.navigate(Screen.CadastroAnimal.route) },
                        onDeleted = { nome -> aviso("$nome foi excluído") }
                    )
                }
            }
            composable(Screen.Ajuda.route) {
                HelpScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}

/** Mostra o conteúdo só para administradores; para os outros, volta para a tela anterior. */
@Composable
private fun AdminOnly(isAdmin: Boolean, onDenied: () -> Unit, content: @Composable () -> Unit) {
    if (isAdmin) content() else LaunchedEffect(Unit) { onDenied() }
}
