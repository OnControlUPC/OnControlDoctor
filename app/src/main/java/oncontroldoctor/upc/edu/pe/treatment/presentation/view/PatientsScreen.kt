package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(
    viewModel: PatientsViewModel,
    doctorUuid: String,
    onPatientClick: (String) -> Unit = {}
) {
    val patients by viewModel.patients.collectAsState()

    LaunchedEffect(doctorUuid) {
        viewModel.loadPatients(doctorUuid)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Pacientes") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(patients) { state ->
                PatientCardSimple(
                    state = state,
                    doctorUuid = doctorUuid,
                    onActivate = { externalId -> viewModel.activateLink(externalId) },
                    onActivePatientClick = { patientUuid -> onPatientClick(patientUuid) }
                )
            }
        }
    }
}


@Composable
fun PatientCardSimple(
    state: PatientConnectionState,
    doctorUuid: String,
    onActivate: (externalId: String) -> Unit,
    onActivePatientClick: (String) -> Unit = {}
) {
    val patient = state.patient
    val isActive = state.connectionStatus == ConnectionStatus.ACTIVE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isActive) Modifier
                    .border(2.dp, Color(0xFF1976D2), MaterialTheme.shapes.medium)
                    .clickable { onActivePatientClick(patient.uuid) }
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = patient.photoUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${patient.firstName} ${patient.lastName}", style = MaterialTheme.typography.titleSmall)
                Text(patient.email, style = MaterialTheme.typography.bodySmall)
            }

            if (state.connectionStatus == ConnectionStatus.DISABLED || state.connectionStatus == ConnectionStatus.ACCEPTED) {
                Button(onClick = {
                    state.externalId?.let { onActivate(it) }
                }) {
                    Text("Activar")
                }
            }
        }
    }
}