package com.example.pet_health.data.remote.model

data class HealthRecordDto(
    val id: String,
    val petId: String,
    val date: Long,
    val symptom: String? = null,
    val diagnosis: String? = null,
    val prescription: String? = null,
    val weight: Float? = null,
    val height: Float? = null,
    val alert: Boolean = false
)
