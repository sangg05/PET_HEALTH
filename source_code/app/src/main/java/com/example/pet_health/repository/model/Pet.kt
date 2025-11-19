package com.example.pet_health.repository.model

data class Pet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val avatarUrl: String?,
    val weightKg: Float,
    val sizeCm: Float?
)
