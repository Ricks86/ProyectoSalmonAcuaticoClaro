package com.example.aquacheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.model.ChecklistSession
import com.example.aquacheck.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Propósito de la clase: Maneja el estado de la pantalla de Dashboard.
 * Pertenece a la Capa de Presentación (ViewModel según MVVM).
 *
 * Por qué es necesaria: Separa la lógica de obtención de datos de la UI. Convierte el flujo
 * frío (Cold Flow) que viene de la base de datos en un flujo de estado (StateFlow) que la 
 * pantalla de Compose puede consumir reactivamente y sobrevivir a cambios de configuración.
 *
 * Alcance e Impacto: Es directamente instanciada y consumida por `DashboardScreen`. 
 * Si falla, el usuario no podrá ver su historial de chequeos pre-buceo.
 *
 * @see com.example.aquacheck.ui.screens.DashboardScreen
 * @see ChecklistRepository
 */
class DashboardViewModel(private val repository: ChecklistRepository) : ViewModel() {

    /**
     * @property sessions Flujo reactivo (StateFlow) que contiene la lista actualizada de 
     * todos los chequeos almacenados en la base de datos.
     * Utiliza `stateIn` para mantener el último estado emitido en memoria.
     */
    val sessions: StateFlow<List<ChecklistSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

/**
 * Propósito de la clase: Fábrica para instanciar el DashboardViewModel pasándole dependencias.
 * Requerido porque ViewModel no acepta parámetros en su constructor por defecto.
 */
class DashboardViewModelFactory(private val repository: ChecklistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * ViewModel: Componente de Jetpack diseñado para almacenar y administrar datos 
 * relacionados con la UI de manera que sobrevivan a los cambios de configuración 
 * (como la rotación de pantalla).
 * StateFlow: Es como un "tubo" de datos que siempre recuerda y emite el último valor a quien 
 * se conecte. Ideal para representar el estado actual de una pantalla (UDF - Unidirectional Data Flow).
 * SharingStarted.WhileSubscribed(5000): Estrategia que apaga la recolección de la DB si 
 * la pantalla no está visible durante 5 segundos, ahorrando batería y recursos.
 * ==============================================================================
 */
