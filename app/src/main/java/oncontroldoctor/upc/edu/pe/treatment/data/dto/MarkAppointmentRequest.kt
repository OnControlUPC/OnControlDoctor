package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class MarkAppointmentRequest(
    val appointmentId: Long,
    val doctorProfileUuid: String,
    val newStatus: String
)