package oncontroldoctor.upc.edu.pe.treatment.data.remote

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TreatmentService {
    @GET("patients/search")
    suspend fun searchPatients(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Response<List<PatientDto>>

    @GET("doctor-patient-links/links/{doctorUuid}/{patientUuid}")
    suspend fun getDoctorPatientLink(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
        @Path("patientUuid") patientUuid: String
    ): Response<DoctorPatientLinkDto>

    @GET("/api/v1/patients/{uuid}")
    suspend fun getPatientByUuid(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): Response<PatientDto>

    @GET("doctor-patient-links/doctor/{doctorUuid}")
    suspend fun getPatientsByStatus(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
        @Query("status") status: String = "ACTIVE"
    ): Response<List<DoctorPatientLinkSimpleDto>>

    @POST("doctor-patient-links")
    suspend fun invitePatient(
        @Header("Authorization") token: String,
        @Body request: DoctorPatientLinkRequestDto
    ): Response<Unit>

    @PATCH("doctor-patient-links/{externalId}/delete")
    suspend fun cancelRequest(
        @Header("Authorization") token: String,
        @Path("externalId") externalId: String
    ): Response<Unit>

    @POST("treatments")
    suspend fun createTreatment(
        @Header("Authorization") token: String,
        @Body treatment: CreateTreatmentRequestDto
    ): Response<Unit>


    @PATCH("doctor-patient-links/{externalId}/activate")
    suspend fun activatePatient(
        @Header("Authorization") token: String,
        @Path("externalId") externalId: String
    ): Response<Unit>

    @GET("treatments/doctor/{doctorUuid}")
    suspend fun getTreatmentByProfileUuid(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
    ): Response<List<Treatment>>

    @GET("treatments/{treatmentExternalId}/procedures")
    suspend fun getProceduresByTreatmentExternalId(
        @Header("Authorization") token: String,
        @Path("treatmentExternalId") treatmentExternalId: String
    ): Response<List<Procedure>>

    @POST("treatments/{treatmentId}/procedures")
    suspend fun createProcedure(
        @Header("Authorization") token: String,
        @Path("treatmentId") treatmentId: String,
        @Body procedure: CreateProcedureRequestDto
    ): Response<Unit>

    @PATCH("treatments/procedures/{procedureId}/cancel")
    suspend fun cancelProcedure(
        @Header("Authorization") token: String,
        @Path("procedureId") procedureId: Long,
        @Query("doctorProfileUuid") doctorProfileUuid: String,
    ): Response<Unit>

    @GET("treatments/symptom-logs/patient")
    suspend fun getSymptomLogsByPatient(
        @Header("Authorization") token: String,
        @Query("patientUuid") patientUuid: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<SymptomDto>>

    @GET("appointments/doctor/{patientUuid}")
    suspend fun getAppointments(
        @Header("Authorization") token: String,
        @Path("patientUuid") patientUuid: String
    ): Response<List<AppointmentSimpleDto>>

    @DELETE("appointments/{id}/delete")
    suspend fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: Long
    ): Response<Unit>

    @PATCH("appointments/mark")
    suspend fun markAppointment(
        @Header("Authorization") token: String,
        @Body request: MarkAppointmentRequest
    ): Response<Unit>

    @POST("appointments")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: CreateAppointmentDto
    ): Response<Unit>

    @GET("treatments/{externalId}/predicted-executions")
    suspend fun getProcedureCalendar(
        @Header("Authorization") token: String,
        @Path("externalId") externalId: String
    ): Response<List<ProcedureCalendarDto>>

    @GET("treatments/patient/{patientUuid}")
    suspend fun getTreatmentsByPatientUuid(
        @Header("Authorization") token: String,
        @Path("patientUuid") patientUuid: String
    ): Response<List<Treatment>>

    @GET("appointments/calendar")
    suspend fun getAppointmentsCalendar(
        @Header("Authorization") token: String
    ): Response<List<AppointmentSimpleDto>>

}