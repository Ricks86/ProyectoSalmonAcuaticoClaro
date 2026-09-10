package com.example.aquacheck.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Propósito de la clase: Representa una fila individual dentro de un chequeo (Capa de Persistencia).
 * Es el nivel de "detalle" en la relación maestro-detalle con [ChecklistSession].
 *
 * Por qué es necesaria: Permite la estructura dinámica del checklist. Cada componente de seguridad 
 * (como la máscara o el compresor) es un registro individual, lo que facilita agregar o quitar reglas
 * de seguridad en el futuro sin alterar el esquema de la sesión principal.
 *
 * Alcance e Impacto: Altamente acoplada a [ChecklistSession] mediante una Foreign Key. Si se elimina
 * la sesión padre, la restricción de 'CASCADE' borrará automáticamente estos ítems.
 *
 * @see ChecklistSession
 * @see ValidationState
 */
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistSession::class,
            parentColumns = arrayOf("sessionId"),
            childColumns = arrayOf("sessionId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChecklistItem(
    /** @param itemId Llave primaria autogenerada por SQLite. Identifica de manera única a esta fila en la tabla. */
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    
    /** @param sessionId Llave foránea (Foreign Key) que vincula este ítem con su [ChecklistSession] padre. */
    val sessionId: String,
    
    /** @param categoria Agrupación lógica del ítem de revisión (Ej: "Equipamiento", "Entorno", "Salud"). */
    val categoria: String,
    
    /** @param nombreItem Nombre específico del componente a evaluar (Ej: "Botella de emergencia"). */
    val nombreItem: String,
    
    /** @param estadoValidacion Resultado de la revisión del ítem. Nulo si aún no se ha evaluado. */
    val estadoValidacion: ValidationState?,
    
    /** @param observacion Notas adicionales. Requerido por negocio si el estado es NO_CUMPLE u OBSERVADO. */
    val observacion: String?,
    
    /** @param fotoUri Ruta del almacenamiento local apuntando a una imagen de evidencia física (opcional). */
    val fotoUri: String?
)

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * ForeignKey: Define la integridad referencial en SQLite.
 * `onDelete = ForeignKey.CASCADE` asegura que no queden "ítems huérfanos" en la base de datos;
 * si eliminas una sesión, Room eliminará todos sus ítems asociados para mantener la BD limpia.
 * `autoGenerate = true` le delega a SQLite la tarea de asignar un ID numérico secuencial.
 * ==============================================================================
 */
