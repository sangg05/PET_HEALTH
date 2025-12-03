package com.example.pet_health.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                withContext(Dispatchers.IO) { userDao.insertUser(it) }
                currentUser.value = it // cập nhật state
            }
            Pair(true, "Đăng nhập thành công")
        } catch (e: Exception) {
            Pair(false, e.message ?: "Lỗi đăng nhập")
        }
    }
}

class PetRepository (private val db: AppDatabase) {
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun getPets(): List<PetEntity> {
        return try {
            val snapshot = firestore.collection("pets").get().await()
            snapshot.documents.map { doc ->
                val birthDate = doc.getLong("birthDate") ?: 0L
                PetEntity(
                    petId = doc.id,
                    userId = doc.getString("userId") ?: "",
                    name = doc.getString("name") ?: "",
                    species = doc.getString("species") ?: "Tất cả",
                    breed = doc.getString("breed") ?: "",
                    color = doc.getString("color"),
                    imageUrl = doc.getString("avatarUrl"),
                    birthDate = birthDate,
                    weightKg = (doc.getDouble("weightKg") ?: 0.0).toFloat(),
                    sizeCm = (doc.getDouble("sizeCm") ?: 0.0).toFloat(),
                    healthStatus = doc.getString("healthStatus") ?: "",
                    adoptionDate = doc.getLong("adoptionDate")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPets(): List<PetEntity> {
        return db.petDao().getAllPets()
    }

    // Thêm pet vào Room
    suspend fun insertPet(pet: PetEntity) {
        db.petDao().insertPet(pet)
    }

    //Update
    suspend fun updatePet(pet: PetEntity) {
        db.petDao().updatePet(pet)
    }

    suspend fun getPetById(petId: String): PetEntity? {
        return db.petDao().getPetById(petId)
    }

    suspend fun getPetByIdFromFirestore(petId: String): PetEntity? {
        return try {
            val doc = firestore.collection("pets").document(petId).get().await()
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
            firestore.collection("pets").document(petId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

