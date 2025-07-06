package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateProcedureRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.RecurrenceType
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format

data class PatientProfileUiState(
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class PatientProfileViewModel(
    private val patientUuid: String,
    private val repository: TreatmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientProfileUiState())
    val uiState: StateFlow<PatientProfileUiState> = _uiState

    var treatments by mutableStateOf<List<Treatment>>(emptyList())
        private set

    private val _procedures = mutableStateOf<List<Procedure>>(emptyList())
    val procedures: State<List<Procedure>> = _procedures

    private val _symptoms = mutableStateOf<List<SymptomDto>>(emptyList())
    val symptoms: State<List<SymptomDto>> = _symptoms

    fun loadTreatments(doctorUuid: String, patientUuid: String) {
        viewModelScope.launch {
            val allTreatments = repository.getTreatmentsByDoctor(doctorUuid)
            treatments = allTreatments.filter { it.patientProfileUuid == patientUuid }
        }
    }

    fun createTreatment(
        title: String,
        startDate: String,
        endDate: String,
        doctorUuid: String,
        patientUuid: String
    ) {
        viewModelScope.launch {
            try {
                repository.createTreatment(
                    title = title,
                    startDate = startDate,
                    endDate = endDate,
                    doctorProfileUuid = doctorUuid,
                    patientProfileUuid = patientUuid
                )
                loadTreatments(doctorUuid, patientUuid)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al crear tratamiento"
                )
            }
        }
    }

    suspend fun loadProceduresByTreatment(treatmentExternalId: String) {
        try {
            _procedures.value = repository.getProceduresByTreatmentExternalId(treatmentExternalId)
        } catch (e: Exception) {
            _procedures.value = emptyList()
        }
    }

    fun loadProceduresByTreatmentAsync(treatmentExternalId: String) {
        viewModelScope.launch {
            loadProceduresByTreatment(treatmentExternalId)
        }
    }

    fun createProcedure(
        treatmentId: String,
        doctorUuid: String,
        description: String,
        recurrenceType: RecurrenceType,
        interval: Int,
        totalOccurrences: Int? = null,
        untilDate: String? = null
    ) {
        viewModelScope.launch {
            try {
                val request = CreateProcedureRequestDto(
                    doctorProfileUuid = doctorUuid,
                    description = description,
                    recurrenceType = recurrenceType,
                    interval = interval,
                    totalOccurrences = totalOccurrences,
                    untilDate = untilDate
                )
                val success = repository.createProcedure(treatmentId, request)
                if (success) {
                    loadProceduresByTreatment(treatmentId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo crear el procedimiento"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al crear procedimiento"
                )
            }
        }
    }

    fun cancelProcedure(
        procedureId: Long,
        doctorUuid: String,
        treatmentId: String
    ) {
        viewModelScope.launch {
            val success = repository.cancelProcedure(procedureId, doctorUuid)
            if (success) {
                loadProceduresByTreatment(treatmentId)
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "No se pudo cancelar el procedimiento"
                )
            }
        }
    }

    fun loadSymptoms(patientUuid: String, from: LocalDateTime, to: LocalDateTime) {
        viewModelScope.launch {
            try {
                _symptoms.value = repository.getSymptomsByPatientUuid(patientUuid, from.toString(), to.toString())
            } catch (e: Exception) {
                _symptoms.value = emptyList()
            }
        }
    }


    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val patient = repository.getPatientByUuid(patientUuid)
                _uiState.value = PatientProfileUiState(
                    name = patient.firstName + " " + patient.lastName,
                    email = patient.email,
                    photoUrl = patient.photoUrl,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}

class PatientProfileViewModelFactory(
    private val patientUuid: String,
    private val repository: TreatmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientProfileViewModel(patientUuid, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
