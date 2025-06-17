package oncontroldoctor.upc.edu.pe.treatment.domain.usecase

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepository
import retrofit2.Response

class GetDoctorPatientsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(token: String, doctorUuid: String, status: String): Response<List<DoctorPatientLinkDto>> {
        return repository.getDoctorPatients(token, doctorUuid, status)
    }
}
