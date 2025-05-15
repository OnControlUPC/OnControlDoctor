package oncontroldoctor.upc.edu.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import oncontroldoctor.upc.edu.pe.domain.model.Notification

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        loadFakeNotifications()
    }

    private fun loadFakeNotifications() {
        _notifications.value = listOf(
            Notification(
                id = 1,
                title = "Nueva cita agendada",
                message = "Paciente María Ramírez ha aceptado la cita del 15/05 a las 10:00.",
                timestamp = "10:15 AM",
                isRead = false
            ),
            Notification(
                id = 2,
                title = "Síntoma registrado",
                message = "Carlos Fernández reportó 'náuseas persistentes'.",
                timestamp = "08:40 AM",
                isRead = true
            )
        )
    }
}