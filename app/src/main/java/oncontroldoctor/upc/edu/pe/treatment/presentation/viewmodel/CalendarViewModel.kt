package oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.ProcedureCalendarDto
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository

class CalendarViewModel(
    private val repository: TreatmentRepository
): ViewModel(){
    private val _appointments = MutableStateFlow<List<AppointmentSimpleDto>>(emptyList())
    val appointments: StateFlow<List<AppointmentSimpleDto>> = _appointments

    private val _procedures = MutableStateFlow<List<ProcedureCalendarDto>>(emptyList())
    val procedures: StateFlow<List<ProcedureCalendarDto>> = _procedures

    fun loadCalendar(patientUuid: String) {
        viewModelScope.launch {
            val appointmentsList = repository.getAppointments(patientUuid)
            val treatments = repository.getTreatmentsByPatient(patientUuid)
            val allProcedures = treatments
                .flatMap { treatment ->
                    repository.getProcedureCalendar(treatment.externalId)
                }
            _appointments.value = appointmentsList
            _procedures.value = allProcedures
        }
    }
}

class CalendarViewModelFactory(
    private val repository: TreatmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}