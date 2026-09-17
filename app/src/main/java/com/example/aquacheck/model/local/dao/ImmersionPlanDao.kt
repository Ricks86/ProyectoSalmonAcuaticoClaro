package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aquacheck.model.local.entities.ImmersionPlan
import com.example.aquacheck.model.local.entities.ImmersionStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [ImmersionPlan].
 *
 * Es uno de los DAOs más completos del sistema. Los planes de inmersión son el eje
 * central de la app: cada pantalla de historial, chequeo activo y reporte los consume.
 */
@Dao
interface ImmersionPlanDao {

    /**
     * Crea un nuevo plan de inmersión y retorna su `id` autogenerado.
     * El `id` es necesario para crear el [ChecklistRecord] asociado al plan.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: ImmersionPlan): Long

    /** Inserta múltiples planes. Útil para la carga inicial de datos simulados. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<ImmersionPlan>)

    /** Actualiza todos los campos de un plan existente (ej. cambio de equipo asignado o duración). */
    @Update
    suspend fun update(plan: ImmersionPlan)

    /** Retorna todos los planes ordenados por fecha descendente (el más reciente primero). */
    @Query("SELECT * FROM immersion_plans ORDER BY date DESC")
    fun getAll(): Flow<List<ImmersionPlan>>

    /** Busca un plan por su id. Retorna `null` si no existe. */
    @Query("SELECT * FROM immersion_plans WHERE id = :id")
    suspend fun getById(id: Int): ImmersionPlan?

    /**
     * Filtra los planes por su [ImmersionStatus]. Permite a la UI mostrar solo los planes
     * activos (EN_CURSO), los pendientes (PLANIFICADA) o el historial (FINALIZADA).
     */
    @Query("SELECT * FROM immersion_plans WHERE status = :status ORDER BY date DESC")
    fun getByStatus(status: ImmersionStatus): Flow<List<ImmersionPlan>>

    /**
     * Actualiza únicamente el [ImmersionStatus] de un plan sin reescribir toda la fila.
     * Es la operación que ocurre cuando el Supervisor inicia o finaliza una faena.
     */
    @Query("UPDATE immersion_plans SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: ImmersionStatus)

    /** Retorna el total de planes. Usado para la carga condicional de datos simulados. */
    @Query("SELECT COUNT(*) FROM immersion_plans")
    suspend fun count(): Int
}
