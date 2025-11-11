package pet_health.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pet_health.data.local.dao.*
import com.example.pet_health.data.model.*

@Database(
    entities = [
        User::class,
        Pet::class,
        HealthRecord::class,
        Reminder::class,
        Vaccine::class,
        UserActivityLog::class,
        PetImage::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun petDao(): PetDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun reminderDao(): ReminderDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun userActivityLogDao(): UserActivityLogDao
    abstract fun petImageDao(): PetImageDao
}