package oncontroldoctor.upc.edu.pe.authentication.data.model

data class SignInWithGoogleRequest(
    val idToken: String,
    val role: String
)
