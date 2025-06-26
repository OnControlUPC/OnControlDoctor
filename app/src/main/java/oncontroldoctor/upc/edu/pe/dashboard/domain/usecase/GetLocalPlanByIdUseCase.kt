package oncontroldoctor.upc.edu.pe.dashboard.domain.usecase

import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull
import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository

class GetLocalPlanByIdUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(planId: Long): PlanFull?{
        return repository.getLocalPlanById(planId)
    }
}