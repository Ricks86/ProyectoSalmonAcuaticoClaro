package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Plantilla de checklist de seguridad, creada y administrada por el Prevencionista.
 *
 * Una Template es el "molde" que define qué preguntas de seguridad deben responderse
 * antes y después de una inmersión. Al crear un [ImmersionPlan], el Prevencionista
 * asigna una Template, que luego genera las respuestas ([ChecklistAnswer]) concretas
 * a través de sus ítems ([TemplateItem]).
 *
 * @property id Llave primaria autogenerada.
 * @property title Nombre descriptivo de la plantilla (ej. "Chequeo Estándar Jaulas Norte").
 * @property difficultyLevel Nivel de dificultad operativa asociado (ej. "Básico", "Comercial").
 *           Es un String libre para permitir que el Prevencionista defina sus propias categorías.
 * @property workType Tipo de faena para la que aplica esta plantilla (ej. "Cosecha", "Mantención de redes").
 */
@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val difficultyLevel: String,
    val workType: String
)
