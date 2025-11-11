package com.example.pet_health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["petId"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class Reminder(
    @PrimaryKey val reminderId: String,
    val petId: String,
    val type: String, // tiêm phòng, tái khám, tẩy giun
    val date: Long,
    val repeat: String? = null, // hàng tuần/tháng/không
    val note: String? = null,
    val notificationSent: Boolean = false
)
