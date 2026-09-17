package com.example.aquacheck.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Esquema de colores oscuro de AquaCheck.
 *
 * La app está diseñada exclusivamente en modo oscuro para mejorar la legibilidad
 * en entornos de trabajo al aire libre y bajo condiciones de poca luz (noche, interior de bodegas).
 * No se provee un esquema de luz alternativo en el MVP.
 */
private val AquaCheckColorScheme = darkColorScheme(
    primary          = AquaOrange,
    onPrimary        = AquaOnOrange,
    secondary        = AquaNavy,
    onSecondary      = AquaOnDark,
    background       = AquaBackground,
    onBackground     = AquaOnDark,
    surface          = AquaSurface,
    onSurface        = AquaOnDark,
    surfaceVariant   = AquaSurfaceVariant,
    onSurfaceVariant = AquaOnDarkSecondary,
    error            = AquaDanger,
    onError          = Color.White,
)

/**
 * Tema principal de la app AquaCheck.
 *
 * Envuelve el contenido en el esquema de colores oscuro de la marca.
 * Debe usarse como nodo raíz en [MainActivity] y en todos los `@Preview`.
 */
@Composable
fun AquaCheckTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AquaCheckColorScheme,
        typography  = Typography,
        content     = content
    )
}