package oncontroldoctor.upc.edu.pe.profile.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel.UiState

@Composable
fun CompleteProfileEntry(
    viewModel: CompleteProfileViewModel,
    token: String,
    onProfileCompleted: (uuid: String) -> Unit,
    onRequireProfileCompletion: () -> Unit,
    onAccountDeactivated: () -> Unit
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        viewModel.checkProfile(token)
    }

    when(uiState){
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.ProfileLoaded -> {
            val uuid = (uiState as UiState.ProfileLoaded).uuid
            LaunchedEffect(uuid) {
                onProfileCompleted(uuid)
            }
        }

        is UiState.ShouldCompleteProfile -> {
            LaunchedEffect(Unit) {
                onRequireProfileCompletion()
            }
        }

        is UiState.Error -> {
            val message = (uiState as UiState.Error).message
            LaunchedEffect(message) {
                onAccountDeactivated()
            }
        }
    }
}