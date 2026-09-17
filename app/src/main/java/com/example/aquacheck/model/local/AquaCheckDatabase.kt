package com.example.aquacheck.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.aquacheck.model.local.dao.ChecklistAnswerDao
import com.example.aquacheck.model.local.dao.ChecklistRecordDao
import com.example.aquacheck.model.local.dao.ImmersionPlanDao
import com.example.aquacheck.model.local.dao.TemplateDao
import com.example.aquacheck.model.local.dao.TemplateItemDao
import com.example.aquacheck.model.local.dao.UserDao
import com.example.aquacheck.model.local.entities.AnswerStatus
import com.example.aquacheck.model.local.entities.ChecklistAnswer
import com.example.aquacheck.model.local.entities.ChecklistRecord
import com.example.aquacheck.model.local.entities.ChecklistType
import com.example.aquacheck.model.local.entities.ImmersionLevel
import com.example.aquacheck.model.local.entities.ImmersionPlan
import com.example.aquacheck.model.local.entities.ImmersionStatus
import com.example.aquacheck.model.local.entities.Template
import com.example.aquacheck.model.local.entities.TemplateItem
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.local.entities.UserRole

/**
 * Conversores de tipos para Room.
 *
 * SQLite solo puede almacenar tipos primitivos (Int, Long, String, etc.).
 * Room no sabe cómo guardar un `enum class` de Kotlin directamente, por lo que
 * necesita estas funciones de conversión: una para escribir (enum → String)
 * y otra para leer (String → enum).
 *
 * Esta clase se registra en [AquaCheckDatabase] con la anotación `@TypeConverters`
 * para que Room la aplique automáticamente en todas las entidades de la BD.
 */
class AquaCheckConverters {

    @TypeConverter fun fromUserRole(value: UserRole): String = value.name
    @TypeConverter fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter fun fromImmersionLevel(value: ImmersionLevel): String = value.name
    @TypeConverter fun toImmersionLevel(value: String): ImmersionLevel = ImmersionLevel.valueOf(value)

    @TypeConverter fun fromImmersionStatus(value: ImmersionStatus): String = value.name
    @TypeConverter fun toImmersionStatus(value: String): ImmersionStatus = ImmersionStatus.valueOf(value)

    @TypeConverter fun fromChecklistType(value: ChecklistType): String = value.name
    @TypeConverter fun toChecklistType(value: String): ChecklistType = ChecklistType.valueOf(value)

    @TypeConverter fun fromAnswerStatus(value: AnswerStatus): String = value.name
    @TypeConverter fun toAnswerStatus(value: String): AnswerStatus = AnswerStatus.valueOf(value)
}

/**
 * Base de datos local de AquaCheck construida con Room sobre SQLite.
 *
 * Es la clase central de la capa de datos. Room la implementa en tiempo de compilación
 * (via KSP) generando el código SQL necesario a partir de las anotaciones en las entidades.
 *
 * Se implementa como Singleton para garantizar que toda la app comparta una única
 * conexión a la base de datos, evitando condiciones de carrera y cierres inesperados.
 *
 * Cuando se necesite agregar una nueva entidad o modificar un campo existente, se debe
 * incrementar el número de `version` y proveer una estrategia de migración.
 */
@Database(
    entities = [
        User::class,
        Template::class,
        TemplateItem::class,
        ImmersionPlan::class,
        ChecklistRecord::class,
        ChecklistAnswer::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AquaCheckConverters::class)
abstract class AquaCheckDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateItemDao(): TemplateItemDao
    abstract fun immersionPlanDao(): ImmersionPlanDao
    abstract fun checklistRecordDao(): ChecklistRecordDao
    abstract fun checklistAnswerDao(): ChecklistAnswerDao

    companion object {
        @Volatile
        private var INSTANCE: AquaCheckDatabase? = null

        /**
         * Retorna la instancia única de la base de datos.
         *
         * El bloque `synchronized` garantiza que aunque dos hilos llamen a esta función
         * al mismo tiempo, solo uno creará la instancia. `@Volatile` asegura que el valor
         * de `INSTANCE` sea visible inmediatamente para todos los hilos tras ser asignado.
         *
         * @param context Se recomienda pasar `applicationContext` para evitar memory leaks.
         */
        fun getInstance(context: Context): AquaCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AquaCheckDatabase::class.java,
                    "aquacheck.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
