package com.example.pet_health.repository

import android.net.Uri
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.local.datasources.VaccineLocalDataSource
import com.example.pet_health.data.remote.datasources.VaccineRemoteDataSource
import java.util.UUID

class VaccineRepository(
    private val local: VaccineLocalDataSource,
    private val remote: VaccineRemoteDataSource
) {

    // Thêm vaccine / thuốc
    suspend fun addVaccine(
        petId: String,
        name: String,
        date: Long,
        clinic: String? = null,
        doseNumber: Int? = null,
        note: String? = null,
        imageUri: Uri? = null,
        nextDoseDate: Long? = null
    ): Boolean {
        val id = UUID.randomUUID().toString()

        // Upload ảnh
        val uploadedUrl = remote.uploadVaccineImage(petId, imageUri)

        // Tạo entity
        val vaccine = VaccineEntity(
            vaccineId = id,
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
            // Optionally vẫn lưu Room nếu Firebase fail
            local.insert(vaccine)
            false
        }
    }

    // Lấy danh sách vaccine theo petId
    suspend fun getVaccinesForPet(petId: String) =
        local.getByPet(petId)
}
