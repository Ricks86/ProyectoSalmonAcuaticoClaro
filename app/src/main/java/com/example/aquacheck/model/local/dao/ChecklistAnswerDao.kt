package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aquacheck.model.local.entities.ChecklistAnswer
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [ChecklistAnswer].
 *
 * Es el DAO de escritura más frecuente durante la ejecución de un chequeo:
 * cada vez que el Supervisor responde una pregunta en la UI, se llama a [update]
 * para persistir el veredicto ([ChecklistAnswer.status]) y la evidencia fotográfica simulada.
 */
@Dao
interface ChecklistAnswerDao {

    /** Inserta una respuesta individual para un ítem del checklist. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: ChecklistAnswer)

    /**
     * Inserta todas las respuestas de un chequeo de una vez.
     * Se usa al iniciar un [ChecklistRecord]: se crean las respuestas en blanco
     * (una por cada [TemplateItem]) para que el Supervisor las vaya completando.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(answers: List<ChecklistAnswer>)

    /** Actualiza el estado y/o la evidencia de una respuesta ya existente. */
    @Update
    suspend fun update(answer: ChecklistAnswer)

    /**
     * Retorna todas las respuestas de un registro de chequeo específico.
     * Este [Flow] alimenta la lista de ítems en la pantalla de ejecución del checklist.
     */
    @Query("SELECT * FROM checklist_answers WHERE recordId = :recordId ORDER BY id ASC")
    fun getByRecordId(recordId: Int): Flow<List<ChecklistAnswer>>

    /** Busca una respuesta por su id. Útil para obtener el estado de un ítem individual. */
    @Query("SELECT * FROM checklist_answers WHERE id = :id")
    suspend fun getById(id: Int): ChecklistAnswer?
}
