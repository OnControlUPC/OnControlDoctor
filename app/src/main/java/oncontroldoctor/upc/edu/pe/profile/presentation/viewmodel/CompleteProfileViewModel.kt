package oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.CreateDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorUuidUseCase

class CompleteProfileViewModel(
    private val getDoctorUuidUseCase: GetDoctorUuidUseCase,
    private val getDoctorProfileUseCase: GetDoctorProfileUseCase,
    private val createDoctorProfileUseCase: CreateDoctorProfileUseCase
): ViewModel() {
    sealed class UiState {
        object Loading : UiState()
        object ShouldCompleteProfile : UiState()
        data class ProfileLoaded(val uuid: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val profileCreationState = mutableStateOf<Boolean?>(null)

    val uiState = _uiState.asStateFlow()

    fun checkProfile(token: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val uuid = getDoctorUuidUseCase(token)

                if (uuid == null) {
                    _uiState.value = UiState.ShouldCompleteProfile
                    return@launch
                }

                val profile = getDoctorProfileUseCase(uuid, token)

                if (profile == null) {
                    _uiState.value = UiState.ShouldCompleteProfile
                    return@launch
                }

                if (!profile.active) {
                    _uiState.value = UiState.Error("Tu cuenta ha sido desactivada.")
                    return@launch
                }

                _uiState.value = UiState.ProfileLoaded(profile.uuid)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error inesperado: ${e.message}")
            }
        }
    }
    fun createProfile(token: String, request: DoctorProfileRequest){
        viewModelScope.launch{
            try{
                val success = createDoctorProfileUseCase(token, request)
                profileCreationState.value = success
            }catch (e: Exception){
                profileCreationState.value = false
            }
        }
    }

}