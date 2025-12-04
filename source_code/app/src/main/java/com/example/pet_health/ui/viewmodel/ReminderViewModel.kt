package com.example.pet_health.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health.data.entity.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ReminderViewModel : ViewModel() {

    // Danh sách hiển thị lên UI
    private val _reminders = mutableStateOf<List<Reminder>>(emptyList())
    val reminders: State<List<Reminder>> = _reminders

    // Firebase Instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var snapshotListener: ListenerRegistration? = null

    // <--- THÊM BIẾN NÀY: Lưu ID vừa tạo để UI biết đường chuyển trang
    var createdReminderId = mutableStateOf<String?>(null)

    init {
        // Tự động tải dữ liệu khi khởi tạo
        fetchRemindersRealtime()
    }

    // 1. Lắng nghe dữ liệu Realtime
    private fun fetchRemindersRealtime() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("ReminderViewModel", "User chưa đăng nhập, không thể tải dữ liệu")
            _reminders.value = emptyList()
            return
        }

        // Đường dẫn: users -> [userID] -> reminders
        val ref = db.collection("users").document(userId).collection("reminders")

        snapshotListener = ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ReminderViewModel", "Lỗi lắng nghe Firestore", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.toObjects(Reminder::class.java)
                _reminders.value = list
                Log.d("ReminderViewModel", "Đã tải ${list.size} nhắc nhở")
            }
        }
    }

    // 2. Thêm nhắc nhở mới
    fun addReminder(reminder: Reminder) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("ReminderViewModel", "Lỗi: User ID là null, không thể lưu")
            return
        }

        // Reset ID trước khi thực hiện lưu
        createdReminderId.value = null

        Log.d("ReminderViewModel", "Đang lưu nhắc nhở: ${reminder.title} cho user: $userId")

        db.collection("users").document(userId)
            .collection("reminders")
            .document(reminder.id)
            .set(reminder)
            .addOnSuccessListener {
                Log.d("ReminderViewModel", "Lưu thành công lên Firebase!")
                // <--- CẬP NHẬT ID MỚI ĐỂ UI CHUYỂN TRANG
                createdReminderId.value = reminder.id
            }
            .addOnFailureListener { e ->
                Log.e("ReminderViewModel", "Lưu thất bại", e)
            }
    }

    // 3. Cập nhật (Sửa)
    fun updateReminder(updatedReminder: Reminder) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("reminders")
            .document(updatedReminder.id)
            .set(updatedReminder)
            .addOnSuccessListener { Log.d("ReminderViewModel", "Cập nhật thành công") }
            .addOnFailureListener { e -> Log.e("ReminderViewModel", "Cập nhật thất bại", e) }
    }

    // 4. Cập nhật trạng thái
    fun updateReminderStatus(id: String, newStatus: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "status" to newStatus,
            "isDone" to (newStatus == "Hoàn thành")
        )

        db.collection("users").document(userId)
            .collection("reminders")
            .document(id)
            .update(updates)
    }

    // 5. Xóa
    fun deleteReminder(id: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("reminders")
            .document(id)
            .delete()
    }

    // 6. Lấy chi tiết
    fun getReminderById(id: String): Reminder? {
        return _reminders.value.find { it.id == id }
    }

    override fun onCleared() {
        super.onCleared()
        snapshotListener?.remove()
    }
}

// Factory
class ReminderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}