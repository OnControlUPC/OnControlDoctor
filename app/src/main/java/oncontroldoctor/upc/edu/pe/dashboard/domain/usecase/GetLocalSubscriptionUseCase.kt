package oncontroldoctor.upc.edu.pe.dashboard.domain.usecase

import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository

class GetLocalSubscriptionUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): SubscriptionEntity?{
        return repository.getLocalSubscription()
    }
}