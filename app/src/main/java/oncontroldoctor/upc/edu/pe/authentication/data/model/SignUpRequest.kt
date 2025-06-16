package oncontroldoctor.upc.edu.pe.authentication.data.model

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String
)
