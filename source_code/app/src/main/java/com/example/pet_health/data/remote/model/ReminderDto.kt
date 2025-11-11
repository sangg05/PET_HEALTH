package com.example.pet_health.data.remote.model

data class ReminderDto(
    val id: String,
    val petId: String,
    val type: String,
    val date: Long,
    val repeat: String? = null,
    val note: String? = null,
    val notificationSent: Boolean = false
)