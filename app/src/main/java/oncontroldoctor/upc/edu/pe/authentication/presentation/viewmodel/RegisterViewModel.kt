package oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignUpUseCase

class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    var isLoading = mutableStateOf(false)
        private set
    var success = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun register(request: SignUpRequest) {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            signUpUseCase(request).fold(
                onSuccess = {
                    success.value = true
                },
                onFailure = {
                    errorMessage.value = it.message ?: "Registration failed. Please try again."
                }
            )
            isLoading.value = false
        }
    }
    fun setError(message: String) {
        errorMessage.value = message
    }

}
