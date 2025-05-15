package oncontroldoctor.upc.edu.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import oncontroldoctor.upc.edu.pe.data.model.ChatPreview

class MessageListViewModel : ViewModel() {

    private val _chats = MutableStateFlow<List<ChatPreview>>(emptyList())
    val chats: StateFlow<List<ChatPreview>> = _chats

    init {
        loadDummyChats()
    }

    private fun loadDummyChats() {
        _chats.value = listOf(
            ChatPreview(
                id = 1,
                patientName = "María Ramírez",
                patientAvatarUrl = null,
                lastMessage = "Doctor, me sentí mejor hoy",
                lastMessageTime = "14:30"
            ),
            ChatPreview(
                id = 2,
                patientName = "Carlos Fernández",
                patientAvatarUrl = null,
                lastMessage = "¿Confirmamos la cita del lunes?",
                lastMessageTime = "Ayer 10:15"
            )
        )
    }
}