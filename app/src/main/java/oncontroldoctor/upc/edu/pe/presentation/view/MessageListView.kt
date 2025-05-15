package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.MessageListViewModel
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage

@Composable
fun MessageListView(
    viewModel: MessageListViewModel = viewModel(),
    onChatClick: (Int) -> Unit
) {
    val chatList by viewModel.chats.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mensajes", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        chatList.forEach { chat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onChatClick(chat.id) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (chat.patientAvatarUrl != null) {
                        AsyncImage(
                            model = chat.patientAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.patientName.first().toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(chat.patientName, fontWeight = FontWeight.Bold)
                        Text(chat.lastMessage, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                    }

                    Text(
                        text = chat.lastMessageTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}