package oncontroldoctor.upc.edu.pe.treatment.data.repository

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.TreatmentDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.TreatmentRequestDto
import retrofit2.Response

interface TreatmentRepository {
    suspend fun searchPatients(token: String, query: String): Response<List<PatientDto>>
    suspend fun linkDoctorPatient(token: String, request: DoctorPatientLinkRequestDto): Response<Unit>
    suspend fun getDoctorPatients(token: String, doctorUuid: String, status: String): Response<List<DoctorPatientLinkDto>>
    suspend fun activateLink(token: String, externalId: String): Response<Unit>
    suspend fun deactivateLink(token: String, externalId: String): Response<Unit>
    suspend fun getTreatmentsByPatient(token: String, patientUuid: String): Response<List<TreatmentDto>>
    suspend fun addTreatment(token: String, request: TreatmentRequestDto): Response<Unit>

}
