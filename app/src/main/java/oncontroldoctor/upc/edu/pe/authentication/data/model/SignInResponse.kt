package oncontroldoctor.upc.edu.pe.authentication.data.model

import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession

data class SignInResponse(
    val id: Long?,
    val username: String?,
    val token: String?,
    val role: String?
)
fun SignInResponse.toDomain(): UserSession? {
    return if (id != null && username != null && token != null && role == "ROLE_ADMIN") {
        UserSession(id, username, token, role)
    } else null
}




