package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.ActivatePatientLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.CancelPatientRequestUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.CheckPatientConnectionUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.InvitePatientUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.SearchPatientsUseCase
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState

class PatientSearchViewModel(
    private val searchPatientsUseCase: SearchPatientsUseCase,
    private val checkConnectionUseCase: CheckPatientConnectionUseCase,
    private val invitePatientUseCase: InvitePatientUseCase,
    private val cancelRequestUseCase: CancelPatientRequestUseCase,
    private val activateLinkUseCase: ActivatePatientLinkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientSearchUiState>(PatientSearchUiState.Idle)
    val uiState: StateFlow<PatientSearchUiState> = _uiState.asStateFlow()

    private var lastQuery: String = ""
    private var lastDoctorUuid: String = ""


    fun searchPatients(name: String, doctorUuid: String) {
        lastQuery = name
        lastDoctorUuid = doctorUuid
        viewModelScope.launch {
            _uiState.value = PatientSearchUiState.Loading

            try {
                val patients = searchPatientsUseCase(name)
                val connections = mutableListOf<PatientConnectionState>()
                val unconnected = mutableListOf<PatientConnectionState>()

                patients.forEach { patient ->
                    val link = checkConnectionUseCase(doctorUuid, patient.uuid)
                    val state = PatientConnectionState.from(patient, link)

                    if (state.connectionStatus in listOf(
                            ConnectionStatus.ACTIVE,
                            ConnectionStatus.ACCEPTED,
                            ConnectionStatus.DISABLED,
                            ConnectionStatus.PENDING
                        ))
                    {
                        connections.add(state)
                    } else {
                        unconnected.add(state)
                    }
                }

                _uiState.value = PatientSearchUiState.Result(
                    added = connections.filter { it.connectionStatus != ConnectionStatus.REJECTED && it.connectionStatus != ConnectionStatus.DELETED },
                    found = unconnected.filter { it.connectionStatus == ConnectionStatus.NONE || it.connectionStatus != ConnectionStatus.PENDING  }
                )

            } catch (e: Exception) {
                _uiState.value = PatientSearchUiState.Error(e.message ?: "Error al buscar pacientes")
            }
        }
    }

    fun reload() {
        if (lastQuery.length >= 3) {
            searchPatients(lastQuery, lastDoctorUuid)
        }
    }

    fun invitePatient(doctorUuid: String, patientUuid: String) {
        viewModelScope.launch {
            invitePatientUseCase(doctorUuid, patientUuid)
            reload()
        }
    }

    fun cancelRequest(externalId: String) {
        viewModelScope.launch {
            cancelRequestUseCase(externalId)
            reload()
        }
    }

    fun activateLink(externalId: String) {
        viewModelScope.launch {
            activateLinkUseCase(externalId)
            reload()
        }
    }

    fun resetState() {
        _uiState.value = PatientSearchUiState.Idle
    }
}



sealed class PatientSearchUiState {
    object Idle : PatientSearchUiState()
    object Loading : PatientSearchUiState()
    data class Result(
        val added: List<PatientConnectionState>,
        val found: List<PatientConnectionState>
    ) : PatientSearchUiState()
    data class Error(val message: String) : PatientSearchUiState()
}
