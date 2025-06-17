package oncontroldoctor.upc.edu.pe.treatment.data.repository

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import retrofit2.Response

class TreatmentRepositoryImpl(
    private val service: TreatmentService
) : TreatmentRepository {

    override suspend fun searchPatients(token: String, query: String): Response<List<PatientDto>> {
        return service.searchPatients(token, query)
    }

    override suspend fun linkDoctorPatient(token: String, request: DoctorPatientLinkRequestDto): Response<Unit> {
        return service.linkDoctorPatient(token, request)
    }

    override suspend fun getDoctorPatients(token: String, doctorUuid: String, status: String): Response<List<DoctorPatientLinkDto>> {
        return service.getDoctorPatients(token, doctorUuid, status)
    }
}
