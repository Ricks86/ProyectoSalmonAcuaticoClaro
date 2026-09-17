package com.example.aquacheck.model.repository

import com.example.aquacheck.model.local.AquaCheckDatabase
import com.example.aquacheck.model.local.dao.ChecklistAnswerDao
import com.example.aquacheck.model.local.dao.ChecklistRecordDao
import com.example.aquacheck.model.local.dao.ImmersionPlanDao
import com.example.aquacheck.model.local.dao.TemplateDao
import com.example.aquacheck.model.local.dao.TemplateItemDao
import com.example.aquacheck.model.local.dao.UserDao
import com.example.aquacheck.model.local.entities.ImmersionLevel
import com.example.aquacheck.model.local.entities.ImmersionPlan
import com.example.aquacheck.model.local.entities.ImmersionStatus
import com.example.aquacheck.model.local.entities.Template
import com.example.aquacheck.model.local.entities.TemplateItem
import com.example.aquacheck.model.local.entities.User
import com.example.aquacheck.model.local.entities.UserRole

/**
 * Repositorio central de AquaCheck. Es la única fuente de verdad (Single Source of Truth)
 * para toda la capa de datos de la aplicación.
 *
 * Abstrae la base de datos Room de los ViewModels: ningún ViewModel debe acceder
 * directamente a un DAO. Cualquier operación de lectura o escritura pasa por aquí.
 *
 * Expone los DAOs de forma pública para que los ViewModels puedan suscribirse a sus
 * [kotlinx.coroutines.flow.Flow] directamente (patrón Observer reactivo).
 *
 * @param db La instancia única de [AquaCheckDatabase] inyectada desde [MainActivity].
 */
class AquaCheckRepository(private val db: AquaCheckDatabase) {

    // ─── Exposición de DAOs ───────────────────────────────────────────────────
    val userDao: UserDao               = db.userDao()
    val templateDao: TemplateDao       = db.templateDao()
    val templateItemDao: TemplateItemDao = db.templateItemDao()
    val immersionPlanDao: ImmersionPlanDao = db.immersionPlanDao()
    val checklistRecordDao: ChecklistRecordDao = db.checklistRecordDao()
    val checklistAnswerDao: ChecklistAnswerDao = db.checklistAnswerDao()

    // ─── Datos Simulados (Mock Data) ──────────────────────────────────────────

    /**
     * Puebla la base de datos con datos de prueba si aún está vacía.
     *
     * Se ejecuta al iniciar la app (desde [AuthViewModel]). Verifica primero si ya
     * existen usuarios antes de insertar, evitando duplicados en reinicios de la app.
     *
     * Inserta en orden respetando las dependencias de Foreign Key:
     * 1. Usuarios → 2. Plantillas → 3. Ítems de Plantilla → 4. Planes de Inmersión.
     */
    suspend fun populateInitialData() {
        if (userDao.count() > 0) return

        insertMockUsers()
        val templateIds = insertMockTemplates()
        insertMockTemplateItems(templateIds)
        insertMockImmersionPlans(templateIds)
    }

    /**
     * Inserta un usuario de prueba por cada rol oficial de la app.
     * Son las cuentas que aparecerán en la pantalla de selección de inicio de sesión.
     */
    private suspend fun insertMockUsers() {
        userDao.insertAll(
            listOf(
                User(name = "Carlos Mendoza",  role = UserRole.BUZO),
                User(name = "Roberto Silva",   role = UserRole.BUZO_DE_EMERGENCIA),
                User(name = "María González",  role = UserRole.SUPERVISOR),
                User(name = "Pedro Herrera",   role = UserRole.JEFE_DE_CENTRO),
                User(name = "Ana Castillo",    role = UserRole.PREVENCIONISTA)
            )
        )
    }

    /**
     * Crea dos plantillas de checklist representativas de las faenas más comunes en AquaChile.
     * @return Lista con los ids autogenerados de las plantillas, en orden de inserción.
     */
    private suspend fun insertMockTemplates(): List<Int> {
        val idCosecha = templateDao.insert(
            Template(
                title           = "Chequeo Estándar — Cosecha",
                difficultyLevel = "Comercial",
                workType        = "Cosecha"
            )
        )
        val idRedes = templateDao.insert(
            Template(
                title           = "Chequeo Mantención de Redes",
                difficultyLevel = "Intermedio",
                workType        = "Mantención de redes"
            )
        )
        return listOf(idCosecha.toInt(), idRedes.toInt())
    }

