package oncontroldoctor.upc.edu.pe.communication.data.repository

import oncontroldoctor.upc.edu.pe.communication.domain.repository.CommunicationRepository
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService

class CommunicationRepositoryImpl(
    private val service: TreatmentService
): CommunicationRepository, BaseService(){
    override suspend fun getAllPatientsActive(doctorUuid: String): List<DoctorPatientLinkSimpleDto> {
        val active = authorizedCall { token ->
            service.getPatientsByStatus(token, doctorUuid, "ACTIVE")
        }.getOrElse {
            throw Exception("Error getting active patients: ${it.message}")
        }
        return active
    }

}

