package oncontroldoctor.upc.edu.pe.profile.presentation.components

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import coil3.compose.AsyncImage
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import oncontroldoctor.upc.edu.pe.shared.data.model.S3UploadRequest
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants.BASE_URL
import oncontroldoctor.upc.edu.pe.shared.data.remote.S3Service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


@Composable
fun ImageUploadSection(
    context: android.content.Context,
    token: String,
    userId: Long,
    urlPhoto: String,
    onImageUploaded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var uploadInProgress by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(result.data!!)
            croppedUri?.let { uri ->
                coroutineScope.launch {
                    uploadInProgress = true
                    try {
                        val s3Service = Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(S3Service::class.java)

                        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                        val fileName = "avatar_${System.currentTimeMillis()}.jpg"

                        val uploadRequest = S3UploadRequest(
                            category = "profile",
                            filename = fileName,
                            contentType = mimeType,
                            userId = userId
                        )

                        val response = s3Service.getSignedUrl("Bearer $token", uploadRequest)

                        if (response.isSuccessful) {
                            val uploadUrl = response.body()?.uploadUrl
                            val accessUrl = response.body()?.accessUrl

                            Log.d("UPLOAD", "Signed URL obtained: $uploadUrl")
                            Log.d("UPLOAD", "Access URL received: $accessUrl")

                            withContext(Dispatchers.IO) {
                                val tempFile = uri.toFile()
                                val requestBody = tempFile.readBytes().toRequestBody(mimeType.toMediaType())

                                val uploadHttpRequest = Request.Builder()
                                    .url(uploadUrl!!)
                                    .put(requestBody)
                                    .addHeader("Content-Type", mimeType)
                                    .build()

                                val client = OkHttpClient()
                                val result = client.newCall(uploadHttpRequest).execute()

                                if (result.isSuccessful) {
                                    onImageUploaded(accessUrl ?: "")
                                    Log.d("UPLOAD", "Image uploaded successfully.")
                                } else {
                                    Log.e("UPLOAD", "Upload failed with code: ${result.code}")
                                }
                            }
                        } else {
                            Log.e("UPLOAD", "Failed to get signed URL: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("UPLOAD", "Exception during upload", e)
                    } finally {
                        uploadInProgress = false
                    }
                }
            }
        }
    }


    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
            val intent = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f)
                .getIntent(context)
            cropLauncher.launch(intent)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.outline, shape = CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
        ) {
            if (urlPhoto.isNotBlank()) {
                AsyncImage(
                    model = urlPhoto,
                    contentDescription = "Selected profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    "No hay imagen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Seleccionar Foto de Perfil")
        }
        if (uploadInProgress) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Cargando imagen...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}