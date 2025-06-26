package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.ActivatePatientLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState


class PatientsViewModel(
    private val repository: TreatmentRepository,
    private val activateLinkUseCase: ActivatePatientLinkUseCase
) : ViewModel() {

    private var lastDoctorUuid: String = ""

    private val _patients = MutableStateFlow<List<PatientConnectionState>>(emptyList())
    val patients: StateFlow<List<PatientConnectionState>> = _patients

    suspend fun DoctorPatientLinkSimpleDto.toPatientConnectionStateWithDetails(
        repository: TreatmentRepository
    ): PatientConnectionState {
        val patientDto = repository.getPatientByUuid(this.patientUuid)
        return PatientConnectionState(
            patient = patientDto,
            connectionStatus = ConnectionStatus.valueOf(this.status),
            externalId = this.externalId
        )
    }

    fun loadPatients(doctorUuid: String) {
        viewModelScope.launch {
            val links = repository.getAllPatients(doctorUuid)
            val status = links.map { it.toPatientConnectionStateWithDetails(repository) }
            _patients.value = status
        }
    }
    fun activateLink(externalId: String) {
        viewModelScope.launch {
            activateLinkUseCase(externalId)
            reload()
        }
    }

    fun reload() {
        loadPatients(lastDoctorUuid)
    }
}