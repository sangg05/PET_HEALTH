package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.UserActivityLogEntity

@Dao
interface UserActivityLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: UserActivityLogEntity)

    @Query("SELECT * FROM user_activity_log WHERE userId = :uid ORDER BY timestamp DESC")
    suspend fun getLogs(uid: String): List<UserActivityLogEntity>
}
