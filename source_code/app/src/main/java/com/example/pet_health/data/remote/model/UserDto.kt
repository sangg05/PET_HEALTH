package com.example.pet_health.data.remote.model

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatarUrl: String? = null
)