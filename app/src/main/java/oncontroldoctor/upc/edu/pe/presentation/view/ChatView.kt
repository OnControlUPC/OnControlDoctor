package oncontroldoctor.upc.edu.pe.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import oncontroldoctor.upc.edu.pe.data.model.Message
import oncontroldoctor.upc.edu.pe.presentation.viewmodel.ChatViewModel

@Composable
fun ChatView(
    chatId: Int,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message = message, isDoctor = message.senderId == 100)
            }
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribir mensaje...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (input.text.isNotBlank()) {
                    viewModel.sendMessage(input.text)
                    input = TextFieldValue("")
                }
            }) {
                Text("Enviar")
            }
        }
    }
}
@Composable
fun MessageBubble(message: Message, isDoctor: Boolean) {
    val backgroundColor = if (isDoctor) Color(0xFFDCF8C6) else Color(0xFFE0E0E0)
    val alignment = if (isDoctor) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(text = message.content)
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
        }
    }
}