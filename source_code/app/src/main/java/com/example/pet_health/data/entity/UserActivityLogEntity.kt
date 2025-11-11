package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "user_activity_log",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class UserActivityLogEntity(
    @PrimaryKey val logId: String,
    val userId: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)
