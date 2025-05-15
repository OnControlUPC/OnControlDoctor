package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.PatientDashboardViewModel

@Composable
fun PatientDashboardView(
    patientId: Int,
    viewModel: PatientDashboardViewModel = viewModel(),
    onNavigate: (String) -> Unit
) {
    val treatments by viewModel.treatments.collectAsState()
    val activeTreatments = treatments.filter { it.isActive }.take(3)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tratamientos activos", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        activeTreatments.forEach { treatment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = treatment.name,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Opciones", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Grid de botones cuadrados
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DashboardOptionCard("Historial") { onNavigate("history/$patientId") }
                DashboardOptionCard("Chat") { onNavigate("chat/1") } // chatId real luego
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DashboardOptionCard("Calendario") { onNavigate("calendarPatient/$patientId") }
                DashboardOptionCard("Citas") { onNavigate("appointments/$patientId") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                DashboardOptionCard("Solicitudes") { onNavigate("requests/$patientId") }
            }
        }
    }
}

@Composable
fun DashboardOptionCard(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
