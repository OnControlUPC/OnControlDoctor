package oncontroldoctor.upc.edu.pe.dashboard.data.mapper

import oncontroldoctor.upc.edu.pe.billing.data.model.PlanResponse
import oncontroldoctor.upc.edu.pe.dashboard.data.local.PlanEntity
import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull

fun PlanEntity.toDashboardDomain(): PlanFull {
    return PlanFull(
        id = id,
        name = name,
        priceAmount = priceAmount,
        currencyCode = currencyCode,
        durationDays = durationDays,
        maxPatients = maxPatients,
        maxStorageMb = maxStorageMb,
        messagingEnabled = messagingEnabled,
        symptomTrackingEnabled = symptomTrackingEnabled,
        customRemindersEnabled = customRemindersEnabled,
        calendarIntegrationEnabled = calendarIntegrationEnabled,
        basicReportsEnabled = basicReportsEnabled,
        advancedReportsEnabled = advancedReportsEnabled
    )
}


fun PlanResponse.toEntity(): PlanEntity {
    return PlanEntity(
        id = id,
        name = name,
        priceAmount = priceAmount.toDouble(),
        currencyCode = currencyCode,
        durationDays = durationDays,
        trialDays = trialDays,
        maxPatients = maxPatients,
        messagingEnabled = messagingEnabled,
        symptomTrackingEnabled = symptomTrackingEnabled,
        customRemindersEnabled = customRemindersEnabled,
        calendarIntegrationEnabled = calendarIntegrationEnabled,
        basicReportsEnabled = basicReportsEnabled,
        advancedReportsEnabled = advancedReportsEnabled,
        maxStorageMb = maxStorageMb,
        active = active
    )
}