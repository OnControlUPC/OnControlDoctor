package oncontroldoctor.upc.edu.pe.treatment.data.model

data class Treatment(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val externalId: String,
    val title: Title,
    val period: Period,
    val status: String,
    val doctorProfileUuid: String,
    val patientProfileUuid: String
)

data class Title(val value: String)
data class Period(val startDate: String, val endDate: String)
