package com.example.pet_health.data.remote.model

data class PetImageDto(
    val id: String,
    val petId: String,
    val imageUrl: String,
    val description: String? = null
)
