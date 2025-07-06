package oncontroldoctor.upc.edu.pe.communication.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("fileUrl") val fileUrl: String? = null,
    @SerializedName("senderRole") val senderRole: String? = null,
    @SerializedName("senderUuid") val senderUuid: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
)