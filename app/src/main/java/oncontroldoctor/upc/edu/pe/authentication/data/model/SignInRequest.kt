package oncontroldoctor.upc.edu.pe.authentication.data.model

data class SignInRequest(
    val identifier: String,
    val password: String
)