package com.example.aquacheck.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquacheck.data.model.ChecklistSession
import com.example.aquacheck.data.repository.ChecklistRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.viewmodel.DashboardViewModel
import com.example.aquacheck.ui.viewmodel.DashboardViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Propósito de la clase: Punto de entrada (UI) que muestra el historial de los chequeos de buceo.
 * Es el componente "Stateful" (con estado) que se conecta con la arquitectura.
 *
 * Por qué es necesaria: Integra la capa de presentación (UI) con la de negocio (ViewModel). 
 * Se encarga de instanciar el ViewModel, recolectar sus flujos de datos y pasarlos hacia 
 * abajo a los componentes "Stateless" (sin estado) para que sean dibujados.
 *
 * Alcance e Impacto: Es la primera pantalla visible. Conecta el botón flotante para crear
 * nuevas sesiones y las tarjetas de la lista para ver el detalle.
 *
 * @see DashboardContent
 * @see DashboardViewModel
 */
@Composable
fun DashboardScreen(
    repository: ChecklistRepository,
    onNavigateToNewCheck: () -> Unit,
    onNavigateToSession: (String) -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(repository))
    val sessions by viewModel.sessions.collectAsState()

    DashboardContent(
        sessions = sessions,
        onNavigateToNewCheck = onNavigateToNewCheck,
        onNavigateToSession = onNavigateToSession
    )
}

/**
 * Propósito de la función: Componente puramente visual (Stateless) del Dashboard.
 * 
 * Por qué es necesaria: Al no depender de ViewModels ni Repositorios, es extremadamente fácil 
 * de testear de forma aislada y permite utilizar las funciones `@Preview` de Android Studio.
 *
 * @param sessions Lista de chequeos a mostrar.
 * @param onNavigateToNewCheck Callback para el botón "+".
 * @param onNavigateToSession Callback para tocar una tarjeta del historial.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    sessions: List<ChecklistSession>,
    onNavigateToNewCheck: () -> Unit,
    onNavigateToSession: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AquaCheck Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewCheck) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Chequeo")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions) { session ->
                SessionItem(session = session, onClick = { onNavigateToSession(session.sessionId) })
            }
        }
    }
}

/**
 * Propósito de la función: Representa gráficamente un solo elemento (fila) dentro del historial de chequeos.
 */
@Composable
fun SessionItem(session: ChecklistSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(session.fechaHora))
            Text(text = "Centro: ${session.centroOperacion}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Fecha: $dateString", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Buzo: ${session.nombreBuzo}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Resultado: ${session.resultadoFinal}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardContentPreview() {
    AquaCheckTheme {
        DashboardContent(
            sessions = listOf(
                ChecklistSession("1", "Centro Sur", System.currentTimeMillis(), "Juan Perez", "Pedro Gomez", "Apto"),
                ChecklistSession("2", "Centro Norte", System.currentTimeMillis() - 86400000, "Luis Rojas", "Carlos Soto", "Requiere Revisión")
            ),
            onNavigateToNewCheck = {},
            onNavigateToSession = {}
        )
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * @Composable: Anotación obligatoria en Jetpack Compose que indica que esta función 
 * emitirá nodos en el árbol de UI (es decir, dibuja elementos gráficos).
 * Scaffold: Estructura base de Material Design (provee barra superior, botón flotante, etc).
 * LazyColumn: Equivalente a un RecyclerView. Solo dibuja los elementos que caben en 
 * la pantalla actual, reciclándolos mientras haces scroll para ahorrar memoria.
 * @Preview: Permite ver el diseño resultante directamente en el IDE sin correr la app.
 * ==============================================================================
 */
