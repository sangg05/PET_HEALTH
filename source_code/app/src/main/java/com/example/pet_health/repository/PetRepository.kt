package com.example.pet_health.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.SymptomLogEntity
import com.example.pet_health.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import pet_health.data.local.AppDatabase





class UserRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getDatabase(context)

    val repository = PetRepository(database)

    private val userDao = database.userDao()



    suspend fun registerUser(name: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false
            val user = UserEntity(userId = uid, name = name, email = email, passwordHash = password)

            // Lưu vào Room luôn
            withContext(Dispatchers.IO) { userDao.insertUser(user) }

            // Lưu Firestore async, không block UI
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firestore.collection("users").document(uid).set(user).await()
                } catch(e: Exception) {}
            }
            true // trả về thành công ngay
        } catch (e: Exception) {
            false
        }
    }


    var currentUser = mutableStateOf<UserEntity?>(null)
        private set
    suspend fun loginUser(email: String, password: String): Pair<Boolean, String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser!!.uid
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(UserEntity::class.java)
            user?.let {
                withContext(Dispatchers.IO) {
                    // Xóa dữ liệu user cũ trước
                    database.clearAllTables()

                    // Lưu user mới
                    userDao.insertUser(it)

                    // Sync pets của user mới
                    syncPetsFromFirebase(uid)

                    syncAllSymptomsForUser(uid)
                }
                currentUser.value = it
            }
            Pair(true, "Đăng nhập thành công")
        } catch (e: Exception) {
            Pair(false, e.message ?: "Lỗi đăng nhập")
        }
    }
    private suspend fun syncPetsFromFirebase(userId: String) {
        try {
            val petsSnapshot = firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .get()
                .await()

            val pets = petsSnapshot.documents.mapNotNull { doc ->
                PetEntity(
                    petId = doc.getString("petId") ?: return@mapNotNull null,
                    userId = userId, // ✅ Đảm bảo userId là của user hiện tại
                    name = doc.getString("name") ?: "",
                    species = doc.getString("species") ?: "",
                    breed = doc.getString("breed") ?: "",
                    color = doc.getString("color"),
                    imageUrl = doc.getString("imageUrl"),
                    birthDate = doc.getLong("birthDate") ?: 0L,
                    weightKg = doc.getDouble("weightKg")?.toFloat() ?: 0f,
                    sizeCm = doc.getDouble("sizeCm")?.toFloat(),
                    healthStatus = doc.getString("healthStatus") ?: "",
                    adoptionDate = doc.getLong("adoptionDate")
                )
            }

            pets.forEach { pet ->
                repository.insertPet(pet)
            }

            Log.d("UserRepository", "Synced ${pets.size} pets for user $userId")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error syncing pets", e)
        }
    }


    //  Hàm đăng xuất
    suspend fun logoutUser() {
        auth.signOut()
        currentUser.value = null
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
    private suspend fun syncAllSymptomsForUser(userId: String) {
        val pets = database.petDao().getPetsByUserId(userId)
        pets.forEach { pet ->
            repository.syncSymptomsFromFirebase(pet.petId)
        }
    }
}

class PetRepository (private val db: AppDatabase) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //  Lấy pets theo userId
    suspend fun getPetsFromFirebase(): List<PetEntity> {
        return try {
            val userId = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val birthDate = doc.getLong("birthDate") ?: 0L
                PetEntity(
                    petId = doc.id,
                    userId = doc.getString("userId") ?: userId,
                    name = doc.getString("name") ?: "",
                    species = doc.getString("species") ?: "Tất cả",
                    breed = doc.getString("breed") ?: "",
                    color = doc.getString("color"),
                    imageUrl = doc.getString("imageUrl"),
                    birthDate = birthDate,
                    weightKg = (doc.getDouble("weightKg") ?: 0.0).toFloat(),
                    sizeCm = (doc.getDouble("sizeCm") ?: 0.0).toFloat(),
                    healthStatus = doc.getString("healthStatus") ?: "",
                    adoptionDate = doc.getLong("adoptionDate")
                )
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error getting pets from Firebase", e)
            emptyList()
        }
    }


    suspend fun getAllPets(): List<PetEntity> {
        return db.petDao().getAllPets()
    }

    // Thêm: Lấy pets của user hiện tại từ Room
    suspend fun getPetsByUserId(userId: String): List<PetEntity> {
        return db.petDao().getPetsByUserId(userId)
    }

    suspend fun insertPet(pet: PetEntity) {
        db.petDao().insertPet(pet)
    }

    suspend fun updatePet(pet: PetEntity) {
        try {
            // Cập nhật Room
            db.petDao().updatePet(pet)

            // Cập nhật Firebase
            val userId = auth.currentUser?.uid ?: return
            firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(pet.petId)
                .set(pet)
                .await()
        } catch (e: Exception) {
            Log.e("PetRepository", "Error updating pet", e)
        }
    }

    suspend fun getPetById(petId: String): PetEntity? {
        return db.petDao().getPetById(petId)
    }

    // Lấy pet từ Firestore theo userId
    suspend fun getPetByIdFromFirestore(petId: String): PetEntity? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val doc = firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .get()
                .await()
            doc.toObject(PetEntity::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePet(pet: PetEntity) {
        db.petDao().deletePet(pet)
    }

    suspend fun deletePetFromFirestore(petId: String) {
        try {
            val userId = auth.currentUser?.uid ?: return
            firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Thêm triệu chứng vào Room
    suspend fun insertSymptom(symptom: SymptomLogEntity) {
        db.symptomLogDao().insertLog(symptom)
    }
    // Đồng bộ triệu chứng lên Firebase
    suspend fun uploadSymptomToFirebase(symptom: SymptomLogEntity) {
        try {
            val userId = auth.currentUser?.uid ?: return
            firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(symptom.petId)
                .collection("symptoms")
                .document(symptom.id)
                .set(symptom)
                .await()
        } catch (e: Exception) {
            Log.e("PetRepository", "Error uploading symptom", e)
        }
    }
    // Xóa triệu chứng khỏi Room
    suspend fun deleteSymptom(symptom: SymptomLogEntity) {
        db.symptomLogDao().deleteLog(symptom)
    }
    // Xóa triệu chứng khỏi Firebase
    suspend fun deleteSymptomFromFirebase(petId: String, symptomId: String) {
        try {
            val userId = auth.currentUser?.uid ?: return
            firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("symptoms")
                .document(symptomId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("PetRepository", "Error deleting symptom", e)
        }
    }
    // Lấy danh sách triệu chứng của pet từ Room
    fun getSymptomsOfPet(petId: String): Flow<List<SymptomLogEntity>> {
        return db.symptomLogDao().getLogsByPet(petId)
    }
    suspend fun syncSymptomsFromFirebase(petId: String) {
        try {
            val userId = auth.currentUser?.uid ?: return
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("symptoms")
                .get()
                .await()

            val symptoms = snapshot.documents.mapNotNull { doc ->
                SymptomLogEntity(
                    id = doc.id,
                    petId = petId,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                )
            }

            symptoms.forEach { symptom ->
                db.symptomLogDao().insertLog(symptom)
            }

            Log.d("PetRepository", "Synced ${symptoms.size} symptoms for pet $petId")
        } catch (e: Exception) {
            Log.e("PetRepository", "Error syncing symptoms", e)
        }
    }


}
