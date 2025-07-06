package oncontroldoctor.upc.edu.pe.dashboard.presentation.view

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.dashboard.presentation.viewmodel.DashboardViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@SuppressLint("ContextCastToActivity")
@Composable
fun SetStatusBarColor(color: Color, darkIcons: Boolean = false) {
    val view = LocalView.current
    val activity = LocalContext.current as Activity
    SideEffect {
        val window: Window = activity.window
        window.statusBarColor = color.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
    }
}


@Composable
fun DashboardHomeScreen(
    dashboardViewModel: DashboardViewModel
) {
    LaunchedEffect(Unit) {
        dashboardViewModel.loadProfile()
    }

    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    SetStatusBarColor(primaryColor, darkIcons = false) // Asegura que los iconos de la barra de estado sean claros sobre el color primario
    val profile by dashboardViewModel.profile.collectAsState()
    val profileName = "Dr. " + (profile?.lastName ?: "Usuario")
    val profileImageUrl = profile?.urlPhoto
    val headerHeight = 120.dp
    val plan by dashboardViewModel.planState.collectAsState()
    val citas = listOf(
        Appointment("Juan Pérez", "Consulta general", "10:00 AM", "24.06.2025"),
        Appointment("María López", "Control", "12:00 PM", "25.06.2025"),
        Appointment("Carlos Ruiz", "Revisión anual", "09:30 AM", "26.06.2025"),
        Appointment("Laura García", "Seguimiento", "01:00 PM", "27.06.2025")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Header superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(primaryColor)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bienvenido", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineSmall) // Usa onPrimary
                    Text(profileName, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium) // Usa onPrimary
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onPrimary, // Usa onPrimary
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                    )
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape), // Usa onPrimary
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeight + 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // Sección "Próximas Citas"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Próximas citas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                TextButton(onClick = { /* Navegar a citas completas */ }) {
                    Text("Ver más", color = MaterialTheme.colorScheme.primary) // Color primario para el botón
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre título y lista

            if (citas.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre tarjetas
                ) {
                    items(citas.take(5)) { cita ->
                        Card(
                            modifier = Modifier.width(220.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Fondo de tarjeta
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(cita.paciente, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(cita.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) // Color más suave
                                Text(cita.hora, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) // Color más suave
                                Text(cita.fecha, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall) // Color más suave
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Aún no tienes citas programadas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(24.dp))
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Sección "Acciones rápidas"
            Text("Acciones rápidas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionTile(title = "Agregar un paciente", icon = Icons.Default.Person) { /* TODO */ }
                ActionTile(
                    title = "Crear cita",
                    icon = Icons.Default.MailOutline,
                    onClick = { /* TODO */ }
                )
                ActionTile(
                    title = "Panel de sintomas",
                    icon = Icons.Default.Face,
                    onClick = {
                        if (plan?.symptomTrackingEnabled == true) {
                            // Aquí puedes implementar la lógica para el panel de síntomas
                        } else {
                            Toast.makeText(context, "Funcionalidad no disponible en tu plan", Toast.LENGTH_SHORT).show()
                        }
                    })
                ActionTile(
                    title = "Ajustar alertas",
                    icon = Icons.Default.Notifications,
                    onClick = {
                        if (plan?.customRemindersEnabled == true) {
                            // Aquí puedes implementar la lógica para ajustar alertas
                        } else {
                            Toast.makeText(context, "Funcionalidad no disponible en tu plan", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}


@Composable
fun ActionTile(
    title: String,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val bgColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Color más suave para deshabilitado
    val iconColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) // Icono más tenue para deshabilitado
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Texto más tenue para deshabilitado

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}

data class Appointment(
    val paciente: String,
    val descripcion: String,
    val hora: String,
    val fecha: String
)
