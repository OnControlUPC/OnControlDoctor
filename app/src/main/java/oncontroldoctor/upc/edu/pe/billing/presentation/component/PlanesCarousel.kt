package oncontroldoctor.upc.edu.pe.billing.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
fun PlanesCarousel(plans: List<Plan>){
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(plans){ plan ->
            PlanCard(plan)
        }
    }
}

@Composable
fun PlanCard(plan: Plan){
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .width(220.dp)
            .height(360.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plan.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: ${plan.priceAmount} ${plan.currencyCode}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Duration: ${plan.durationDays} days",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Max patients: ${plan.maxPatients}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Storage: ${plan.maxStorageMb} MB",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Features:", style = MaterialTheme.typography.labelMedium)
            plan.features.forEach { feature ->
                Text(text = "• $feature", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text("Comprar")
            }
        }
    }
}