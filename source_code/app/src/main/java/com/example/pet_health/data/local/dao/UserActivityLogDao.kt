package pet_health.data.local.dao

import androidx.room.*
import pet_health.data.model.UserActivityLog

@Dao
interface UserActivityLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: UserActivityLog)

    @Query("SELECT * FROM user_activity_log WHERE userId = :uid ORDER BY timestamp DESC")
    suspend fun getLogs(uid: String): List<UserActivityLog>
}
