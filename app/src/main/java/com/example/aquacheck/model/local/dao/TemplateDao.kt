package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aquacheck.model.local.entities.Template
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [Template].
 *
 * Permite al Prevencionista crear, editar y consultar las plantillas de checklist
 * disponibles para ser asignadas a un [ImmersionPlan].
 */
@Dao
interface TemplateDao {

    /**
     * Inserta una plantilla y retorna el `id` autogenerado por Room.
     * El `id` retornado es necesario para luego insertar los [TemplateItem] asociados.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: Template): Long

    /** Inserta una lista de plantillas. Útil para la carga inicial de datos simulados. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<Template>)

    /** Actualiza una plantilla existente (ej. el Prevencionista modifica el título o tipo de faena). */
    @Update
    suspend fun update(template: Template)

    /** Retorna todas las plantillas ordenadas por nombre. El [Flow] reacciona a cualquier cambio. */
    @Query("SELECT * FROM templates ORDER BY title ASC")
    fun getAll(): Flow<List<Template>>

    /** Busca una plantilla por su [Template.id]. Retorna `null` si no existe. */
    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getById(id: Int): Template?

    /** Retorna el total de plantillas. Usado para la carga condicional de datos simulados. */
    @Query("SELECT COUNT(*) FROM templates")
    suspend fun count(): Int
}
