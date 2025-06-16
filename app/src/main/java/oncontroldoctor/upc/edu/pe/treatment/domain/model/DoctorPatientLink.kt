package oncontroldoctor.upc.edu.pe.treatment.domain.model

data class DoctorPatientLink(
    val externalId: String,
    val doctorUuid: String,
    val patientUuid: String,
    val doctorFullName: String,
    val patientFullName: String,
    val status: String,
    val createdAt: String,
    val disabledAt: String?
)