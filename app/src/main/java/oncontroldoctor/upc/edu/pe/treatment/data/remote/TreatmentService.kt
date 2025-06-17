package oncontroldoctor.upc.edu.pe.treatment.data.remote

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
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

    @POST("doctor-patient-links")
    suspend fun linkDoctorPatient(
        @Header("Authorization") token: String,
        @Body request: DoctorPatientLinkRequestDto
    ): Response<Unit>

    @GET("doctor-patient-links/doctor/{doctorUuid}")
    suspend fun getDoctorPatients(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String,
        @Query("status") status: String
    ): Response<List<DoctorPatientLinkDto>>

    @PATCH("doctor-patient-links/{linkId}/activate")
    suspend fun activateLink(
        @Header("Authorization") token: String,
        @Path("linkId") linkId: Long
    ): Response<Unit>
}
