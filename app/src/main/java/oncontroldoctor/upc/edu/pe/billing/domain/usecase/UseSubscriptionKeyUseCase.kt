package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class UseSubscriptionKeyUseCase(
    private val repository: BillingRepository
) {
    suspend operator fun invoke(keyId: Long, userId: Long): Result<Unit> {
        return repository.useSubscriptionKey(keyId, userId)
    }
}
