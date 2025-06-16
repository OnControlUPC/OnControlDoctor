package oncontroldoctor.upc.edu.pe.billing.data.mapper

import oncontroldoctor.upc.edu.pe.billing.data.model.PlanResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionKeyResponse
import oncontroldoctor.upc.edu.pe.billing.data.model.SubscriptionResponse
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan
import oncontroldoctor.upc.edu.pe.billing.domain.model.Subscription
import oncontroldoctor.upc.edu.pe.billing.domain.model.SubscriptionKey


fun PlanResponse.toDomain(): Plan {
    val features = mutableListOf<String>()

    if (messagingEnabled) features.add("Messaging")
    if (symptomTrackingEnabled) features.add("symptom monitoring")
    if (customRemindersEnabled) features.add("Personalized reminders")
    if (calendarIntegrationEnabled) features.add("Calendar integration")
    if (basicReportsEnabled) features.add("Basic reports")
    if (advancedReportsEnabled) features.add("Advanced Reports")

    return Plan(
        id = id,
        name = name,
        priceAmount = priceAmount.toDouble(),
        currencyCode = currencyCode,
        durationDays = durationDays,
        maxPatients = maxPatients,
        features = features,
        maxStorageMb = maxStorageMb
    )
}


fun SubscriptionKeyResponse.toDomain(): SubscriptionKey {
    return SubscriptionKey(
        id = id,
        code = code,
        status = status,
        durationDays = durationDays,
        planId = planId
    )
}

fun SubscriptionResponse.toDomain(): Subscription {
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
