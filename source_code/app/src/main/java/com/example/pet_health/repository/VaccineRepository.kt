package com.example.pet_health.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.repository.CloudinaryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import pet_health.data.local.AppDatabase

class VaccineRepository(context: Context) {
    private val cloudinary = CloudinaryRepository()
    private val db = AppDatabase.getDatabase(context)
    private val vaccineDao = db.vaccineDao()
    private val petDao = db.petDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Lấy tất cả vaccine từ ROOM
    fun getAllVaccines(): Flow<List<VaccineEntity>> = vaccineDao.getAllVaccines()

    // Lấy tất cả pet
    suspend fun getAllPets(): List<PetEntity> {
        return petDao.getAllPets()
    }

    // ---------------------------
    //    SYNC FROM FIRESTORE
    // ---------------------------
    suspend fun syncVaccinesFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("VaccineRepo", "❌ User not logged in, cannot sync")
            return
        }

        try {
            Log.d("VaccineRepo", "🔄 Syncing vaccines from Firestore...")

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("vaccines")
                .get()
                .await()

            val vaccinesFromFirestore = snapshot.documents.mapNotNull { doc ->
                try {
                    VaccineEntity(
                        vaccineId = doc.getString("vaccineId") ?: return@mapNotNull null,
                        petId = doc.getString("petId") ?: "",
                        name = doc.getString("name") ?: "",
                        date = doc.getLong("date") ?: 0L,
                        type = doc.getString("type") ?: "",
                        clinic = doc.getString("clinic"),
                        doseNumber = doc.getLong("doseNumber")?.toInt(),
                        endDate = doc.getLong("endDate"),
                        note = doc.getString("note"),
                        photoUrl = doc.getString("photoUrl"),
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        nextDoseDate = doc.getLong("nextDoseDate")
                    )
                } catch (e: Exception) {
                    Log.e("VaccineRepo", "Error parsing vaccine: ${e.message}")
                    null
                }
            }

            // Lưu tất cả vào Room DB
            vaccinesFromFirestore.forEach { vaccine ->
                vaccineDao.insertVaccine(vaccine)
            }

            Log.d("VaccineRepo", "✅ Synced ${vaccinesFromFirestore.size} vaccines from Firestore")
        } catch (e: Exception) {
            Log.e("VaccineRepo", "❌ Error syncing vaccines: ${e.message}")
            e.printStackTrace()
        }
    }

    // ---------------------------
    //         ADD VACCINE
    // ---------------------------
    suspend fun addVaccine(vaccine: VaccineEntity) {
        // 1. Lưu vào Room DB
        vaccineDao.insertVaccine(vaccine)

        // 2. Lưu vào Firestore với userId
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val vaccineMap = vaccineToMap(vaccine)

            firestore.collection("users")
                .document(userId)
                .collection("vaccines")
                .document(vaccine.vaccineId)
                .set(vaccineMap)
                .addOnSuccessListener {
                    Log.d("VaccineRepo", "✅ Vaccine saved to Firestore: ${vaccine.vaccineId}")
                }
                .addOnFailureListener { e ->
                    Log.e("VaccineRepo", "❌ Error saving vaccine: ${e.message}")
                }
        } else {
            Log.e("VaccineRepo", "❌ User not logged in!")
        }
    }

    // ---------------------------
    //      DELETE VACCINE
    // ---------------------------
    suspend fun deleteVaccine(vaccine: VaccineEntity) {
        // 1. Xóa khỏi Room DB
        vaccineDao.deleteVaccine(vaccine)

        // 2. Xóa khỏi Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                firestore.collection("users")
                    .document(userId)
                    .collection("vaccines")
                    .document(vaccine.vaccineId)
                    .delete()
                    .await()
                Log.d("VaccineRepo", "✅ Vaccine deleted: ${vaccine.vaccineId}")
            } catch (e: Exception) {
                Log.e("VaccineRepo", "❌ Error deleting vaccine: ${e.message}")
            }
        }
    }

    // ---------------------------
    //      UPDATE VACCINE
    // ---------------------------
    suspend fun updateVaccine(vaccine: VaccineEntity) {
        // 1. Update Room DB
        vaccineDao.updateVaccine(vaccine)

        // 2. Update Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val vaccineMap = vaccineToMap(vaccine)

            firestore.collection("users")
                .document(userId)
                .collection("vaccines")
                .document(vaccine.vaccineId)
                .set(vaccineMap)
                .addOnSuccessListener {
                    Log.d("VaccineRepo", "✅ Vaccine updated: ${vaccine.vaccineId}")
                }
                .addOnFailureListener { e ->
                    Log.e("VaccineRepo", "❌ Error updating vaccine: ${e.message}")
                }
        }
    }

    // Convert VaccineEntity to Map
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

    // Upload ảnh vaccine lên Cloudinary
    suspend fun uploadVaccineImage(context: Context, uri: Uri, userId: String): String? {
        val result = cloudinary.uploadImage(context, uri, userId)
        return result.getOrNull()
    }
}