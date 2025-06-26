package oncontroldoctor.upc.edu.pe.treatment.data.remote

import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateProcedureRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateTreatmentRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment
import retrofit2.Response
import retrofit2.http.Body
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

}