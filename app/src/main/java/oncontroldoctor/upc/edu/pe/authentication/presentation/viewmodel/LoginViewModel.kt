package oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.domain.model.UserSession
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInUseCase
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInWithGoogleUseCase

class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
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
            signInUseCase(identifier, password).fold(
                onSuccess = { session ->
                    if (session.role != "ROLE_ADMIN") {
                        errorMessage.value = "This account does not have privileges to access the system"
                    } else {
                        userSession.value = session
                    }
                },
                onFailure = {
                    errorMessage.value = it.message ?: "An error occurred"
                }
            )
            isLoading.value = false
        }
    }
    fun signInWithGoogle(idToken: String){
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken, "ROLE_ADMIN").fold(
                onSuccess = { session ->
                    userSession.value = session
                },
                onFailure = {
                    errorMessage.value = it.message ?: "An error occurred while signing in with Google"
                }
            )
            isLoading.value = false
        }
    }
}
