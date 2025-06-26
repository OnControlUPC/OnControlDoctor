package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository

class SearchPatientsUseCase(
    private val repository: TreatmentRepository
) {
    suspend operator fun invoke(name: String): List<PatientDto> {
        return repository.searchPatients(name)
    }
}
