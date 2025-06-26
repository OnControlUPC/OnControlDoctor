package oncontroldoctor.upc.edu.pe.treatment.data.dto

import com.google.gson.annotations.SerializedName

data class PatientDto(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("birthDate")
    val birthDate: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("active")
    val active: Boolean
)