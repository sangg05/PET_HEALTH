package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.HealthRecordEntity


@Dao
interface HealthRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records WHERE petId = :petId ORDER BY date DESC")
    suspend fun getRecordsForPet(petId: String): List<HealthRecordEntity>

    @Delete
    suspend fun deleteRecord(record: HealthRecordEntity)
}
