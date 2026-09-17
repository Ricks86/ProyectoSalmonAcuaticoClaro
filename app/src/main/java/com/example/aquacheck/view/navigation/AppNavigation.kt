package com.example.aquacheck.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aquacheck.model.repository.AquaCheckRepository
import com.example.aquacheck.view.screens.DashboardScreen
import com.example.aquacheck.view.screens.LoginScreen
import com.example.aquacheck.viewmodel.AuthViewModel

/**
 * Rutas de navegación de la app, definidas como constantes para evitar errores de tipeo.
 *
 * Usar un objeto con constantes en lugar de strings sueltos es una práctica estándar
 * en proyectos Compose para mantener el grafo de navegación mantenible.
 */
object Routes {
    /** Pantalla inicial: selector de usuario. */
    const val LOGIN     = "login"

    /** Pantalla principal tras iniciar sesión. */
    const val DASHBOARD = "dashboard"
}

/**
 * Grafo de navegación principal de AquaCheck.
 *
 * Crea el [AuthViewModel] en este nivel para que sea compartido entre [LoginScreen]
 * y [DashboardScreen] sin necesidad de pasarlo como parámetro a través de la pila.
 * Ambas pantallas observan el mismo [AuthViewModel.loggedInUser] para reaccionar
 * a cambios de sesión.
 *
 * La transición Login → Dashboard ocurre automáticamente cuando [AuthViewModel.loggedInUser]
 * pasa de `null` a un usuario válido. El camino inverso ocurre en el logout.
 *
 * @param repository La instancia del repositorio creada en [MainActivity] y distribuida aquí.
 */
@Composable
fun AppNavigation(repository: AquaCheckRepository) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(repository)
    )
    val loggedInUser by authViewModel.loggedInUser.collectAsState()

    NavHost(
        navController  = navController,
        startDestination = Routes.LOGIN
    ) {
        // ── Pantalla de Login ────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel  = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        // Elimina el Login del back stack: presionar Atrás desde
                        // el Dashboard no vuelve al selector de usuario.
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Pantalla del Dashboard ───────────────────────────────────────────
        composable(Routes.DASHBOARD) {
            // Si el usuario cierra sesión mientras está en el Dashboard, navega
            // de vuelta al Login. El usuario es no-nulo aquí porque se navegó
            // a este destino solo tras un login exitoso.
            val user = loggedInUser ?: return@composable

            DashboardScreen(
                repository   = repository,
                loggedInUser = user,
                onLogout     = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
    }
}
