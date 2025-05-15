package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.NotificationViewModel

@Composable
fun NotificationView(viewModel: NotificationViewModel = viewModel()) {
    val notifications by viewModel.notifications.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notificaciones", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        notifications.forEach { notification ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (notification.isRead) Color(0xFFF0F0F0) else Color(0xFFE3F2FD)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(notification.title, style = MaterialTheme.typography.titleMedium)
                    Text(notification.message, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        notification.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}