package oncontroldoctor.upc.edu.pe.authentication.data.remote

import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInResponse
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignInWithGoogleRequest
import oncontroldoctor.upc.edu.pe.authentication.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<Unit>

    @POST("authentication/sign-in-with-google")
    suspend fun signInWithGoogle(@Body request: SignInWithGoogleRequest): Response<SignInResponse>}