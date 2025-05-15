package oncontroldoctor.upc.edu.pe.data.remote

import oncontroldoctor.upc.edu.pe.data.model.AuthRequest
import oncontroldoctor.upc.edu.pe.data.model.AuthResponse
import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body loginRequest: AuthRequest): Response<AuthResponse>
}