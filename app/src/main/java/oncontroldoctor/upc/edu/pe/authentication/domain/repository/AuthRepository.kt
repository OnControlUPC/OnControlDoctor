package oncontroldoctor.upc.edu.pe.authentication.domain.repository

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInWithGoogleRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession

interface AuthRepository {
    suspend fun signIn(request: SignInRequest): UserSession?
    suspend fun signUp(request: SignUpRequest): Boolean
    suspend fun signInWithGoogle(request: SignInWithGoogleRequest): UserSession?
}