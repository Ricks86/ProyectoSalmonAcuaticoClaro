package com.example.aquacheck.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.local.entities.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones sobre la tabla de [User].
 *
 * Permite consultar la lista de personal disponible para asignar a un [ImmersionPlan],
 * filtrar por rol (ej. obtener solo los buzos para el campo [ChecklistRecord.diverId])
 * y poblar la base de datos con usuarios simulados en el MVP.
 */
@Dao
interface UserDao {

    /** Inserta un usuario. Si ya existe un registro con el mismo id, lo reemplaza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    /** Inserta una lista de usuarios de una sola vez. Útil para la carga inicial de datos simulados. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    /** Retorna todos los usuarios ordenados alfabéticamente. El [Flow] se actualiza ante cualquier cambio en la tabla. */
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAll(): Flow<List<User>>

    /** Busca un usuario por su [User.id]. Retorna `null` si no existe. */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): User?

    /**
     * Filtra usuarios por [UserRole]. Útil para poblar selectores en la UI
     * (ej. mostrar solo BUZO al elegir quién descenderá, o solo SUPERVISOR al elegir quién valida).
     */
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun getByRole(role: UserRole): Flow<List<User>>

    /** Retorna el total de usuarios en la tabla. Se usa para decidir si se deben insertar datos de prueba. */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}
