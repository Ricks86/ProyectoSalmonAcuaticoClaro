package com.example.aquacheck.model.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registro de ejecución de un checklist de seguridad asociado a un [ImmersionPlan].
 *
 * Cada vez que un Supervisor lleva a cabo el proceso de verificación (antes o después
 * de una inmersión), se crea un ChecklistRecord. Este actúa como el "encabezado"
 * del evento de chequeo, mientras que las respuestas concretas se almacenan en
 * [ChecklistAnswer] (relación 1→N).
 *
 * Un mismo plan puede tener hasta dos registros: uno de tipo [ChecklistType.PRE]
 * y uno de tipo [ChecklistType.POST].
 *
 * @property id Llave primaria autogenerada.
 * @property immersionPlanId FK al [ImmersionPlan] al que pertenece este chequeo.
 *           Si el plan se elimina, el registro se elimina en cascada.
 * @property diverId FK al [User] con rol BUZO que será inspeccionado.
 * @property supervisorId FK al [User] con rol SUPERVISOR que conduce el chequeo.
 * @property type Indica si es un chequeo Pre-inmersión o Post-inmersión. Ver [ChecklistType].
 * @property timestamp Momento exacto en que se inició el registro, en milisegundos (epoch Unix).
 */
@Entity(
    tableName = "checklist_records",
    foreignKeys = [
        ForeignKey(
            entity = ImmersionPlan::class,
            parentColumns = ["id"],
            childColumns = ["immersionPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["diverId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("immersionPlanId"), Index("diverId"), Index("supervisorId")]
)
data class ChecklistRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val immersionPlanId: Int,
    val diverId: Int,
    val supervisorId: Int,
    val type: ChecklistType,
    val timestamp: Long
)

/**
 * Momento del ciclo de inmersión en que se ejecuta el checklist.
 *
 * - [PRE]: Chequeo previo al descenso. Valida que buzo y equipo estén en condiciones.
 * - [POST]: Chequeo posterior a la salida del agua. Confirma el estado del buzo y el equipo.
 */
enum class ChecklistType {
    PRE,
    POST
}
