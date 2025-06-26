package oncontroldoctor.upc.edu.pe.dashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: Long,
    val adminId: Long,
    val planId: Long,
    val status: String,
    val startDate: String,
    val endDate: String,
    val trialUsed: Boolean,
    val cancelledAt: String?
)