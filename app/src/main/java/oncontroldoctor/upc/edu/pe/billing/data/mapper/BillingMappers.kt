package oncontroldoctor.upc.edu.pe.billing.data.mapper

import oncontroldoctor.upc.edu.pe.billing.data.model.PlanResponse
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan

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


