package com.example.pet_health.data.repository

import android.content.Context
import android.util.Log
import com.example.pet_health.data.entity.NotificationEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import pet_health.data.local.AppDatabase

class NotificationRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val notificationDao = db.notificationDao()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // LẤY NOTIFICATIONS CỦA USER HIỆN TẠI
    fun getNotifications(): Flow<List<NotificationEntity>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return notificationDao.getNotificationsByUserId(userId)
    }

    // LẤY SỐ LƯỢNG CHƯA ĐỌC
    fun getUnreadCount(): Flow<Int> {
        val userId = auth.currentUser?.uid ?: return flowOf(0)
        return notificationDao.getUnreadCount(userId)
    }

    // ĐÁNH DẤU ĐÃ ĐỌC
    suspend fun markAsRead(id: Int) {
        notificationDao.markAsRead(id)

        // Cập nhật Firestore
        try {
            val userId = auth.currentUser?.uid ?: return
            firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .document(id.toString())
                .update("isRead", true)
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error updating Firestore", e)
        }
    }

    // LƯU NOTIFICATION MỚI
    suspend fun saveNotification(
        reminderId: String,
        title: String,
        message: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        val notification = NotificationEntity(
            userId = userId,
            reminderId = reminderId,
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )

        // Lưu vào Room
        notificationDao.insert(notification)

        // Lưu lên Firestore
        try {
            firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .add(notification.toMap()) // ✅ Chuyển thành Map
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error saving to Firestore", e)
        }
    }

    // ĐỒNG BỘ TỪ FIRESTORE
    suspend fun syncFromFirebase() {
        val userId = auth.currentUser?.uid ?: return

        try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .get()
                .await()

            val notifications = snapshot.documents.mapNotNull { doc ->
                try {
                    NotificationEntity(
                        id = doc.id.hashCode(), // Dùng hashCode của documentId
                        userId = userId,
                        reminderId = doc.getString("reminderId") ?: "",
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }

            notifications.forEach { notificationDao.insert(it) }

            Log.d("NotificationRepo", "Synced ${notifications.size} notifications")
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error syncing notifications", e)
        }
    }

    // HELPER: Chuyển Entity thành Map
    private fun NotificationEntity.toMap(): Map<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "reminderId" to reminderId,
            "title" to title,
            "message" to message,
            "timestamp" to timestamp,
            "isRead" to isRead
        )
    }
}