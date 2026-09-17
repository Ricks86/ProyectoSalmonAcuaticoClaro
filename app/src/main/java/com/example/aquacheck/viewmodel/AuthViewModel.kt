package com.example.aquacheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.repository.AquaCheckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsable del estado de autenticación de la sesión activa.
 *
 * En AquaCheck no existe un registro de usuario ni contraseñas: el alta la gestiona RRHH.
 * La "autenticación" consiste en seleccionar un usuario de la lista y declararlo activo
 * para toda la sesión. El usuario logueado persiste en memoria mientras la app está abierta.
 *
 * Es creado en el nivel del grafo de navegación ([AppNavigation]) para que tanto
 * [LoginScreen] como [DashboardScreen] compartan la misma instancia y el mismo estado.
 *
 * @param repository La fuente de datos. Se usa para cargar los usuarios disponibles
 *                   y para disparar la población de datos simulados al primer inicio.
 */
class AuthViewModel(private val repository: AquaCheckRepository) : ViewModel() {

    /**
     * Lista reactiva de todos los usuarios del sistema. Alimenta la pantalla de login
     * con los selectores de usuario. Se mantiene activa mientras haya al menos un
     * suscriptor (WhileSubscribed).
     */
    val users: StateFlow<List<User>> = repository.userDao.getAll()
        .stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = emptyList()
        )

    /**
     * Usuario actualmente en sesión. `null` indica que no hay sesión iniciada
     * y la app debe mostrar la pantalla de Login.
     */
    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    init {
        // Asegura que los datos de prueba estén disponibles antes de que el usuario
        // interactúe con la pantalla de Login.
        viewModelScope.launch {
            repository.populateInitialData()
        }
    }

    /**
     * Establece el usuario seleccionado como la sesión activa.
     * El cambio en [loggedInUser] dispara la navegación hacia el Dashboard en [AppNavigation].
     *
     * @param user El usuario elegido en la pantalla de Login.
     */
    fun login(user: User) {
        _loggedInUser.value = user
    }

    /**
     * Cierra la sesión activa limpiando el estado.
     * [AppNavigation] detecta el cambio y redirige automáticamente al Login.
     */
    fun logout() {
        _loggedInUser.value = null
    }

    // ─── Factory ─────────────────────────────────────────────────────────────

    /**
     * Fábrica necesaria para inyectar [AquaCheckRepository] en el ViewModel,
     * ya que [ViewModel] no acepta parámetros en su constructor por defecto.
     */
    class Factory(private val repository: AquaCheckRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
