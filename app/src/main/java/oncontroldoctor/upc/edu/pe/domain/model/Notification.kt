package oncontroldoctor.upc.edu.pe.domain.model

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean,
)
