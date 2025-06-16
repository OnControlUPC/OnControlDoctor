package oncontroldoctor.upc.edu.pe.treatment.data.model

data class DoctorPatientLinkDto(
    val externalId: String,
    val doctorUuid: String,
    val patientUuid: String,
    val doctorFullName: String,
    val patientFullName: String,
    val status: String,
    val createdAt: String,
    val disabledAt: String?
)
