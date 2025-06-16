package oncontroldoctor.upc.edu.pe.profile.data.remote

import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileResponse
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorUuidResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileService {
    @GET("doctors/me/uuid")
    suspend fun getDoctorUuid(@Header("Authorization") token: String): Response<DoctorUuidResponse>

    @GET("doctors/{uuid}")
    suspend fun getDoctorProfileByUuid(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): Response<DoctorProfileResponse>

    @POST("doctors")
    suspend fun createProfile(
        @Header("Authorization") token: String,
        @Body request: DoctorProfileRequest
    ): Response<Unit>

}