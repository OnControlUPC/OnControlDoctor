package oncontroldoctor.upc.edu.pe.shared.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.authentication.presentation.AuthModule
import oncontroldoctor.upc.edu.pe.authentication.presentation.view.LoginScreen
import oncontroldoctor.upc.edu.pe.authentication.presentation.view.RegisterScreen
import oncontroldoctor.upc.edu.pe.billing.presentation.BillingModule
import oncontroldoctor.upc.edu.pe.billing.presentation.view.BillingScreen
import oncontroldoctor.upc.edu.pe.communication.presentation.ChatModule
import oncontroldoctor.upc.edu.pe.communication.presentation.view.ChatScreen
import oncontroldoctor.upc.edu.pe.dashboard.AppDatabase
import oncontroldoctor.upc.edu.pe.dashboard.presentation.DashboardModule
import oncontroldoctor.upc.edu.pe.dashboard.presentation.view.DashboardScreen
import oncontroldoctor.upc.edu.pe.profile.presentation.ProfileModule
import oncontroldoctor.upc.edu.pe.profile.presentation.view.CompleteProfileScreen
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel

@Composable
fun OncoAppNav() {
    val navController = rememberNavController()
    val hasToken = SessionHolder.getToken() != null
    val startDestination = when {
        !hasToken -> "login"
        else -> "checkProfile"
    }
    NavHost(navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            val loginViewModel = AuthModule.provideLoginViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = {
                    SessionHolder.saveSession(it.id, it.username, it.token)
                    navController.navigate("checkProfile") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("register") {
            val registerViewModel = AuthModule.provideRegisterViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("checkProfile") {
            val viewModel = ProfileModule.getCompleteProfileViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.checkProfile()
            }

            when (val state = uiState) {
                is CompleteProfileViewModel.UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("CARGADOS2")
                        CircularProgressIndicator()
                    }
                }

                is CompleteProfileViewModel.UiState.ShouldCompleteProfile -> {
                    navController.navigate("completeProfile") {
                        popUpTo("checkProfile") { inclusive = true }
                    }
                }

                is CompleteProfileViewModel.UiState.ProfileLoaded -> {
                    SessionHolder.saveUuid(state.uuid)
                    navController.navigate("checkSubscription") {
                        popUpTo("checkProfile") { inclusive = true }
                    }
                }

                is CompleteProfileViewModel.UiState.Error -> {
                    LaunchedEffect(Unit) {
                        SessionHolder.clearSession()
                        navController.navigate("login") {
                            popUpTo("checkProfile") { inclusive = true }
                        }
                    }
                }
            }
        }
        composable("completeProfile") {
            val viewModel = ProfileModule.getCompleteProfileViewModel()
            CompleteProfileScreen(
                viewModel = viewModel,
                onProfileCompleted = {
                    navController.navigate("checkSubscription") {
                        popUpTo("completeProfile") { inclusive = true }
                    }
                }
            )
        }


        composable("checkSubscription") {
            val viewModel = remember { BillingModule.provideBillingViewModel() }
            val isLoading by viewModel.isLoading.collectAsState()
            val hasSubscription by viewModel.hasSubscription.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.checkSubscription()
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    Text("Checking subscription...")
                }

                hasSubscription -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("dashboard") {
                            popUpTo("checkSubscription") { inclusive = true }
                        }
                    }
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("billing") {
                            popUpTo("checkSubscription") { inclusive = true }
                        }
                    }
                }

            }
        }

        composable("billing") {
            val billingViewModel = remember { BillingModule.provideBillingViewModel() }
            BillingScreen(
                viewModel = billingViewModel,
                onSubscriptionValidated = {
                    navController.navigate("dashboard") {
                        popUpTo("billing") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            val context = LocalContext.current
            val db = AppDatabase.getInstance(context)

            val dashboardViewModel = remember {
                DashboardModule.provideViewModel(
                    subscriptionDao = db.subscriptionDao(),
                    planDao = db.planDao()
                )
            }
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                navControllerG = navController
            )
        }
        composable(
            route = "messages/{patientUuid}?symptomId={symptomId}",
            arguments = listOf(
                navArgument("patientUuid") { type = NavType.StringType },
                navArgument("symptomId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val patientUuid = backStackEntry.arguments?.getString("patientUuid") ?: ""
            val symptomId = backStackEntry.arguments?.getString("symptomId")
            val repository = ChatModule.provideChatRepository()
            ChatScreen(
                patientUuid = patientUuid,
                repository = repository,
                symptomId = symptomId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}




























