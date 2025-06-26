package oncontroldoctor.upc.edu.pe.treatment.data.dto

import com.google.gson.annotations.SerializedName

data class DoctorPatientLinkDto(
    @SerializedName("externalId")
    val externalId: String,

    @SerializedName("status")
    val status: String
)
