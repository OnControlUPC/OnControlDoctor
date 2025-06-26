package oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.profile.data.local.ProfileHolder
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.CreateDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorUuidUseCase
class CompleteProfileViewModel(
    private val getDoctorUuidUseCase: GetDoctorUuidUseCase,
    private val getDoctorProfileUseCase: GetDoctorProfileUseCase,
    private val createDoctorProfileUseCase: CreateDoctorProfileUseCase
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object ShouldCompleteProfile : UiState()
        data class ProfileLoaded(val uuid: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _profileCreationState = MutableStateFlow<Result<Unit>?>(null)
    val profileCreationState = _profileCreationState.asStateFlow()


    fun checkProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            getDoctorUuidUseCase().fold(
                onSuccess = { uuid ->
                    if (uuid == null) {
                        _uiState.value = UiState.ShouldCompleteProfile
                        return@fold
                    }

                    getDoctorProfileUseCase(uuid).fold(
                        onSuccess = { profile ->
                            if (profile == null) {
                                _uiState.value = UiState.ShouldCompleteProfile
                            } else if (!profile.active) {
                                _uiState.value = UiState.Error("Your account is not active. Please contact support.")
                            } else {
                                ProfileHolder.doctorProfile = profile
                                _uiState.value = UiState.ProfileLoaded(profile.uuid)
                            }
                        },
                        onFailure = {
                            _uiState.value = UiState.Error("Error getting profile: ${it.message}")
                        }
                    )
                },
                onFailure = {
                    _uiState.value = UiState.Error("Error getting UUID: ${it.message}")
                }
            )
        }
    }

    fun createProfile(request: DoctorProfileRequest) {
        viewModelScope.launch {
            val result = createDoctorProfileUseCase(request)
            _profileCreationState.value = result
        }
    }
}
