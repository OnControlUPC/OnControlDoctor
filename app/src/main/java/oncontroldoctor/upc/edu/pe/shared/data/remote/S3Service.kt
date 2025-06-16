package oncontroldoctor.upc.edu.pe.shared.data.remote

import oncontroldoctor.upc.edu.pe.shared.data.model.S3UploadRequest
import oncontroldoctor.upc.edu.pe.shared.data.model.S3UrlResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface S3Service {
    @POST("storage/presign-upload")
    suspend fun getSignedUrl(
        @Header("Authorization") token: String,
        @Body body: S3UploadRequest
    ): Response<S3UrlResponse>
}