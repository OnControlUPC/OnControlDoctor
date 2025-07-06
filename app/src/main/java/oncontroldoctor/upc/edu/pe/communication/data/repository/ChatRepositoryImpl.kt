package oncontroldoctor.upc.edu.pe.communication.data.repository

import oncontroldoctor.upc.edu.pe.communication.data.remote.PatientsLIstService
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto

class ChatRepositoryImpl(
    private val service: PatientsLIstService
): ChatRepository, BaseService(){
    override suspend fun getActivePatients(doctorUuid: String): List<DoctorPatientLinkSimpleDto> {
        val active = authorizedCall { token ->
            service.getPatientsByStatus(token, doctorUuid, "ACTIVE")
        }.getOrElse {
            throw Exception("Error obteniendo pacientes activos: ${it.message}")
        }
        return active
    }
    override suspend fun getPatient(patientUuid: String): PatientDto{
        return authorizedCall { token ->
            service.getPatient(token, patientUuid)
        }.getOrElse {
            throw Exception("Error obteniendo paciente: ${it.message}")
        }
    }
}
