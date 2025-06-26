package oncontroldoctor.upc.edu.pe.treatment.data.dto

import oncontroldoctor.upc.edu.pe.treatment.presentation.model.ConnectionStatus
import oncontroldoctor.upc.edu.pe.treatment.presentation.model.PatientConnectionState


fun DoctorPatientLinkSimpleDto.toPatientConnectionState(): PatientConnectionState {
    return PatientConnectionState(
        patient = PatientDto(
            uuid = this.patientUuid,
            firstName = this.patientFullName,
            lastName = "",
            email = "",
            phoneNumber = "",
            birthDate = "",
            gender = "",
            photoUrl = "",
            active = true
        ),
        connectionStatus = ConnectionStatus.valueOf(this.status),
        externalId = this.externalId
    )
}