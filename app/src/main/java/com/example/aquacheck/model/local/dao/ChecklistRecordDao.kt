package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquacheck.model.local.entities.ChecklistRecord
import com.example.aquacheck.model.local.entities.ChecklistType
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [ChecklistRecord].
 *
 * Un registro se crea cuando el Supervisor inicia formalmente un chequeo Pre o Post.
 * Desde aquí se obtiene el `id` del registro para luego persistir cada
 * [ChecklistAnswer] individual.
 */
@Dao
interface ChecklistRecordDao {

    /**
     * Inserta un nuevo registro de chequeo y retorna su `id` autogenerado.
     * Este `id` es el FK que deberán tener todas las [ChecklistAnswer] de este chequeo.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ChecklistRecord): Long

    /** Retorna todos los registros ordenados por fecha descendente (el más reciente primero). */
    @Query("SELECT * FROM checklist_records ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ChecklistRecord>>

    /** Busca un registro por su id. Retorna `null` si no existe. */
    @Query("SELECT * FROM checklist_records WHERE id = :id")
    suspend fun getById(id: Int): ChecklistRecord?

    /** Retorna todos los registros de un plan específico. Permite ver el historial de chequeos de una faena. */
    @Query("SELECT * FROM checklist_records WHERE immersionPlanId = :planId ORDER BY timestamp DESC")
    fun getByPlanId(planId: Int): Flow<List<ChecklistRecord>>

    /**
     * Busca si ya existe un chequeo de un tipo específico para un plan dado.
     * Se usa para evitar duplicar chequeos (un plan solo puede tener un PRE y un POST).
     * Retorna `null` si no se ha realizado ese tipo de chequeo aún.
     */
    @Query("SELECT * FROM checklist_records WHERE immersionPlanId = :planId AND type = :type LIMIT 1")
    suspend fun getByPlanIdAndType(planId: Int, type: ChecklistType): ChecklistRecord?
}
