package oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
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

    private val _hasSubscription = MutableStateFlow(false)
    val hasSubscription: StateFlow<Boolean> = _hasSubscription

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    var plans by mutableStateOf<List<Plan>>(emptyList())
        private set

    var keyValidationMessage by mutableStateOf<String?>(null)
        private set

    private var validatedKey: SubscriptionKey? = null

    private val _keyState = MutableStateFlow<KeyValidationState>(KeyValidationState.Idle)
    val keyState: StateFlow<KeyValidationState> = _keyState.asStateFlow()


    fun checkSubscription() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val adminId = SessionHolder.getUserId()
            if (adminId == null) {
                _errorMessage.value = "Admin ID not found"
                _isLoading.value = false
                _hasSubscription.value = false
                return@launch
            }
            Log.d("SUBSCRIPTION", "Consultando suscripción para ID: $adminId")

            getActiveSubscriptionUseCase(adminId).fold(
                onSuccess = { subscription ->
                    if (subscription != null && subscription.status == "ACTIVE") {
                        SessionHolder.savePlanId(subscription.planId)
                        _hasSubscription.value = true
                        Log.d("BILLING", "Subs activa encontrada $subscription")
                    } else {
                        _hasSubscription.value = false
                        Log.d("BILLING", "Subs no activa o null")
                    }
                    _isLoading.value = false
                },
                onFailure = {
                    if ((it as? retrofit2.HttpException)?.code() == 404) {
                        Log.w("BILLING", "No hay suscripción activa (404)")
                        _hasSubscription.value = false
                    } else {
                        Log.e("BILLING", "Error inesperado al consultar: ${it.message}")
                        _errorMessage.value = "Error al consultar suscripción: ${it.message}"
                    }
                    _isLoading.value = false
                }
            )
        }
    }

    fun loadPlans() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            getPlansUseCase().fold(
                onSuccess = {
                    plans = it
                },
                onFailure = {
                    _errorMessage.value = "Error al cargar planes: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun validateKey(code: String) {
        viewModelScope.launch {
            _keyState.value = KeyValidationState.Loading
            validateKeyUseCase(code).fold(
                onSuccess = { key ->
                    _keyState.value = KeyValidationState.Valid(key)
                    validatedKey = key
                },
                onFailure = { e ->
                    _keyState.value = KeyValidationState.Invalid(e.message ?: "Error validating key")
                }
            )
        }
    }


    fun redeemKey(onSuccess: () -> Unit) {
        val key = validatedKey ?: return
        viewModelScope.launch {
            useKeyUseCase(key.id, SessionHolder.getUserId() ?: return@launch).fold(
                onSuccess = {
                    keyValidationMessage = "Suscripción activada con éxito"
                    onSuccess()
                },
                onFailure = {
                    keyValidationMessage = "Error al canjear clave: ${it.message}"
                }
            )
        }
    }
}
sealed class KeyValidationState {
    object Idle : KeyValidationState()
    object Loading : KeyValidationState()
    data class Valid(val key: SubscriptionKey) : KeyValidationState()
    data class Invalid(val error: String) : KeyValidationState()
    object Redeemed : KeyValidationState()
}


