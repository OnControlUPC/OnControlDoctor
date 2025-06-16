package oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInUseCase

class LoginViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    var userSession = mutableStateOf<UserSession?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    fun login(identifier : String, password: String){
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val session = signInUseCase(identifier, password)
                if(session != null){
                    if(session.role != "ROLE_ADMIN"){
                        errorMessage.value = "This account does not have privileges access the system"
                    } else{
                        userSession.value = session
                    }
                } else {
                    errorMessage.value = "Invalid credentials"
                }
            } catch (e: Exception){
                errorMessage.value = e.message ?: "An error occurred"
            } finally {
                isLoading.value = false
            }
        }

    }

}