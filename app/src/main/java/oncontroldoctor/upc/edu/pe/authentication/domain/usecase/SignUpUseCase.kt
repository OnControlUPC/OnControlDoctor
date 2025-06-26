package oncontroldoctor.upc.edu.pe.authentication.domain.usecase

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: SignUpRequest): Result<Unit> {
        return try {
            val success = repository.signUp(request)
            if (success) {
                Result.success(Unit)
            } else {

                Result.failure(Exception("Cannot register user. Please check the data. ${success}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
