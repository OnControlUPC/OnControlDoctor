package oncontroldoctor.upc.edu.pe.communication.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("fileUrl") val fileUrl: String? = null,
    @SerializedName("senderRole") val senderRole: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("doctorUuid") val doctorUuid: String? = null,
    @SerializedName("patientUuid") val patientUuid: String? = null
)