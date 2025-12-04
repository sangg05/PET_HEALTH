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
    @PrimaryKey var recordId: String = "",
    var petId: String = "",
    var date: Long = 0L,
    var symptom: String? = null,
    var diagnosis: String? = null,
    var prescription: String? = null,
    var weight: Float? = null,
    var height: Float? = null,
    var alert: Boolean = false
)
