package com.example.pet_health.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pet_health.data.local.dao.*
import com.example.pet_health.data.entity.*
import com.example.pet_health.data.local.dao.PetDao
import com.example.pet_health.data.local.dao.SymptomLogDao
import com.example.pet_health.data.local.dao.NotificationDao


@Database(
    entities = [
        UserEntity::class,
        PetEntity::class,
        HealthRecordEntity::class,
        SymptomLogEntity::class,
        ReminderEntity::class,
        VaccineEntity::class,
        UserActivityLogEntity::class,
        PetImageEntity::class,
        NotificationEntity::class
    ],
    version = 3,
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
    abstract fun symptomLogDao(): SymptomLogDao
    abstract fun notificationDao(): NotificationDao


    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}