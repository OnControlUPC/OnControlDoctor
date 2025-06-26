package oncontroldoctor.upc.edu.pe.dashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import oncontroldoctor.upc.edu.pe.dashboard.data.local.PlanEntity

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans WHERE id = :id")
    suspend fun getPlanById(id: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)

    @Query("DELETE FROM plans")
    suspend fun clearAll()
}