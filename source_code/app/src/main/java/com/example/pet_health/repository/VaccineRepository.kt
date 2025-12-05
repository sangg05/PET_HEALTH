package com.example.pet_health.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.repository.CloudinaryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import pet_health.data.local.AppDatabase

class VaccineRepository(context: Context) {
    private val cloudinary = CloudinaryRepository()
    private val db = AppDatabase.getDatabase(context)
    private val vaccineDao = db.vaccineDao()
    private val petDao = db.petDao()
    private val firestore = FirebaseFirestore.getInstance()

    // Lấy tất cả vaccine từ ROOM
    fun getAllVaccines(): Flow<List<VaccineEntity>> = vaccineDao.getAllVaccines()

    // Lấy tất cả pet
    suspend fun getAllPets(): List<PetEntity> {
        return petDao.getAllPets()
    }

    // ---------------------------
    //         ADD VACCINE
    // ---------------------------
    suspend fun addVaccine(vaccine: VaccineEntity) {
        vaccineDao.insertVaccine(vaccine)

        firestore.collection("vaccines")
            .document(vaccine.petId)
            .collection("records")
            .document(vaccine.vaccineId)
            .set(vaccineToMap(vaccine))
    }

    // ---------------------------
    //      DELETE VACCINE
    // ---------------------------
    suspend fun deleteVaccine(vaccine: VaccineEntity) {
        vaccineDao.deleteVaccine(vaccine)

        try {
            firestore.collection("vaccines")
                .document(vaccine.petId)
                .collection("records")
                .document(vaccine.vaccineId)
                .delete()
                .await()
        } catch (e: Exception) {
        }
    }

    // ---------------------------
    //      UPDATE VACCINE
    // ---------------------------
    suspend fun updateVaccine(vaccine: VaccineEntity) {
        vaccineDao.updateVaccine(vaccine)

        firestore.collection("vaccines")
            .document(vaccine.petId)
            .collection("records")
            .document(vaccine.vaccineId)
            .set(vaccineToMap(vaccine))
    }

    private fun vaccineToMap(v: VaccineEntity): Map<String, Any?> {
        return mapOf(
            "vaccineId" to v.vaccineId,
            "petId" to v.petId,
            "name" to v.name,
            "date" to v.date,
            "type" to v.type,
            "clinic" to v.clinic,
            "doseNumber" to v.doseNumber,
            "endDate" to v.endDate,
            "note" to v.note,
            "photoUrl" to v.photoUrl,
            "createdAt" to v.createdAt,
            "nextDoseDate" to v.nextDoseDate
        )
    }
    suspend fun uploadVaccineImage(context: Context, uri: Uri, userId: String): String? {
        val result = cloudinary.uploadImage(context, uri, userId)
        return result.getOrNull()
    }
}
