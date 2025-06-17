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

    fun searchPatients(token: String, query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = searchPatientsUseCase(token, query)
            _isLoading.value = false
            if (response.isSuccessful) {
                _patients.value = response.body() ?: emptyList()
            }
        }
    }

    fun linkDoctorWithPatient(token: String, doctorUuid: String, patientUuid: String) {
        val request = DoctorPatientLinkRequestDto(doctorUuid, patientUuid)
        viewModelScope.launch {
            val response = linkDoctorPatientUseCase(token, request)
            _isLinkSuccessful.value = response.isSuccessful
        }
    }

    fun loadDoctorLinkedPatients(token: String, doctorUuid: String) {
        viewModelScope.launch {
            val response = getDoctorPatientsUseCase(token, doctorUuid)
            if (response.isSuccessful) {
                _linkedPatients.value = response.body() ?: emptyList()
            }
        }
    }

    fun resetLinkSuccessFlag() {
        _isLinkSuccessful.value = null
    }
}
