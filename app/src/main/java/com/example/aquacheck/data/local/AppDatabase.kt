package com.example.aquacheck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aquacheck.data.model.ChecklistItem
import com.example.aquacheck.data.model.ChecklistSession

/**
 * Propósito de la clase: Punto principal de acceso a la base de datos persistida subyacente (SQLite).
 * Inicializa Room y proporciona las instancias de los DAOs asociados.
 *
 * Por qué es necesaria: Room requiere una clase abstracta que extienda de `RoomDatabase`. 
 * Es el motor que instancia internamente las tablas y ejecuta las migraciones. Sin esto, 
 * no existe la base de datos física `aquacheck_database`.
 *
 * Alcance e Impacto: Es un Single Source of Truth a nivel de sistema de archivos. La inicialización
 * debe ser de tipo Singleton para evitar memory leaks o bloqueos si múltiples hilos 
 * intentan abrir conexiones SQLite concurrentemente. Usada directamente en [com.example.aquacheck.MainActivity].
 *
 * @see ChecklistDao
 */
@Database(entities = [ChecklistSession::class, ChecklistItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Expone el DAO para el manejo de los chequeos. Room inyectará la implementación concreta.
     * @return Una instancia de [ChecklistDao] lista para usar.
     */
    abstract fun checklistDao(): ChecklistDao

    companion object {
        /**
         * @Volatile asegura que el valor de INSTANCE sea visible inmediatamente para todos los hilos,
         * previniendo condiciones de carrera al inicializar el Singleton.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia única (Singleton) de la base de datos.
         * Utiliza un bloque `synchronized` para garantizar thread-safety durante el primer llamado.
         * 
         * @param context Contexto de la aplicación, usado para buscar la ruta del archivo SQLite.
         * @return [AppDatabase] instanciada y lista para consultas.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aquacheck_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * @Database: Anotación de Room donde listamos todas las `entities` (tablas) que contendrá, y su
 * versión. Si cambiamos las columnas en el futuro, debemos subir la 'version' y crear una 'Migration'.
 * Singleton (patrón): Asegura que solo se cree una única instancia de la clase durante tod-o
 * el ciclo de vida de la App, algo crítico y mandatorio en bases de datos locales.
 * ==============================================================================
 */
