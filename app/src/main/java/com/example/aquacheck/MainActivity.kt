package com.example.aquacheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aquacheck.model.local.AquaCheckDatabase
import com.example.aquacheck.model.repository.AquaCheckRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.view.navigation.AppNavigation

/**
 * Única Activity de la aplicación (Single-Activity Architecture).
 *
 * Su responsabilidad es mínima: instanciar la base de datos y el repositorio
 * (inyección de dependencias manual para el MVP), y entregar el control a
 * [AppNavigation] dentro del tema visual de la app.
 *
 * En un proyecto de producción, esta lógica de inyección migra a Hilt o Koin.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inyección de dependencias manual:
        // Room garantiza que getInstance retorne siempre la misma instancia (Singleton).
        val database   = AquaCheckDatabase.getInstance(applicationContext)
        val repository = AquaCheckRepository(database)

        setContent {
            AquaCheckTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}