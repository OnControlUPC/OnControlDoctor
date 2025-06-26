package oncontroldoctor.upc.edu.pe.dashboard.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.dashboard.presentation.viewmodel.DashboardViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.TreatmentModule
import oncontroldoctor.upc.edu.pe.treatment.presentation.view.PatientSearchScreen
import oncontroldoctor.upc.edu.pe.treatment.presentation.view.PatientTreatmentPanelScreen
import oncontroldoctor.upc.edu.pe.treatment.presentation.view.PatientsScreen
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientsViewModel

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val navItemsLeft = listOf("home", "patients")
    val navItemsRight = listOf("messages", "settings")

    Scaffold(
        bottomBar = {
            BottomAppBar {
                // Ítems del lado izquierdo
                navItemsLeft.forEach { route ->
                    BottomBarItem(
                        icon = when (route) {
                            "home" -> Icons.Default.Home
                            "patients" -> Icons.Default.Person
                            else -> Icons.Default.Home
                        },
                        label = route,
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de búsqueda central
                FloatingActionButton(
                    onClick = { navController.navigate("search") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp) // FAB size
                        .align(Alignment.CenterVertically)
                        .offset(y = (-4).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Ítems del lado derecho
                navItemsRight.forEach { route ->
                    BottomBarItem(
                        icon = when (route) {
                            "messages" -> Icons.Default.MailOutline
                            "settings" -> Icons.Default.Settings
                            else -> Icons.Default.Settings
                        },
                        label = route,
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") { DashboardHomeScreen(
                dashboardViewModel = dashboardViewModel
            ) }
            composable("patients") {
                val doctorUuid = SessionHolder.getUserUuid() ?: ""

                val patientsViewModelFactory = TreatmentModule.providePatientsViewModelFactory()

                val patientsViewModel: PatientsViewModel = viewModel(factory = patientsViewModelFactory)

                LaunchedEffect(doctorUuid, patientsViewModel) { // Clave también con el ViewModel para asegurar que se llama si la instancia cambia (aunque no debería con viewModel())
                    if (doctorUuid.isNotEmpty()) {
                        patientsViewModel.loadPatients(doctorUuid) // Reemplaza con el nombre real de tu método
                    }
                }

                PatientsScreen(
                    viewModel = patientsViewModel,
                    doctorUuid = doctorUuid
                )
            }
            composable("messages") { /* Chat general o bandeja */ }
            composable("settings") { /* Configuraciones del doctor */ }
            composable("search") {
                val viewModel = TreatmentModule.getPatientSearchViewModel()
                val doctorUuid = SessionHolder.getUserUuid() ?: ""
                PatientSearchScreen(
                    viewModel = viewModel,
                    doctorUuid = doctorUuid,
                    onPatientSelected = { patientState ->
                        navController.navigate("panel/${patientState.patient.uuid}")
                    }
                )
            }
            composable("panel/{patientUuid}") { backStackEntry ->
                val patientUuid = backStackEntry.arguments?.getString("patientUuid") ?: ""
                val repository = TreatmentModule.provideTreatmentRepository() // Obtén el repositorio, no el factory

                PatientTreatmentPanelScreen(
                    patientUuid = patientUuid,
                    repository = repository,
                    onTreatmentSelected = {},
                    onAppointmentsSelected = {},
                    onCalendarSelected = {},
                    onSymptomsSelected = {}
                )
            }

        }
    }
}

@Composable
fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = color)
            Text(
                text = label.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}
