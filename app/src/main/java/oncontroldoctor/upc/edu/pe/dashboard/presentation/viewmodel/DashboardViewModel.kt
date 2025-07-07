package oncontroldoctor.upc.edu.pe.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalPlanUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.SyncSubscriptionAndPlanUseCase
import oncontroldoctor.upc.edu.pe.profile.data.local.ProfileHolder
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile


class DashboardViewModel(
    private val syncSubscriptionAndPlanUseCase: SyncSubscriptionAndPlanUseCase,
    private val getLocalSubscriptionUseCase: GetLocalSubscriptionUseCase,
    private val getLocalPlanUseCase: GetLocalPlanUseCase
) : ViewModel() {
    sealed class UiState {
        object Loading : UiState()
        data class Ready(
            val subscription: SubscriptionEntity,
            val plan: PlanFull
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadDashboard(adminId: Long){
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                syncSubscriptionAndPlanUseCase(adminId)

                val subscription = getLocalSubscriptionUseCase()
                val plan = getLocalPlanUseCase()

                if(subscription != null && plan != null){
                    _uiState.value = UiState.Ready(subscription, plan)
                } else {
                    _uiState.value = UiState.Error("Cannot load subscription or plan")
                }
            } catch (e: Exception){
                _uiState.value = UiState.Error("Error ${e.message}")
            }
        }
    }
    val planState = MutableStateFlow<PlanFull?>(null)
    fun loadLocalPlan(){
        viewModelScope.launch {
            val plan = getLocalPlanUseCase()
            planState.value = plan
        }
    }

    // DashboardViewModel.kt
    private val _profile = MutableStateFlow<DoctorProfile?>(null)
    val profile = _profile.asStateFlow()

    fun loadProfile() {
        _profile.value = ProfileHolder.doctorProfile
    }
}