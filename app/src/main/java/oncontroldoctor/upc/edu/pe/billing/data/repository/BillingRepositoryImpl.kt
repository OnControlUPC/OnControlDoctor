package oncontroldoctor.upc.edu.pe.billing.data.repository

import oncontroldoctor.upc.edu.pe.billing.data.mapper.toDomain
import oncontroldoctor.upc.edu.pe.billing.data.model.UseSubscriptionKeyRequest
import oncontroldoctor.upc.edu.pe.billing.data.remote.BillingService
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository
import javax.inject.Inject

class BillingRepositoryImpl(
    private val billingService: BillingService
) : BillingRepository
{
    override suspend fun getPlans(token: String): List<Plan> {
        val response = billingService.getPlans("Bearer $token")
        if(response.isSuccessful){
            return response.body()?.map{it.toDomain()} ?: emptyList()
        } else {
            throw Exception("Error fetching plans: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getSubscriptionKey(token: String, code: String): SubscriptionKey {
        val response = billingService.getSubscriptionKey( "Bearer $token", code)
        if(response.isSuccessful){
            return response.body()?.toDomain()
                ?: throw Exception("Subscription key not found")
        } else {
            throw Exception("Error fetching subscription key: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun useSubscriptionKey(token: String, keyId: Long, userId: Long) {
        val request = UseSubscriptionKeyRequest(subscriptionKeyId = keyId, userId = userId)
        val response = billingService.useSubscriptionKey("Bearer $token", request)
        if (!response.isSuccessful) {
            throw Exception("Error usando la clave de suscripción: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getActiveSubscription( token: String, adminId: Long): Subscription? {
        val response = billingService.getActiveSubscription("Bearer $token", adminId)
        return if (response.isSuccessful) {
            response.body()?.toDomain()
        } else if (response.code() == 404) {
            null
        } else {
            throw Exception("Error fetching active subscription: ${response.errorBody()?.string()}")
        }
    }

}

























