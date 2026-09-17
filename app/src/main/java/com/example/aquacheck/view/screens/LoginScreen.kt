package com.example.aquacheck.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.local.entities.UserRole
import com.example.aquacheck.model.repository.AquaCheckRepository
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.AquaNavy
import com.example.aquacheck.ui.theme.AquaOrange
import com.example.aquacheck.ui.theme.AquaSurface
import com.example.aquacheck.viewmodel.AuthViewModel

/**
 * Pantalla de inicio de sesión de AquaCheck.
 *
 * No existe formulario de usuario/contraseña. El sistema presenta la lista de usuarios
 * registrados por RRHH y permite elegir quién operará la app en esta sesión.
 * Es el punto de entrada a la aplicación.
 *
 * @param authViewModel Compartido con [DashboardScreen] para persistir la sesión activa.
 * @param onLoginSuccess Callback invocado tras seleccionar un usuario. Dispara la
 *        navegación al Dashboard en [AppNavigation].
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val users by authViewModel.users.collectAsState()

    LoginContent(
        users        = users,
        onUserSelect = { user ->
            authViewModel.login(user)
            onLoginSuccess()
        }
    )
}

/**
 * Componente visual puro (Stateless) de la pantalla de Login.
 * Recibe la lista de usuarios y un callback; no conoce nada de ViewModels.
 *
 * @param users Lista de usuarios a mostrar como opciones de sesión.
 * @param onUserSelect Callback ejecutado al presionar un botón de usuario.
 */
@Composable
fun LoginContent(
    users: List<User>,
    onUserSelect: (User) -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // ── Logo Placeholder ──────────────────────────────────────────────────
        LogoPlaceholder()

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text      = "Selecciona tu perfil",
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = AquaNavy)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Lista de usuarios ─────────────────────────────────────────────────
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(bottom = 32.dp)
        ) {
            items(users) { user ->
                UserLoginCard(user = user, onClick = { onUserSelect(user) })
            }
        }
    }
}

/**
 * Tarjeta de selección para un usuario individual.
 * Muestra nombre, rol y un botón de acción "Iniciar sesión".
 */
@Composable
fun UserLoginCard(user: User, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border   = BorderStroke(1.dp, AquaNavy),
        colors   = CardDefaults.outlinedCardColors(containerColor = AquaSurface)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Person,
                    contentDescription = null,
                    tint               = AquaOrange,
                    modifier           = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text     = user.name,
                        style    = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text  = user.role.displayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick  = onClick,
                colors   = ButtonDefaults.buttonColors(containerColor = AquaOrange),
                shape    = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text  = "Entrar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Placeholder del logo de AquaCheck. Será reemplazado por un [Image] con el logo
 * oficial de la marca en una iteración posterior.
 */
@Composable
fun LogoPlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape  = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AquaNavy),
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.AccountCircle,
                contentDescription = "Logo AquaCheck",
                tint               = AquaOrange,
                modifier           = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text      = "AquaCheck",
            style     = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color     = AquaOrange
        )
        Text(
            text      = "Seguridad en faenas de buceo",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Convierte el [UserRole] en una etiqueta legible para mostrar en la UI.
 * Centraliza la lógica de presentación del rol para no repetirla en cada Composable.
 */
fun UserRole.displayName(): String = when (this) {
    UserRole.BUZO                -> "Buzo Profesional"
    UserRole.BUZO_DE_EMERGENCIA  -> "Buzo de Emergencia"
    UserRole.SUPERVISOR          -> "Supervisor de Buceo"
    UserRole.JEFE_DE_CENTRO      -> "Jefe de Centro"
    UserRole.PREVENCIONISTA      -> "Prevencionista de Riesgos"
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    AquaCheckTheme {
        LoginContent(
            users = listOf(
                User(1, "Carlos Mendoza", UserRole.BUZO),
                User(2, "María González", UserRole.SUPERVISOR),
                User(3, "Ana Castillo",   UserRole.PREVENCIONISTA)
            ),
            onUserSelect = {}
        )
    }
}
