package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository

class ActivatePatientLinkUseCase(
    private val repository: TreatmentRepository
) {
    suspend operator fun invoke(externalId: String): Boolean {
        return repository.activateLink(externalId)
    }
}
