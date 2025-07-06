package oncontroldoctor.upc.edu.pe.communication.domain.repository

import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto

interface ChatRepository {
    suspend fun getActivePatients(doctorUuid: String): List<DoctorPatientLinkSimpleDto>
    suspend fun getPatient(patientUuid: String): PatientDto
}