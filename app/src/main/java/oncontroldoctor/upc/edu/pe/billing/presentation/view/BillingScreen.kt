package oncontroldoctor.upc.edu.pe.billing.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionManager
import oncontroldoctor.upc.edu.pe.billing.presentation.component.PlanesCarousel
import oncontroldoctor.upc.edu.pe.billing.presentation.component.SubscriptionKeyInput
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.BillingUiState
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.BillingViewModel
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.KeyValidationState
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.SubscriptionState

@Composable
fun BillingScreen(
    viewModel: BillingViewModel,
    adminId: Long,
    onSubscriptionValidated: () -> Unit
) {
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    val keyState by viewModel.keyState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: return

    LaunchedEffect(Unit) {
        viewModel.checkActiveSubscription(token, adminId)
    }
    when(subscriptionState){
        is SubscriptionState.Loading -> {
            CircularProgressIndicator()
        }
        is SubscriptionState.Active -> {
            LaunchedEffect(Unit) {
                onSubscriptionValidated()
            }
        }
        is SubscriptionState.NoActiveSubscription -> {
            LaunchedEffect(Unit) {
                viewModel.loadPlans(token)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Available Plans")
                Spacer(modifier = Modifier.height(8.dp))

                when (uiState) {
                    is BillingUiState.PlansLoaded -> {
                        val plans = (uiState as BillingUiState.PlansLoaded).plans
                        PlanesCarousel(plans = plans)
                    }
                    is BillingUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is BillingUiState.Error -> {
                        Text(
                            text = (uiState as BillingUiState.Error).message,
                            color = Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (keyState) {
                    is KeyValidationState.Redeemed -> {
                        LaunchedEffect(Unit) {
                            onSubscriptionValidated()
                            viewModel.checkActiveSubscription(token, adminId)
                        }
                    }
                    else -> {
                        SubscriptionKeyInput(
                            keyState = keyState,
                            onValidateKey = { code -> viewModel.validateKey(token,code) },
                            onRedeemKey = { viewModel.redeemKey(token, adminId) }
                        )
                    }
                }
            }
        }

        is SubscriptionState.Error -> {
            Text("Error verifying subscription", color = Color.Red)
        }
    }
}
