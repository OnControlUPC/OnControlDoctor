package oncontroldoctor.upc.edu.pe.treatment.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.*
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SymptomsList(
    symptoms: List<SymptomDto>,
    from: LocalDateTime,
    to: LocalDateTime,
    patientUuid: String? = null,
    navControllerG: NavController
) {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val grouped = symptoms
        .mapNotNull {
            val date = try { LocalDateTime.parse(it.loggedAt, formatter) } catch (e: Exception) { null }
            if (date != null && !date.isBefore(from) && !date.isAfter(to))
                it to date
            else null
        }
        .sortedByDescending { it.second }
        .groupBy { (_, date) ->
            val daysFromStart = java.time.Duration.between(from.toLocalDate().atStartOfDay(), date.toLocalDate().atStartOfDay()).toDays().toInt()
            val groupStart = from.toLocalDate().plusDays((daysFromStart / 7) * 7L)
            val groupEnd = groupStart.plusDays(6).coerceAtMost(to.toLocalDate())
            groupStart to groupEnd
        }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (range, items) ->
            item {
                Text(
                    text = "Del ${range.first.format(outputFormatter)} al ${range.second.format(outputFormatter)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(top = 8.dp)
                )
            }
            items(items) { (symptom) ->
                var expanded by remember { mutableStateOf(false) }

                val cardColor = when (symptom.severity) {
                    "MILD" -> SeverityMildBackground
                    "MODERATE" -> SeverityModerateBackground
                    "SEVERE" -> SeveritySevereBackground
                    "CRITICAL" -> SeverityCriticalBackground
                    else -> SeverityDefaultBackground
                }
                val textColor = when (symptom.severity) {
                    "MILD" -> SeverityMildText
                    "MODERATE" -> SeverityModerateText
                    "SEVERE" -> SeveritySevereText
                    "CRITICAL" -> SeverityCriticalText
                    else -> SeverityDefaultText
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { expanded = !expanded },
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) { // Padding interno de la tarjeta
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) { // Columna para el texto del síntoma
                                val formattedDate = try {
                                    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                    LocalDateTime.parse(symptom.loggedAt, inputFormatter).format(outputFormatter)
                                } catch (e: Exception) {
                                    "Fecha inválida"
                                }

                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.7f)), // Texto de fecha más sutil
                                    fontWeight = FontWeight.Normal // Peso normal
                                )
                                Text(
                                    text = "Síntoma: ${symptom.symptomType}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                                    fontWeight = FontWeight.Medium // Un poco más de peso
                                )
                                Text(
                                    text = "Severidad: ${symptom.severity}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
                                )
                            }
                            // Botón de contacto
                            IconButton(
                                onClick = {
                                    navControllerG.navigate("messages/${patientUuid}?symptomId=${symptom.id}")
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Filled.MailOutline,
                                    contentDescription = "Contactar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (expanded) {
                            Spacer(Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Nota: ${symptom.notes}",
                                style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.9f))
                            )
                        }
                    }
                }
            }
        }
    }
}


