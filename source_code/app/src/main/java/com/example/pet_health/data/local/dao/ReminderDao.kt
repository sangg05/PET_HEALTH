package pet_health.data.local.dao

import androidx.room.*
import pet_health.data.model.Reminder

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE petId = :petId ORDER BY date ASC")
    suspend fun getReminders(petId: String): List<Reminder>

    @Query("UPDATE reminders SET notificationSent = :isSent WHERE reminderId = :id")
    suspend fun updateNotificationSent(id: String, isSent: Boolean)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}
