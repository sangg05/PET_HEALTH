package com.example.pet_health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Pet(
    @PrimaryKey val petId: String,
    val userId: String,
    val name: String,
    val species: String, // chó, mèo, thỏ...
    val breed: String,
    val color: String? = null,
    val avatarUrl: String? = null,
    val birthDate: Long,
    var weightKg: Float,
    var sizeCm: Float? = null,
    var healthStatus: String,
    val adoptionDate: Long? = null
)
