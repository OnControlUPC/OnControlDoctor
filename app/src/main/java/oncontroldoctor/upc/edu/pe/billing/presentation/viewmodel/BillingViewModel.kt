package oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetActiveSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetPlansUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.UseSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.ValidateSubscriptionKeyUseCase


class BillingViewModel(
    private val getPlansUseCase: GetPlansUseCase,
    private val validateKeyUseCase: ValidateSubscriptionKeyUseCase,
    private val useKeyUseCase: UseSubscriptionKeyUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase
) : ViewModel() {

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Loading)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    private val _uiState = MutableStateFlow<BillingUiState>(BillingUiState.Loading)
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    private val _keyState = MutableStateFlow<KeyValidationState>(KeyValidationState.Idle)
    val keyState: StateFlow<KeyValidationState> = _keyState.asStateFlow()

    private var validatedKey: SubscriptionKey? = null

    fun checkActiveSubscription(token: String, adminId: Long){
        viewModelScope.launch {
            _subscriptionState.value = SubscriptionState.Loading
            try{
                val subscription = getActiveSubscriptionUseCase(token, adminId)
                if(subscription != null) {
                    _subscriptionState.value = SubscriptionState.Active(subscription)
                } else {
                    _subscriptionState.value = SubscriptionState.NoActiveSubscription
                }
            } catch (e: Exception){
                _subscriptionState.value = SubscriptionState.Error("Error cheking active subscription: ${e.message}")
            }
        }
    }

    fun loadPlans(token: String) {
        viewModelScope.launch {
            _uiState.value = BillingUiState.Loading
            try {
                val plans = getPlansUseCase(token)
                _uiState.value = BillingUiState.PlansLoaded(plans)
            } catch (e: Exception) {
                _uiState.value = BillingUiState.Error(e.message ?: "Error al cargar planes")
            }
        }
    }

    fun validateKey(token: String, code: String) {
        viewModelScope.launch {
            _keyState.value = KeyValidationState.Loading
            try {
                val key = validateKeyUseCase(token, code)
                validatedKey = key
                _keyState.value = KeyValidationState.Valid(key)
            } catch (e: Exception) {
                _keyState.value = KeyValidationState.Invalid(e.message ?: "Key inválida")
            }
        }
    }

    fun redeemKey(token: String, userId: Long) {
        validatedKey?.let { key ->
            viewModelScope.launch {
                _keyState.value = KeyValidationState.Loading
                try {
                    useKeyUseCase(token, key.id, userId)
                    _keyState.value = KeyValidationState.Redeemed
                } catch (e: Exception) {
                    _keyState.value = KeyValidationState.Invalid("Error al canjear: ${e.message}")
                }
            }
        }
    }

    fun resetKeyState() {
        _keyState.value = KeyValidationState.Idle
        validatedKey = null
    }
}

sealed class BillingUiState {
    object Loading : BillingUiState()
    data class PlansLoaded(val plans: List<Plan>) : BillingUiState()
    data class Error(val message: String) : BillingUiState()
}

sealed class KeyValidationState {
    object Idle : KeyValidationState()
    object Loading : KeyValidationState()
    data class Valid(val key: SubscriptionKey) : KeyValidationState()
    data class Invalid(val error: String) : KeyValidationState()
    object Redeemed : KeyValidationState()
}

sealed class SubscriptionState {
    object Loading: SubscriptionState()
    object NoActiveSubscription : SubscriptionState()
    data class Active(val subscription: Subscription) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()

}