package oncontroldoctor.upc.edu.pe.profile.presentation.components

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
    onImageUploaded: (String) -> Unit
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

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, shape = CircleShape)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            if (urlPhoto. isNotBlank()) {
                AsyncImage(
                    model = urlPhoto,
                    contentDescription = "Selected profile photo",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )
            } else {
                Text("No image yet", color = Color.DarkGray)
            }
        }
        Button(onClick = {
            imagePickerLauncher.launch("image/*")
        }) {
            Text("Select Profile Photo")
        }
        if (uploadInProgress) {
            Text("Uploading image...")
        }
    }

}