package oncontroldoctor.upc.edu.pe.billing.domain.model

data class Subscription(
    val id: Long,
    val adminId: Long,
    val planId: Long,
    val status: String,
    val startDate: String,
    val endDate: String,
    val trialUsed: Boolean,
    val cancelledAt: String?
)