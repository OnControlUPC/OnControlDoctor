package oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.communication.data.remote.ChatService
import oncontroldoctor.upc.edu.pe.communication.domain.repository.CommunicationRepository
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto

class CommunicationViewModel(
    private val repository: CommunicationRepository,
    private val chatService: ChatService
): ViewModel(){
    private val _patients = MutableStateFlow<List<DoctorPatientLinkSimpleDto>>(emptyList())
    val patients: StateFlow<List<DoctorPatientLinkSimpleDto>> = _patients

    fun loadActivePatients(doctorUuid: String) {
        viewModelScope.launch {
            try {
                val result = repository.getAllPatientsActive(doctorUuid)
                _patients.value = result
            } catch (e: Exception) {
                _patients.value = emptyList()
            }
        }
    }
    fun sendMessageToPatient(
        patientUuid: String,
        message: String,
        type: String = "TEXT",
        fileUrl: String? = null,
        senderRole: String? = null,
        senderUuid: String? = null
    ) {
        viewModelScope.launch {
            try {
                val doctorUuid = SessionHolder.getUserUuid()
                val chatMessage = ChatMessage(
                    content = message,
                    type = type,
                    fileUrl = fileUrl,
                    senderRole = senderRole,
                    senderUuid = senderUuid
                )
                chatService.sendMessage(
                    content = chatMessage.content,
                    type = chatMessage.type,
                    fileUrl = chatMessage.fileUrl,
                    senderRole = chatMessage.senderRole,
                    senderUuid = chatMessage.senderUuid,
                    doctorUuid = doctorUuid.toString(),
                    patientUuid = patientUuid
                )
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}

class CommunicationViewModelFactory(
    private val repository: CommunicationRepository,
    private val chatService: ChatService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunicationViewModel(repository, chatService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}