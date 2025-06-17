package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.GetDoctorPatientsUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.LinkDoctorPatientUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.SearchPatientsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


class TreatmentViewModel(
    private val searchPatientsUseCase: SearchPatientsUseCase,
    private val linkDoctorPatientUseCase: LinkDoctorPatientUseCase,
    private val getDoctorPatientsUseCase: GetDoctorPatientsUseCase
) : ViewModel() {

    private val _patients = MutableStateFlow<List<PatientDto>>(emptyList())
    val patients: StateFlow<List<PatientDto>> = _patients

    private val _linkedPatients = MutableStateFlow<List<DoctorPatientLinkDto>>(emptyList())
    val linkedPatients: StateFlow<List<DoctorPatientLinkDto>> = _linkedPatients

    private val _isLinkSuccessful = MutableStateFlow<Boolean?>(null)
    val isLinkSuccessful: StateFlow<Boolean?> = _isLinkSuccessful

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun resetLinkSuccessFlag() {
        _isLinkSuccessful.value = null
    }

    fun searchPatients(token: String, query: String) {
        _isLoading.value = true
        _patients.value = emptyList()
        viewModelScope.launch {
            val response = searchPatientsUseCase("Bearer $token", query)
            _isLoading.value = false
            if (response.isSuccessful) {
                _patients.value = response.body() ?: emptyList()
            }
        }
    }

    fun linkDoctorWithPatient(token: String, doctorUuid: String, patientUuid: String) {
        val request = DoctorPatientLinkRequestDto(doctorUuid, patientUuid)
        viewModelScope.launch {
            val response = linkDoctorPatientUseCase("Bearer $token", request)
            _isLinkSuccessful.value = response.isSuccessful
        }
    }

    fun loadDoctorLinkedPatients(token: String, doctorUuid: String) {
        viewModelScope.launch {
            val statuses = listOf("ACCEPTED", "ACTIVE", "PENDING")
            val responses = statuses.map { status ->
                async {
                    getDoctorPatientsUseCase("Bearer $token", doctorUuid, status)
                }
            }

            val results = responses.awaitAll()
                .filter { it.isSuccessful }
                .flatMap { it.body() ?: emptyList() }

            _linkedPatients.value = results
        }
    }

}
