package oncontroldoctor.upc.edu.pe.data.remote

import retrofit2.Response
import oncontroldoctor.upc.edu.pe.data.model.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileService {
    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>
}