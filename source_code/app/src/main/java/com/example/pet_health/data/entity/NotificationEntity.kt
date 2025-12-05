package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val reminderId: String,          // Liên kết với Reminder
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),           // Thời gian nhận thông báo
    val isRead: Boolean = false       // Đã xem hay chưa
)
