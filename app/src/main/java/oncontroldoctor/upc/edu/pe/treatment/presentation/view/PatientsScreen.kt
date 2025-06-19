package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.TreatmentViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton


@Composable
fun PatientsScreen(
    viewModel: TreatmentViewModel,
    doctorUuid: String,
    token: String,
    onNavigateToPatientDetail: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val linkedPatients by viewModel.linkedPatients.collectAsState()
    val patientsFound by viewModel.patients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLinkSuccessful by viewModel.isLinkSuccessful.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDoctorLinkedPatients(token, doctorUuid)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar paciente") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (searchQuery.text.isNotBlank()) {
                            viewModel.searchPatients(token, searchQuery.text)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar paciente"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.text.isBlank()) {
            Text("Pacientes vinculados", style = MaterialTheme.typography.titleMedium)

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {

            LazyColumn {

                items(linkedPatients.filter { it.status in listOf("ACCEPTED", "ACTIVE", "PENDING", "DISABLED") }) { link ->
                    val statusColor = when (link.status) {
                        "ACCEPTED" -> MaterialTheme.colorScheme.primary
                        "ACTIVE" -> MaterialTheme.colorScheme.tertiary
                        "PENDING" -> MaterialTheme.colorScheme.secondary
                        "DISABLED" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    val actionLabel = when (link.status) {
                        "ACTIVE" -> "Desactivar"
                        "ACCEPTED", "DISABLED" -> "Activar"
                        else -> null
                    }

                    val onClickAction = when (link.status) {
                        "ACTIVE" -> {
                            { viewModel.deactivateLink(token, link.externalId, doctorUuid) }
                        }
                        "ACCEPTED", "DISABLED" -> {
                            { viewModel.activateLink(token, link.externalId, doctorUuid) }
                        }
                        else -> null
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable(enabled = link.status == "ACTIVE") {
                                onNavigateToPatientDetail(link.patientUuid, link.patientFullName)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "- ${link.patientFullName}",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = link.status,
                            color = statusColor,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        if (actionLabel != null && onClickAction != null) {
                            Button(onClick = onClickAction) {
                                Text(actionLabel)
                            }
                        }
                    }
                    Divider()
                }
            }
            }
        } else {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (patientsFound.isNotEmpty()) {
                Text("Resultados de búsqueda", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(patientsFound) { patient ->
                        Column {
                            Text("- ${patient.firstName} ${patient.lastName}")
                            Button(
                                onClick = {
                                    viewModel.linkDoctorWithPatient(token, doctorUuid, patient.uuid)
                                },
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text("Vincular paciente")
                            }
                            Divider()
                        }
                    }
                }
            } else {
                Text("Paciente no encontrado.")
            }
        }

        isLinkSuccessful?.let { success ->
            if (success) {
                Text("Vínculo creado correctamente", color = MaterialTheme.colorScheme.primary)
                viewModel.resetLinkSuccessFlag()
                viewModel.loadDoctorLinkedPatients(token, doctorUuid)
            } else {
                Text("No se pudo vincular", color = MaterialTheme.colorScheme.error)
                viewModel.resetLinkSuccessFlag()
            }
        }
    }
}
