package com.example.pet_health.data.remote.model

data class PetDto(
    val id: String,
    val userId: String,
    val name: String,
    val species: String,
    val breed: String,
    val color: String? = null,
    val avatarUrl: String? = null,
    val birthDate: Long,
    val weightKg: Float,
    val sizeCm: Float? = null,
    val healthStatus: String,
    val adoptionDate: Long? = null
)