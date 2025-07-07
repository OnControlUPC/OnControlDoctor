package oncontroldoctor.upc.edu.pe.treatment.presentation.model

import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto

data class PatientConnectionState(
    val patient: PatientDto,
    val connectionStatus: ConnectionStatus?,
    val externalId: String? = null,
    val lastMessagePreview: String? = null
){
    companion object {
        fun from(patient: PatientDto, link: DoctorPatientLinkDto?): PatientConnectionState {
            val status = when (link?.status) {
                "ACTIVE" -> ConnectionStatus.ACTIVE
                "ACCEPTED" -> ConnectionStatus.ACCEPTED
                "DISABLED" -> ConnectionStatus.DISABLED
                "PENDING" -> ConnectionStatus.PENDING
                "REJECTED" -> ConnectionStatus.REJECTED
                "DELETED" -> ConnectionStatus.DELETED
                else -> ConnectionStatus.NONE
            }

            return PatientConnectionState(
                patient = patient,
                connectionStatus = status,
                externalId = link?.externalId
            )
        }
    }

}


enum class ConnectionStatus {
    ACTIVE,
    ACCEPTED,
    DISABLED,
    PENDING,
    REJECTED,
    DELETED,
    NONE
}



