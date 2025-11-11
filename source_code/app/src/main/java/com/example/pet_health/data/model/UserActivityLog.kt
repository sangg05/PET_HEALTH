package com.example.pet_health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "user_activity_log",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class UserActivityLog(
    @PrimaryKey val logId: String,
    val userId: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)
