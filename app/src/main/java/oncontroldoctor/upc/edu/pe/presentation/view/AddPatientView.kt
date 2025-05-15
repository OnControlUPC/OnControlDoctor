package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPatientView(
    onSendRequest: (String) -> Unit
) {
    var dni by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Buscar paciente por DNI", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (dni.isNotBlank()) onSendRequest(dni)
        }) {
            Text("Enviar solicitud")
        }
    }
}
