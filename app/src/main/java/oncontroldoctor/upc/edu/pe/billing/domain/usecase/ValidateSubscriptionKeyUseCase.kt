package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import jakarta.inject.Inject
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class ValidateSubscriptionKeyUseCase(
    private val repository: BillingRepository
) {
    suspend operator fun invoke(token: String, code: String): SubscriptionKey{
        return repository.getSubscriptionKey(token, code)
    }
}