package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientSearchUiState
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientSearchViewModel

@Composable
fun PatientSearchScreen(
    viewModel: PatientSearchViewModel,
    doctorUuid: String,
    onPatientSelected: (PatientConnectionState) -> Unit

) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Barra de búsqueda
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (query.length >= 3) {
                    viewModel.searchPatients(query, doctorUuid)
                } else {
                    viewModel.resetState()
                }
            },
            label = { Text("Buscar pacientes") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is PatientSearchUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PatientSearchUiState.Error -> {
                val message = (uiState as PatientSearchUiState.Error).message
                Text(text = message, color = Color.Red)
            }

            is PatientSearchUiState.Result -> {
                val added = (uiState as PatientSearchUiState.Result).added
                val found = (uiState as PatientSearchUiState.Result).found


                LazyColumn {
                    if (added.isNotEmpty()) {
                        item {
                            Text("Agregados", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(added) { patientState ->
                            PatientCard(
                                patientState,
                                viewModel,
                                doctorUuid,
                                onActivePatientClick = { _ ->
                                    onPatientSelected(patientState)
                                }
                            )
                        }
                    }

                    if (found.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Encontrados", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(found) { patientState ->
                            PatientCard(
                                patientState,
                                viewModel,
                                doctorUuid,
                                onActivePatientClick = { patientUuid ->
                                    // TODO: Navegar al panel de tratamientos del paciente con patientUuid
                                }
                            )
                        }
                    }
                }
            }

            PatientSearchUiState.Idle -> {
                Text("Introduce al menos 3 letras para comenzar la búsqueda.")
            }
        }
    }
}

@Composable
fun PatientCard(
    state: PatientConnectionState,
    viewModel: PatientSearchViewModel,
    doctorUuid: String,
    onActivePatientClick: (String) -> Unit = {}
) {
    val patient = state.patient
    val isActive = state.connectionStatus == ConnectionStatus.ACTIVE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
              if(isActive) Modifier
                  .border(2.dp, Color(0xFF1976D2), MaterialTheme.shapes.medium)
                  .clickable {onActivePatientClick(patient.uuid) }
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

            when (state.connectionStatus) {
                ConnectionStatus.NONE -> {
                    Button(onClick = {
                        viewModel.invitePatient(doctorUuid, patient.uuid)
                    }) {
                        Text("Invitar")
                    }
                }
                ConnectionStatus.PENDING -> {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Solicitud enviada", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = {
                            state.externalId?.let { viewModel.cancelRequest(it) }
                        }) {
                            Text("Cancelar")
                        }
                    }
                }
                ConnectionStatus.DISABLED, ConnectionStatus.ACCEPTED -> {
                    Button(onClick = {
                        state.externalId?.let { viewModel.activateLink(it) }
                    }) {
                        Text("Activar")
                    }
                }
                else -> {} // ACTIVE o DELETED no requieren acción
            }
        }
    }
}
