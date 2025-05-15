package oncontroldoctor.upc.edu.pe.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import oncontroldoctor.upc.edu.pe.presentation.view.CalendarView
import oncontroldoctor.upc.edu.pe.presentation.view.ChatView
import oncontroldoctor.upc.edu.pe.presentation.view.MessageListView
import oncontroldoctor.upc.edu.pe.presentation.view.NotificationView
import oncontroldoctor.upc.edu.pe.presentation.view.PatientListView

@Composable
@Preview
fun Home() {

    val navController = rememberNavController()

    val navigationItems = listOf(
        NavigationItem(
            icon = Icons.Default.Face,
            title = "Patients",
            route = "Patients"
        ),
        NavigationItem(
            icon = Icons.Default.Email,
            title = "Messages",
            route = "Messages"
        ),
        NavigationItem(
            icon = Icons.Default.DateRange,
            title = "Calendar",
            route = "Calendar"
        ),
        NavigationItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            route = "Notifications"
        )
    )

    val selectedIndex = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex.intValue == index,
                        icon = {
                            Icon(item.icon, contentDescription = null)
                        },
                        onClick = {
                            selectedIndex.intValue = index
                            navController.navigate(item.route)
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = "Patients",
            modifier = Modifier.padding(padding)
        ) {
            composable("Patients")
            {
                PatientListView(viewModel = viewModel()) { patientId ->
                    navController.navigate("patientDashboard/$patientId")
                }
            }
            composable("Messages")
            {
                MessageListView { chatId ->
                    navController.navigate("chat/$chatId")
                }
            }
            composable("chat/{chatId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId")?.toIntOrNull() ?: return@composable
                ChatView(chatId = chatId)
            }
            composable("Calendar")
            {
                CalendarView()
            }
            composable("Notifications")
            {
                NotificationView()
            }
        }

    }

}

data class NavigationItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)