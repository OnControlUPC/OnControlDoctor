package oncontroldoctor.upc.edu.pe.billing.data.remote

import oncontroldoctor.upc.edu.pe.billing.data.model.PlanResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionKeyResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.UseSubscriptionKeyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BillingService {
    @GET("plans")
    suspend fun getPlans(@Header("Authorization") token: String): Response<List<PlanResponse>>

    @GET("subscription-keys/code/{code}")
    suspend fun getSubscriptionKey(
        @Header("Authorization") token: String,
        @Path("code") code: String
    ): Response<SubscriptionKeyResponse>

    @POST("subscription-keys/use")
    suspend fun useSubscriptionKey(
        @Header("Authorization") token: String,
        @Body request: UseSubscriptionKeyRequest
    ): Response<Unit>

    @GET("subscriptions/admin/{adminId}/active")
    suspend fun getActiveSubscription(
        @Header("Authorization") token: String,
        @Path("adminId") adminId: Long
    ): Response<SubscriptionResponse>
}