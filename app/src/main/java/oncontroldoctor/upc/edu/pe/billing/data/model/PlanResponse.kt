package oncontroldoctor.upc.edu.pe.billing.data.model

data class PlanResponse(
    val id: Long,
    val name: String,
    val priceAmount: Double,
    val currencyCode: String,
    val durationDays: Int,
    val trialDays: Int?,
    val maxPatients: Int,
    val messagingEnabled: Boolean,
    val symptomTrackingEnabled: Boolean,
    val customRemindersEnabled: Boolean,
    val calendarIntegrationEnabled: Boolean,
    val basicReportsEnabled: Boolean,
    val advancedReportsEnabled: Boolean,
    val maxStorageMb: Int,
    val active: Boolean
)





