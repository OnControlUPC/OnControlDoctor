package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddTreatmentDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSubmit(title, startDate, endDate)
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Nuevo tratamiento") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Inicio (YYYY-MM-DD)") })
                OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Fin (YYYY-MM-DD)") })
            }
        }
    )
}
