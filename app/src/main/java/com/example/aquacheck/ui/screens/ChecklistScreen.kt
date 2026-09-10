package com.example.aquacheck.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession
import com.example.aquacheck.data.model.ValidationState
import com.example.aquacheck.data.repository.ChecklistRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.viewmodel.ChecklistViewModel
import com.example.aquacheck.ui.viewmodel.ChecklistViewModelFactory

/**
 * Propósito de la clase: Pantalla Stateful responsable del llenado dinámico del checklist.
 * 
 * Por qué es necesaria: Orquesta la recolección de los datos reactivos del [ChecklistViewModel]
 * y los pasa al componente [ChecklistContent] que los dibuja.
 *
 * Alcance e Impacto: Es el "core" operativo de la app. Si falla la lógica aquí, 
 * los chequeos no podrán ser aprobados o rechazados.
 */
@Composable
fun ChecklistScreen(
    repository: ChecklistRepository,
    sessionId: String,
    onFinished: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ChecklistViewModel = viewModel(factory = ChecklistViewModelFactory(repository, sessionId))
    val session by viewModel.session.collectAsState()
    val items by viewModel.items.collectAsState()

    ChecklistContent(
        session = session,
        items = items,
        onStateChange = { item, newState, obs ->
            viewModel.updateItemState(item, newState, obs)
        },
        onFinished = {
            viewModel.finishChecklist(onFinished)
        }
    )
}

/**
 * Propósito de la función: Dibuja la pantalla con la lista completa de ítems de seguridad. Stateless.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistContent(
    session: ChecklistSession?,
    items: List<ChecklistItem>,
    onStateChange: (ChecklistItem, ValidationState, String) -> Unit,
    onFinished: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checklist de Seguridad") })
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = onFinished,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Finalizar Chequeo")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                ChecklistItemRow(item = item, onStateChange = { newState, obs ->
                    onStateChange(item, newState, obs)
                })
            }
        }
    }
}

/**
 * Propósito de la función: Componente visual de una sola fila de evaluación. 
 * Contiene lógica condicional para forzar al supervisor a justificar una anomalía.
 *
 * @param item El ítem evaluado (Ej: Compresor, Oxígeno).
 * @param onStateChange Función disparada al marcar o modificar una nota.
 */
@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onStateChange: (ValidationState, String) -> Unit
) {
    // Almacena localmente las observaciones de este componente mientras se digitan.
    var obs by remember(item.observacion) { mutableStateOf(item.observacion ?: "") }
    var selectedState by remember(item.estadoValidacion) { mutableStateOf(item.estadoValidacion) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${item.categoria}: ${item.nombreItem}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Simula un grupo de RadioButtons iterando sobre las constantes del Enum
            ValidationState.values().forEach { state ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedState == state,
                        onClick = { 
                            selectedState = state
                            onStateChange(state, obs)
                        }
                    )
                    Text(text = state.name)
                }
            }

            // Regla UI: Mostrar campo de texto obligatorio sólo si hay problemas
            if (selectedState == ValidationState.NO_CUMPLE || selectedState == ValidationState.OBSERVADO) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = obs,
                    onValueChange = { 
                        obs = it
                        if (selectedState != null) {
                            onStateChange(selectedState!!, obs)
                        }
                    },
                    label = { Text("Observación (Obligatorio)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistContentPreview() {
    AquaCheckTheme {
        ChecklistContent(
            session = null,
            items = listOf(
                ChecklistItem(itemId = 1, sessionId = "1", categoria = "Equipamiento", nombreItem = "Máscara facial con comunicaciones", estadoValidacion = ValidationState.CUMPLE, observacion = null, fotoUri = null),
                ChecklistItem(itemId = 2, sessionId = "1", categoria = "Equipamiento", nombreItem = "Botella de emergencia", estadoValidacion = ValidationState.NO_CUMPLE, observacion = "Falta carga", fotoUri = null),
            ),
            onStateChange = { _, _, _ -> },
            onFinished = {}
        )
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * collectAsState(): Función clave que "escucha" un Flow (normalmente del ViewModel)
 * y provoca que Compose se vuelva a dibujar cada vez que llega un nuevo dato desde la Base de Datos.
 * Card: Componente Material Design que aporta elevación, bordes curvos y sensación de bloque.
 * ==============================================================================
 */
