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
    @PrimaryKey val petId: String,
    val userId: String,
    val name: String,
    val species: String,     // Loài: chó, mèo, thỏ…
    val breed: String,       // Giống
    val color: String? = null,      // Màu lông
    var imageUrl: String? = null,  // URL ảnh avatar
    val birthDate: Long,             // timestamp sinh nhật
    var weightKg: Float,             // cân nặng
    var sizeCm: Float? = null,       // chiều cao/cỡ
    var healthStatus: String,        // tình trạng sức khỏe
    val adoptionDate: Long? = null   // timestamp ngày nhận nuôi
)
