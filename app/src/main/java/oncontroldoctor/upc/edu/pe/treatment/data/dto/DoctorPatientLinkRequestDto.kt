package oncontroldoctor.upc.edu.pe.treatment.data.dto

import com.google.gson.annotations.SerializedName

data class DoctorPatientLinkRequestDto(
    @SerializedName("doctorUuid")
    val doctorUuid: String,

    @SerializedName("patientUuid")
    val patientUuid: String
)
