package oncontroldoctor.upc.edu.pe.dashboard.data.remote

import oncontroldoctor.upc.edu.pe.billing.data.model.PlanResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DashboardService {

    @GET("subscriptions/admin/{adminId}/active")
    suspend fun getActiveSubscription(
        @Header("Authorization") token: String,
        @Path("adminId") adminId: Long
    ): Response<SubscriptionResponse>

    @GET("plans/{planId}")
    suspend fun getPlanById(
        @Header("Authorization") token: String,
        @Path("planId") planId: Long
    ): Response<PlanResponse>

}