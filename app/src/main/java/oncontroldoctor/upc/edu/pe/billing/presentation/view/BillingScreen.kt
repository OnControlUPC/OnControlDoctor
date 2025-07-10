package oncontroldoctor.upc.edu.pe.billing.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.billing.presentation.component.PlanesCarousel
import oncontroldoctor.upc.edu.pe.billing.presentation.component.SubscriptionKeyInput
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.BillingViewModel

@Composable
fun BillingScreen(
    viewModel: BillingViewModel,
    onSubscriptionValidated: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val hasSubscription by viewModel.hasSubscription.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val plans = viewModel.plans
    val keyState by viewModel.keyState.collectAsState()

    when (hasSubscription) {
        true -> {
            LaunchedEffect(Unit) {
                onSubscriptionValidated()
            }
        }

        false -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Obtén nuestros planes",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                LaunchedEffect(Unit) {
                    viewModel.loadPlans()
                }

                if (plans.isEmpty() && !isLoading) {
                    viewModel.loadPlans()
                }

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    PlanesCarousel(
                        plans = plans,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SubscriptionKeyInput(
                        keyState = keyState,
                        onValidateKey = viewModel::validateKey,
                        onRedeemKey = {
                            viewModel.redeemKey {
                                viewModel.checkSubscription()
                            }
                        }
                    )
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}