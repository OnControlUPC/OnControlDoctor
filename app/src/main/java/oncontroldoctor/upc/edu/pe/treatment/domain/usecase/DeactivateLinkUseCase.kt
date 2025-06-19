package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository
import retrofit2.Response

class DeactivateLinkUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, externalId: String): Response<Unit> {
        return repository.deactivateLink(token, externalId)
    }
}
