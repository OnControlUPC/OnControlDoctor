package oncontroldoctor.upc.edu.pe.authentication.domain.usecase

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInWithGoogleRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, role: String): Result<UserSession>{
        return try{
            val session = repository.signInWithGoogle(SignInWithGoogleRequest(idToken, role))
            if (session != null){
                Result.success(session)
            } else {
                Result.failure(Exception("Cannot sign in with Google. User not found or invalid role."))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}