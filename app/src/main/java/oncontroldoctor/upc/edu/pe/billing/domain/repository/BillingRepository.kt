package oncontroldoctor.upc.edu.pe.billing.domain.repository

import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey


interface BillingRepository {
    suspend fun getPlans(token: String): List<Plan>
    suspend fun getSubscriptionKey(token: String, code: String): SubscriptionKey
    suspend fun useSubscriptionKey(token: String, keyId: Long, userId: Long)
    suspend fun getActiveSubscription(token: String, adminId: Long): Subscription?

}