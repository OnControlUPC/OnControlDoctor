package oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInUseCase
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignUpUseCase

class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase
): ViewModel() {
    var userSession = mutableStateOf<UserSession?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    fun register(username: String, email:String, password: String, role: String = "ROLE_ADMIN"){
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try{
                val registered = signUpUseCase(username, email, password, role)
                if(registered){
                    val session = signInUseCase(email, password)
                    if(session!=null){
                        userSession.value = session
                    } else {
                        errorMessage.value = "Registration successful, but automatic login failed."
                    }
                } else {
                    errorMessage.value = "Error registering. Please check the data."
                }
            } catch (e: Exception){
                errorMessage.value = e.message ?: "Unexpected error."
            } finally {
                isLoading.value = false
            }
        }

    }
}