package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository

class InvitePatientUseCase(
    private val repository: TreatmentRepository
) {
    suspend operator fun invoke(doctorUuid: String, patientUuid: String): Boolean {
        return repository.invitePatient(doctorUuid, patientUuid)
    }
}
