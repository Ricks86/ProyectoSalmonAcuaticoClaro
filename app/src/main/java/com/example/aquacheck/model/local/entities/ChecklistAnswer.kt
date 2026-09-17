package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Respuesta individual a un [TemplateItem] dentro de un [ChecklistRecord].
 *
 * Es el nivel más granular del modelo de datos: cada ChecklistAnswer registra
 * el veredicto del Supervisor sobre una pregunta específica de seguridad
 * (ej. "¿El regulador de presión está calibrado?" → APROBADO).
 *
 * Si el [TemplateItem] tiene [TemplateItem.requiresPhoto] en `true`, el Supervisor
 * debe proveer el nombre de un archivo de evidencia en [mockImageUri].
 *
 * @property id Llave primaria autogenerada.
 * @property recordId FK al [ChecklistRecord] al que pertenece esta respuesta.
 *           Si el record se elimina, las respuestas se eliminan en cascada.
 * @property templateItemId FK al [TemplateItem] (la pregunta) que se está respondiendo.
 * @property status Resultado de la evaluación del Supervisor para este ítem. Ver [AnswerStatus].
 * @property mockImageUri Nombre simulado del archivo de evidencia fotográfica
 *           (ej. "evidencia_regulador.png"). Es `null` si el ítem no requiere foto
 *           o si aún no fue adjuntada.
 */
@Entity(
    tableName = "checklist_answers",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistRecord::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TemplateItem::class,
            parentColumns = ["id"],
            childColumns = ["templateItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recordId"), Index("templateItemId")]
)
data class ChecklistAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recordId: Int,
    val templateItemId: Int,
    val status: AnswerStatus,
    val mockImageUri: String? = null
)

/**
 * Resultado de la evaluación de un ítem de seguridad por parte del Supervisor.
 *
 * - [APROBADO]: El ítem cumple con los estándares de seguridad. No requiere acción.
 * - [RECHAZADO]: El ítem presenta una falla o incumplimiento. Puede bloquear la inmersión.
 * - [OBSERVACION]: El ítem presenta una anomalía menor que se registra pero no bloquea la faena.
 */
enum class AnswerStatus {
    APROBADO,
    RECHAZADO,
    OBSERVACION
}
