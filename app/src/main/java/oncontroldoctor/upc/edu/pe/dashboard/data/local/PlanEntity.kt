package oncontroldoctor.upc.edu.pe.dashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey val id: Long,
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
