package oncontroldoctor.upc.edu.pe.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import oncontroldoctor.upc.edu.pe.domain.model.CalendarEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events

    init {
        loadFakeEvents()
    }

    private fun loadFakeEvents() {
        _events.value = listOf(
            CalendarEvent(1, "Cita con María", "2025-05-15", "10:00", "Cita"),
            CalendarEvent(2, "Quimioterapia Carlos", "2025-05-15", "14:00", "Procedimiento"),
            CalendarEvent(3, "Análisis Lucía", "2025-05-16", "09:30", "Procedimiento")
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsForSelectedDate(): List<CalendarEvent> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateStr = _selectedDate.value.format(formatter)
        return _events.value.filter { it.date == dateStr }
    }
}