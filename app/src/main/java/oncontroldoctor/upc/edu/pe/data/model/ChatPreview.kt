package oncontroldoctor.upc.edu.pe.data.model

data class ChatPreview (
    val id: Int,
    val patientName: String,
    val patientAvatarUrl: String?,
    val lastMessage: String,
    val lastMessageTime: String
)