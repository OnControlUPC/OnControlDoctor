package oncontroldoctor.upc.edu.pe.authentication.domain.usecase

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository

class SignUpUseCase(
    private val repository: AuthRepository
){
    suspend operator fun invoke(username: String, email: String, password:String, role: String): Boolean{
        val request = SignUpRequest(username, email, password, role)
        return repository.signUp(request)
    }
}