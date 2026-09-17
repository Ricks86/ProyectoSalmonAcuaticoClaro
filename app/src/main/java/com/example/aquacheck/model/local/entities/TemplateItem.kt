package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Pregunta individual de una [Template] de seguridad.
 *
 * Cada TemplateItem representa un ítem concreto que el Supervisor debe validar
 * durante el chequeo Pre o Post inmersión (ej. "¿El equipo de respiración tiene presión
 * suficiente?", "¿El cordón umbilical está libre de enredos?").
 *
 * La relación con [Template] es de tipo 1→N: una plantilla tiene muchos ítems.
 * Al eliminarse la plantilla padre, sus ítems se eliminan en cascada (CASCADE).
 *
 * @property id Llave primaria autogenerada.
 * @property templateId FK que vincula este ítem con su [Template] padre.
 * @property questionText Enunciado de la pregunta de seguridad tal como aparecerá en pantalla.
 * @property requiresPhoto Indica si el Supervisor debe adjuntar evidencia fotográfica para
 *           este ítem. En el MVP se simula con un nombre de archivo de texto (ej. "foto.png").
 */
@Entity(
    tableName = "template_items",
    foreignKeys = [
        ForeignKey(
            entity = Template::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("templateId")]
)
data class TemplateItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val templateId: Int,
    val questionText: String,
    val requiresPhoto: Boolean
)
