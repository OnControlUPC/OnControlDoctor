package oncontroldoctor.upc.edu.pe.communication.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import oncontroldoctor.upc.edu.pe.communication.data.local.LastPatientMessageEntity

@Dao
interface LastPatientMessageDao {
    @Query("SELECT * FROM last_patient_message WHERE patientUuid = :patientUuid LIMIT 1")
    suspend fun getLastMessageByPatientUuid(patientUuid: String): LastPatientMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLastMessage(entity: LastPatientMessageEntity)
}