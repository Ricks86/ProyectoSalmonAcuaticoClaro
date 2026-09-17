package com.example.aquacheck.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquacheck.model.local.entities.ImmersionPlan
import com.example.aquacheck.model.local.entities.ImmersionStatus
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.local.entities.UserRole
import com.example.aquacheck.model.repository.AquaCheckRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.AquaDanger
import com.example.aquacheck.ui.theme.AquaNavy
import com.example.aquacheck.ui.theme.AquaOrange
import com.example.aquacheck.ui.theme.AquaSuccess
import com.example.aquacheck.ui.theme.AquaSurface
import com.example.aquacheck.ui.theme.AquaSurfaceVariant
import com.example.aquacheck.ui.theme.AquaWarning
import com.example.aquacheck.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

// ─── Modelos de navegación ────────────────────────────────────────────────────

/**
 * Ítem de la barra de navegación inferior (BottomNav).
 *
 * @property label Etiqueta visible bajo el ícono.
 * @property icon Ícono representativo de la sección.
 */
data class BottomNavItem(val label: String, val icon: ImageVector)

/**
 * Ítem del menú lateral (NavigationDrawer).
 *
 * @property label Nombre de la sección secundaria de la app.
 * @property icon Ícono del menú lateral.
 */
data class DrawerItem(val label: String, val icon: ImageVector)

// ─── Pantalla Principal (Stateful) ────────────────────────────────────────────

/**
 * Pantalla del Dashboard principal de AquaCheck.
 *
 * Actúa como contenedor Stateful: obtiene los datos del [DashboardViewModel],
 * resuelve el estado de sesión desde [AuthViewModel] y delega el dibujo
 * al componente Stateless [DashboardContent].
 *
 * @param repository Repositorio de datos para construir el ViewModel.
 * @param loggedInUser El usuario cuya sesión está activa. Pasado como parámetro
 *        porque ya fue resuelto por [AuthViewModel] antes de navegar aquí.
 * @param onLogout Callback que dispara el cierre de sesión y la navegación al Login.
 */
@Composable
fun DashboardScreen(
    repository    : AquaCheckRepository,
    loggedInUser  : User,
    onLogout      : () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(repository, loggedInUser)
    )

    val activePlans  by viewModel.activePlans.collectAsState()
    val plannedPlans by viewModel.plannedPlans.collectAsState()
    val finishedPlans by viewModel.finishedPlans.collectAsState()

    DashboardContent(
        user          = viewModel.loggedInUser,
        activePlans   = activePlans,
        plannedPlans  = plannedPlans,
        finishedPlans = finishedPlans,
        onLogout      = onLogout
    )
}

// ─── Contenido visual (Stateless) ────────────────────────────────────────────

/**
 * Componente visual puro del Dashboard. Stateless: solo recibe datos y callbacks.
 *
 * Estructura Material Design 3:
 * - [ModalNavigationDrawer]: menú lateral deslizante para secciones secundarias.
 * - [TopAppBar]: título de la app y botón para abrir el drawer.
 * - [NavigationBar]: barra inferior con accesos rápidos a los módulos principales.
 * - Contenido: saludo personalizado y grid de tarjetas de resumen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    user          : User,
    activePlans   : List<ImmersionPlan>,
    plannedPlans  : List<ImmersionPlan>,
    finishedPlans : List<ImmersionPlan>,
    onLogout      : () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope       = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("Inicio",     Icons.Default.Home),
        BottomNavItem("Checklist",  Icons.Default.Check),
        BottomNavItem("Historial",  Icons.Default.List),
        BottomNavItem("Perfil",     Icons.Default.Person)
    )

    val drawerItems = listOf(
        DrawerItem("Planificador", Icons.Default.Edit),
        DrawerItem("Informes",     Icons.Default.List),
        DrawerItem("Configuración",Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            DashboardDrawer(
                user        = user,
                drawerItems = drawerItems,
                onLogout    = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text       = "AquaCheck",
                            fontWeight = FontWeight.ExtraBold,
                            color      = AquaOrange
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector        = Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint               = AquaOrange
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AquaNavy
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = AquaSurface) {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick  = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector        = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label  = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = AquaOrange,
                                selectedTextColor   = AquaOrange,
                                indicatorColor      = AquaSurfaceVariant,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (selectedTab) {
                0 -> HomeTab(
                    user          = user,
                    activePlans   = activePlans,
                    plannedPlans  = plannedPlans,
                    finishedPlans = finishedPlans,
                    modifier      = Modifier.padding(innerPadding)
                )
                else -> ComingSoonTab(
                    label    = bottomNavItems[selectedTab].label,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ─── Menú Lateral ────────────────────────────────────────────────────────────

/**
 * Contenido del NavigationDrawer lateral.
 * Muestra el nombre y rol del usuario activo, los accesos a secciones secundarias
 * y el botón para cerrar sesión.
 */
