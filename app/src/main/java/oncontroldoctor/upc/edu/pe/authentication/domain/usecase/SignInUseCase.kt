package oncontroldoctor.upc.edu.pe.authentication.domain.usecase

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(identifier: String, password: String): Result<UserSession> {
        return try {
            val session = repository.signIn(SignInRequest(identifier, password))
            if (session != null) {
                Result.success(session)
            } else {
                Result.failure(Exception("Invalid credentials or user not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
