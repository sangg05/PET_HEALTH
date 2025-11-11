package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "health_records",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["petId"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class HealthRecordEntity(
    @PrimaryKey val recordId: String,
    val petId: String,
    val date: Long,
    val symptom: String? = null,
    val diagnosis: String? = null,
    val prescription: String? = null,
    val weight: Float? = null,
    val height: Float? = null,
    val alert: Boolean = false
)
