package com.example.pet_health.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health.data.entity.Reminder
import com.example.pet_health.utils.ReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// Sửa thành AndroidViewModel để lấy context cho AlarmManager
class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    // Danh sách hiển thị lên UI
    private val _reminders = mutableStateOf<List<Reminder>>(emptyList())
    val reminders: State<List<Reminder>> = _reminders

    // Firebase Instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var snapshotListener: ListenerRegistration? = null

    // Biến lưu ID vừa tạo để chuyển trang
    var createdReminderId = mutableStateOf<String?>(null)

    // Khởi tạo bộ đặt lịch (Scheduler)
    private val scheduler = ReminderScheduler(application.applicationContext)

    init {
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
            Log.e("ReminderViewModel", "Lỗi: User ID là null")
            return
        }

        createdReminderId.value = null // Reset ID

        Log.d("ReminderViewModel", "Đang lưu: ${reminder.title}")

        db.collection("users").document(userId)
            .collection("reminders")
            .document(reminder.id)
            .set(reminder)
            .addOnSuccessListener {
                Log.d("ReminderViewModel", "Lưu Firebase thành công!")

                // === ĐẶT LỊCH HỆ THỐNG ===
                scheduler.scheduleReminder(reminder)

                // Cập nhật ID để UI chuyển trang
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
            .addOnSuccessListener {
                Log.d("ReminderViewModel", "Cập nhật thành công")

                // === CẬP NHẬT LẠI LỊCH HỆ THỐNG ===
                scheduler.scheduleReminder(updatedReminder)
            }
            .addOnFailureListener { e ->
                Log.e("ReminderViewModel", "Cập nhật thất bại", e)
            }
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
            .addOnSuccessListener {
                // Nếu đã hoàn thành, có thể hủy lịch báo thức nếu muốn
                // val reminder = getReminderById(id)
                // if (reminder != null && newStatus == "Hoàn thành") scheduler.cancelReminder(reminder)
            }
    }

    // 5. Xóa
    fun deleteReminder(id: String) {
        val userId = auth.currentUser?.uid ?: return

        // Tìm reminder trước khi xóa để lấy thông tin hủy lịch
        val reminderToDelete = getReminderById(id)

        db.collection("users").document(userId)
            .collection("reminders")
            .document(id)
            .delete()
            .addOnSuccessListener {
                // === HỦY LỊCH HỆ THỐNG ===
                if (reminderToDelete != null) {
                    scheduler.cancelReminder(reminderToDelete)
                    Log.d("ReminderViewModel", "Đã hủy lịch báo thức")
                }
            }
            .addOnFailureListener { e ->
                Log.w("ReminderViewModel", "Lỗi khi xóa", e)
            }
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

// Factory cần nhận Application Context
class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}