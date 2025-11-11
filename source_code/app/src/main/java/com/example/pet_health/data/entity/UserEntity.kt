package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val passwordHash: String,
    val personalNotes: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
