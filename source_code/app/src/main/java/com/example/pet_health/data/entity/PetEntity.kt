package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"], // khóa chính của UserEntity
            childColumns = ["userId"],  // khóa ngoại
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")] // tạo index cho search theo userId
)
data class PetEntity(
    @PrimaryKey
    val petId: String = "",
    val userId: String = "",
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val color: String? = null,
    val imageUrl: String? = null,
    val birthDate: Long = 0L,
    val weightKg: Float = 0f,
    val sizeCm: Float? = null,
    val healthStatus: String = "",
    val adoptionDate: Long? = null
)
