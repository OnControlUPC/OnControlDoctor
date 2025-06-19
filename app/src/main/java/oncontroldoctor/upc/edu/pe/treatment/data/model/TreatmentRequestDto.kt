package oncontroldoctor.upc.edu.pe.treatment.data.model

data class TreatmentRequestDto(
    val title: String,
    val startDate: String,
    val endDate: String,
    val doctorProfileUuid: String,
    val patientProfileUuid: String
)
