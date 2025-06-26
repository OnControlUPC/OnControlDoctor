package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class ValidateSubscriptionKeyUseCase(
    private val repository: BillingRepository
) {
    suspend operator fun invoke(code: String): Result<SubscriptionKey> {
        return repository.getSubscriptionKey(code)
    }
}
