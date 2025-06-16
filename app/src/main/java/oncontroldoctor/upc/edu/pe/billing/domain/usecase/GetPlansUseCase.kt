package oncontroldoctor.upc.edu.pe.billing.domain.usecase

import jakarta.inject.Inject
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository

class GetPlansUseCase(
    private val repository: BillingRepository
) {
    suspend operator fun invoke(token: String): List<Plan>{
        return repository.getPlans(token)
    }
}