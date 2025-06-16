package oncontroldoctor.upc.edu.pe.billing.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.KeyValidationState

@Composable
fun SubscriptionKeyInput(
    keyState: KeyValidationState,
    onValidateKey: (String) -> Unit,
    onRedeemKey: () -> Unit
){
    var keyInput by remember { mutableStateOf("") }
    var errorMessage by remember {mutableStateOf<String?>(null)}

    Column{
        OutlinedTextField(
            value = keyInput,
            onValueChange = {
                keyInput = it
                errorMessage = null
            },
            label = { Text("Enter your Subscription Key") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        if(errorMessage != null){
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        if (keyState is KeyValidationState.Invalid) {
            Text(
                text = keyState.error,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (keyState){
            is KeyValidationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is KeyValidationState.Valid -> {
                Text(
                    text = "Key is valid: ${keyState.key.code}",
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(onClick = onRedeemKey){
                    Text("Redeem Key")
                }
            } else -> {
                Button(
                    onClick = {onValidateKey(keyInput)},
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Validate Key")
                }
            }
        }
    }
}