package oncontroldoctor.upc.edu.pe.authentication.domain.model

data class UserSession(
    val id: Long,
    val username: String,
    val token: String,
    val role: String
)
