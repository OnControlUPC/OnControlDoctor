package oncontroldoctor.upc.edu.pe.shared.data.model

data class S3UploadRequest(
    val category: String,
    val filename: String,
    val contentType: String,
    val userId: Long
)
