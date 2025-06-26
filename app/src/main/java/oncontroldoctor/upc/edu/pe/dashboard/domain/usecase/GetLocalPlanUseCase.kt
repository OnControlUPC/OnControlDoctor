package oncontroldoctor.upc.edu.pe.dashboard.domain.usecase

import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull
import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository

class GetLocalPlanUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): PlanFull?{
        return repository.getLocalPlan()
    }
}