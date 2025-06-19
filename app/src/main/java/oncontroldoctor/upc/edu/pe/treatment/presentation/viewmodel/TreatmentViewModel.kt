package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import oncontroldoctor.upc.edu.pe.treatment.data.model.TreatmentDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.TreatmentRequestDto
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.ActivateLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.AddTreatmentUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.DeactivateLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.GetTreatmentsUseCase


class TreatmentViewModel(
    private val searchPatientsUseCase: SearchPatientsUseCase,
    private val linkDoctorPatientUseCase: LinkDoctorPatientUseCase,
    private val getDoctorPatientsUseCase: GetDoctorPatientsUseCase,
    private val activateLinkUseCase: ActivateLinkUseCase,
    private val deactivateLinkUseCase: DeactivateLinkUseCase,
    private val addTreatmentUseCase: AddTreatmentUseCase,
    private val getTreatmentsUseCase: GetTreatmentsUseCase
) : ViewModel() {


    private val _patients = MutableStateFlow<List<PatientDto>>(emptyList())
    val patients: StateFlow<List<PatientDto>> = _patients

    private val _linkedPatients = MutableStateFlow<List<DoctorPatientLinkDto>>(emptyList())
    val linkedPatients: StateFlow<List<DoctorPatientLinkDto>> = _linkedPatients

    private val _isLinkSuccessful = MutableStateFlow<Boolean?>(null)
    val isLinkSuccessful: StateFlow<Boolean?> = _isLinkSuccessful

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _treatments = MutableStateFlow<List<TreatmentDto>>(emptyList())
    val treatments: StateFlow<List<TreatmentDto>> = _treatments

    var isAddTreatmentDialogVisible by mutableStateOf(false)
        private set

    fun showAddTreatmentDialog() {
        isAddTreatmentDialogVisible = true
    }

    fun hideAddTreatmentDialog() {
        isAddTreatmentDialogVisible = false
    }

    fun loadTreatments(token: String, patientUuid: String) {
        viewModelScope.launch {
            val response = getTreatmentsUseCase("Bearer $token", patientUuid)
            if (response.isSuccessful) {
                _treatments.value = response.body() ?: emptyList()
            }
        }
    }

    fun addTreatment(
        token: String,
        title: String,
        startDate: String,
        endDate: String,
        doctorUuid: String,
        patientUuid: String
    ) {
        val treatment = TreatmentRequestDto(
            title = title,
            startDate = startDate,
            endDate = endDate,
            doctorProfileUuid = doctorUuid,
            patientProfileUuid = patientUuid
        )

        viewModelScope.launch {
            val response = addTreatmentUseCase("Bearer $token", treatment)
            if (response.isSuccessful) {
                loadTreatments(token, patientUuid)
            }
        }
    }

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
            val statuses = listOf("ACTIVE", "ACCEPTED", "PENDING", "DISABLED")
            val priorityMap = mapOf(
                "ACTIVE" to 1,
                "ACCEPTED" to 2,
                "PENDING" to 3,
                "DISABLED" to 4
            )

            val allLinks = kotlinx.coroutines.coroutineScope {
                val responses = statuses.map { status ->
                    async {
                        getDoctorPatientsUseCase("Bearer $token", doctorUuid, status)
                    }
                }

                responses.awaitAll()
                    .filter { it.isSuccessful }
                    .flatMap { it.body() ?: emptyList() }
            }

            val uniqueLinks = allLinks
                .groupBy { it.patientUuid }
                .map { (_, links) ->
                    links.minByOrNull { priorityMap[it.status] ?: Int.MAX_VALUE }!!
                }

            _linkedPatients.value = uniqueLinks
        }
    }

    fun activateLink(token: String, externalId: String, doctorUuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = activateLinkUseCase("Bearer $token", externalId)
            if (response.isSuccessful) {
                loadDoctorLinkedPatients(token, doctorUuid)
            }
            _isLoading.value = false
        }
    }

    fun deactivateLink(token: String, externalId: String, doctorUuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = deactivateLinkUseCase("Bearer $token", externalId)
            if (response.isSuccessful) {
                loadDoctorLinkedPatients(token, doctorUuid)
            }
            _isLoading.value = false
        }
    }

}
