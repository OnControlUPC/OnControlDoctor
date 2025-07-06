package oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.communication.data.remote.ChatService
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState

class ChatViewModel(
    private val repository: ChatRepository
): ViewModel() {

    private val _patients = MutableStateFlow<List<PatientConnectionState>>(emptyList())
    val patients = _patients

    private lateinit var chatService: ChatService

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    @SuppressLint("CheckResult")
    fun startChat(patientUuid: String){
        chatService = ChatService(
            patientUuid = patientUuid
        )
        chatService.connect()
        chatService.incomingMessages
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ msg ->
                _messages.update { it + msg }
            }
    }

    fun send(content: String){
        chatService.sendMessage(content, "TEXT", null)
    }

    fun disconnect() {
        chatService.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        chatService.disconnect()
    }

    private var lastDoctorUuid: String = ""

    fun load(doctorUuid: String){
        viewModelScope.launch {
            val activeChats = repository.getActivePatients(doctorUuid)
            val completeData = activeChats.map{
                it.toPatientConnectionStateWithDetails(repository)
            }
            _patients.value = completeData
        }

    }
    fun reload(){
        load(lastDoctorUuid)
    }

    suspend fun DoctorPatientLinkSimpleDto.toPatientConnectionStateWithDetails(
        repository: ChatRepository
    ): PatientConnectionState {
        val patientDto = repository.getPatient(this.patientUuid)
        return PatientConnectionState(
            patient = patientDto,
            connectionStatus = ConnectionStatus.valueOf(this.status),
            externalId = this.externalId
        )
    }


}