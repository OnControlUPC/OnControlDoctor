package oncontroldoctor.upc.edu.pe.profile.domain.model

data class DoctorProfile(
    val uuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val documentType: String,
    val documentNumber: String,
    val specialty: String,
    val cmpCode: String,
    val urlPhoto: String,
    val active: Boolean
)
