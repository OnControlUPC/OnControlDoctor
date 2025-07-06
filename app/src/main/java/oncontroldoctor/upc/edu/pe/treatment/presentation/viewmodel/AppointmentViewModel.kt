package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateAppointmentDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.MarkAppointmentRequest
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class AppointmentViewModel(
    private val repository: TreatmentRepository
) : ViewModel() {

    private val _appointments = mutableStateOf<List<AppointmentSimpleDto>>(emptyList())
    val appointments: State<List<AppointmentSimpleDto>> = _appointments

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error


    fun loadAppointments(patientUuid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _appointments.value = repository.getAppointments(patientUuid)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            try {
                val success = repository.cancelAppointment(appointmentId)
                if (success) {
                    _appointments.value = _appointments.value.filterNot { it.id == appointmentId }
                } else {
                    _error.value = "No se pudo cancelar la cita"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun markAppointment(request: MarkAppointmentRequest, patientUuid: String) {
        viewModelScope.launch {
            val success = repository.markAppointment(request)
            if (success) {
                loadAppointments(patientUuid)
            }
        }
    }

    fun createAppointment(
        request: CreateAppointmentDto,
        patientUuid: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val localDateTime = LocalDateTime.parse(request.scheduledAt, formatter)
                val systemZone = ZoneId.systemDefault()
                val zonedDateTime = localDateTime.atZone(systemZone)
                val utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()

                val updatedRequest = request.copy(scheduledAt = utcDateTime.format(formatter))
                val success = repository.createAppointment(updatedRequest)
                if (success) {
                    loadAppointments(patientUuid)
                }
                onResult(success)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(false)
            } finally {
                _loading.value = false
            }

        }
    }

}

class AppointmentViewModelFactory(
    private val repository: TreatmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}