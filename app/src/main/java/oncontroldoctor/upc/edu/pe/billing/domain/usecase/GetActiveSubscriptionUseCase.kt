package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class GetActiveSubscriptionUseCase(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(adminId: Long): Result<Subscription?> {
        return billingRepository.getActiveSubscription(adminId)
    }
}
