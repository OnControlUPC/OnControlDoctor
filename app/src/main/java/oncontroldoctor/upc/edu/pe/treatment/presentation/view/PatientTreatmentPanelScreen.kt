package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.background
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.*
import oncontroldoctor.upc.edu.pe.treatment.data.dto.RecurrenceType
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.AppointmentViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.AppointmentViewModelFactory
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.CalendarViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.CalendarViewModelFactory
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientProfileViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientProfileViewModelFactory

import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTreatmentPanelScreen(
    navControllerG: NavController,
    patientUuid: String,
    repository: TreatmentRepository,
    onTreatmentSelected: () -> Unit,
    onAppointmentsSelected: () -> Unit,
    onCalendarSelected: () -> Unit,
    onSymptomsSelected: () -> Unit
) {
    val viewModel: PatientProfileViewModel = viewModel(
        factory = PatientProfileViewModelFactory(patientUuid, repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val symptoms by viewModel.symptoms

    var selectedTab by remember { mutableStateOf(0) }
    val doctorUuid = SessionHolder.getUserUuid().toString()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTreatment by remember { mutableStateOf<Treatment?>(null) }

    var to by remember { mutableStateOf(LocalDate.now().atStartOfDay()) }
    var from by remember { mutableStateOf(to.minusDays(7)) }

    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(repository)
    )

    val calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(repository)
    )

    LaunchedEffect(Unit) {
        viewModel.loadTreatments(doctorUuid, patientUuid)
    }


    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp),
                color = MaterialTheme.colorScheme.primary // Usar el color primario del tema
            ){
                TopAppBar(
                    title = {
                        if (uiState.isLoading) {
                            Text("Cargando perfil...", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary))
                        } else if (uiState.error != null) {
                            Text("Error: ${uiState.error}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error))
                        } else {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        "Panel de Tratamiento",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary // Texto en color onPrimary
                                        )
                                    )
                                    Text(
                                        uiState.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) // Un poco más claro
                                        )
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        if (!uiState.isLoading && uiState.photoUrl.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(end = 10.dp)
                            ) {
                                AsyncImage(
                                    model = uiState.photoUrl,
                                    contentDescription = "Patient Image",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape) // Borde con color onPrimary
                                        .align(Alignment.Center)
                                    ,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Usar el color primario del tema
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(96.dp),
                )
            }

        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                SectionSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onTreatmentSelected = onTreatmentSelected,
                    onAppointmentsSelected = onAppointmentsSelected,
                    onCalendarSelected = onCalendarSelected,
                    onSymptomsSelected = onSymptomsSelected
                )
                when (selectedTab) {
                    0 -> TreatmentList(
                        treatments = viewModel.treatments,
                        onTreatmentClick = { selectedTreatment = it
                        }
                    )
                    1 ->  AppointmentListScreen(
                        viewModel = appointmentViewModel,
                        patientUuid = patientUuid,
                        onAppointmentClick = { appointment ->

                        }
                    )
                    2 -> CalendarView(
                        calendarViewModel = calendarViewModel,
                        patientUuid = patientUuid,
                        onDayClick = { selectedDate ->
                        }
                    )
                    3 -> {
                        LaunchedEffect(from, to, patientUuid) {
                            viewModel.loadSymptoms(patientUuid, from, to)
                        }
                        SymptomsList(symptoms, from, to, patientUuid, navControllerG = navControllerG)

                        Button(
                            onClick = {
                                from = from.minusDays(7)
                                viewModel.loadSymptoms(patientUuid, from, to)

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("Ver más antiguos")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar tratamiento")
                }
            }
        },
    )

    if (showDialog) {
        CreateTreatmentDialog(
            onDismiss = { showDialog = false },
            onCreate = { title, startDate, endDate ->
                viewModel.createTreatment(
                    title = title,
                    startDate = startDate,
                    endDate = endDate,
                    doctorUuid = doctorUuid,
                    patientUuid = patientUuid
                )
                showDialog = false
            }
        )
    }
    if (selectedTreatment != null) {
        TreatmentDetailBottomSheet(
            treatment = selectedTreatment!!,
            procedures = viewModel.procedures.value,
            onDismiss = { selectedTreatment = null },
            onLoadProcedures = { treatmentId ->
                viewModel.loadProceduresByTreatmentAsync(treatmentId)
            },
            doctorUuid = doctorUuid,
            onCreateProcedure = { treatmentId, doctorUuid, description, recurrenceType, interval, totalOccurrences, untilDate ->
                viewModel.createProcedure(
                    treatmentId = treatmentId,
                    doctorUuid = doctorUuid,
                    description = description,
                    recurrenceType = RecurrenceType.valueOf(recurrenceType),
                    interval = interval,
                    totalOccurrences = totalOccurrences,
                    untilDate = untilDate
                )
            },
            onCancelProcedure = { procedureId, doctorUuid, treatmentId ->
                viewModel.cancelProcedure(procedureId, doctorUuid, treatmentId)
            }
        )
    }
}


