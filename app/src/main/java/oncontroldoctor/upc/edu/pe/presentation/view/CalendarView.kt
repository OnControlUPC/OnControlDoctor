package oncontroldoctor.upc.edu.pe.presentation.view
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.CalendarViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.getValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(viewModel: CalendarViewModel = viewModel()) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val eventsForDay = viewModel.getEventsForSelectedDate()

    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("es", "PE"))

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Calendario", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { viewModel.previousDay() }) {
                Text("<")
            }

            Text(
                text = selectedDate.format(formatter),
                style = MaterialTheme.typography.titleMedium
            )

            Button(onClick = { viewModel.nextDay() }) {
                Text(">")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // Puedes usar DatePickerDialog aquí si quieres algo visual
        }) {
            Text("Seleccionar fecha")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (eventsForDay.isEmpty()) {
            Text("No hay eventos para este día.")
        } else {
            eventsForDay.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(event.title)
                        Text("${event.type} - ${event.time}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
