package oncontroldoctor.upc.edu.pe.profile.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionManager
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.presentation.components.ImageUploadSection
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel

@Composable
fun CompleteProfileScreen(
    viewModel: CompleteProfileViewModel,
    onProfileCompleted: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: return
    val userId = sessionManager.getUserId() ?: return

    var step by remember { mutableStateOf(1) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("DNI") }
    var documentNumber by remember { mutableStateOf("") }
    var cmpCode by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var urlPhoto by remember { mutableStateOf("") }

    val creationState = viewModel.profileCreationState.value

    LaunchedEffect(creationState) {
        if (creationState == true) {
            onProfileCompleted()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Complete your profile step $step to 3", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        when (step) {
            1 -> {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") })
            }

            2 -> {
                OutlinedTextField(value = documentNumber, onValueChange = { documentNumber = it }, label = { Text("Document Number") })
                OutlinedTextField(value = cmpCode, onValueChange = { cmpCode = it }, label = { Text("CMP Code") })
                OutlinedTextField(value = specialty, onValueChange = { specialty = it }, label = { Text("Specialty") })
            }

            3 -> {
                ImageUploadSection(
                    context = context,
                    token = token,
                    userId = userId,
                    urlPhoto = urlPhoto,
                    onImageUploaded = { uploadedUrl ->
                        urlPhoto = uploadedUrl
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (urlPhoto.isNotBlank()) {
            Text("Image selected and uploaded.")
            Text(urlPhoto, style = MaterialTheme.typography.bodySmall)
        } else if (step == 3) {
            Text("No image selected yet")
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (step > 1) {
                OutlinedButton(onClick = { step-- }) {
                    Text("Back")
                }
            }
            Button(onClick = {
                if (step < 3) {
                    step++
                } else {
                    val request = DoctorProfileRequest(
                        userId = userId,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phoneNumber = phoneNumber,
                        documentType = documentType,
                        documentNumber = documentNumber,
                        specialty = specialty,
                        CMPCode = cmpCode,
                        photoUrl = urlPhoto
                    )
                    viewModel.createProfile(token, request)
                }
            }) {
                Text(if (step < 3) "Next" else "Finalize")
            }
        }

        if (creationState == false) {
            Text("Error saving your profile", color = MaterialTheme.colorScheme.error)
        }
    }
}

