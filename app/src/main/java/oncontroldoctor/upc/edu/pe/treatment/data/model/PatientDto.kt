package oncontroldoctor.upc.edu.pe.treatment.data.model

data class PatientDto(
    val uuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val birthDate: String,
    val gender: String,
    val photoUrl: String,
    val active: Boolean
)
