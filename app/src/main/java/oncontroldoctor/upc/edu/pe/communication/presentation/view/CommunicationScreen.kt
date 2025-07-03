package oncontroldoctor.upc.edu.pe.communication.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel.CommunicationViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun CommunicationScreen(
    viewModel: CommunicationViewModel = viewModel(),
    onPatientClick: (String) -> Unit // Navega al chat
) {
    val patients by viewModel.patients.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(patients) { patient ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPatientClick(patient.patientUuid) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = patient.patientFullName, style = MaterialTheme.typography.titleMedium)
                    // Puedes agregar más datos del paciente aquí
                }
            }
        }
    }
}