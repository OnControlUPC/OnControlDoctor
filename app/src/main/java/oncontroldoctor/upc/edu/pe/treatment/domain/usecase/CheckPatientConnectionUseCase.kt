package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository

class CheckPatientConnectionUseCase(
    private val repository: TreatmentRepository
) {
    suspend operator fun invoke(doctorUuid: String, patientUuid: String): DoctorPatientLinkDto? {
        return repository.checkConnection(doctorUuid, patientUuid)
    }
}