@Composable
fun DashboardDrawer(
    user        : User,
    drawerItems : List<DrawerItem>,
    onLogout    : () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = AquaSurface) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = "AquaCheck",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = AquaOrange
            )
            Text(
                text  = "AquaChile",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = AquaNavy)

        // Información del usuario activo
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Default.Person,
                contentDescription = null,
                tint               = AquaOrange,
                modifier           = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text       = user.name,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = user.role.displayName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(color = AquaNavy)
        Spacer(modifier = Modifier.height(8.dp))

        // Secciones del drawer
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon   = { Icon(item.icon, contentDescription = item.label, tint = AquaOrange) },
                label  = { Text(item.label) },
                selected = false,
                onClick  = { /* Próximos sprints */ },
                colors   = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = AquaSurface
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = AquaNavy)

        TextButton(
            onClick  = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Cerrar sesión", color = AquaDanger)
        }
    }
}

// ─── Tab: Inicio ─────────────────────────────────────────────────────────────

/**
 * Contenido de la pestaña "Inicio" del Dashboard.
 * Muestra el saludo personalizado y el grid de tarjetas de resumen por módulo.
 */
@Composable
fun HomeTab(
    user          : User,
    activePlans   : List<ImmersionPlan>,
    plannedPlans  : List<ImmersionPlan>,
    finishedPlans : List<ImmersionPlan>,
    modifier      : Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Saludo personalizado según rol
        Text(
            text       = "Bienvenido,",
            style      = MaterialTheme.typography.bodyLarge,
            color      = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text       = user.name,
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text  = user.role.displayName(),
            style = MaterialTheme.typography.bodySmall,
            color = AquaOrange
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text  = "Resumen de faenas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Grid de tarjetas de resumen
        val summaryCards = listOf(
            DashboardCardData(
                title      = "En Curso",
                count      = activePlans.size,
                subtitle   = "Faenas activas ahora",
                accentColor = AquaDanger
            ),
            DashboardCardData(
                title      = "Programadas",
                count      = plannedPlans.size,
                subtitle   = "Próximas faenas",
                accentColor = AquaWarning
            ),
            DashboardCardData(
                title      = "Completadas",
                count      = finishedPlans.size,
                subtitle   = "En el historial",
                accentColor = AquaSuccess
            ),
            DashboardCardData(
                title      = "Total",
                count      = activePlans.size + plannedPlans.size + finishedPlans.size,
                subtitle   = "Todos los planes",
                accentColor = AquaOrange
            )
        )

        LazyVerticalGrid(
            columns             = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(bottom = 16.dp)
        ) {
            items(summaryCards) { card ->
                SummaryCard(data = card)
            }
        }
    }
}

/**
 * Datos necesarios para renderizar una tarjeta de resumen en el grid del Dashboard.
 *
 * @property title Título de la tarjeta (ej. "En Curso").
 * @property count Número a destacar visualmente (cantidad de planes en ese estado).
 * @property subtitle Texto secundario que contextualiza el contador.
 * @property accentColor Color del borde superior y del contador, según el estado semántico.
 */
data class DashboardCardData(
    val title      : String,
    val count      : Int,
    val subtitle   : String,
    val accentColor: androidx.compose.ui.graphics.Color
)

/**
 * Tarjeta individual del grid de resumen del Dashboard.
 * Muestra un contador prominente y una descripción del estado del plan.
 */
@Composable
fun SummaryCard(data: DashboardCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = AquaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = data.count.toString(),
                style      = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color      = data.accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = data.title,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text  = data.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Tab Placeholder ─────────────────────────────────────────────────────────

/**
 * Pantalla placeholder para las pestañas que aún no han sido implementadas.
 * Evita que la app crashee al navegar a secciones en construcción.
 *
 * @param label Nombre de la sección, mostrado en el mensaje informativo.
 */
@Composable
fun ComingSoonTab(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier        = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = label,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = "Disponible en el próximo sprint",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun DashboardContentPreview() {
    AquaCheckTheme {
        DashboardContent(
            user          = User(1, "María González", UserRole.SUPERVISOR),
            activePlans   = listOf(),
            plannedPlans  = listOf(),
            finishedPlans = listOf(),
            onLogout      = {}
        )
    }
}
