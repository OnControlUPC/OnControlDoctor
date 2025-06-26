package oncontroldoctor.upc.edu.pe.shared.data.mappers

import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionKeyResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionResponse
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey
import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity

fun SubscriptionKeyResponse.toDomain(): SubscriptionKey {
    return SubscriptionKey(
        id = id,
        code = code,
        status = status,
        durationDays = durationDays,
        planId = planId
    )
}

fun SubscriptionResponse.toDomain(): Subscription? {
    return Subscription(
        id = id,
        adminId = adminId,
        planId = planId,
        status = status,
        startDate = startDate,
        endDate = endDate,
        trialUsed = trialUsed,
        cancelledAt = cancelledAt
    )
}

fun SubscriptionResponse.toEntity(): SubscriptionEntity{
    return SubscriptionEntity(
        id = id,
        adminId = adminId,
        planId = planId,
        status = status,
        startDate = startDate,
        endDate = endDate,
        trialUsed = trialUsed,
        cancelledAt = cancelledAt
    )
}