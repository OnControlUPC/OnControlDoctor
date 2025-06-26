package oncontroldoctor.upc.edu.pe.billing.data.repository

import android.util.Log
import oncontroldoctor.upc.edu.pe.billing.data.mapper.toDomain
import oncontroldoctor.upc.edu.pe.billing.data.model.UseSubscriptionKeyRequest
import oncontroldoctor.upc.edu.pe.billing.data.remote.BillingService
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository
import oncontroldoctor.upc.edu.pe.shared.data.mappers.toDomain
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService

class BillingRepositoryImpl(
    private val billingService: BillingService
) : BillingRepository, BaseService()
{
    override suspend fun getPlans(): Result<List<Plan>> {
        return authorizedCall { token ->
            billingService.getPlans(token)
        }.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getSubscriptionKey(code: String): Result<SubscriptionKey> {
        return authorizedCall { token ->
            billingService.getSubscriptionKey(token, code)
        }.map { it.toDomain() }
    }

    override suspend fun useSubscriptionKey(keyId: Long, userId: Long): Result<Unit> {
        val request = UseSubscriptionKeyRequest(subscriptionKeyId = keyId, userId = userId)
        return authorizedCall { token ->
            billingService.useSubscriptionKey(token, request)
        }
    }

    override suspend fun getActiveSubscription(adminId: Long): Result<Subscription?> {
        return authorizedCall { token ->
            billingService.getActiveSubscription(token, adminId)
        }.mapCatching { response ->
            val domain = response.toDomain()
            Log.d("BILLING", "Resultado toDomain() = $domain")
            domain
        }
    }


}

























