package oncontroldoctor.upc.edu.pe.presentation.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import oncontroldoctor.upc.edu.pe.data.model.Message

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val doctorId = 100

    init {
        loadDummyChat()
    }

    private fun loadDummyChat() {
        _messages.value = listOf(
            Message(1, 1, senderId = 100, content = "Buenos días, ¿cómo se siente hoy?", timestamp = "08:00"),
            Message(2, 1, senderId = 202, content = "Un poco mareada pero mejor", timestamp = "08:05"),
            Message(3, 1, senderId = 100, content = "Me alegra saber eso.", timestamp = "08:06")
        )
    }

    fun sendMessage(content: String) {
        val newMessage = Message(
            id = (_messages.value.size + 1),
            chatId = 1,
            senderId = doctorId,
            content = content,
            timestamp = "Ahora"
        )
        _messages.value += newMessage
    }
}