package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.ReminderEntity

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminderEntity: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE petId = :petId ORDER BY date ASC")
    suspend fun getReminders(petId: String): List<ReminderEntity>

    @Query("UPDATE reminders SET notificationSent = :isSent WHERE reminderId = :id")
    suspend fun updateNotificationSent(id: String, isSent: Boolean)

    @Delete
    suspend fun deleteReminder(reminderEntity: ReminderEntity)
}
