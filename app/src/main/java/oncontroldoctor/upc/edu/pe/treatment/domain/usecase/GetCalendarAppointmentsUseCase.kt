package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import android.util.Log
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService

class GetCalendarAppointmentsUseCase(
    private val service: TreatmentService
) {
    suspend operator fun invoke(token: String): List<AppointmentSimpleDto> {
        val response = service.getAppointmentsCalendar(token)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            Log.e("UseCase", "Status: ${response.code()}, Error: ${response.errorBody()?.string()}")
            throw Exception("Error obteniendo citas del calendario")
        }
    }
}
