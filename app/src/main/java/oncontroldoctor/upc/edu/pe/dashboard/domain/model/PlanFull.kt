package oncontroldoctor.upc.edu.pe.dashboard.domain.model

data class PlanFull(
    val id: Long,
    val name: String,
    val priceAmount: Double,
    val currencyCode: String,
    val durationDays: Int,
    val maxPatients: Int,
    val maxStorageMb: Int,
    val messagingEnabled: Boolean,
    val symptomTrackingEnabled: Boolean,
    val customRemindersEnabled: Boolean,
    val calendarIntegrationEnabled: Boolean,
    val basicReportsEnabled: Boolean,
    val advancedReportsEnabled: Boolean
)
