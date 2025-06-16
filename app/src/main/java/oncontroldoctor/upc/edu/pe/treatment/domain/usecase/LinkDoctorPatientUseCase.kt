package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository

class LinkDoctorPatientUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, request: DoctorPatientLinkRequestDto) =
        repository.linkDoctorPatient(token, request)
}
