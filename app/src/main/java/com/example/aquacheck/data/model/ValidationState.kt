package com.example.aquacheck.data.model

/**
 * Propósito de la clase: Define los posibles estados de validación para un ítem del checklist.
 * Pertenece a la Capa de Acceso a Datos/Modelo, sirviendo como un contrato estricto de valores
 * posibles que la capa de UI y Dominio pueden utilizar.
 *
 * Por qué es necesaria: Evita el uso de "Magic Strings" (como "Cumple", "CUMPLE", etc.) asegurando
 * type-safety. Si escribiéramos strings a mano, un error tipográfico podría romper la lógica
 * de cálculo del resultado final.
 *
 * Alcance e Impacto: Utilizado directamente por [ChecklistItem] y [ChecklistViewModel]. Si se elimina
 * o altera un valor sin actualizar la UI, los cálculos de "Apto" o "No Apto" fallarán.
 *
 * @see ChecklistItem
 */
enum class ValidationState {
    /** Indica que el ítem de seguridad cumple con el estándar y está listo. */
    CUMPLE,
    /** Indica una falla crítica en el ítem (ej. falta carga en el tanque de emergencia). Provocará un chequeo "No Apto". */
    NO_CUMPLE,
    /** Indica una anomalía no crítica pero que requiere atención o registro. Provocará un chequeo "Requiere Revisión". */
    OBSERVADO,
    /** El ítem no aplica para el contexto de la operación actual (ej. un equipo que no se utilizará). */
    NO_APLICA
}

/*
 * ==============================================================================
 * Glosario Pedagógico:
 * enum class: Estructura de Kotlin que representa un grupo cerrado de constantes.
 * En la persistencia con Room, las clases `enum` son convertidas automáticamente a String o Int
 * dependiendo de la configuración (por defecto String), permitiendo guardar estos valores en la base de datos de manera segura.
 * ==============================================================================
 */
