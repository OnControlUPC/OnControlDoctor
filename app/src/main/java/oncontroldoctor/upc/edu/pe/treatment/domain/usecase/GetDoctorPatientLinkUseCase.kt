package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository

class GetDoctorPatientsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, doctorUuid: String) =
        repository.getDoctorPatients(token, doctorUuid)
}
