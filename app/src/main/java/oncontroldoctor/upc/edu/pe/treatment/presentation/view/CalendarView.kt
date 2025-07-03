package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.ProcedureCalendarDto
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.draw.clip
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.StatusSuccessText

@Composable
fun CalendarView(
    calendarViewModel: CalendarViewModel,
    patientUuid: String,
    onDayClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.dayOfWeek.value % 7

    val appointments by calendarViewModel.appointments.collectAsState()
    val procedures by calendarViewModel.procedures.collectAsState()

    val appointmentsByDate = appointments.groupBy {
        LocalDateTime.parse(it.scheduledAt, formatter).toLocalDate()
    }
    val proceduresByDate = procedures.groupBy {
        LocalDateTime.parse(it.scheduledAt, formatter).toLocalDate()
    }

    val allDates = (appointmentsByDate.keys + proceduresByDate.keys).toList().sorted()
    val initialDate = remember(allDates) { allDates.firstOrNull() ?: LocalDate.now() }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val items = (appointmentsByDate[selectedDate] ?: emptyList()) +
            (proceduresByDate[selectedDate] ?: emptyList())

    LaunchedEffect(patientUuid, currentMonth) {
        calendarViewModel.loadCalendar(patientUuid)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Mes anterior",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Mes siguiente",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach {
                Text(
                    it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val cellSize = 48.dp

        val days: List<Int?> = List(firstDayOfWeek) { null } +
                (1..daysInMonth).map { it } +
                List((7 - (firstDayOfWeek + daysInMonth) % 7) % 7) { null }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height((cellSize * 6) + 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            items(days.size) { index ->
                val day = days[index]
                if (day != null) {
                    val date = currentMonth.withDayOfMonth(day)
                    val isToday = date == LocalDate.now()
                    val isSelected = date == selectedDate

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(CircleShape)
                            .background(
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    isToday -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isToday) 1.dp else 0.dp,
                                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedDate = date
                                onDayClick(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                if (appointmentsByDate[date]?.isNotEmpty() == true) {
                                    Box(
                                        Modifier
                                            .size(5.dp)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    )
                                }
                                if (proceduresByDate[date]?.isNotEmpty() == true) {
                                    Box(
                                        Modifier
                                            .size(5.dp)
                                            .background(MaterialTheme.colorScheme.error, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(cellSize))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay actividades para el día seleccionado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items.forEach { item ->
                SmallInfoCard(item = item)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SmallInfoCard(item: Any) {
    val (title, scheduledAt, status) = when (item) {
        is AppointmentSimpleDto -> Triple("Cita: ${item.locationName ?: "Reunión virtual"}", item.scheduledAt, item.status)
        is ProcedureCalendarDto -> Triple("Procedimiento: ${item.procedureName}", item.scheduledAt, item.status)
        else -> Triple("", "", "")
    }

    val dt = LocalDateTime.parse(scheduledAt, DateTimeFormatter.ISO_DATE_TIME)
    val pretty = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy • HH:mm"))

    val borderColor = when (status) {
        "SCHEDULED", "PENDING" -> MaterialTheme.colorScheme.primary
        "COMPLETED", "COMPLETED_ON_TIME", "REGULARIZED" -> StatusSuccessText
        "MISSED", "CANCELLED_BY_DOCTOR", "CANCELLED_BY_PATIENT" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val statusIcon = when (status) {
        "COMPLETED", "COMPLETED_ON_TIME" -> Icons.Default.CheckCircle
        "REGULARIZED" -> Icons.Default.Autorenew
        "MISSED", "CANCELLED_BY_DOCTOR", "CANCELLED_BY_PATIENT" -> Icons.Default.Cancel
        "SCHEDULED", "PENDING" -> Icons.Default.Schedule
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(pretty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = statusIcon,
                contentDescription = "Estado: $status",
                modifier = Modifier.size(24.dp),
                tint = borderColor
            )
        }
    }
}