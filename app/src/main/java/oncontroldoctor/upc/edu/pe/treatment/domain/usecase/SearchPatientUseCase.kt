package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository

class SearchPatientsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, query: String) = repository.searchPatients(token, query)
}
