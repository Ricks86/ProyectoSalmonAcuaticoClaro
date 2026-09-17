package com.example.aquacheck.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Paleta AquaCheck (Modo Oscuro) ──────────────────────────────────────────

/** Fondo principal de la app. Azul marino profundo que simula el ambiente submarino. */
val AquaBackground = Color(0xFF050B15)

/** Color de superficies (cards, sheets, drawers). Ligeramente más claro que el fondo. */
val AquaSurface = Color(0xFF0D1B2A)

/** Variante de superficie para elementos de segundo nivel (inputs, dividers). */
val AquaSurfaceVariant = Color(0xFF162236)

/** Color secundario de la marca. Azul marino AquaChile, usado en headers y elementos de navegación. */
val AquaNavy = Color(0xFF1E4685)

/** Color de acento y CTA principal. Naranja de alta visibilidad para entornos de trabajo. */
val AquaOrange = Color(0xFFF5991D)

/** Color de texto base sobre fondos oscuros. */
val AquaOnDark = Color(0xFFFFFFFF)

/** Color de texto sobre el acento naranja. */
val AquaOnOrange = Color(0xFF000000)

/** Color para texto y elementos secundarios sobre fondos oscuros. */
val AquaOnDarkSecondary = Color(0xFFB0BEC5)

/** Verde de éxito, usado para el estado APROBADO en chequeos. */
val AquaSuccess = Color(0xFF4CAF50)

/** Rojo de alerta, usado para el estado RECHAZADO en chequeos. */
val AquaDanger = Color(0xFFF44336)

/** Amarillo de advertencia, usado para el estado OBSERVACIÓN en chequeos. */
val AquaWarning = Color(0xFFFFEB3B)