@Composable
fun SectionSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onTreatmentSelected: () -> Unit,
    onAppointmentsSelected: () -> Unit,
    onCalendarSelected: () -> Unit,
    onSymptomsSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val tabLabels = listOf("Tratam.", "Citas", "Calend.", "Síntomas")
        val tabCallbacks = listOf(
            onTreatmentSelected,
            onAppointmentsSelected,
            onCalendarSelected,
            onSymptomsSelected
        )

        tabLabels.forEachIndexed { index, label ->
            TextButton(
                onClick = {
                    onTabSelected(index)
                    tabCallbacks[index]()
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
                    .clip(shape = RoundedCornerShape(8.dp)) // Esquinas más redondeadas
                    .background(
                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Transparent // Usar primary para seleccionado
                    )
                    .padding(horizontal =8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant // Texto en onPrimary o onSurfaceVariant
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp, // Ajuste el tamaño de la fuente para hacerla más legible
                        fontWeight = FontWeight.Medium // Un poco más de peso
                    )
                )
            }
        }
    }
}


@Composable
fun TreatmentList(
    treatments: List<Treatment>,
    onTreatmentClick: (Treatment) -> Unit
) {
    val sortedTreatments = treatments.sortedByDescending { it.status == "ACTIVE" }
    LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) { // Añadir padding horizontal a la columna
        items(sortedTreatments) { treatment ->
            // Determinar los colores de la tarjeta basados en el estado del tratamiento
            val cardBackgroundColor = when (treatment.status) {
                "ACTIVE" -> StatusInfoBackground // Tratamiento activo (similar a cita programada)
                "COMPLETED" -> StatusSuccessBackground // Tratamiento completado
                "CANCELLED" -> StatusNeutralBackground // Tratamiento cancelado
                else -> StatusNeutralBackground // Por defecto
            }

            val textColor = when (treatment.status) {
                "ACTIVE" -> StatusInfoText
                "COMPLETED" -> StatusSuccessText
                "CANCELLED" -> StatusNeutralText
                else -> StatusNeutralText
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp) // Espaciado vertical entre tarjetas
                    .clickable { onTreatmentClick(treatment) },
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        treatment.title.value,
                        style = MaterialTheme.typography.titleMedium.copy(color = textColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Estado: ${treatment.status}",
                        style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
                    )
                    Text(
                        "Periodo: ${treatment.period.startDate} - ${treatment.period.endDate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
                    )
                }
            }
        }
    }
}


