package oncontroldoctor.upc.edu.pe.shared.presentation.navigation
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.Face
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
////import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
////import oncontroldoctor.upc.edu.pe.authentication.presentation.AuthModule
////import oncontroldoctor.upc.edu.pe.treatment.presentation.view.PatientsScreen
//
//data class NavigationItem(
//    val icon: ImageVector,
//    val title: String,
//    val route: String
//)

//@Composable
//fun HomeScreen(
//    onNavigateToPatientDetail: (String, String) -> Unit
//) {
//    val navController = rememberNavController()
//
//    val token = remember { SessionHolder.getToken() ?: "" }
//    val doctorUuid = remember { SessionHolder.getUserUuid() ?: "" }
//
//    val treatmentViewModel = remember { AuthModule.getTreatmentViewModel() }
//
//    val navigationItems = listOf(
//        NavigationItem(Icons.Default.Face, "Pacientes", "Patients"),
//        NavigationItem(Icons.Default.Email, "Mensajes", "Messages"),
//        NavigationItem(Icons.Default.DateRange, "Calendario", "Calendar"),
//        NavigationItem(Icons.Default.Notifications, "Notificaciones", "Notifications")
//    )
//    val selectedIndex = remember { mutableStateOf(0) }
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                navigationItems.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        selected = selectedIndex.value == index,
//                        icon = { Icon(item.icon, contentDescription = item.title) },
//                        label = { Text(item.title) },
//                        onClick = {
//                            selectedIndex.value = index
//                            navController.navigate(item.route) {
//                                launchSingleTop = true
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = "Patients",
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            composable("Patients") {
//                PatientsScreen(
//                    viewModel = treatmentViewModel,
//                    doctorUuid = doctorUuid,
//                    token = token,
//                    onNavigateToPatientDetail = onNavigateToPatientDetail
//                )
//            }
//
//            composable("Messages") {
//                Text("Mensajes", modifier = Modifier.fillMaxSize())
//            }
//            composable("Calendar") {
//                Text("Calendario", modifier = Modifier.fillMaxSize())
//            }
//            composable("Notifications") {
//                Text("Notificaciones", modifier = Modifier.fillMaxSize())
//            }
//        }
//    }
// }
