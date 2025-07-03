package oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF1F9EED)
val OnPrimaryLight = Color.White // Text/icons on primary color

// Secondary colors for the light theme
val SecondaryLight = Color(0xFF3B6E8F)

// Tertiary colors for the light theme
val TertiaryLight = Color(0xFF0D3552)

// Backgrounds for the light theme
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFF5F5F5) // Card backgrounds, sheets, etc.
val OnBackgroundLight = Color.Black // Text/icons on background color
val OnSurfaceLight = Color.Black // Text/icons on surface color

// Error colors for the light theme
val ErrorLight = Color(0xFFB3261E)
val OnErrorLight = Color.White // Text/icons on error color

// Other role colors for the light theme
val SurfaceVariantLight = Color(0xFFE1E3E1) // Used for less prominent surfaces
val OutlineLight = Color(0xFF79747E) // Used for outlines, dividers
val InversePrimaryLight = Color(0xFFB9D3EA) // Used for inverse primary elements

// --- Colores Semánticos de Estado (para reutilizar en Citas, Tratamientos, etc.) ---

// Colores para estados de Éxito/Completado/Activo
val StatusSuccessBackground = Color(0xFFE0F2F1) // Muy ligero verde azulado
val StatusSuccessText = Color(0xFF00695C)       // Verde azulado oscuro

// Colores para estados de Advertencia/Pendiente/Próximo (inminente)
val StatusWarningBackground = Color(0xFFFFF9C4) // Amarillo muy claro
val StatusWarningText = Color(0xFFAB8A0D)       // Amarillo oscuro

// Colores para estados Informativos/Programado/Activo por defecto
val StatusInfoBackground = Color(0xFFE3F2FD)   // Azul muy claro
val StatusInfoText = Color(0xFF1976D2)         // Azul oscuro

// Colores para estados de Error/Perdido/Cancelado
val StatusErrorBackground = Color(0xFFFFEBEE)   // Rojo muy claro
val StatusErrorText = Color(0xFFD32F2F)         // Rojo oscuro

// Colores para estados Neutros/Por defecto/Cancelado por Doctor
val StatusNeutralBackground = Color(0xFFF5F5F5) // Gris muy claro
val StatusNeutralText = Color(0xFF424242)       // Gris oscuro

// Colores para estados de Énfasis/Inminente (una subcategoría de advertencia, si es muy urgente)
val StatusEmphasisBackground = Color(0xFFC8E6C9) // Verde claro (para lo más inminente, como tu cita de 0-15 min)
val StatusEmphasisText = Color(0xFF2E7D32)       // Verde oscuro


val SeverityMildBackground = Color(0xFFE8F5E9)    // Verde muy claro
val SeverityMildText = Color(0xFF388E3C)          // Verde oscuro

val SeverityModerateBackground = Color(0xFFFFFDE7) // Amarillo pálido
val SeverityModerateText = Color(0xFFBD942F)      // Amarillo oscuro

val SeveritySevereBackground = Color(0xFFFFE0B2)  // Naranja pálido
val SeveritySevereText = Color(0xFFBD6204)        // Naranja oscuro

val SeverityCriticalBackground = Color(0xFFFFCDD2) // Rojo pálido
val SeverityCriticalText = Color(0xFFD32F2F)      // Rojo oscuro

val SeverityDefaultBackground = Color(0xFFF5F5F5) // Gris muy claro
val SeverityDefaultText = Color(0xFF616161)       // Gris oscuro