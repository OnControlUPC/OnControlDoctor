package oncontroldoctor.upc.edu.pe.authentication.data.repository

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.toDomain
import oncontroldoctor.upc.edu.pe.authentication.data.remote.AuthService
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val service: AuthService
): AuthRepository {
    override suspend fun signIn(request: SignInRequest): UserSession? {
        val response = service.signIn(request)
        return if (response.isSuccessful){
            response.body()?.toDomain()
        } else {
            null
        }
    }

    override suspend fun signUp(request: SignUpRequest): Boolean{
        val response = service.signUp(request)
        return response.isSuccessful
    }

}