    /**
     * Inserta los ítems de seguridad para cada plantilla.
     * Cada ítem es una pregunta concreta que el Supervisor evaluará en terreno.
     *
     * @param templateIds Los ids de las plantillas creadas en [insertMockTemplates].
     */
    private suspend fun insertMockTemplateItems(templateIds: List<Int>) {
        val (idCosecha, idRedes) = templateIds

        // Ítems para "Chequeo Estándar — Cosecha"
        templateItemDao.insertAll(
            listOf(
                TemplateItem(templateId = idCosecha, requiresPhoto = false,
                    questionText = "¿El equipo de respiración está completamente cargado y operativo?"),
                TemplateItem(templateId = idCosecha, requiresPhoto = false,
                    questionText = "¿El traje de buceo está libre de rasgaduras o daños visibles?"),
                TemplateItem(templateId = idCosecha, requiresPhoto = true,
                    questionText = "¿El sistema de comunicaciones umbilical funciona correctamente?"),
                TemplateItem(templateId = idCosecha, requiresPhoto = false,
                    questionText = "¿Se ha verificado la tabla de descompresión para esta profundidad?"),
                TemplateItem(templateId = idCosecha, requiresPhoto = false,
                    questionText = "¿El cordón umbilical de seguridad está libre de enredos?")
            )
        )

        // Ítems para "Chequeo Mantención de Redes"
        templateItemDao.insertAll(
            listOf(
                TemplateItem(templateId = idRedes, requiresPhoto = true,
                    questionText = "¿El equipo de iluminación subacuática funciona correctamente?"),
                TemplateItem(templateId = idRedes, requiresPhoto = false,
                    questionText = "¿El cuchillo de buceo está presente y accesible?"),
                TemplateItem(templateId = idRedes, requiresPhoto = false,
                    questionText = "¿El buzo completó el briefing de seguridad previo al descenso?"),
                TemplateItem(templateId = idRedes, requiresPhoto = false,
                    questionText = "¿El equipo de rescate en superficie está desplegado y listo?")
            )
        )
    }

    /**
     * Crea tres planes de inmersión de prueba en distintos estados del ciclo de vida,
     * permitiendo visualizar el dashboard con datos reales desde el primer arranque.
     *
     * @param templateIds Los ids de las plantillas, usados como FK en los planes.
     */
    private suspend fun insertMockImmersionPlans(templateIds: List<Int>) {
        val (idCosecha, idRedes) = templateIds

        immersionPlanDao.insertAll(
            listOf(
                ImmersionPlan(
                    date                 = "2026-09-18",
                    location             = "Jaula 12 — Sector B",
                    assignedEquipment    = "DESCO Modelo X + Compresor Honda GX200",
                    workType             = "Cosecha",
                    immersionLevel       = ImmersionLevel.COMERCIAL,
                    navyApproved         = true,
                    durationMinutes      = 90,
                    tableRecommendations = "Tabla US Navy Rev.7 — NDL 30min a 30m",
                    templateId           = idCosecha,
                    status               = ImmersionStatus.PLANIFICADA
                ),
                ImmersionPlan(
                    date                 = "2026-09-17",
                    location             = "Jaula 8 — Sector A",
                    assignedEquipment    = "DESCO Modelo Y + Compresor Yanmar",
                    workType             = "Mantención de redes",
                    immersionLevel       = ImmersionLevel.INTERMEDIO,
                    navyApproved         = true,
                    durationMinutes      = 60,
                    tableRecommendations = "Tabla NOAA — NDL 40min a 20m",
                    templateId           = idRedes,
                    status               = ImmersionStatus.EN_CURSO
                ),
                ImmersionPlan(
                    date                 = "2026-09-16",
                    location             = "Jaula 5 — Sector C",
                    assignedEquipment    = "DESCO Modelo X + Compresor Honda GX200",
                    workType             = "Cosecha",
                    immersionLevel       = ImmersionLevel.COMERCIAL,
                    navyApproved         = true,
                    durationMinutes      = 75,
                    tableRecommendations = "Tabla US Navy Rev.7 — NDL 25min a 33m",
                    templateId           = idCosecha,
                    status               = ImmersionStatus.FINALIZADA
                )
            )
        )
    }
}
