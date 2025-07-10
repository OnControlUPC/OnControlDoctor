package oncontroldoctor.upc.edu.pe.dashboard.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalPlanUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.SyncSubscriptionAndPlanUseCase
import oncontroldoctor.upc.edu.pe.profile.data.local.ProfileHolder
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentDisplayDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.MarkAppointmentRequest
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.GetCalendarAppointmentsUseCase
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService

class DashboardViewModel(
    private val syncSubscriptionAndPlanUseCase: SyncSubscriptionAndPlanUseCase,
    private val getLocalSubscriptionUseCase: GetLocalSubscriptionUseCase,
    private val getLocalPlanUseCase: GetLocalPlanUseCase,
    private val getCalendarAppointmentsUseCase: GetCalendarAppointmentsUseCase,
    private val treatmentService: TreatmentService
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val subscription: SubscriptionEntity, val plan: PlanFull) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val planState = MutableStateFlow<PlanFull?>(null)

    private val _profile = MutableStateFlow<DoctorProfile?>(null)
    val profile = _profile.asStateFlow()

    private val _appointmentsDisplay = MutableStateFlow<List<AppointmentDisplayDto>>(emptyList())
    val appointmentsDisplay = _appointmentsDisplay.asStateFlow()


    fun loadDashboard(adminId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                syncSubscriptionAndPlanUseCase(adminId)

                val subscription = getLocalSubscriptionUseCase()
                val plan = getLocalPlanUseCase()

                if (subscription != null && plan != null) {
                    _uiState.value = UiState.Ready(subscription, plan)
                } else {
                    _uiState.value = UiState.Error("No se pudo obtener suscripción o plan")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun loadLocalPlan() {
        viewModelScope.launch {
            val plan = getLocalPlanUseCase()
            planState.value = plan
        }
    }

    fun loadProfile() {
        _profile.value = ProfileHolder.doctorProfile
    }

    fun markAppointment(
        request: MarkAppointmentRequest,
        patientUuid: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val token = SessionHolder.getToken()
                if (!token.isNullOrBlank()) {
                    val response = treatmentService.markAppointment("Bearer $token", request)
                    if (response.isSuccessful) {
                        onSuccess()
                        loadAppointmentsFromCalendar(patientUuid)
                    } else {
                        onError("Error al marcar cita: ${response.code()}")
                    }
                } else {
                    onError("Token no disponible")
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error al marcar cita", e)
                onError(e.message ?: "Error desconocido")
            }
        }
    }


    fun loadAppointmentsFromCalendar(patientUuid: String) {
        viewModelScope.launch {
            try {
                val token = SessionHolder.getToken()
                if (token.isNullOrBlank()) {
                    _appointmentsDisplay.value = emptyList()
                    return@launch
                }

                val bearerToken = "Bearer $token"
                val appointments = getCalendarAppointmentsUseCase(bearerToken)

                val enrichedAppointments = appointments.mapNotNull { appointment ->
                    try {
                        val patientResponse = treatmentService.getPatientByUuid(bearerToken, appointment.patientProfileUuid)
                        if (patientResponse.isSuccessful) {
                            val patient = patientResponse.body()
                            AppointmentDisplayDto(
                                id = appointment.id,
                                scheduledAt = appointment.scheduledAt,
                                patientName = "${patient?.firstName ?: "Paciente"} ${patient?.lastName ?: ""}",
                                meetingUrl = appointment.meetingUrl,
                                locationName = appointment.locationName
                            )
                        } else {
                            Log.e("DashboardVM", "Error obteniendo paciente UUID=${appointment.patientProfileUuid}: ${patientResponse.code()}")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("DashboardVM", "Error enriquece cita", e)
                        null
                    }
                }

                _appointmentsDisplay.value = enrichedAppointments

            } catch (e: Exception) {
                Log.e("DashboardVM", "Error al cargar citas del calendario", e)
                _appointmentsDisplay.value = emptyList()
            }
        }
    }
}
