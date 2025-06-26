package oncontroldoctor.upc.edu.pe.dashboard.domain.usecase

import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository

class SyncSubscriptionAndPlanUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(adminId: Long){
        repository.syncSubscriptionAndPlan(adminId)
    }
}