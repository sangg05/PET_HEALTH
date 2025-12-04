package com.example.pet_health.data.remote.datasources

import android.net.Uri
import com.example.pet_health.data.entity.VaccineEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VaccineRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    // 📌 Upload ảnh → lấy URL
    suspend fun uploadVaccineImage(petId: String, uri: Uri?): String? {
        if (uri == null) return null

        val fileName = "vaccine_${petId}_${UUID.randomUUID()}.jpg"
        val storageRef = storage.getReference("vaccine_images/$fileName")

        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    // 📌 Lưu bản ghi lên Firestore
    suspend fun uploadVaccine(vaccine: VaccineEntity) {
        firestore.collection("vaccines")
            .document(vaccine.vaccineId)
            .set(vaccine)
            .await()
    }
}
//package com.example.pet_health.data.remote.datasources
//
//import android.net.Uri
//import com.example.pet_health.data.entity.VaccineEntity
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.tasks.await
//import java.util.UUID
//
//class VaccineRemoteDataSource(
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
//    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
//) {
//
//    // Upload ảnh lên Firebase Storage → trả về URL
//    suspend fun uploadVaccineImage(petId: String, uri: Uri?): String? {
//        if (uri == null) return null
//        return try {
//            val fileName = "vaccine_${petId}_${UUID.randomUUID()}.jpg"
//            val storageRef = storage.getReference("vaccine_images/$fileName")
//            storageRef.putFile(uri).await()
//            storageRef.downloadUrl.await().toString()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    // Upload Vaccine lên Firestore (dùng Map để tránh lỗi serialize)
//    suspend fun uploadVaccine(vaccine: VaccineEntity) {
//        try {
//            val data = mapOf(
//                "vaccineId" to vaccine.vaccineId,
//                "petId" to vaccine.petId,
//                "name" to vaccine.name,
//                "date" to vaccine.date,
//                "clinic" to vaccine.clinic,
//                "doseNumber" to vaccine.doseNumber,
//                "note" to vaccine.note,
//                "photoUrl" to vaccine.photoUrl,
//                "nextDoseDate" to vaccine.nextDoseDate,
//                "createdAt" to vaccine.createdAt
//            )
//            firestore.collection("vaccines")
//                .document(vaccine.vaccineId)
//                .set(data)
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
