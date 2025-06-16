package oncontroldoctor.upc.edu.pe.treatment.domain.model

data class Patient(
    val uuid: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val birthDate: String,
    val gender: String,
    val photoUrl: String,
    val active: Boolean
)