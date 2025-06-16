package oncontroldoctor.upc.edu.pe.billing.domain.model

data class SubscriptionKey(
    val id: Long,
    val code: String,
    val status: String,
    val durationDays: Int,
    val planId: Long
)
