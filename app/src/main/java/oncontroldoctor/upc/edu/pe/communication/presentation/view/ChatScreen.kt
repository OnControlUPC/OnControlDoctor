package oncontroldoctor.upc.edu.pe.communication.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.communication.presentation.components.ChatViewModelFactory
import oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    patientUuid: String,
    repository: ChatRepository
    )
{

    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(repository)
    )
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(patientUuid) {
        viewModel.startChat(patientUuid)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.disconnect() }
    }


    Column(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                MessageBubble(msg, patientUuid)
            }
        }
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            var text by remember { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                viewModel.send(text)
                text = ""
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}


@Composable
fun MessageBubble(
    message: ChatMessage,
    patientUuid: String
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = if (message.senderUuid != patientUuid) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message.type,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray
        )
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
