package oncontroldoctor.upc.edu.pe.data.model

data class Message(
    val id: Int,
    val chatId: Int,
    val senderId: Int,
    val content: String,
    val timestamp: String
)
