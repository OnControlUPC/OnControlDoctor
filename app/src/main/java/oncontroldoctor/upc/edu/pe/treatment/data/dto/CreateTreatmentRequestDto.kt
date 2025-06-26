package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class CreateTreatmentRequestDto(
    val title: String,
    val startDate: String,
    val endDate: String,
    val doctorProfileUuid: String,
    val patientProfileUuid: String
)
