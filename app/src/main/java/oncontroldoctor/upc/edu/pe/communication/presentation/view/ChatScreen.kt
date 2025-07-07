package oncontroldoctor.upc.edu.pe.communication.presentation.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.communication.presentation.components.ChatViewModelFactory
import oncontroldoctor.upc.edu.pe.communication.presentation.viewmodel.ChatViewModel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import oncontroldoctor.upc.edu.pe.R

@Composable
fun ChatScreen(
    patientUuid: String,
    repository: ChatRepository,
    symptomId: String? = null,
    onBack: () -> Unit
) {
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(repository)
    )
    val initialMessages by viewModel.initialMessages.collectAsState()
    val realtimeMessages by viewModel.realtimeMessages.collectAsState()
    val patient by viewModel.patient.collectAsState()
    val patientName = (patient?.firstName + " " + patient?.lastName)
    val symptomText by viewModel.symptomText.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val allMessages = (initialMessages + realtimeMessages)
        .distinctBy { it.id }
        .sortedBy { it.createdAt }

    LaunchedEffect(patientUuid) {
        viewModel.loadInitialMessages(patientUuid)
        viewModel.startChat(patientUuid)
    }
    LaunchedEffect(symptomId) {
        if (!symptomId.isNullOrEmpty()) {
            viewModel.loadSymptomText(symptomId.toLong())
        }
    }
    LaunchedEffect(symptomText) {
        if (text.isEmpty() && symptomText.isNotEmpty()) {
            text = symptomText
        }
    }
    LaunchedEffect(allMessages.size) {
        if (allMessages.isNotEmpty()) {
            listState.animateScrollToItem(allMessages.size - 1)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.disconnect() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = ShaderBrush(
                    ImageShader(
                        image = loadImageBitmapFromRes(R.drawable.background2),
                        tileModeX = TileMode.Repeated,
                        tileModeY = TileMode.Repeated
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = patient?.photoUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                if (patient?.photoUrl.isNullOrEmpty()) {
                    Text(
                        patientName.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = patientName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allMessages) { message ->
                MessageBubble(message, patientUuid)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                placeholder = { Text("Escribe un mensaje...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                maxLines = 7,
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val cleanText = text.trimEnd('\n')
                    viewModel.send(cleanText)
                    text = ""
                },
                enabled = text.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    patientUuid: String
) {
    val isSentByCurrentUser = message.senderRole != "ROLE_PATIENT"

    val bubbleColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(
                    color = bubbleColor,
                    shape = if (isSentByCurrentUser) {
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 4.dp
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 16.dp
                        )
                    }
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatMessageDate(message.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

fun formatMessageDate(utcDate: String?): String {
    return try {
        val odt = OffsetDateTime.parse(utcDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val zone = ZoneId.systemDefault()
        val localZoned = odt.atZoneSameInstant(zone)
        val localDate = localZoned.toLocalDate()
        val today = LocalDate.now(zone)
        if (localDate.isEqual(today)) {
            val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
            localZoned.format(timeFmt)
        } else {
            val dateTimeFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            localZoned.format(dateTimeFmt)
        }
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

@Composable
fun loadImageBitmapFromRes(resId: Int): ImageBitmap {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    return bitmap.asImageBitmap()
}