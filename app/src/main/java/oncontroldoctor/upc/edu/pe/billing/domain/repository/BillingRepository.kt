package oncontroldoctor.upc.edu.pe.billing.domain.repository

import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey


interface BillingRepository {
    suspend fun getPlans(): Result<List<Plan>>
    suspend fun getSubscriptionKey(code: String): Result<SubscriptionKey>
    suspend fun useSubscriptionKey(keyId: Long, userId: Long): Result<Unit>
    suspend fun getActiveSubscription(adminId: Long): Result<Subscription?>
}

