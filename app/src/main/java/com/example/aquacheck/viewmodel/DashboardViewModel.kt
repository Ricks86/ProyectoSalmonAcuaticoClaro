package com.example.aquacheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.model.local.entities.ImmersionPlan
import com.example.aquacheck.model.local.entities.ImmersionStatus
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.repository.AquaCheckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel del Dashboard principal.
 *
 * Expone los datos necesarios para que [DashboardScreen] construya su contenido:
 * el usuario logueado (para el saludo personalizado) y los planes de inmersión
 * organizados por estado (para las tarjetas de resumen).
 *
 * Recibe el [loggedInUser] como parámetro de constructor en lugar de observarlo
 * desde un repositorio, porque ya fue resuelto por [AuthViewModel] antes de navegar
 * al Dashboard.
 *
 * @param repository Fuente de datos para los [ImmersionPlan].
 * @param loggedInUser El usuario cuya sesión está activa.
 */
class DashboardViewModel(
    repository: AquaCheckRepository,
    val loggedInUser: User
) : ViewModel() {

    /**
     * Todos los planes de inmersión ordenados por fecha descendente.
     * Las tarjetas de resumen del Dashboard filtran desde este flujo.
     */
    val allPlans: StateFlow<List<ImmersionPlan>> = repository.immersionPlanDao.getAll()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Planes con estado [ImmersionStatus.PLANIFICADA]: faenas programadas pero aún no iniciadas.
     * Se usa en la tarjeta "Planes Programados" del Dashboard.
     */
    val plannedPlans: StateFlow<List<ImmersionPlan>> =
        repository.immersionPlanDao.getByStatus(ImmersionStatus.PLANIFICADA)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Planes con estado [ImmersionStatus.EN_CURSO]: faenas activas en este momento.
     * El Dashboard los destaca visualmente como alertas de alta prioridad.
     */
    val activePlans: StateFlow<List<ImmersionPlan>> =
        repository.immersionPlanDao.getByStatus(ImmersionStatus.EN_CURSO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Planes con estado [ImmersionStatus.FINALIZADA]: historial de faenas concluidas.
     * Usado en la tarjeta "Historial" del Dashboard.
     */
    val finishedPlans: StateFlow<List<ImmersionPlan>> =
        repository.immersionPlanDao.getByStatus(ImmersionStatus.FINALIZADA)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ─── Factory ─────────────────────────────────────────────────────────────

    /** Fábrica que inyecta tanto el repositorio como el usuario logueado. */
    class Factory(
        private val repository: AquaCheckRepository,
        private val loggedInUser: User
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(repository, loggedInUser) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
