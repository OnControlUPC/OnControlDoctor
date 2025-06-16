package oncontroldoctor.upc.edu.pe.billing.data.model

data class SubscriptionKeyResponse(
    val id: Long,
    val code: String,
    val status: String,
    val durationDays: Int,
    val planId: Long
)


