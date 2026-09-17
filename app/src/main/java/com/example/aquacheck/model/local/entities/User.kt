package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de usuarios del sistema AquaCheck.
 *
 * Representa a cada persona que opera o supervisa una faena de buceo en los centros de AquaChile.
 * El rol determina qué acciones puede ejecutar dentro de la app (ej: solo el Prevencionista
 * puede crear plantillas; solo el Supervisor puede cerrar un chequeo).
 *
 * El alta de usuarios es gestionada externamente por RRHH; no existe flujo de registro en la app.
 *
 * @property id Llave primaria autogenerada por Room.
 * @property name Nombre completo del trabajador tal como aparece en su credencial de empresa.
 * @property role Cargo operativo del usuario, que define sus permisos dentro de la app.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val role: UserRole
)

/**
 * Cargos operativos reconocidos en las faenas de buceo de AquaChile.
 *
 * - [BUZO]: Ejecuta la inmersión. Protagonista del checklist Pre/Post.
 * - [BUZO_DE_EMERGENCIA]: Buzo en standby en superficie, listo para asistir en caso de emergencia.
 * - [SUPERVISOR]: Responsable en superficie de validar el chequeo y autorizar el descenso.
 * - [JEFE_DE_CENTRO]: Administra el centro de engorde; puede revisar historial y planes.
 * - [PREVENCIONISTA]: Crea y gestiona las plantillas de seguridad que se asignan a cada plan.
 */
enum class UserRole {
    BUZO,
    BUZO_DE_EMERGENCIA,
    SUPERVISOR,
    JEFE_DE_CENTRO,
    PREVENCIONISTA
}
