package oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.communication.data.remote.ChatService
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ChatViewModel(
    private val repository: ChatRepository
): ViewModel() {

    private val _patients = MutableStateFlow<List<PatientConnectionState>>(emptyList())
    val patients = _patients

    private lateinit var chatService: ChatService

    private val _initialMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val initialMessages: StateFlow<List<ChatMessage>> = _initialMessages

    private val _realtimeMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val realtimeMessages: StateFlow<List<ChatMessage>> = _realtimeMessages

    private var currentPage = 0
    private val pageSize = 20
    private var endReached = false
    private var isLoading = false

    private val _patient = MutableStateFlow<PatientDto?>(null)
    val patient: StateFlow<PatientDto?> = _patient

    private val _symptomText = MutableStateFlow<String>("")
    val symptomText: StateFlow<String> = _symptomText


    private var lastDoctorUuid: String = SessionHolder.getUserUuid()?: ""

    fun loadInitialMessages(patientUuid: String) {
        currentPage = 0
        endReached = false
        _initialMessages.value = emptyList()
        loadMoreMessages(lastDoctorUuid, patientUuid)
    }

    fun loadMoreMessages(doctorUuid: String, patientUuid: String) {
        if (isLoading || endReached) return
        isLoading = true
        viewModelScope.launch {
            val newMessages = repository.getConversation(doctorUuid, patientUuid, currentPage, pageSize)
            if (newMessages.isEmpty()) {
                endReached = true
            } else {
                _initialMessages.value = newMessages + _initialMessages.value
                currentPage++
            }
            isLoading = false
        }
    }


    @SuppressLint("CheckResult")
    fun startChat(patientUuid: String) {
        chatService = ChatService(patientUuid = patientUuid)
        chatService.connect()
        chatService.incomingMessages
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { msg ->
                println("Mensaje recibido en ViewModel: ${msg.content}")
                _realtimeMessages.update { it + msg }
            }
        viewModelScope.launch {
            _patient.value = repository.getPatient(patientUuid)
        }
    }

    fun send(content: String){
        if (::chatService.isInitialized) {
            chatService.sendMessage(content, "TEXT", null)
        }
    }

    fun disconnect() {
        if (::chatService.isInitialized) {
            chatService.disconnect()
        }
    }

    override fun onCleared() {
        if (::chatService.isInitialized) {
            chatService.disconnect()
        }
        super.onCleared()
    }

    fun load(doctorUuid: String){
        viewModelScope.launch {
            val activeChats = repository.getActivePatients(doctorUuid)
            val completeData = activeChats.map{
                it.toPatientConnectionStateWithDetails(repository)
            }
            _patients.value = completeData
        }

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

    fun loadSymptomText(symptomId: Long) {
        viewModelScope.launch {
            try {
                val symptom: SymptomDto = repository.getSymptomLog(symptomId)
                val formattedDate = formatDateTime(symptom.loggedAt)
                val text = "Sobre ${symptom.symptomType} ocurrido el $formattedDate\n"
                _symptomText.value = text
            } catch (e: Exception) {
                _symptomText.value = "Error obteniendo síntoma"
            }
        }
    }

    fun formatDateTime(dateTime: String): String{
        val formattedDate = try {
            val ldt = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val instant = ldt.toInstant(ZoneOffset.UTC)
            val zoned = instant.atZone(ZoneId.systemDefault())
            val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            zoned.format(fmt)
        } catch (e: Exception) {
            "Fecha inválida"
        }
        return formattedDate
    }


}