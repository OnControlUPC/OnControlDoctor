package oncontroldoctor.upc.edu.pe.treatment.domain.repository

import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateProcedureRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment

interface TreatmentRepository {
    suspend fun searchPatients(name: String): List<PatientDto>
    suspend fun checkConnection(doctorUuid: String, patientUuid: String): DoctorPatientLinkDto?
    suspend fun invitePatient(doctorUuid: String, patientUuid: String): Boolean
    suspend fun cancelRequest(externalId: String): Boolean
    suspend fun activateLink(externalId: String): Boolean
    suspend fun getAllPatients(doctorUuid: String): List<DoctorPatientLinkSimpleDto>
    suspend fun getPatientByUuid(uuid: String): PatientDto
    suspend fun getTreatmentsByDoctor(doctorUuid: String): List<Treatment>
    suspend fun createTreatment(
        title: String,
        startDate: String,
        endDate: String,
        doctorProfileUuid: String,
        patientProfileUuid: String
    ): Boolean
    suspend fun getProceduresByTreatmentExternalId(treatmentId: String): List<Procedure>
    suspend fun createProcedure(
        treatmentId: String,
        request: CreateProcedureRequestDto
    ): Boolean
    suspend fun cancelProcedure(procedureId: Long, doctorUuid: String): Boolean
}