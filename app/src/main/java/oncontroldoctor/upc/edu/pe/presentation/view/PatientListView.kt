package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.PatientListViewModel

@Composable
fun PatientListView(
    viewModel: PatientListViewModel,
    onPatientClick: (Int) -> Unit
) {
    val patients = viewModel.patients.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(patients.value) { patient ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPatientClick(patient.id) }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "DNI: ${patient.dni}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Ir al perfil",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
