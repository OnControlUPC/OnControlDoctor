package oncontroldoctor.upc.edu.pe.communication.domain.repository

import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto

interface CommunicationRepository {
    suspend fun getAllPatientsActive(doctorUuid: String): List<DoctorPatientLinkSimpleDto>

}