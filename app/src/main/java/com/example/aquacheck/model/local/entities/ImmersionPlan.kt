package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Plan de inmersión: documento central que agrupa toda la información operativa y técnica
 * de una jornada de buceo en un centro de engorde de AquaChile.
 *
 * Antes de que un buzo pueda descender, se debe crear y aprobar un ImmersionPlan.
 * Este plan define el contexto (dónde, qué se hará, con qué equipo), los parámetros
 * técnicos de la inmersión (duración, nivel, tablas de descompresión) y la [Template]
 * de checklist que el Supervisor usará para validar el estado del buzo y el equipo.
 *
 * @property id Llave primaria autogenerada.
 * @property date Fecha de la inmersión en formato ISO 8601 (ej. "2026-09-17").
 * @property location Identificación del lugar físico dentro del centro (ej. "Jaula 12 - Sector B").
 * @property assignedEquipment Descripción del equipo de buceo asignado para la faena
 *           (ej. "Equipo DESCO Modelo X, Compresor Honda GX200").
 * @property workType Tipo de faena a realizar (ej. "Cosecha", "Mantención redes", "Tratamiento").
 * @property immersionLevel Categoría técnica de la inmersión según la profundidad y complejidad.
 *           Ver [ImmersionLevel] para los valores posibles.
 * @property navyApproved Indica si la Armada de Chile ha emitido el permiso de zarpe
 *           requerido para operar. Campo crítico de cumplimiento regulatorio.
 * @property durationMinutes Tiempo máximo planificado de la inmersión en minutos.
 * @property tableRecommendations Tablas de descompresión o recomendaciones técnicas del plan
 *           (almacenadas como texto libre para el MVP).
 * @property templateId FK opcional a la [Template] de checklist asignada. Puede ser nulo
 *           si la plantilla fue eliminada (SET_NULL).
 * @property status Estado actual del ciclo de vida del plan. Ver [ImmersionStatus].
 */
@Entity(
    tableName = "immersion_plans",
    foreignKeys = [
        ForeignKey(
            entity = Template::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("templateId")]
)
data class ImmersionPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val location: String,
    val assignedEquipment: String,
    val workType: String,
    val immersionLevel: ImmersionLevel,
    val navyApproved: Boolean,
    val durationMinutes: Int,
    val tableRecommendations: String,
    val templateId: Int?,
    val status: ImmersionStatus
)

/**
 * Categoría técnica de la inmersión según profundidad y complejidad de la faena.
 *
 * - [BASICO]: Inmersiones superficiales de baja complejidad.
 * - [INTERMEDIO]: Mayor profundidad o uso de equipo especializado liviano.
 * - [COMERCIAL]: Faenas de buceo profesional con equipo pesado (DESCO, umbilical).
 * - [ESPECIALISTA]: Inmersiones de alta dificultad técnica o condiciones extremas.
 */
enum class ImmersionLevel {
    BASICO,
    INTERMEDIO,
    COMERCIAL,
    ESPECIALISTA
}

/**
 * Ciclo de vida de un [ImmersionPlan].
 *
 * - [PLANIFICADA]: El plan fue creado pero el buzo aún no inicia la faena.
 * - [EN_CURSO]: El buzo se encuentra bajo el agua. El chequeo Pre ya fue completado.
 * - [FINALIZADA]: La faena terminó y el chequeo Post fue registrado.
 */
enum class ImmersionStatus {
    PLANIFICADA,
    EN_CURSO,
    FINALIZADA
}
