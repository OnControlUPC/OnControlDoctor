package oncontroldoctor.upc.edu.pe.communication.data.remote

import oncontroldoctor.upc.edu.pe.treatment.data.dto.DoctorPatientLinkSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.PatientDto
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

    @GET("/api/v1/patients/{uuid}")
    suspend fun getPatient(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): Response<PatientDto>
}