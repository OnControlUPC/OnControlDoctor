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
import androidx.compose.material3.ButtonDefaults
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
            placeholder = { Text("Ingrese nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is PatientSearchUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is PatientSearchUiState.Error -> {
                val message = (uiState as PatientSearchUiState.Error).message
                Text(
                    text = "Error: $message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }

            is PatientSearchUiState.Result -> {
                val added = (uiState as PatientSearchUiState.Result).added
                val found = (uiState as PatientSearchUiState.Result).found


                LazyColumn {
                    if (added.isNotEmpty()) {
                        item {
                            Text(
                                "Agregados",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
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
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (found.isNotEmpty()) {
                        item {
                            Text(
                                "Encontrados",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(found) { patientState ->
                            PatientCard(
                                patientState,
                                viewModel,
                                doctorUuid,
                                onActivePatientClick = { patientUuid ->
                                }
                            )
                        }
                    } else if (added.isEmpty()) {
                        item {
                            Text(
                                "No se encontraron pacientes con ese nombre o correo electrónico.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            PatientSearchUiState.Idle -> {
                Text(
                    "Introduce al menos 3 letras para comenzar la búsqueda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
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

    val cardBorderColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, cardBorderColor, MaterialTheme.shapes.medium)
            .then(
                if (isActive) Modifier.clickable { onActivePatientClick(patient.uuid) }
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = patient.photoUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${patient.firstName} ${patient.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    patient.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when (state.connectionStatus) {
                ConnectionStatus.NONE -> {
                    Button(
                        onClick = {
                            viewModel.invitePatient(doctorUuid, patient.uuid)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Invitar")
                    }
                }
                ConnectionStatus.PENDING -> {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Solicitud enviada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = {
                                state.externalId?.let { viewModel.cancelRequest(it) }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
                ConnectionStatus.DISABLED, ConnectionStatus.ACCEPTED -> {
                    Button(
                        onClick = {
                            state.externalId?.let { viewModel.activateLink(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text("Activar")
                    }
                }
                else -> {}
            }
        }
    }
}
