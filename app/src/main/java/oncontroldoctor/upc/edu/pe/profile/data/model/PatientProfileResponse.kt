package oncontroldoctor.upc.edu.pe.profile.data.model

data class PatientProfileResponse(
    val uuid: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val birthDate: String?,
    val gender: String?,
    val photoUrl: String?,
    val active: Boolean?
)
