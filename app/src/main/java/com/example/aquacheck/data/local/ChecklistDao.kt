package com.example.aquacheck.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession
import kotlinx.coroutines.flow.Flow

/**
 * Propósito de la clase: Define el Data Access Object (DAO) para las operaciones CRUD sobre
 * las sesiones y los ítems del checklist en la base de datos local (SQLite/Room).
 *
 * Por qué es necesaria: Room requiere una interfaz donde escribimos las sentencias SQL y
 * las asociamos a funciones de Kotlin. Sirve como un puente seguro (en tiempo de compilación)
 * entre el repositorio y la base de datos subyacente. Aisla al Repository de la sintaxis SQL.
 *
 * Alcance e Impacto: Es consumida únicamente por [com.example.aquacheck.data.repository.ChecklistRepository].
 * Alterar consultas aquí impactará directamente qué datos se muestran en la pantalla o cómo
 * se guardan.
 *
 * @see com.example.aquacheck.data.model.ChecklistSession
 * @see com.example.aquacheck.data.model.ChecklistItem
 */
@Dao
interface ChecklistDao {

    /**
     * Obtiene el historial completo de chequeos ordenado por fecha descendente.
     * Al devolver un [Flow], Room emitirá automáticamente una nueva lista cada vez
     * que la tabla 'checklist_sessions' sea modificada.
     * 
     * @return [Flow] que emite la lista actualizada de sesiones de buceo.
     */
    @Query("SELECT * FROM checklist_sessions ORDER BY fechaHora DESC")
    fun getAllSessions(): Flow<List<ChecklistSession>>

    /**
     * Busca una sesión específica a través de su identificador.
     * Es una función `suspend`, por lo que Room asegura de manera nativa
     * que se ejecute en un hilo secundario de I/O, sin bloquear la UI.
     * 
     * @param sessionId El ID (UUID) que identifica el chequeo de buceo buscado.
     * @return La sesión encontrada o nulo si no existe en la base de datos.
     */
    @Query("SELECT * FROM checklist_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): ChecklistSession?

    /**
     * Inserta una nueva sesión principal. Si ya existe una con el mismo ID, la reemplaza.
     * Función `suspend` para evitar bloqueos del Main Thread.
     * 
     * @param session La sesión de chequeo de buceo a guardar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChecklistSession)

    /**
     * Actualiza los datos de una sesión existente (por ejemplo, para cambiar el resultadoFinal
     * o el syncStatus tras sincronizar con OneDrive).
     * 
     * @param session La sesión con los datos ya modificados.
     */
    @Update
    suspend fun updateSession(session: ChecklistSession)

    /**
     * Devuelve todos los ítems de seguridad asociados a un chequeo específico.
     * Utiliza un [Flow] para que la interfaz reaccione en tiempo real si un ítem
     * cambia de estado (ej: de Pendiente a CUMPLE).
     * 
     * @param sessionId El identificador foráneo del chequeo al que pertenecen los ítems.
     * @return [Flow] que emite la lista de ítems de seguridad del chequeo.
     */
    @Query("SELECT * FROM checklist_items WHERE sessionId = :sessionId")
    fun getItemsForSession(sessionId: String): Flow<List<ChecklistItem>>

    /**
     * Inserta la lista inicial de ítems a evaluar para un chequeo recién creado.
     * 
     * @param items Lista de objetos [ChecklistItem] inicializados (máscaras, compresores, etc).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItem>)

    /**
     * Actualiza un ítem específico (por ejemplo, cuando el supervisor pulsa "No Cumple"
     * y añade una observación).
     * 
     * @param item El ítem de revisión con el estado validado u observaciones incluidas.
     */
    @Update
    suspend fun updateItem(item: ChecklistItem)
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * @Dao: Marca la interfaz para que Room genere automáticamente la implementación 
 * en tiempo de compilación.
 * @Query: Permite escribir sentencias SQL puras con verificación en tiempo de compilación.
 * Flow: Tipo de Coroutines que representa un flujo de datos asíncrono y reactivo (patrón Observer). 
 * Room soporta Flow nativamente.
 * suspend: Indica que la función puede ser pausada y reanudada, obligando a llamarla desde 
 * una Coroutine, ideal para operaciones I/O pesadas como escritura en disco.
 * ==============================================================================
 */
