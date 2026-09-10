package com.example.aquacheck.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aquacheck.data.repository.ChecklistRepository
import com.example.aquacheck.ui.screens.ChecklistScreen
import com.example.aquacheck.ui.screens.DashboardScreen
import com.example.aquacheck.ui.screens.NewCheckScreen

/**
 * Propósito de la función: Define y gestiona el enrutamiento (NavGraph) de toda la aplicación.
 * Pertenece a la Capa de Presentación (UI).
 *
 * Por qué es necesaria: En lugar de usar múltiples Activities o Fragments (el estándar antiguo),
 * Single-Activity Compose usa un `NavHost` para intercambiar pantallas virtualmente.
 *
 * Alcance e Impacto: Es el "directorio" de la app. Si una ruta está mal escrita o falta un 
 * argumento (como `sessionId`), la app fallará al intentar navegar.
 *
 * @param repository El Single Source of Truth, inyectado aquí y distribuido a las pantallas.
 */
@Composable
fun AppNavigation(repository: ChecklistRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        
        // Pantalla 1: Historial
        composable("dashboard") {
            DashboardScreen(
                repository = repository,
                onNavigateToNewCheck = { navController.navigate("new_check") },
                onNavigateToSession = { sessionId -> navController.navigate("checklist/$sessionId") }
            )
        }
        
        // Pantalla 2: Formulario inicial
        composable("new_check") {
            NewCheckScreen(
                repository = repository,
                onSessionCreated = { sessionId -> 
                    // popBackStack quita el formulario de la historia para que el botón "Atrás" 
                    // no devuelva al formulario vacío.
                    navController.popBackStack()
                    navController.navigate("checklist/$sessionId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Pantalla 3: Llenado dinámico del checklist (Requiere parámetro)
        composable("checklist/{sessionId}") { backStackEntry ->
            // Extrae el ID de la URL virtual
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            ChecklistScreen(
                repository = repository,
                sessionId = sessionId,
                onFinished = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * NavHost: Contenedor visual que intercambia qué @Composable se dibuja basado en la ruta actual.
 * navController: El objeto controlador que mantiene la "pila" (BackStack) de pantallas por las 
 * que ha pasado el usuario.
 * composable("ruta"): Define un destino en el mapa de navegación.
 * ==============================================================================
 */
