package oncontroldoctor.upc.edu.pe.billing.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.KeyValidationState
@Composable
fun SubscriptionKeyInput(
    keyState: KeyValidationState,
    onValidateKey: (String) -> Unit,
    onRedeemKey: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Enter Subscription Key") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onValidateKey(code) },
            enabled = code.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate Key")
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (keyState) {
            is KeyValidationState.Loading -> {
                CircularProgressIndicator()
            }

            is KeyValidationState.Invalid -> {
                Text(keyState.error, color = Color.Red)
            }

            is KeyValidationState.Valid -> {
                Text("Key valid for: ${keyState.key.durationDays} days")
                Button(
                    onClick = onRedeemKey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Redeem Key")
                }
            }

            is KeyValidationState.Redeemed -> {
                Text("Key successfully redeemed!", color = Color.Green)
            }

            KeyValidationState.Idle -> {}
        }
    }
}

