package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.treatment.data.dto.RecurrenceType
import oncontroldoctor.upc.edu.pe.treatment.data.model.Procedure
import oncontroldoctor.upc.edu.pe.treatment.data.model.Treatment
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientProfileViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTreatmentPanelScreen(
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
    var selectedTab by remember { mutableStateOf(0) }
    val doctorUuid = SessionHolder.getUserUuid().toString()


    var showDialog by remember { mutableStateOf(false) }
    var selectedTreatment by remember { mutableStateOf<Treatment?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTreatments(doctorUuid, patientUuid)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.isLoading) {
                        Text("Cargando perfil...")
                    } else if (uiState.error != null) {
                        Text("Error: ${uiState.error}")
                    } else {
                        Column {
                            Text("Panel de Tratamiento", style = MaterialTheme.typography.headlineSmall)
                            Text(uiState.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                actions = {
                    if (!uiState.isLoading && uiState.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = uiState.photoUrl,
                            contentDescription = "Patient Image",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 10.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                modifier = Modifier.height(96.dp)
            )
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
                    1 -> AppointmentList()
                    2 -> CalendarView()
                    3 -> SymptomsList()
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tratamiento")
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
                    .padding(horizontal = 2.dp)
                    .height(32.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (selectedTab == index) Color.Blue else Color.Gray
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
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
    LazyColumn {
        items(sortedTreatments) { treatment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onTreatmentClick(treatment) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(treatment.title.value, style = MaterialTheme.typography.titleMedium)
                    Text("Estado: ${treatment.status}", style = MaterialTheme.typography.bodySmall)
                    Text("Periodo: ${treatment.period.startDate} - ${treatment.period.endDate}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AppointmentList() {
    // Placeholder: Replace with the actual list of appointments
    Text(text = "Citas programadas con el paciente", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun CalendarView() {
    // Placeholder: Replace with a calendar view for the patient
    Text(text = "Calendario de citas", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun SymptomsList() {
    // Placeholder: Replace with the actual list of symptoms
    Text(text = "Síntomas reportados", style = MaterialTheme.typography.bodyLarge)
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
        title = { Text("Nuevo Tratamiento") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") }
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Fecha inicio (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Fecha fin (YYYY-MM-DD)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(title, startDate, endDate)
                },
                enabled = title.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
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

    // Llama a la carga de procedimientos cuando se muestra el panel
    LaunchedEffect(treatment.externalId) {
        onLoadProcedures(treatment.externalId)
    }

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.fillMaxHeight()) {
        Column(Modifier.padding(16.dp)) {
            Text("Procedimientos de: ${treatment.title.value}")
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showCreateProcedureDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear procedimiento")
            }
            LazyColumn {
                items(procedures) { procedure ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Descripción: ${procedure.description}")
                                if(procedure.startDateTime == null) Text("Fecha: Paciente aún no inicia")
                                else Text("Fecha: ${procedure.startDateTime}")
                                Text("Estado: ${procedure.status}")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    onCancelProcedure(procedure.id, doctorUuid, treatment.externalId)
                                },
                                enabled = procedure.status != "CANCELLED" && procedure.status != "COMPLETED"
                            ) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCreateProcedureDialog) {
        CreateProcedureDialog(
            doctorUuid = doctorUuid,
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
                showCreateProcedureDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProcedureDialog(
    doctorUuid: String,
    onDismiss: () -> Unit,
    onCreate: (
        description: String,
        recurrenceType: String,
        interval: Int,
        totalOccurrences: Int?,
        untilDate: String?
    ) -> Unit
) {
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
        title = { Text("Nuevo Procedimiento", style = MaterialTheme.typography.titleSmall) },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción", style = MaterialTheme.typography.bodySmall) },
                    textStyle = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = recurrenceType,
                        onValueChange = {},
                        label = { Text("Tipo de recurrencia", style = MaterialTheme.typography.bodySmall) },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodySmall,
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
                                text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                                onClick = {
                                    recurrenceType = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Intervalo", style = MaterialTheme.typography.bodySmall) },
                    textStyle = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Finalizar por:", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = useTotalOccurrences,
                        onCheckedChange = { useTotalOccurrences = it }
                    )
                }
                if (useTotalOccurrences) {
                    OutlinedTextField(
                        value = totalOccurrences,
                        onValueChange = { totalOccurrences = it },
                        label = { Text("Total de ocurrencias", style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                } else {
                    OutlinedTextField(
                        value = untilDate,
                        onValueChange = { untilDate = it },
                        label = { Text("Hasta fecha (YYYY-MM-DD)", style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodySmall
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
                                (!useTotalOccurrences && untilDate.isNotBlank()))
            ) {
                Text("Crear", style = MaterialTheme.typography.bodySmall)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.bodySmall)
            }
        }
    )
}