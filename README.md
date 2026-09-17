# AquaCheck Buceo

## Descripción del Proyecto
AquaCheck Buceo es una aplicación móvil nativa diseñada para la empresa AquaChile con el propósito de digitalizar el sistema de pre y post-chequeo de seguridad de los buzos en los centros de engorde. Actualmente, este proceso se realiza de forma manual y visual mediante documentos físicos, lo que genera retrasos, omisiones y errores humanos. La aplicación busca automatizar estas inspecciones, generar una trazabilidad digital de fácil acceso y optimizar los tiempos de verificación operativa.

## Equipo de Desarrollo
* **Equipo:** CriticalCode 4
* **Sección:** 002D
* **Integrantes:** Juan Fernández 

## Definición del MVP (Producto Mínimo Viable)
El alcance del proyecto para esta entrega inicial prioriza la lógica de negocio y se centra en las siguientes funcionalidades:
* **Autenticación simulada:** Inicio de sesión rápido según los roles de la empresa (Buzo profesional, Buzo de emergencia, Supervisor de buceo, Jefe de centro, Prevencionista de riesgo).
* **Planificador de inmersiones:** Registro de inmersiones especificando la fecha, ubicación, nivel de inmersión, tipo de labor y equipamiento.
* **Checklist digital dinámico:** Formularios pre y post inmersión para validar los elementos de seguridad, estructurados mediante un sistema de plantillas modificables por el prevencionista de riesgo.
* **Historial y consultas:** Visualización de inmersiones previas, auditorías de chequeos y reportes de rendimiento.
* **Captura de evidencia:** Opción para adjuntar fotografías del equipamiento revisado, simulado en esta fase mediante un string (ej. `foto.png`) vinculado a los ítems del checklist.

**Fuera del alcance:** Análisis de imágenes mediante IA, cálculos médicos reales, validaciones legales externas, alta/registro de usuarios nuevos y el uso simultáneo en múltiples centros.

## Tecnologías y Arquitectura
* **Entorno de Desarrollo:** Android Studio utilizando Kotlin y Gradle.
* **Arquitectura:** Patrón de diseño MVVM (Model-View-ViewModel).
* **Capa de Datos:** Base de datos local implementando Room Database, operando temporalmente con datos simulados (mock data).
* **Interfaz (UI/UX):** Construida con Jetpack Compose e implementando componentes de Material Design 3, incluyendo `NavigationBar`, `NavigationDrawer`, `TopAppBar` y `Floating Action Button (FAB)`.
* **Documentación:** Todo el código fuente está documentado con el estándar KDoc en español para facilitar la comprensión educativa y mantenibilidad futura.

## Identidad Visual y Diseño
La interfaz de la aplicación emplea un modo oscuro nativo para reducir la fatiga visual en pantallas OLED y representar el entorno marítimo:
* **Logotipo:** Representación de un buzo (`docs/diseno/logo.png`).
* **Fondo Principal:** Azul marino profundo (`#050B15`).
* **Color Secundario:** Azul técnico (`#1E4685`) para barras de navegación, encabezados y contenedores.
* **Color Principal (CTA):** Naranja de alta visibilidad (`#F5991D`) inspirado en indumentaria de rescate para botones de acción.
* **Texto:** Blanco puro (`#FFFFFF`) para asegurar un contraste óptimo y lectura rápida.

## Flujo de Usuario y Pantallas
El flujo principal de navegación establecido para los usuarios es: `Login → Inicio (Dashboard) → {Planificador de Inmersiones / Checklist Digital / Historial de Inmersiones / Perfil de Usuario}`.
* **Diagrama UML:** El Diagrama de Actividad que representa este recorrido está disponible en `docs/diseno/flujo-usuario-uml.png`.
* **Interfaces Gráficas:** Las vistas generadas como referencia (Login, Dashboard, Planificador, Checklist, Evidencia, Historial, Informes y Perfil) se encuentran respaldadas en el directorio `docs/diseno/`.

## Evidencias
Toda la evidencia documental del proceso de análisis y diseño se encuentra correctamente almacenada en los directorios `docs/evidencias/clase-01/` y `docs/evidencias/clase-02/`.