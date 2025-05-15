package oncontroldoctor.upc.edu.pe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class DoctorEntity (
    @PrimaryKey
    val name: String,
    val lastname: String,
    val email: String,
    val dni: String
)
