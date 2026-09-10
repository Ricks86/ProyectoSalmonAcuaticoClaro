package com.example.aquacheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession
import com.example.aquacheck.data.model.ValidationState
import com.example.aquacheck.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Propósito de la clase: Controla y mantiene el estado en la pantalla de llenado dinámico del Checklist.
 * Capa de Presentación (ViewModel).
 *
 * Por qué es necesaria: Mantiene sincronizados los estados seleccionados en UI (radio buttons, texto)
 * con la base de datos de manera atómica y reacciona a los cambios en tiempo real. También engloba 
 * la regla de negocio de "Aprobar" o "Rechazar" el chequeo con base en los resultados.
 *
 * Alcance e Impacto: Es consumida por [com.example.aquacheck.ui.screens.ChecklistScreen].
 *
 * @see com.example.aquacheck.ui.screens.ChecklistScreen
 */
class ChecklistViewModel(
    private val repository: ChecklistRepository,
    private val sessionId: String
) : ViewModel() {

    private val _session = MutableStateFlow<ChecklistSession?>(null)
    /** @property session El estado actual de la cabecera del chequeo, expuesto inmutablemente a la UI. */
    val session: StateFlow<ChecklistSession?> = _session.asStateFlow()

    private val _items = MutableStateFlow<List<ChecklistItem>>(emptyList())
    /** @property items El listado de ítems de seguridad evaluados, expuestos inmutablemente a la UI. */
    val items: StateFlow<List<ChecklistItem>> = _items.asStateFlow()

    init {
        // Carga inicial reactiva. Cualquier cambio en la BD actualizará _items automáticamente.
        viewModelScope.launch {
            _session.value = repository.getSessionById(sessionId)
            repository.getItemsForSession(sessionId).collect { list ->
                _items.value = list
            }
        }
    }

    /**
     * Reacciona a la interacción del usuario en la pantalla y persiste el estado (Ej: marcar la máscara como NO_CUMPLE).
     *
     * @param item El ítem original antes del cambio.
     * @param newState El nuevo estado ([ValidationState]) que el usuario seleccionó.
     * @param observacion Texto introducido. Si no es un estado observacional, se fuerza a nulo por seguridad.
     */
    fun updateItemState(item: ChecklistItem, newState: ValidationState, observacion: String) {
        val updatedItem = item.copy(
            estadoValidacion = newState,
            observacion = if (newState == ValidationState.OBSERVADO || newState == ValidationState.NO_CUMPLE) observacion else null
        )
        viewModelScope.launch {
            repository.updateItem(updatedItem)
        }
    }

    /**
     * Finaliza el chequeo evaluando los resultados de todos los ítems de seguridad.
     * Aplica la principal regla de negocio del sistema de evaluación.
     * 
     * @param onFinished Callback ejecutado al concluir el guardado para que la UI regrese a Dashboard.
     */
    fun finishChecklist(onFinished: () -> Unit) {
        viewModelScope.launch {
            val currentSession = _session.value ?: return@launch
            val currentItems = _items.value
            
            // Regla de Negocio: 1 crítico anula el buceo. 1 observación requiere revisión.
            val hasCritical = currentItems.any { it.estadoValidacion == ValidationState.NO_CUMPLE }
            val hasObservado = currentItems.any { it.estadoValidacion == ValidationState.OBSERVADO }
            
            val resultado = when {
                hasCritical -> "No Apto"
                hasObservado -> "Requiere Revisión"
                else -> "Apto"
            }

            val updatedSession = currentSession.copy(resultadoFinal = resultado)
            repository.updateSession(updatedSession)
            onFinished()
        }
    }
}

/**
 * Propósito de la clase: Inyectar [ChecklistRepository] y el [sessionId] en [ChecklistViewModel].
 */
class ChecklistViewModelFactory(
    private val repository: ChecklistRepository,
    private val sessionId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChecklistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChecklistViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * MutableStateFlow / StateFlow: MutableStateFlow permite modificar su valor internamente.
 * asStateFlow() lo convierte en StateFlow de solo lectura hacia afuera, protegiendo
 * el estado interno para que la UI no pueda alterarlo directamente sin pasar por las funciones.
 * init { }: Bloque de inicialización que se ejecuta al construirse el ViewModel.
 * ==============================================================================
 */
