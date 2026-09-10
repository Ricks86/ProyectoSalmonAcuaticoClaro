package com.example.aquacheck.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquacheck.data.repository.ChecklistRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.viewmodel.NewCheckViewModel
import com.example.aquacheck.ui.viewmodel.NewCheckViewModelFactory

/**
 * Propósito de la clase: Pantalla Stateful que envuelve el formulario para iniciar un nuevo chequeo pre-buceo.
 * 
 * Por qué es necesaria: Aquí se mantiene el estado local de los TextFields (el texto que el 
 * usuario está escribiendo) mediante `remember { mutableStateOf() }` y se delega la acción de 
 * "Guardar" al [NewCheckViewModel].
 *
 * Alcance e Impacto: Es el primer paso en la recolección de datos obligatoria para cualquier 
 * operación de buceo.
 *
 * @see NewCheckContent
 */
@Composable
fun NewCheckScreen(
    repository: ChecklistRepository,
    onSessionCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: NewCheckViewModel = viewModel(factory = NewCheckViewModelFactory(repository))
    
    // Estado local para los campos del formulario
    var centroOperacion by remember { mutableStateOf("") }
    var nombreBuzo by remember { mutableStateOf("") }
    var nombreSupervisor by remember { mutableStateOf("") }

    NewCheckContent(
        centroOperacion = centroOperacion,
        onCentroOperacionChange = { centroOperacion = it },
        nombreBuzo = nombreBuzo,
        onNombreBuzoChange = { nombreBuzo = it },
        nombreSupervisor = nombreSupervisor,
        onNombreSupervisorChange = { nombreSupervisor = it },
        onSubmit = {
            viewModel.createSession(
                centroOperacion = centroOperacion,
                nombreBuzo = nombreBuzo,
                nombreSupervisor = nombreSupervisor,
                onSessionCreated = onSessionCreated
            )
        }
    )
}

/**
 * Propósito de la función: Renderiza visualmente el formulario de nuevo chequeo. Stateless.
 *
 * @param centroOperacion Texto actual del centro.
 * @param onCentroOperacionChange Callback invocado cuando el usuario teclea en el centro.
 * @param nombreBuzo Texto actual del buzo.
 * @param onNombreBuzoChange Callback invocado cuando el usuario teclea el buzo.
 * @param nombreSupervisor Texto actual del supervisor.
 * @param onNombreSupervisorChange Callback invocado cuando el usuario teclea el supervisor.
 * @param onSubmit Acción a ejecutar al presionar el botón final.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCheckContent(
    centroOperacion: String,
    onCentroOperacionChange: (String) -> Unit,
    nombreBuzo: String,
    onNombreBuzoChange: (String) -> Unit,
    nombreSupervisor: String,
    onNombreSupervisorChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nuevo Chequeo") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = centroOperacion,
                onValueChange = onCentroOperacionChange,
                label = { Text("Centro de Operación") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombreBuzo,
                onValueChange = onNombreBuzoChange,
                label = { Text("Nombre del Buzo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombreSupervisor,
                onValueChange = onNombreSupervisorChange,
                label = { Text("Nombre del Supervisor") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                // Lógica de validación visual: El botón se deshabilita si algún campo está vacío
                enabled = centroOperacion.isNotBlank() && nombreBuzo.isNotBlank() && nombreSupervisor.isNotBlank()
            ) {
                Text("Comenzar Checklist")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewCheckContentPreview() {
    AquaCheckTheme {
        NewCheckContent(
            centroOperacion = "Centro Sur",
            onCentroOperacionChange = {},
            nombreBuzo = "Pedro Ramirez",
            onNombreBuzoChange = {},
            nombreSupervisor = "Carlos Soto",
            onNombreSupervisorChange = {},
            onSubmit = {}
        )
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * remember { mutableStateOf() }: Fundamental en Compose. Le dice al framework: "recuerda este 
 * valor, y si cambia, vuelve a dibujar automáticamente (recomposición) todas las partes de la UI 
 * que dependan de él".
 * OutlinedTextField: Componente oficial de Material 3 para entradas de texto.
 * State Hoisting (Elevación de Estado): El patrón usado al dividir esta pantalla en 'Screen' 
 * (que tiene el estado) y 'Content' (que recibe el estado como parámetro). Es la piedra angular 
 * de una buena arquitectura en Compose.
 * ==============================================================================
 */
