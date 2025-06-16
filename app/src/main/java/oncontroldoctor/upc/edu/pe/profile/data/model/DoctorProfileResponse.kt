package oncontroldoctor.upc.edu.pe.profile.data.model

import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile

data class DoctorProfileResponse(
    val uuid: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val documentType: String?,
    val documentNumber: String?,
    val specialty: String?,
    val CMPCode: String?,
    val urlPhoto: String?,
    val active: Boolean?
)

fun DoctorProfileResponse.toDomain(): DoctorProfile?{
    if(
        uuid == null || firstName == null || lastName == null ||
        email == null || phoneNumber == null || documentType == null ||
        documentNumber == null || specialty == null || CMPCode == null ||
        urlPhoto == null || active == null
    ){
        return null
    }

    return DoctorProfile(
        uuid = uuid,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        documentType = documentType,
        documentNumber = documentNumber,
        specialty = specialty,
        cmpCode = CMPCode,
        urlPhoto = urlPhoto,
        active = active
    )

}