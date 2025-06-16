package oncontroldoctor.upc.edu.pe.authentication.domain.usecase

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(identifier: String, password: String): UserSession? {
        val request = SignInRequest(identifier, password)
        return repository.signIn(request)
    }
}