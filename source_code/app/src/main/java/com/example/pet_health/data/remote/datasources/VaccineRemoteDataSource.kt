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

    // 1. Upload ảnh -> Lấy URL
    suspend fun uploadVaccineImage(petId: String, uri: Uri?): String? {
        if (uri == null) return null
        return try {
            val fileName = "vaccine_${petId}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.getReference("vaccine_images/$fileName")

            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. Lưu bản ghi lên Firestore
    suspend fun uploadVaccine(vaccine: VaccineEntity) {
        try {
            firestore.collection("vaccines")
                .document(vaccine.vaccineId)
                .set(vaccine)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 3. Xóa bản ghi trên Firestore (MỚI THÊM)
    suspend fun deleteVaccine(vaccine: VaccineEntity) {
        try {
            // Xóa document trong collection "vaccines" dựa trên ID
            firestore.collection("vaccines")
                .document(vaccine.vaccineId)
                .delete()
                .await()

            // (Tùy chọn) Nếu bạn muốn xóa luôn ảnh trên Storage để tiết kiệm dung lượng:
            /*
            if (!vaccine.photoUrl.isNullOrEmpty()) {
                val imageRef = storage.getReferenceFromUrl(vaccine.photoUrl)
                imageRef.delete().await()
            }
            */
        } catch (e: Exception) {
            e.printStackTrace()
        }
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