@Composable
fun CreateTreatmentDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Tratamiento", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre campos
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Fecha inicio (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Fecha fin (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(title, startDate, endDate)
                },
                enabled = title.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(MaterialTheme.colorScheme.outline))            ) {
                Text("Cancelar")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentDetailBottomSheet(
    treatment: Treatment,
    procedures: List<Procedure>,
    onDismiss: () -> Unit,
    onLoadProcedures: (String) -> Unit,
    doctorUuid: String,
    onCreateProcedure: (
        treatmentId: String,
        doctorUuid: String,
        description: String,
        recurrenceType: String,
        interval: Int,
        totalOccurrences: Int?,
        untilDate: String?
    ) -> Unit,
    onCancelProcedure: (procedureId: Long, doctorUuid: String, treatmentId: String) -> Unit
) {
    var showCreateProcedureDialog by remember { mutableStateOf(false) }

    LaunchedEffect(treatment.externalId) {
        onLoadProcedures(treatment.externalId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(), // Ocupa un poco menos de altura
        sheetState = rememberModalBottomSheetState() // Asegurarse de tener un sheetState
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Procedimientos de: ${treatment.title.value}",
                style = MaterialTheme.typography.headlineSmall, // Título más grande
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = { showCreateProcedureDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Crear procedimiento")
            }
            Spacer(Modifier.height(16.dp)) // Más espacio

            if (procedures.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No hay procedimientos registrados para este tratamiento.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(procedures) { procedure ->
                        // Determinar los colores de la tarjeta del procedimiento
                        val procedureCardBackgroundColor = when (procedure.status) {
                            "COMPLETED" -> StatusSuccessBackground
                            "PENDING" -> StatusWarningBackground
                            "CANCELLED" -> StatusNeutralBackground
                            else -> StatusNeutralBackground
                        }
                        val procedureTextColor = when (procedure.status) {
                            "COMPLETED" -> StatusSuccessText
                            "PENDING" -> StatusWarningText
                            "CANCELLED" -> StatusNeutralText
                            else -> StatusNeutralText
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = procedureCardBackgroundColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Menor elevación
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Descripción: ${procedure.description}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = procedureTextColor)
                                    )
                                    Text(
                                        "Fecha: ${procedure.startDateTime ?: "Paciente aún no inicia"}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = procedureTextColor.copy(alpha = 0.8f))
                                    )
                                    Text(
                                        "Estado: ${procedure.status}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = procedureTextColor.copy(alpha = 0.8f))
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(
                                    onClick = {
                                        onCancelProcedure(procedure.id, doctorUuid, treatment.externalId)
                                    },
                                    enabled = procedure.status != "CANCELLED" && procedure.status != "COMPLETED",
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error, // Usar color de error
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCreateProcedureDialog) {
        CreateProcedureDialog(
            onDismiss = { showCreateProcedureDialog = false },
            onCreate = { description, recurrenceType, interval, totalOccurrences, untilDate ->
                onCreateProcedure(
                    treatment.externalId,
                    doctorUuid,
                    description,
                    recurrenceType,
                    interval,
                    totalOccurrences,
                    untilDate
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProcedureDialog(
    onDismiss: () -> Unit,
    onCreate: (
        description: String,
        recurrenceType: String,
        interval: Int,
        totalOccurrences: Int?,
        untilDate: String?
    ) -> Unit
)
{
    var description by remember { mutableStateOf("") }
    var recurrenceType by remember { mutableStateOf("DAILY") }
    val recurrenceOptions = listOf("DAILY", "WEEKLY", "EVERY_X_HOURS")
    var expanded by remember { mutableStateOf(false) }
    var interval by remember { mutableStateOf("1") }
    var useTotalOccurrences by remember { mutableStateOf(true) }
    var totalOccurrences by remember { mutableStateOf("") }
    var untilDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Procedimiento", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = recurrenceType,
                        onValueChange = {},
                        label = { Text("Tipo de recurrencia") },
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        recurrenceOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    recurrenceType = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Intervalo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Finalizar por:")
                    Switch(
                        checked = useTotalOccurrences,
                        onCheckedChange = { useTotalOccurrences = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                if (useTotalOccurrences) {
                    OutlinedTextField(
                        value = totalOccurrences,
                        onValueChange = { totalOccurrences = it },
                        label = { Text("Total de ocurrencias") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = untilDate,
                        onValueChange = { untilDate = it },
                        label = { Text("Hasta fecha (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(
                        description,
                        recurrenceType,
                        interval.toIntOrNull() ?: 1,
                        if (useTotalOccurrences) totalOccurrences.toIntOrNull() else null,
                        if (!useTotalOccurrences) untilDate else null
                    )
                    onDismiss()
                },
                enabled = description.isNotBlank() && interval.isNotBlank() &&
                        ((useTotalOccurrences && totalOccurrences.isNotBlank()) ||
                                (!useTotalOccurrences && untilDate.isNotBlank())),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline)
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}