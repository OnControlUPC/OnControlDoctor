package oncontroldoctor.upc.edu.pe.dashboard

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import oncontroldoctor.upc.edu.pe.dashboard.data.local.PlanEntity
import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.PlanDao
import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.SubscriptionDao

@Database(
    entities = [PlanEntity::class, SubscriptionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "onco_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
