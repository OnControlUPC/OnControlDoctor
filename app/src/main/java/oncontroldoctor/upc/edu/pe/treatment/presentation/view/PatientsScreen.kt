package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.TreatmentViewModel

@Composable
fun PatientsScreen(
    viewModel: TreatmentViewModel,
    doctorUuid: String,
    token: String
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
            onValueChange = {
                searchQuery = it
                if (it.text.isNotBlank()) {
                    viewModel.searchPatients(token, it.text)
                }
            },
            label = { Text("Buscar paciente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.text.isBlank()) {
            Text("Pacientes vinculados", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(linkedPatients.filter { it.status == "ACTIVE" }) { link ->
                    Text("- ${link.patientFullName}")
                    Divider()
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
