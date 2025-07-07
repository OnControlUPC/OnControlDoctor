package oncontroldoctor.upc.edu.pe.communication.domain.repository

import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto

interface ChatRepository {
    suspend fun getActivePatients(doctorUuid: String): List<DoctorPatientLinkSimpleDto>
    suspend fun getPatient(patientUuid: String): PatientDto
    suspend fun getConversation(doctorUuid: String, patientUuid: String, page: Int, size: Int): List<ChatMessage>
    suspend fun getSymptomLog(symptomId: Long): SymptomDto
}