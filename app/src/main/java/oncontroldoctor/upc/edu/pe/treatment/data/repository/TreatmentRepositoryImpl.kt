package oncontroldoctor.upc.edu.pe.treatment.data.repository

import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateAppointmentDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateProcedureRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateTreatmentRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.MarkAppointmentRequest
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.ProcedureCalendarDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import retrofit2.HttpException
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TreatmentRepositoryImpl(
    private val service: TreatmentService
) : TreatmentRepository, BaseService() {

    override suspend fun searchPatients(name: String): List<PatientDto> {
        return authorizedCall { token ->
            service.searchPatients(token, name)
        }.getOrElse {
            throw Exception("Error buscando pacientes: ${it.message}")
        }
    }

    override suspend fun checkConnection(doctorUuid: String, patientUuid: String): DoctorPatientLinkDto? {
        return authorizedCall { token ->
            service.getDoctorPatientLink(token, doctorUuid, patientUuid)
        }.fold(
            onSuccess = { it },
            onFailure = {
                if ((it as? HttpException)?.code() == 404) null
                else throw Exception("Error al verificar vínculo: ${it.message}")
            }
        )
    }

    override suspend fun invitePatient(doctorUuid: String, patientUuid: String): Boolean {
        val request = DoctorPatientLinkRequestDto(doctorUuid, patientUuid)
        return authorizedCall { token ->
            service.invitePatient(token, request)
        }.isSuccess
    }

    override suspend fun cancelRequest(externalId: String): Boolean {
        return authorizedCall { token ->
            service.cancelRequest(token, externalId)
        }.isSuccess
    }

    override suspend fun activateLink(externalId: String): Boolean {
        return authorizedCall { token ->
            service.activatePatient(token, externalId)
        }.isSuccess
    }

    override suspend fun getAllPatients(doctorUuid: String): List<DoctorPatientLinkSimpleDto> {
        val active = authorizedCall { token ->
            service.getPatientsByStatus(token, doctorUuid, "ACTIVE")
        }.getOrElse {
            throw Exception("Error getting active patients: ${it.message}")
        }

        val accepted = authorizedCall { token ->
            service.getPatientsByStatus(token, doctorUuid, "ACCEPTED")
        }.getOrElse {
            throw Exception("Error getting accepted patients: ${it.message}")
        }

        val disabled = authorizedCall { token ->
            service.getPatientsByStatus(token, doctorUuid, "DISABLED")
        }.getOrElse {
            throw Exception("Error getting disabled patients: ${it.message}")
        }

        return active + accepted + disabled
    }


    override suspend fun getPatientByUuid(uuid: String): PatientDto {
        return authorizedCall { token ->
            service.getPatientByUuid(token, uuid)
        }.getOrElse {
            throw Exception("Error obteniendo paciente: ${it.message}")
        }
    }

    override suspend fun getTreatmentsByDoctor(doctorUuid: String): List<Treatment> {
        return authorizedCall { token ->
            service.getTreatmentByProfileUuid(token, doctorUuid)
        }.getOrElse {
            throw Exception("Error obteniendo tratamientos: ${it.message}")
        }
    }

    override suspend fun getTreatmentsByPatient(patientUuid: String): List<Treatment>{
        return authorizedCall { token->
            service.getTreatmentsByPatientUuid(token, patientUuid)
        }.getOrElse {
            throw Exception("Error obteniendo tratamiento por paciente: ${it.message}")
        }
    }

    override suspend fun createTreatment(
        title: String,
        startDate: String,
        endDate: String,
        doctorProfileUuid: String,
        patientProfileUuid: String
    ): Boolean {
        val request = CreateTreatmentRequestDto(
            title = title,
            startDate = startDate,
            endDate = endDate,
            doctorProfileUuid = doctorProfileUuid,
            patientProfileUuid = patientProfileUuid
        )
        return authorizedCall { token ->
            service.createTreatment(token, request)
        }.isSuccess
    }

    override suspend fun getProceduresByTreatmentExternalId(treatmentId: String): List<Procedure>{
        return authorizedCall { token ->
            service.getProceduresByTreatmentExternalId(token, treatmentId)
        }.getOrElse {
            throw Exception("Error obteniendo procedimientos: ${it.message}")
        }
    }

    override suspend fun createProcedure(
        treatmentId: String,
        request: CreateProcedureRequestDto
    ): Boolean {
        return authorizedCall { token ->
            service.createProcedure(token, treatmentId, request)
        }.isSuccess
    }

    override suspend fun cancelProcedure(
        procedureId: Long,
        doctorUuid: String
    ): Boolean {
        return authorizedCall { token ->
            service.cancelProcedure(token, procedureId, doctorUuid)
        }.isSuccess
    }

    override suspend fun getSymptomsByPatientUuid(
        patientUuid: String,
        from: String,
        to: String
    ): List<SymptomDto> {
        val fromUtc = convertLimaToUtc(from)
        val toUtc = convertLimaToUtc(to)
        return authorizedCall { token ->
            service.getSymptomLogsByPatient(token, patientUuid, fromUtc, toUtc)
        }.getOrElse {
            throw Exception("Error obteniendo síntomas: ${it.message}")
        }.map { dto ->
            dto.copy(loggedAt = convertUtcToLima(dto.loggedAt))
        }
    }

    fun convertLimaToUtc(limaDateTime: String): String {
        val localDateTime = java.time.LocalDateTime.parse(limaDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val limaZoned = localDateTime.atZone(ZoneId.of("America/Lima"))
        val utcZoned = limaZoned.withZoneSameInstant(ZoneId.of("UTC"))
        return utcZoned.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun convertUtcToLima(utcDateTime: String): String {
        val localDateTime = java.time.LocalDateTime.parse(utcDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val utcZoned = localDateTime.atZone(ZoneId.of("UTC"))
        val limaZoned = utcZoned.withZoneSameInstant(ZoneId.of("America/Lima"))
        return limaZoned.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    override suspend fun getAppointments(patientUuid: String): List<AppointmentSimpleDto> {
        return authorizedCall { token ->
            service.getAppointments(token, patientUuid)
        }.getOrElse {
            throw Exception("Error obteniendo citas: ${it.message}")
        }.map { dto ->
            dto.copy(scheduledAt = convertUtcToLima(dto.scheduledAt))
        }
    }

    override suspend fun cancelAppointment(appointmentId: Long): Boolean {
        return authorizedCall { token ->
            service.cancelAppointment(token, appointmentId)
        }.isSuccess
    }

    override suspend fun markAppointment(markAppointmentRequest: MarkAppointmentRequest): Boolean {
        return authorizedCall { token ->
            service.markAppointment(token, markAppointmentRequest)
        }.isSuccess
    }

    override suspend fun createAppointment(request: CreateAppointmentDto): Boolean {
        return authorizedCall { token ->
            service.createAppointment(token, request)
        }.isSuccess
    }

    override suspend fun getProcedureCalendar(externalId: String): List<ProcedureCalendarDto> {
        return authorizedCall { token ->
            service.getProcedureCalendar(token, externalId)
        }.getOrElse {
            throw Exception("Error obteniendo calendario de procedimientos: ${it.message}")
        }.map { dto ->
            dto.copy(scheduledAt = convertUtcToLima(dto.scheduledAt))
        }
    }
    override suspend fun getDoctorAppointments(doctorUuid: String): List<AppointmentSimpleDto> {
        val response = authorizedCall { token ->
            service.getAppointments(doctorUuid, token)
        }.getOrElse { throw it }

        return response
    }


}
