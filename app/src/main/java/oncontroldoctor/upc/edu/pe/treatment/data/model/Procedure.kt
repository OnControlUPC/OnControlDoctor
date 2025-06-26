package oncontroldoctor.upc.edu.pe.treatment.data.model

data class Procedure(
    val id: Long,
    val external_id: String,
    val description: String,
    val status: String,
    val recurrenceType: String,
    val interval: Int,
    val totalOccurrences: Int,
    val untilDate: String,
    val startDateTime: String
)
