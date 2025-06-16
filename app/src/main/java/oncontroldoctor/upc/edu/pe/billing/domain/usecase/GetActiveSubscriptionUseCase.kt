package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import jakarta.inject.Inject
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class GetActiveSubscriptionUseCase(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(token: String, adminId: Long): Subscription? {
        return billingRepository.getActiveSubscription(token, adminId)
    }
}