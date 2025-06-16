package oncontroldoctor.upc.edu.pe.billing.domain.model

data class Plan(
    val id: Long,
    val name: String,
    val priceAmount: Double,
    val currencyCode: String,
    val durationDays: Int,
    val maxPatients: Int,
    val features: List<String>,
    val maxStorageMb: Int
)