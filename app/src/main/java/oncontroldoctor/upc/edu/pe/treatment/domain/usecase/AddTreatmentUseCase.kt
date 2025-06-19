package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.model.TreatmentRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository

class AddTreatmentUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, request: TreatmentRequestDto) =
        repository.addTreatment(token, request)
}
