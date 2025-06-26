package oncontroldoctor.upc.edu.pe.billing.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.billing.domain.model.Plan

@Composable
fun PlanesCarousel(plans: List<Plan>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(plans) { plan ->
            Card(
                modifier = Modifier
                    .width(250.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(plan.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${plan.durationDays} days", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Price: S/.${plan.priceAmount} ${plan.currencyCode}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Features:", style = MaterialTheme.typography.labelMedium)
                    plan.features.forEach { feature ->
                        Text("• $feature", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* Acción de suscripción */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Subscribe Now")
                    }
                }
            }
        }
    }
}
