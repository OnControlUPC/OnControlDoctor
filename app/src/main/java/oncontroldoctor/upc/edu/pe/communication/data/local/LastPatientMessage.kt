package oncontroldoctor.upc.edu.pe.communication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_patient_message")
data class LastPatientMessageEntity(
    @PrimaryKey val patientUuid: String,
    val lastMessageId: Long
)
