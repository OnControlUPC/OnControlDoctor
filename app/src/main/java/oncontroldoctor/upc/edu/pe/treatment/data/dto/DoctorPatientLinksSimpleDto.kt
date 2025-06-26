package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class DoctorPatientLinkSimpleDto(
    val externalId: String,
    val doctorUuid: String,
    val patientUuid: String,
    val doctorFullName: String,
    val patientFullName: String,
    val status: String,
    val createdAt: String,
    val disabledAt: String?
){
}