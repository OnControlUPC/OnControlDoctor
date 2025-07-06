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
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.*
import oncontroldoctor.upc.edu.pe.treatment.data.dto.SymptomDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SymptomsList(
    symptoms: List<SymptomDto>,
    from: LocalDateTime,
    to: LocalDateTime
) {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val grouped = symptoms
        .mapNotNull {
            val date = try { LocalDateTime.parse(it.loggedAt, formatter) } catch (e: Exception) { null }
            // Filtra los síntomas que están dentro del rango de fechas [from, to]
            if (date != null && !date.isBefore(from) && !date.isAfter(to)) // Simplificación de la condición de rango
                it to date
            else null
        }
        .sortedByDescending { it.second } // Ordenar por fecha de registro descendente
        .groupBy { (_, date) ->
            // Agrupar por semanas, donde la semana comienza desde 'from'
            val daysFromStart = java.time.Duration.between(from.toLocalDate().atStartOfDay(), date.toLocalDate().atStartOfDay()).toDays().toInt()
            val groupStart = from.toLocalDate().plusDays((daysFromStart / 7) * 7L)
            val groupEnd = groupStart.plusDays(6).coerceAtMost(to.toLocalDate()) // Asegura que el fin del grupo no exceda 'to'
            groupStart to groupEnd
        }

    LazyColumn(modifier = Modifier.fillMaxSize()) { // Asegura que la LazyColumn ocupe todo el espacio
        grouped.forEach { (range, items) ->
            item {
                // Encabezado de la semana con estilo similar a DateHeader
                Text(
                    text = "Del ${range.first.format(outputFormatter)} al ${range.second.format(outputFormatter)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary, // Usar el color primario del tema
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Padding consistente
                        .padding(top = 8.dp) // Padding superior para separación
                )
            }
            items(items) { (symptom) ->
                var expanded by remember { mutableStateOf(false) }

                // Determinar los colores de la tarjeta basados en la severidad del síntoma
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
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Padding consistente con otras tarjetas
                        .clickable { expanded = !expanded },
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Elevación sutil
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
                                onClick = { /* Contactar, sin acción definida aquí */ },
                                modifier = Modifier.size(40.dp) // Tamaño del icono
                            ) {
                                Icon(
                                    Icons.Filled.MailOutline,
                                    contentDescription = "Contactar",
                                    tint = MaterialTheme.colorScheme.primary // Usar el color primario del tema
                                )
                            }
                        }
                        if (expanded) {
                            Spacer(Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp) // Divisor sutil
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


