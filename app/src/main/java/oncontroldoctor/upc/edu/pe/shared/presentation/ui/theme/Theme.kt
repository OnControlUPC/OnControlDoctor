package oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Define the light color scheme using the colors from Color.kt
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight,
    inversePrimary = InversePrimaryLight,
)

// Define a dark color scheme (even if not used, it's good practice for MaterialTheme)
// You can remove this if you are absolutely sure you will never need a dark theme.
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4F8FBF), // PrimaryDark
    onPrimary = Color.Black,     // OnPrimaryDark
    secondary = Color(0xFF7BAFD4), // SecondaryDark
    tertiary = Color(0xFF2C5A7A), // TertiaryDark
    background = Color(0xFF121212), // BackgroundDark
    surface = Color(0xFF1E1E1E),   // SurfaceDark
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFF2B8B5),   // ErrorDark
    onError = Color.Black,       // OnErrorDark
    surfaceVariant = Color(0xFF49454F), // SurfaceVariantDark
    outline = Color(0xFF938F99),   // OutlineDark
    inversePrimary = Color(0xFF114D7C), // InversePrimaryDark
)


@Composable
fun OnControlDoctorTheme(
    // We keep darkTheme parameter, but its value will be overridden if dynamicColor is false
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to true to enable dynamic colors on Android 12+
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        // If dynamicColor is enabled and device supports it (Android 12+), use dynamic colors
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // If dynamicColor is false, always use LightColorScheme for this app
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Uses the Typography defined in Type.kt
        content = content
    )
}