package oncontroldoctor.upc.edu.pe.profile.data.model

data class DoctorProfileRequest(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val documentType: String,
    val documentNumber: String,
    val specialty: String,
    val CMPCode: String,
    val photoUrl: String
)
