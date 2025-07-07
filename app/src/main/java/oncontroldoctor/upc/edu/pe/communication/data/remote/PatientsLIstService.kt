package oncontroldoctor.upc.edu.pe.communication.data.remote

import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientsLIstService {

    @GET("doctor-patient-links/doctor/{doctorUuid}")
    suspend fun getPatientsByStatus(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
        @Query("status") status: String = "ACTIVE"
    ): Response<List<DoctorPatientLinkSimpleDto>>

    @GET("patients/{uuid}")
    suspend fun getPatient(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): Response<PatientDto>

    @GET("conversations/{doctorUuid}/{patientUuid}")
    suspend fun getConversation(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
        @Path("patientUuid") patientUuid: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<ChatMessage>>

    @GET("treatments/symptom-logs/{symptomId}")
    suspend fun getSymptomLog(
        @Header("Authorization") token: String,
        @Path("symptomId") symptomId: Long
    ): Response<SymptomDto>


}