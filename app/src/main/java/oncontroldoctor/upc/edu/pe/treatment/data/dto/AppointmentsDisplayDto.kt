package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class AppointmentDisplayDto(
    val id: Long,
    val scheduledAt: String,
    val patientName: String,
    val meetingUrl: String?,
    val locationName: String?
)
