package oncontroldoctor.upc.edu.pe.treatment.data.model

data class TreatmentDto(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val externalId: String,
    val title: TitleWrapper,
    val period: PeriodWrapper,
    val status: String,
    val doctorProfileUuid: String,
    val patientProfileUuid: String
)

data class TitleWrapper(val value: String)
data class PeriodWrapper(val startDate: String, val endDate: String)
