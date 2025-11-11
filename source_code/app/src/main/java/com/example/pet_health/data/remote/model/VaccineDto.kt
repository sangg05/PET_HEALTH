package com.example.pet_health.data.remote.model

data class VaccineDto(
    val id: String,
    val petId: String,
    val name: String,
    val date: Long,
    val clinic: String? = null,
    val doseNumber: Int? = null,
    val note: String? = null,
    val photoUrl: String? = null,
    val nextDoseDate: Long? = null
)
