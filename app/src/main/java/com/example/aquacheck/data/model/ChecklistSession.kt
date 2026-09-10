package com.example.aquacheck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Propósito de la clase: Representa una sesión o evento de chequeo pre-buceo en la Capa de Persistencia.
 * Actúa como la entidad raíz ("Tabla principal") en la base de datos Room.
 * 
 * Por qué es necesaria: Centraliza los metadatos de un chequeo operativo (quién, dónde, cuándo y el veredicto final).
 * Sin ella, no tendríamos cómo agrupar los múltiples ítems evaluados en un evento concreto, imposibilitando el historial.
 *
 * Alcance e Impacto: Impacta directamente el esquema de la base de datos. Cualquier modificación en sus campos
 * requerirá una migración de base de datos. Los repositorios y ViewModels de historial dependen directamente de ella.
 *
 * @see ChecklistItem
 * @see com.example.aquacheck.data.local.ChecklistDao
 */
@Entity(tableName = "checklist_sessions")
data class ChecklistSession(
    /** @param sessionId Identificador único (UUID) de la sesión. Primary Key de la tabla. */
    @PrimaryKey val sessionId: String,
    
    /** @param centroOperacion Nombre de las instalaciones o centro donde se realiza el buceo. */
    val centroOperacion: String,
    
    /** @param fechaHora Timestamp (milisegundos) de cuándo se inició el chequeo. */
    val fechaHora: Long,
    
    /** @param nombreBuzo Nombre de la persona que ejecutará la inmersión. */
    val nombreBuzo: String,
    
    /** @param nombreSupervisor Responsable en superficie que valida el checklist. */
    val nombreSupervisor: String,
    
    /** @param resultadoFinal Veredicto calculado del chequeo ("Apto", "No Apto", "Pendiente", "Requiere Revisión"). */
    val resultadoFinal: String,
    
    /** @param syncStatus Bandera para la futura sincronización Offline-First vía WorkManager (false = pendiente de subir). */
    val syncStatus: Boolean = false
)

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * @Entity: Anotación de Room que marca esta 'data class' de Kotlin como una tabla de SQLite.
 * 'tableName' especifica el nombre exacto de la tabla en el motor de base de datos.
 * @PrimaryKey: Indica cuál campo es la llave primaria, asegurando unicidad para cada registro en SQLite.
 * ==============================================================================
 */
