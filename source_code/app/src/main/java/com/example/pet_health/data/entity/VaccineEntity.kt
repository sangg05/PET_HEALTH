package com.example.pet_health.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccines",
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
data class VaccineEntity(
    // THÊM GIÁ TRỊ MẶC ĐỊNH CHO CÁC TRƯỜNG NÀY:
    @PrimaryKey
    val vaccineId: String = "",
    val petId: String = "",
    val name: String = "",
    val date: Long = 0L,

    // Các trường dưới này đã có mặc định rồi, giữ nguyên
    val clinic: String? = null,
    val doseNumber: Int? = null,
    val note: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val nextDoseDate: Long? = null
) {
    // Firebase cần constructor rỗng, việc thêm default value ở trên
    // sẽ giúp Kotlin tự động sinh ra constructor đó.
}