package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class AppointmentSimpleDto(
    val id: Long,
    val scheduledAt: String,
    val status: String,
    val locationName: String?,
    val locationMapsUrl: String?,
    val meetingUrl: String?,
    val patientProfileUuid: String
)