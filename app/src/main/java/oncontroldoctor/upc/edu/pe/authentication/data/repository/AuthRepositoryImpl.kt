package oncontroldoctor.upc.edu.pe.authentication.data.repository

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInWithGoogleRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.toDomain
import oncontroldoctor.upc.edu.pe.authentication.data.remote.AuthService
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService

class AuthRepositoryImpl(
    private val service: AuthService
): AuthRepository, BaseService() {

    override suspend fun signIn(request: SignInRequest): UserSession? {
        return plainCall {
            service.signIn(request)
        }.getOrNull()?.toDomain()
    }

    override suspend fun signUp(request: SignUpRequest): Boolean {
        return plainCall {
            service.signUp(request)
        }.isSuccess
    }

    override suspend fun signInWithGoogle(request: SignInWithGoogleRequest): UserSession?{
        return plainCall {
            service.signInWithGoogle(request)
        }.getOrNull()?.toDomain()

    }

}