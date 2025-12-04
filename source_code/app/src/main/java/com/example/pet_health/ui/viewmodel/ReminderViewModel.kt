package com.example.pet_health.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health.data.entity.Reminder
import java.util.UUID

// Không cần truyền Repository lúc này vì ta đang làm logic trên RAM trước
class ReminderViewModel : ViewModel() {

    // Danh sách nhắc nhở
    private val _reminders = mutableStateOf<List<Reminder>>(emptyList())
    val reminders: State<List<Reminder>> = _reminders

    init {
        initDummyReminders()
    }

    // Tạo dữ liệu mẫu
    private fun initDummyReminders() {
        _reminders.value = listOf(
            Reminder(
                id = UUID.randomUUID().toString(),
                petName = "Mimi", title = "Mũi FVRCP #4", type = "Tiêm phòng",
                date = "25/10/2026", time = "09:00", repeat = "6 tháng",
                earlyNotify = "30 phút", note = "Mang sổ khám", status = "Sắp tới"
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                petName = "Lu", title = "Tẩy giun định kỳ", type = "Tẩy giun",
                date = "25/04/2025", time = "08:00", repeat = "3 tháng",
                earlyNotify = "Không", note = "Mua thuốc Drontal", status = "Sắp tới"
            )
        )
    }

    // Thêm nhắc nhở
    fun addReminder(reminder: Reminder) {
        val currentList = _reminders.value.toMutableList()
        currentList.add(reminder)
        _reminders.value = currentList
    }

    // Lấy chi tiết theo ID
    fun getReminderById(id: String): Reminder? {
        return _reminders.value.find { it.id == id }
    }

    // Cập nhật trạng thái
    fun updateReminderStatus(id: String, newStatus: String) {
        val currentList = _reminders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }

        if (index != -1) {
            val oldItem = currentList[index]
            val newItem = oldItem.copy(
                status = newStatus,
                isDone = (newStatus == "Hoàn thành")
            )
            currentList[index] = newItem
            _reminders.value = currentList
        }
    }

    // Xóa nhắc nhở
    fun deleteReminder(id: String) {
        val currentList = _reminders.value.toMutableList()
        currentList.removeAll { it.id == id }
        _reminders.value = currentList
    }
    // Hàm cập nhật thông tin nhắc nhở (Edit)
    fun updateReminder(updatedReminder: Reminder) {
        val currentList = _reminders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedReminder.id }

        if (index != -1) {
            currentList[index] = updatedReminder
            _reminders.value = currentList
        }
    }
}

// Factory cho ReminderViewModel (Cần thiết để khởi tạo trong Compose)
class ReminderViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}