package oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<DoctorProfile?>(null)
    val profileState: StateFlow<DoctorProfile?> = _profileState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val uuid = SessionHolder.getUserUuid()
            if (uuid != null) {
                val result = repository.getDoctorProfile(uuid)
                _profileState.value = result.getOrNull()
            }
            _isLoading.value = false
        }
    }
}

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}
