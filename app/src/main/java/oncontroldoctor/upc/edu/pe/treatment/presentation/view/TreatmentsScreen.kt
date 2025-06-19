package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.TreatmentViewModel


@Composable
fun TreatmentsScreen(
    viewModel: TreatmentViewModel,
    patientUuid: String,
    doctorUuid: String,
    token: String
) {
    val treatments by viewModel.treatments.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTreatments(token, patientUuid)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tratamientos del paciente", style = MaterialTheme.typography.titleLarge)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(treatments) { treatment ->
                Text("- ${treatment.title}", style = MaterialTheme.typography.bodyLarge)
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.showAddTreatmentDialog() }) {
            Text("Agregar tratamiento")
        }

        if (viewModel.isAddTreatmentDialogVisible) {
            AddTreatmentDialog(
                onDismiss = { viewModel.hideAddTreatmentDialog() },
                onSubmit = { title, startDate, endDate ->
                    viewModel.addTreatment(token, title, startDate, endDate, doctorUuid, patientUuid)
                }
            )
        }
    }
}
