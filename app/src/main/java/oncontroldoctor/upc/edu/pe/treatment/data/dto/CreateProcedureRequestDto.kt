package oncontroldoctor.upc.edu.pe.treatment.data.dto

enum class RecurrenceType {
    DAILY,
    WEEKLY,
    EVERY_X_HOURS
}

data class CreateProcedureRequestDto(
    val doctorProfileUuid: String,
    val description: String,
    val recurrenceType: RecurrenceType,
    val interval: Int,
    val totalOccurrences: Int? = null,
    val untilDate: String? = null
)