package oncontroldoctor.upc.edu.pe.shared.presentation.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost

import androidx.navigation.compose.rememberNavController
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionManager
import oncontroldoctor.upc.edu.pe.authentication.presentation.PresentationModule
import oncontroldoctor.upc.edu.pe.authentication.presentation.view.LoginScreen
import oncontroldoctor.upc.edu.pe.authentication.presentation.view.RegisterScreen
import oncontroldoctor.upc.edu.pe.billing.data.remote.BillingService
import oncontroldoctor.upc.edu.pe.billing.data.repository.BillingRepositoryImpl
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetActiveSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetPlansUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.UseSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.ValidateSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.billing.presentation.view.BillingScreen
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.BillingViewModel
import oncontroldoctor.upc.edu.pe.profile.presentation.view.CompleteProfileEntry
import oncontroldoctor.upc.edu.pe.profile.presentation.view.CompleteProfileScreen
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun OncoAppNav(startDestination: Boolean) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val hasToken = SessionManager(context).getToken() != null
    NavHost(
        navController = navController,
        startDestination = when {
            hasToken -> "checkProfile"
            else -> "login"
        }
    ) {

        composable("login") {
            val loginViewModel = PresentationModule.getLoginViewModel()
            val context = LocalContext.current
            LoginScreen(
                viewModel = loginViewModel,
                onRegisterClick = {navController.navigate("register") },
                onLoginSuccess = {session ->
                    SessionManager(context).saveSession(session.id, session.username, session.token)
                    navController.navigate("checkProfile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            val registerViewModel = PresentationModule.getRegisterViewModel()
            val context = LocalContext.current
            RegisterScreen(
                viewModel = registerViewModel,
                onLoginClick = { navController.navigate("login") },
                onRegisterSuccess = {session ->
                    SessionManager(context).saveSession(session.id, session.username, session.token)
                    navController.navigate("checkProfile") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("checkProfile"){
            val context = LocalContext.current
            val token = SessionManager(context).getToken()
            if(token != null){
                val viewModel = PresentationModule.getCompleteProfileViewModel()
                CompleteProfileEntry(
                    viewModel = viewModel,
                    token = token,
                    onProfileCompleted = {uuid ->
                        SessionManager(context).saveUuid(uuid)
                        navController.navigate("billing"){
                            popUpTo("checkProfile"){inclusive = true}
                        }
                    },
                    onRequireProfileCompletion = {
                        navController.navigate("completeProfile"){
                            popUpTo("checkProfile"){inclusive = true}
                        }
                    },
                    onAccountDeactivated = {
                        SessionManager(context).clearSession()
                        navController.navigate("login"){
                            popUpTo(0)
                        }
                    }
                )
            } else {
                navController.navigate("login")
            }
        }
        composable("completeProfile"){
            val viewModel = PresentationModule.getCompleteProfileViewModel()
            CompleteProfileScreen(
                viewModel = viewModel,
                onProfileCompleted = {
                    navController.navigate("home"){
                        popUpTo("completeProfile") { inclusive = true }
                    }
                }
            )
        }
        composable("billing") {
            val context = LocalContext.current
            val adminId = SessionManager(context).getUserId()
            val billingService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BillingService::class.java)

            val billingRepository = BillingRepositoryImpl(billingService)

            val viewModel = remember {
                BillingViewModel(
                    getPlansUseCase = GetPlansUseCase(billingRepository),
                    validateKeyUseCase = ValidateSubscriptionKeyUseCase(billingRepository),
                    useKeyUseCase = UseSubscriptionKeyUseCase(billingRepository),
                    getActiveSubscriptionUseCase = GetActiveSubscriptionUseCase(billingRepository)
                )
            }

            BillingScreen(
                viewModel = viewModel,
                adminId = adminId,
                onSubscriptionValidated = { navController.navigate("home") }
            )
        }

        composable("home") {
            val context = LocalContext.current
            Button(onClick = {
                SessionManager(context).clearSession()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }) {
                Text("Cerrar sesión")
            }
        }

    }
}
