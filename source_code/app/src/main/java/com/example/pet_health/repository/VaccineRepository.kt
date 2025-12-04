package com.example.pet_health.repository

import android.net.Uri
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.local.datasources.VaccineLocalDataSource
import com.example.pet_health.data.remote.datasources.VaccineRemoteDataSource

class VaccineRepository(
    private val local: VaccineLocalDataSource,
    private val remote: VaccineRemoteDataSource
) {

    // Thêm vaccine / thuốc
    suspend fun addVaccine(
        id: String, // <--- THÊM THAM SỐ NÀY (Nhận ID từ ViewModel)
        petId: String,
        name: String,
        date: Long,
        clinic: String? = null,
        doseNumber: Int? = null,
        note: String? = null,
        imageUri: Uri? = null,
        nextDoseDate: Long? = null
    ): Boolean {
        // Không tự tạo UUID ở đây nữa
        // val id = UUID.randomUUID().toString()

        // Upload ảnh
        val uploadedUrl = remote.uploadVaccineImage(petId, imageUri)

        // Tạo entity với ID được truyền vào
        val vaccine = VaccineEntity(
            vaccineId = id, // Sử dụng ID từ tham số
            petId = petId,
            name = name,
            date = date,
            clinic = clinic,
            doseNumber = doseNumber,
            note = note,
            photoUrl = uploadedUrl,
            nextDoseDate = nextDoseDate
        )

        return try {
            // Lưu Firestore
            remote.uploadVaccine(vaccine)
            // Lưu Room
            local.insert(vaccine)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Vẫn lưu vào Room để dùng offline nếu mạng lỗi
            local.insert(vaccine)
            false
        }
    }

    // Lấy danh sách vaccine theo petId
    suspend fun getVaccinesForPet(petId: String) =
        local.getByPet(petId)
}