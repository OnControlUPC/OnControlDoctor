package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class CreateAppointmentDto(
    val patientProfileUuid: String,
    val scheduledAt: String,
    val locationName: String?,
    val locationMapsUrl: String?,
    val meetingUrl: String?
)