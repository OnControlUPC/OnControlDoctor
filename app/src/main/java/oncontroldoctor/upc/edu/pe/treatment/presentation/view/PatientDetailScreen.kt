package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PatientDetailScreen(
    patientName: String,
    patientUuid: String,
    onNavigateToTreatments: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Paciente: $patientName", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onNavigateToTreatments(patientUuid) }, modifier = Modifier.fillMaxWidth()) {
            Text("Tratamientos")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
            Text("Citas")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Chat")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Perfil")
        }
    }
}
