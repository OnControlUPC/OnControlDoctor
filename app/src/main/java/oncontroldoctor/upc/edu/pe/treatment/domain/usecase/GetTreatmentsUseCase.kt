package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository

class GetTreatmentsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, patientUuid: String) =
        repository.getTreatmentsByPatient(token, patientUuid)
}
