package pet_health.data.local.dao

import androidx.room.*
import pet_health.data.model.HealthRecord

@Dao
interface HealthRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE petId = :petId ORDER BY date DESC")
    suspend fun getRecordsForPet(petId: String): List<HealthRecord>

    @Delete
    suspend fun deleteRecord(record: HealthRecord)
}
