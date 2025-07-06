package oncontroldoctor.upc.edu.pe.treatment.data.dto

data class SymptomDto(
    val id: Long,
    val loggedAt: String,
    val symptomType: String,
    val severity: String,
    val notes: String,
    val treatmentId: String,
    val createdAt: String
)
