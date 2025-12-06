package com.example.pet_health.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    application: Application,
    private val repo: NotificationRepository
) : AndroidViewModel(application) {

    val notifications = repo.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SỐ LƯỢNG CHƯA ĐỌC
    val unreadCount = repo.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            repo.markAsRead(id)
        }
    }

    // SYNC TỪ FIREBASE
    fun syncNotifications() {
        viewModelScope.launch {
            repo.syncFromFirebase()
        }
    }
}
