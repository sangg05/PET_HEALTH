package com.example.pet_health.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

