package oncontroldoctor.upc.edu.pe.domain.model

data class CalendarEvent(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val type: String
)
