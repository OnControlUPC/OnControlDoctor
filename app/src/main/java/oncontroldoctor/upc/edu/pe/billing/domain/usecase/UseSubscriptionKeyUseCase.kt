package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import jakarta.inject.Inject
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class UseSubscriptionKeyUseCase(
    private val repository: BillingRepository
) {
    suspend operator fun invoke(token: String, keyId: Long, userId: Long){
        repository.useSubscriptionKey(token, keyId, userId)
    }
}