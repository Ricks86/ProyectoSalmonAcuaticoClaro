package com.example.aquacheck.data.repository

import com.example.aquacheck.data.local.ChecklistDao
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession
import kotlinx.coroutines.flow.Flow

/**
 * Propósito de la clase: Actúa como el único punto de acceso (Single Source of Truth) a los datos.
 * Pertenece a la Capa de Acceso a Datos (Data Layer).
 *
 * Por qué es necesaria: En la arquitectura recomendada, los ViewModels no deben comunicarse 
 * directamente con Room, Retrofit o WorkManager. El Repository abstrae de dónde provienen los 
 * datos (red, base local, memoria), permitiendo que la UI sea agnóstica a la infraestructura.
 *
 * Alcance e Impacto: Inyectada en todos los ViewModels. Contiene la lógica de persistencia y en
 * el futuro coordinará la sincronización en segundo plano con OneDrive (WorkManager).
 *
 * @see com.example.aquacheck.data.local.ChecklistDao
 */
class ChecklistRepository(private val checklistDao: ChecklistDao) {

    /**
     * @property allSessions Flujo continuo con todas las sesiones almacenadas.
     * Consumido por el Dashboard para mostrar el historial de buceos.
     */
    val allSessions: Flow<List<ChecklistSession>> = checklistDao.getAllSessions()

    /**
     * Recupera asíncronamente una sesión específica.
     * @param sessionId El ID (UUID) que identifica el chequeo de buceo buscado.
     * @return La sesión encontrada o nulo.
     */
    suspend fun getSessionById(sessionId: String): ChecklistSession? {
        return checklistDao.getSessionById(sessionId)
    }

    /**
     * Guarda transaccionalmente un nuevo chequeo junto con sus ítems de validación.
     * Se ejecuta de manera asíncrona (suspend) para no afectar la responsividad de la UI.
     * 
     * @param session Entidad raíz del chequeo.
     * @param items Lista inicial de equipos/protocolos a revisar.
     */
    suspend fun insertSessionWithItems(session: ChecklistSession, items: List<ChecklistItem>) {
        checklistDao.insertSession(session)
        checklistDao.insertItems(items)
    }

    /**
     * Actualiza el estado de una sesión de buceo en la base de datos (Ej: Cuando cambia 
     * de "Pendiente" a "Apto").
     *
     * @param session La sesión modificada a sobreescribir.
     */
    suspend fun updateSession(session: ChecklistSession) {
        checklistDao.updateSession(session)
        // TOD-O: Integrar aquí WorkManager para la exportación y sincronización hacia OneDrive cuando syncStatus = false o se marque como finalizado
    }

    /**
     * Obtiene el flujo reactivo de ítems de seguridad vinculados a un chequeo.
     * @param sessionId Identificador de la sesión padre.
     * @return [Flow] que emite la lista de ítems de seguridad a evaluar.
     */
    fun getItemsForSession(sessionId: String): Flow<List<ChecklistItem>> {
        return checklistDao.getItemsForSession(sessionId)
    }

    /**
     * Persiste los cambios de un ítem (ej: el usuario acaba de marcarlo como NO_CUMPLE).
     *
     * @param item El ítem actualizado con la validación del supervisor.
     */
    suspend fun updateItem(item: ChecklistItem) {
        checklistDao.updateItem(item)
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * Repository Pattern (Patrón Repositorio): Una clase que oculta los detalles técnicos de 
 * cómo se guardan o consiguen los datos. En proyectos más avanzados, aquí decidiríamos si 
 * pedirle los datos a Retrofit (Internet) o a Room (Local).
 * ==============================================================================
 */
