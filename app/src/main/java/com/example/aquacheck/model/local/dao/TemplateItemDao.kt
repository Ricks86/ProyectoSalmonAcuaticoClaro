package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquacheck.model.local.entities.TemplateItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [TemplateItem].
 *
 * Permite gestionar las preguntas individuales que componen una [Template].
 * Normalmente se usa junto con [TemplateDao]: primero se crea la plantilla,
 * se obtiene su id y luego se insertan sus ítems con ese id como FK.
 */
@Dao
interface TemplateItemDao {

    /** Inserta un ítem de pregunta asociado a una plantilla. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TemplateItem)

    /** Inserta toda la lista de preguntas de una plantilla de una sola vez. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TemplateItem>)

    /**
     * Retorna todos los ítems de una plantilla específica ordenados por su id de inserción.
     * Este [Flow] es el que alimenta la pantalla de ejecución del checklist en la UI.
     */
    @Query("SELECT * FROM template_items WHERE templateId = :templateId ORDER BY id ASC")
    fun getByTemplateId(templateId: Int): Flow<List<TemplateItem>>

    /** Busca un ítem por su id. Útil para actualizar o eliminar una pregunta específica. */
    @Query("SELECT * FROM template_items WHERE id = :id")
    suspend fun getById(id: Int): TemplateItem?

    /**
     * Elimina todos los ítems de una plantilla. Se usa cuando el Prevencionista
     * decide reemplazar completamente las preguntas de una plantilla existente.
     */
    @Query("DELETE FROM template_items WHERE templateId = :templateId")
    suspend fun deleteByTemplateId(templateId: Int)
}
