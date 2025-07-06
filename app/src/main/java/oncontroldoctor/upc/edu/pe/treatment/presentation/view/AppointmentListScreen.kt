package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.treatment.data.dto.AppointmentSimpleDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.CreateAppointmentDto
import oncontroldoctor.upc.edu.pe.treatment.data.dto.MarkAppointmentRequest
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.AppointmentViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.ranges.contains
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.text.style.TextOverflow
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.*
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale
import androidx.compose.ui.graphics.SolidColor


enum class SheetType { NONE, VIRTUAL, MAP }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen(
    viewModel: AppointmentViewModel,
    patientUuid: String,
    onAppointmentClick: (AppointmentSimpleDto) -> Unit
) {
    val appointments by viewModel.appointments
    val loading by viewModel.loading
    val error by viewModel.error

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var selectedAppointment by remember { mutableStateOf<AppointmentSimpleDto?>(null) }
    var sheetType by remember { mutableStateOf(SheetType.NONE) }
    var showDialog by remember { mutableStateOf(false) }

    var showHistory by remember { mutableStateOf(false) }

    LaunchedEffect(patientUuid) {
        viewModel.loadAppointments(patientUuid)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else if (error != null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            } else {
                val (scheduled, history) = appointments.partition { it.status == "SCHEDULED" }
                val sortedScheduled = scheduled.sortedByDescending { it.scheduledAt }
                val sortedHistory = history.sortedByDescending { it.scheduledAt }

                val appointmentsToShow = if (showHistory) sortedHistory else sortedScheduled

                val groupedAppointments = appointmentsToShow.groupBy {
                    LocalDateTime.parse(it.scheduledAt, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
                }

                if (appointmentsToShow.isEmpty()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = if (showHistory) "No hay citas en el historial" else "No hay citas activas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        groupedAppointments.forEach { (date, appointmentsOnDate) ->
                            item {
                                DateHeader(date = date)
                            }
                            items(appointmentsOnDate, key = { it.id }) { appointment ->
                                AppointmentCard(
                                    appointment = appointment,
                                    onCancel = { viewModel.cancelAppointment(appointment.id) },
                                    onClick = {
                                        if (appointment.status == "SCHEDULED") {
                                            when {
                                                appointment.locationName == null && appointment.locationMapsUrl == null -> {
                                                    selectedAppointment = appointment
                                                    sheetType = SheetType.VIRTUAL
                                                    coroutineScope.launch { sheetState.show() }
                                                }
                                                appointment.locationName != null -> {
                                                    selectedAppointment = appointment
                                                    sheetType = SheetType.MAP
                                                    coroutineScope.launch { sheetState.show() }
                                                }
                                                else -> {
                                                    onAppointmentClick(appointment)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(150.dp))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(
                onClick = { showHistory = !showHistory },
                containerColor = if (showHistory)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (showHistory)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (showHistory) "Ver citas activas" else "Ver historial"
                )
            }
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar cita")
            }
        }
    }

    if (selectedAppointment != null && sheetType != SheetType.NONE) {
        val uriHandler = LocalUriHandler.current
        ModalBottomSheet(
            onDismissRequest = {
                selectedAppointment = null
                sheetType = SheetType.NONE
            },
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(16.dp)
            ) {
                val scheduledDate = LocalDateTime.parse(selectedAppointment!!.scheduledAt, DateTimeFormatter.ISO_DATE_TIME)
                val now = LocalDateTime.now(ZoneId.systemDefault())
                val canMark = now.isAfter(scheduledDate) || now.isEqual(scheduledDate)

                when (sheetType) {
                    SheetType.VIRTUAL -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (canMark) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.markAppointment(
                                                    MarkAppointmentRequest(
                                                        appointmentId = selectedAppointment!!.id,
                                                        doctorProfileUuid = SessionHolder.getUserUuid().toString(),
                                                        newStatus = "COMPLETED"
                                                    ),
                                                    patientUuid
                                                )
                                                sheetType = SheetType.NONE
                                                selectedAppointment = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccessText)
                                    ) {
                                        Text("Completar")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.markAppointment(
                                                    MarkAppointmentRequest(
                                                        appointmentId = selectedAppointment!!.id,
                                                        doctorProfileUuid = SessionHolder.getUserUuid().toString(),
                                                        newStatus = "MISSED"
                                                    ),
                                                    patientUuid
                                                )
                                                sheetType = SheetType.NONE
                                                selectedAppointment = null
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorText),
                                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(Color(0xFFD32F2F)))                                   ) {
                                        Text("Perdida")
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Ícono de videollamada",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            if (selectedAppointment!!.meetingUrl == null) {
                                Text(
                                    "El enlace de la reunión estará disponible 15 minutos antes de la cita. Por favor, regresa más tarde.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Enlace de la reunión:",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .clickable {
                                                selectedAppointment!!.meetingUrl?.let { url ->
                                                    uriHandler.openUri(url)
                                                }
                                            },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedAppointment!!.meetingUrl ?: "",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textAlign = TextAlign.Center
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Haz clic en el enlace para unirte a la reunión.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    SheetType.MAP -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (canMark) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.markAppointment(
                                                    MarkAppointmentRequest(
                                                        appointmentId = selectedAppointment!!.id,
                                                        doctorProfileUuid = SessionHolder.getUserUuid().toString(),
                                                        newStatus = "COMPLETED"
                                                    ),
                                                    patientUuid
                                                )
                                                sheetType = SheetType.NONE
                                                selectedAppointment = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccessText)
                                    ) {
                                        Text("Completar")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.markAppointment(
                                                    MarkAppointmentRequest(
                                                        appointmentId = selectedAppointment!!.id,
                                                        doctorProfileUuid = SessionHolder.getUserUuid().toString(),
                                                        newStatus = "MISSED"
                                                    ),
                                                    patientUuid
                                                )
                                                sheetType = SheetType.NONE
                                                selectedAppointment = null
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorText),
                                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(Color(0xFFD32F2F)))
                                    ) {
                                        Text("Perdida")
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ícono de mapa",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "Ubicación de la cita:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clickable {
                                        selectedAppointment!!.locationMapsUrl?.let { url ->
                                            uriHandler.openUri(url)
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = selectedAppointment!!.locationName ?: "Ubicación no especificada",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    selectedAppointment!!.locationMapsUrl?.let { url ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Abrir en Google Maps",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Haz clic para abrir la ubicación en Google Maps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    if (showDialog) {
        CreateAppointmentDialog(
            onDismiss = { showDialog = false },
            onCreate = { scheduledAt, isVirtual, locationName, locationMapsUrl, meetingUrl ->
                viewModel.createAppointment(
                    CreateAppointmentDto(
                        patientProfileUuid = patientUuid,
                        scheduledAt = scheduledAt,
                        locationName = if (isVirtual) null else locationName,
                        locationMapsUrl = if (isVirtual) null else locationMapsUrl?.ifBlank { null },
                        meetingUrl = null
                    ),
                    patientUuid
                ) { }
                showDialog = false
            }
        )
    }

}

@Composable
fun DateHeader(date: LocalDate) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de'yyyy", Locale("es", "ES"))
    }

    val today = LocalDate.now(ZoneId.systemDefault())
    val tomorrow = today.plusDays(1)
    val yesterday = today.minusDays(1)
    val displayText = when (date) {
        yesterday -> "Ayer"
        today -> "Hoy"
        tomorrow -> "Mañana"
        else -> date.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 8.dp)
    )
}

@Composable
fun AppointmentCard(
    appointment: AppointmentSimpleDto,
    onCancel: () -> Unit,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val scheduledDate = LocalDateTime.parse(appointment.scheduledAt, DateTimeFormatter.ISO_DATE_TIME)
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val minutesToStart = Duration.between(now, scheduledDate).toMinutes()

    val cardBackgroundColor = when (appointment.status) {
        "SCHEDULED" -> {
            when {
                minutesToStart in 0..15 -> StatusEmphasisBackground
                minutesToStart < 60 -> StatusWarningBackground
                else -> StatusInfoBackground
            }
        }
        "COMPLETED" -> StatusSuccessBackground
        "MISSED" -> StatusErrorBackground
        "CANCELLED_BY_DOCTOR" -> StatusNeutralBackground
        else -> StatusNeutralBackground
    }

    val textColor = when (appointment.status) {
        "SCHEDULED" -> {
            when {
                minutesToStart in 0..15 -> StatusEmphasisText
                minutesToStart < 60 -> StatusWarningText
                else -> StatusInfoText
            }
        }
        "COMPLETED" -> StatusSuccessText
        "MISSED" -> StatusErrorText
        "CANCELLED_BY_DOCTOR" -> StatusNeutralText
        else -> StatusNeutralText
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(enabled = appointment.status == "SCHEDULED") { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    appointment.locationName ?: appointment.locationMapsUrl ?: "Reunión virtual",
                    style = MaterialTheme.typography.titleMedium.copy(color = textColor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Fecha: ${scheduledDate.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
                )
                Text(
                    "Estado: ${appointment.status}",
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
                )
            }
            if (appointment.status == "SCHEDULED") {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentDialog(
    onDismiss: () -> Unit,
    onCreate: (
        scheduledAt: String,
        isVirtual: Boolean,
        locationName: String?,
        locationMapsUrl: String?,
        meetingUrl: String?
    ) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedTime by remember { mutableStateOf<LocalTime?>(null) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    var isVirtual by remember { mutableStateOf(true) }
    var locationName by remember { mutableStateOf("") }
    var locationMapsUrl by remember { mutableStateOf("") }


    val scheduledLocal = if (pickedDate != null && pickedTime != null) {
        LocalDateTime.of(pickedDate, pickedTime)
    } else null

    val now = LocalDateTime.now()

    val isCreateEnabled = scheduledLocal != null &&
            scheduledLocal.isAfter(now.plusMinutes(14)) &&
            (isVirtual || locationName.isNotBlank())


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Cita", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = if (scheduledLocal != null) {
                            scheduledLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        } else {
                            "Seleccionar fecha y hora"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "Solo puedes agendar citas a partir de 15 minutos en adelante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modalidad virtual", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isVirtual,
                        onCheckedChange = { isVirtual = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                if (!isVirtual) {
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Dirección (obligatorio)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = locationMapsUrl,
                        onValueChange = { locationMapsUrl = it },
                        label = { Text("URL de Google Maps (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(
                        scheduledLocal?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: "",
                        isVirtual,
                        if (isVirtual) null else locationName.ifBlank { null },
                        if (isVirtual) null else locationMapsUrl.ifBlank { null },
                        null
                    )
                },
                enabled = isCreateEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline)
                )
            ) {
                Text("Cancelar")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        pickedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showTimePicker && pickedDate != null) {
        val context = LocalContext.current
        TimePickerDialog(
            context,
            { _, hour, minute ->
                pickedTime = LocalTime.of(hour, minute)
                showTimePicker = false
            },
            now.hour,
            now.minute,
            true
        ).show()
    }
}