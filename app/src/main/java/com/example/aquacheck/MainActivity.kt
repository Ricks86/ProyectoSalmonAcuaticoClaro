package com.example.aquacheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquacheck.data.local.AppDatabase
import com.example.aquacheck.data.repository.ChecklistRepository
import com.example.aquacheck.ui.navigation.AppNavigation
import com.example.aquacheck.ui.theme.AquaCheckTheme

/**
 * Propósito de la clase: Es la única Activity de la aplicación (Single-Activity Architecture).
 * Actúa como contenedor raíz del sistema Android para lanzar el código de Jetpack Compose.
 *
 * Por qué es necesaria: El sistema operativo Android requiere al menos una Activity definida en
 * el AndroidManifest.xml para saber dónde empezar a ejecutar la app cuando el usuario 
 * toca el ícono en su teléfono.
 *
 * Alcance e Impacto: Es el punto de inyección manual más alto. Aquí inicializamos la 
 * Base de Datos y el Repositorio antes de pasarlos a la gráfica de navegación de Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual Dependency Injection (Inyección de Dependencias Manual):
        // En un proyecto grande, esto lo haría Hilt o Koin. 
        // Aquí instanciamos explícitamente el motor de Room y el Repository.
        val database = AppDatabase.getDatabase(this)
        val repository = ChecklistRepository(database.checklistDao())
        
        enableEdgeToEdge()
        
        // setContent puentea el mundo View (Activity tradicional) con el mundo Jetpack Compose.
        setContent {
            AquaCheckTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * ComponentActivity: Clase base moderna y ligera (en vez de AppCompatActivity) recomendada 
 * cuando solo se utilizará Compose.
 * enableEdgeToEdge(): Permite que la UI dibuje debajo de la barra de estado superior (batería, hora)
 * y la barra de navegación inferior, dándole un aspecto moderno a la app.
 * setContent: La función "mágica" que inicializa el motor de renderizado declarativo de Compose.
 * ==============================================================================
 */