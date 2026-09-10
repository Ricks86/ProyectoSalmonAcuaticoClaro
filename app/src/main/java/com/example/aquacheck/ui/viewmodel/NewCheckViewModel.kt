package com.example.aquacheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession
import com.example.aquacheck.data.repository.ChecklistRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Propósito de la clase: Maneja la lógica de negocio detrás de la creación de un nuevo chequeo de buceo.
 * Capa de Presentación (ViewModel).
 *
 * Por qué es necesaria: Quita de la UI (la pantalla) la responsabilidad de fabricar entidades, 
 * generar identificadores (UUID) o conocer las reglas iniciales de negocio (los ítems por defecto).
 *
 * Alcance e Impacto: Es consumida por [com.example.aquacheck.ui.screens.NewCheckScreen].
 * Altere los ítems por defecto aquí si cambian los estándares de seguridad obligatorios de la empresa.
 */
class NewCheckViewModel(private val repository: ChecklistRepository) : ViewModel() {

    /**
     * Crea una nueva sesión en la base de datos junto con la plantilla de ítems de seguridad iniciales.
     * Genera un UUID para asegurar la trazabilidad. Utiliza [viewModelScope] para no bloquear la pantalla.
     *
     * @param centroOperacion Instalación física (Ej: "Centro Norte").
     * @param nombreBuzo El buzo responsable de la inmersión.
     * @param nombreSupervisor Persona en superficie validando.
     * @param onSessionCreated Callback invocado en el Main Thread (UI) con el nuevo [sessionId]
     * generado, permitiendo a la pantalla navegar al siguiente paso automáticamente.
     */
    fun createSession(
        centroOperacion: String,
        nombreBuzo: String,
        nombreSupervisor: String,
        onSessionCreated: (String) -> Unit
    ) {
        val sessionId = UUID.randomUUID().toString()
        val session = ChecklistSession(
            sessionId = sessionId,
            centroOperacion = centroOperacion,
            fechaHora = System.currentTimeMillis(),
            nombreBuzo = nombreBuzo,
            nombreSupervisor = nombreSupervisor,
            resultadoFinal = "Pendiente"
        )

        // Definimos ítems por defecto
        val items = listOf(
            ChecklistItem(sessionId = sessionId, categoria = "Equipamiento", nombreItem = "Máscara facial con comunicaciones", estadoValidacion = null, observacion = null, fotoUri = null),
            ChecklistItem(sessionId = sessionId, categoria = "Equipamiento", nombreItem = "Botella de emergencia", estadoValidacion = null, observacion = null, fotoUri = null),
            ChecklistItem(sessionId = sessionId, categoria = "Entorno", nombreItem = "Disponibilidad de compresores", estadoValidacion = null, observacion = null, fotoUri = null),
            ChecklistItem(sessionId = sessionId, categoria = "Salud", nombreItem = "Oxígeno y elementos de apoyo ante contingencias", estadoValidacion = null, observacion = null, fotoUri = null)
        )

        viewModelScope.launch {
            repository.insertSessionWithItems(session, items)
            onSessionCreated(sessionId)
        }
    }
}

/**
 * Propósito de la clase: Inyectar [ChecklistRepository] en [NewCheckViewModel].
 */
class NewCheckViewModelFactory(private val repository: ChecklistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewCheckViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewCheckViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * viewModelScope: Un "CoroutineScope" ligado exclusivamente al ciclo de vida de este ViewModel.
 * Si el ViewModel es destruido, todas las corrutinas (consultas a Room) lanzadas 
 * en este scope son canceladas automáticamente, evitando Memory Leaks o Crashes.
 * ==============================================================================
 */
